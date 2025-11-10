package com.fu.coffeeshop_management.server.controller;

import com.fu.coffeeshop_management.server.dto.LoyaltyMemberDetailResponse;
import com.fu.coffeeshop_management.server.dto.LoyaltyMemberListItem;
import com.fu.coffeeshop_management.server.dto.PointsHistoryItem;
import com.fu.coffeeshop_management.server.dto.UpdateLoyaltyMemberRequest;
import com.fu.coffeeshop_management.server.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loyalty-members")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService service;

    @GetMapping
    public List<LoyaltyMemberListItem> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "name") String sortBy
    ) {
        return service.listMembers(q, sortBy);
    }

    @PatchMapping(
            path = "/{customerId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LoyaltyMemberDetailResponse edit(
            @PathVariable UUID customerId,
            @RequestBody UpdateLoyaltyMemberRequest req,
            Authentication auth
    ) {
        String actor = (auth != null ? auth.getName() : "SYSTEM");
        return service.editMember(customerId, req, actor);
    }

    @GetMapping("/{customerId}/points-history")
    public List<PointsHistoryItem> pointsHistory(@PathVariable UUID customerId) {
        return service.pointsHistory(customerId);
    }
}
