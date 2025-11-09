package com.fu.coffeeshop_management.server.repository.specs;

import com.fu.coffeeshop_management.server.entity.Voucher;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VoucherSpecs {

    public static Specification<Voucher> filter(
            String codeLike,
            Voucher.VoucherStatus status,
            Voucher.VoucherType type
    ) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (codeLike != null && !codeLike.isBlank()) {
                String like = "%" + codeLike.trim().toUpperCase() + "%";
                ps.add(cb.like(cb.upper(root.get("code")), like));
            }
            if (status != null) {
                ps.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                ps.add(cb.equal(root.get("discountType"), type));
            }

            return cb.and(ps.toArray(new Predicate[0]));
        };
    }
}
