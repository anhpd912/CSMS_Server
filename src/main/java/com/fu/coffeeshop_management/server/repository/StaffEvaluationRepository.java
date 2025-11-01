package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.StaffEvaluation;
import com.fu.coffeeshop_management.server.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffEvaluationRepository extends JpaRepository<StaffEvaluation, UUID> {
    List<StaffEvaluation> findByStaff(User staff);
}
