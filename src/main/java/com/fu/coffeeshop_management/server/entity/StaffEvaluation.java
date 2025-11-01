package com.fu.coffeeshop_management.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "staff_evaluation")
public class StaffEvaluation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "evaluation_id", updatable = false, nullable = false)
    private UUID evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;
}
