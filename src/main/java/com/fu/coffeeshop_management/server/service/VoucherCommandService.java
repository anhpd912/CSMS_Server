package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.UpdateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.exception.BadRequestException;
import com.fu.coffeeshop_management.server.exception.ConflictException;
import com.fu.coffeeshop_management.server.exception.NotFoundException;
import com.fu.coffeeshop_management.server.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoucherCommandService {
    private final VoucherRepository repo;

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
}
