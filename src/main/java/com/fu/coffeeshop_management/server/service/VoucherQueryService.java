package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.VoucherListItem;
import com.fu.coffeeshop_management.server.entity.Voucher;
import com.fu.coffeeshop_management.server.repository.VoucherRepository;
import com.fu.coffeeshop_management.server.repository.specs.VoucherSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherQueryService {

    private final VoucherRepository repo;

    public List<VoucherListItem> list(String codeLike, Voucher.VoucherStatus status,
                                      Voucher.VoucherType type, String sortBy) {
        // sortBy chỉ chấp nhận startDate|endDate, mặc định startDate
        String sortField = "startDate";
        if ("endDate".equalsIgnoreCase(sortBy)) sortField = "endDate";
        Sort sort = Sort.by(Sort.Direction.ASC, sortField); // tăng dần

        return repo.findAll(VoucherSpecs.filter(codeLike, status, type), sort)
                .stream()
                .map(v -> new VoucherListItem(
                        v.getId(), v.getCode(), v.getDiscountType(), v.getDiscountValue(),
                        v.getStartDate(), v.getEndDate(), v.getStatus()))
                .toList();
    }

}
