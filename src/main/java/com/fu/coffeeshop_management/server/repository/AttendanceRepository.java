package com.fu.coffeeshop_management.server.repository;

import com.fu.coffeeshop_management.server.entity.Attendance;
import com.fu.coffeeshop_management.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByUser(User user);
//    Attendance findByUserIdAndDate(UUID userId, String date);
    List<Attendance> findByUserId(UUID userId);
}
