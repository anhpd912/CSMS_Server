package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.dto.LoyaltyMemberDetailResponse;
import com.fu.coffeeshop_management.server.dto.LoyaltyMemberListItem;
import com.fu.coffeeshop_management.server.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("""
        select new com.fu.coffeeshop_management.server.dto.LoyaltyMemberListItem(
            c.id, c.fullName, c.phone, c.email, l.loyaltyId, l.points, l.tier
        )
        from Customer c
        join c.loyalty l
        where (:q is null or :q = '' 
               or upper(c.fullName) like upper(concat('%', :q, '%'))
               or c.phone like concat('%', :q, '%'))
        order by c.fullName asc
    """)
    List<LoyaltyMemberListItem> findMembersOrderByName(@Param("q") String q);

    @Query("""
        select new com.fu.coffeeshop_management.server.dto.LoyaltyMemberListItem(
            c.id, c.fullName, c.phone, c.email, l.loyaltyId, l.points, l.tier
        )
        from Customer c
        join c.loyalty l
        where (:q is null or :q = '' 
               or upper(c.fullName) like upper(concat('%', :q, '%'))
               or c.phone like concat('%', :q, '%'))
        order by l.points desc, c.fullName asc
    """)
    List<LoyaltyMemberListItem> findMembersOrderByPointsDesc(@Param("q") String q);

    boolean existsByPhoneIgnoreCaseAndIdNot(String phone, UUID id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    @Query("""
      select new com.fu.coffeeshop_management.server.dto.LoyaltyMemberDetailResponse(
          c.id, c.fullName, c.phone, c.email,
          l.loyaltyId, l.points, l.tier
      )
      from Customer c
      left join c.loyalty l
      where c.id = :id
    """)
    Optional<LoyaltyMemberDetailResponse> projectDetailById(@Param("id") UUID id);

}
