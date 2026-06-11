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
@Table(name = "transfers")
@SQLDelete(sql = "UPDATE transfers SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")

public class Transfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id",nullable = false)
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id",nullable = false)
    private Account toAccount;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull(message = "The amount is mandatory")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

}
