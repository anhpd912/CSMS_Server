package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.CreateVoucherRequest;
import com.fu.coffeeshop_management.server.dto.VoucherResponse;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.exception.BadRequestException;
import com.fu.coffeeshop_management.server.exception.ConflictException;
import com.fu.coffeeshop_management.server.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VoucherService {

    private final VoucherRepository repo;

    public VoucherService(VoucherRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public VoucherResponse create(CreateVoucherRequest req, String actor) {
        // 1) Uniqueness
        if (repo.existsByCodeIgnoreCase(req.getCode())) {
            throw new ConflictException("Voucher code đã tồn tại: " + req.getCode());
        }

        // 2) Date range
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("endDate phải >= startDate");
        }

        // 3) Value rules
        if (req.getType() == Voucher.VoucherType.PERCENT) {
            if (req.getValue().compareTo(BigDecimal.ZERO) <= 0 ||
                    req.getValue().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("PERCENT phải trong khoảng (0, 100]");
            }
        } else { // FIXED_AMOUNT
            if (req.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("FIXED_AMOUNT phải > 0");
            }
        }

        // 4) Map DTO -> Entity
        Voucher v = new Voucher();
        v.setCode(req.getCode());
        v.setDiscountType(req.getType());
        v.setDiscountValue(req.getValue());
        v.setStartDate(req.getStartDate());
        v.setEndDate(req.getEndDate());
        v.setStatus(req.getStatus());

        v = repo.saveAndFlush(v);

        // 5) Map Entity -> Response
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
