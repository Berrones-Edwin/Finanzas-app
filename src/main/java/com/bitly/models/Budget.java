package com.bitly.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "budgets",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_budget_month_category", 
            columnNames = {"user_id", "category_id", "month","deleted_at"}
        )
    }
)
@SQLDelete(sql = "UPDATE budgets SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Budget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDate month;

    @Column(nullable = false,precision = 12,scale = 2)
    @NotNull(message="The amount is mandatory")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;

    @Column(name = "alert_threshold")
    @Min(value = 1,message = "Alert Threshold must be at least 1%")
    @Max(value = 100,message = "Alert Threshold cannot exceed 100%")
    @Builder.Default
    private Integer alertThreshold = 80;

    @Column(name = "is_alert_sent")
    @Builder.Default
    private Boolean isAlertSent = false;

    @Column(length = 100)
    private String notes;

    @Transient
    private BigDecimal spentAmount;

    @Transient
    private BigDecimal remainingAmount;


}

