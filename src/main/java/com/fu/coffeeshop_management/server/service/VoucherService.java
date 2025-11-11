package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.CreateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.UpdateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.exception.BadRequestException;
import com.fu.coffeeshop_management.server.exception.ConflictException;
import com.fu.coffeeshop_management.server.exception.NotFoundException;
import com.fu.coffeeshop_management.server.repository.VoucherRepository;
import com.fu.coffeeshop_management.server.repository.specs.VoucherSpecs;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {

    private final VoucherRepository repo;

    public VoucherService(VoucherRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public VoucherResponse create(CreateVoucherRequest req, String actor) {
        if (repo.existsByCodeIgnoreCase(req.getCode())) {
            throw new ConflictException("Voucher code đã tồn tại: " + req.getCode());
        }

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("endDate phải >= startDate");
        }

        if (req.getType() == Voucher.VoucherType.PERCENT) {
            if (req.getValue().compareTo(BigDecimal.ZERO) <= 0 ||
                    req.getValue().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("PERCENT phải trong khoảng (0, 100]");
            }
        } else {
            if (req.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("FIXED_AMOUNT phải > 0");
            }
        }

        Voucher v = new Voucher();
        v.setCode(req.getCode());
        v.setDiscountType(req.getType());
        v.setDiscountValue(req.getValue());
        v.setStartDate(req.getStartDate());
        v.setEndDate(req.getEndDate());
        v.setStatus(req.getStatus());

        v = repo.saveAndFlush(v);

        VoucherResponse res = new VoucherResponse();
        res.setId(v.getId());
        res.setCode(v.getCode());
        res.setType(v.getDiscountType());
        res.setValue(v.getDiscountValue());
        res.setStartDate(v.getStartDate());
        res.setEndDate(v.getEndDate());
        res.setStatus(v.getStatus());
        return res;
    }

    public List<VoucherResponse> list(String codeLike,
                                      Voucher.VoucherStatus status,
                                      Voucher.VoucherType type,
                                      String sortBy) {
        String sortField = "endDate".equalsIgnoreCase(sortBy) ? "endDate" : "startDate";
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);

        return repo.findAll(VoucherSpecs.filter(codeLike, status, type), sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional
    public VoucherResponse patch(UUID id, UpdateVoucherRequest req, String actor) {
        Voucher v = repo.findById(id).orElseThrow(() ->
                new NotFoundException("Voucher không tồn tại: " + id));

        if (req.getCode() != null) {
            String newCode = req.getCode().trim().toUpperCase();
            if (!newCode.equals(v.getCode())) {
                if (repo.existsByCodeIgnoreCaseAndIdNot(newCode, id)) {
                    throw new ConflictException("Voucher code đã tồn tại: " + newCode);
                }
                v.setCode(newCode);
            }
        }

        Voucher.VoucherType newType = req.getType() != null ? req.getType() : v.getDiscountType();
        BigDecimal newValue = req.getValue() != null ? req.getValue() : v.getDiscountValue();

        if (req.getType() != null) v.setDiscountType(newType);
        if (req.getValue() != null) v.setDiscountValue(newValue);
        if (req.getStartDate() != null) v.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) v.setEndDate(req.getEndDate());
        if (req.getStatus() != null) v.setStatus(req.getStatus());

        var start = v.getStartDate();
        var end = v.getEndDate();
        var val = v.getDiscountValue();
        var type = v.getDiscountType();

        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("endDate phải >= startDate");
        }
        if (type == Voucher.VoucherType.PERCENT) {
            if (val.compareTo(BigDecimal.ZERO) <= 0 || val.compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("PERCENT phải trong khoảng (0, 100]");
            }
        } else {
            if (val.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("FIXED_AMOUNT phải > 0");
            }
        }

        v = repo.save(v);

        VoucherResponse res = new VoucherResponse();
        res.setId(v.getId());
        res.setCode(v.getCode());
        res.setType(v.getDiscountType());
        res.setValue(v.getDiscountValue());
        res.setStartDate(v.getStartDate());
        res.setEndDate(v.getEndDate());
        res.setStatus(v.getStatus());
        return res;
    }

    public VoucherResponse getById(UUID id) {
        Voucher v = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Voucher không tồn tại: " + id));
        return toResponse(v);
    }

    private VoucherResponse toResponse(Voucher v) {
        VoucherResponse res = new VoucherResponse();
        res.setId(v.getId());
        res.setCode(v.getCode());
        res.setType(v.getDiscountType());
        res.setValue(v.getDiscountValue());
        res.setStartDate(v.getStartDate());
        res.setEndDate(v.getEndDate());
        res.setStatus(v.getStatus());
        return res;
    }
}
