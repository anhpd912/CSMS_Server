package com.fu.coffeeshop_management.server.service;

import com.fu.coffeeshop_management.server.dto.LoyaltyMemberDetailResponse;
import com.fu.coffeeshop_management.server.dto.LoyaltyMemberListItem;
import com.fu.coffeeshop_management.server.dto.PointsHistoryItem;
import com.fu.coffeeshop_management.server.dto.UpdateLoyaltyMemberRequest;
import com.fu.coffeeshop_management.server.entity.Customer;
import com.fu.coffeeshop_management.server.exception.BadRequestException;
import com.fu.coffeeshop_management.server.exception.ConflictException;
import com.fu.coffeeshop_management.server.exception.NotFoundException;
import com.fu.coffeeshop_management.server.repository.CustomerRepository;
import com.fu.coffeeshop_management.server.repository.LoyaltyTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final CustomerRepository customerRepo;
    private final LoyaltyTransactionRepository txRepo;


    private static final Pattern PHONE_RE = Pattern.compile("^[0-9+][0-9]{7,14}$");
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public List<LoyaltyMemberListItem> listMembers(String q, String sortBy) {
        String query = (q == null || q.isBlank()) ? "" : q.trim();
        if ("points".equalsIgnoreCase(sortBy)) {
            return customerRepo.findMembersOrderByPointsDesc(query);
        }
        // default: sort by name asc
        return customerRepo.findMembersOrderByName(query);
    }

    @Transactional
    public LoyaltyMemberDetailResponse editMember(UUID customerId, UpdateLoyaltyMemberRequest req, String actor) {
        if (req == null) throw new BadRequestException("Empty request");
        if (req.fullName() == null && req.phone() == null && req.email() == null) {
            throw new BadRequestException("No field to update");
        }

        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + customerId));

        // fullName
        if (req.fullName() != null) {
            String name = req.fullName().trim();
            if (name.isEmpty()) throw new BadRequestException("Full name must not be empty");
            c.setFullName(name);
        }

        // phone
        if (req.phone() != null) {
            String phone = req.phone().trim();
            if (phone.isEmpty()) throw new BadRequestException("Phone must not be empty");
            if (!PHONE_RE.matcher(phone).matches()) throw new BadRequestException("Invalid phone format");
            if (customerRepo.existsByPhoneIgnoreCaseAndIdNot(phone, customerId)) {
                throw new ConflictException("Phone number already exists: " + phone);
            }
            c.setPhone(phone);
        }

        // email (optional)
        if (req.email() != null) {
            String email = req.email().trim();
            if (!email.isEmpty()) {
                if (!EMAIL_RE.matcher(email).matches()) throw new BadRequestException("Invalid email format");
                if (customerRepo.existsByEmailIgnoreCaseAndIdNot(email, customerId)) {
                    throw new ConflictException("Email already exists: " + email);
                }
                c.setEmail(email);
            } else {
                c.setEmail(null);
            }
        }

        // save
        customerRepo.save(c);

        // project DTO detail
        return customerRepo.projectDetailById(customerId)
                .orElseGet(() -> new LoyaltyMemberDetailResponse(
                        c.getId(), c.getFullName(), c.getPhone(), c.getEmail(),
                        c.getLoyalty() != null ? c.getLoyalty().getLoyaltyId() : null,
                        c.getLoyalty() != null ? c.getLoyalty().getPoints() : null,
                        c.getLoyalty() != null ? c.getLoyalty().getTier() : null
                ));
    }

    @Transactional(readOnly = true)
    public List<PointsHistoryItem> pointsHistory(UUID customerId) {
        Customer c = customerRepo.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));

        if (c.getLoyalty() == null) {
            return List.of();
        }
        return txRepo.findHistoryByCustomerId(customerId);
    }

}
