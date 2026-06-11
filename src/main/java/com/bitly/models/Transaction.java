
package com.bitly.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bitly.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "transactions",
    indexes = {
        @Index(name="idx_user_date",columnList = "user_id,date"),
        @Index(name="idx_user_type",columnList = "user_id,type")
    }
)
@SQLDelete(sql = "UPDATE transactions SET deleted_at =NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Transaction extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",nullable = false)
    @Builder.Default
    private TransactionType transactionType = TransactionType.INCOME;

    @Column(nullable=false,precision = 12,scale = 2)
    @NotNull(message = "The amount is mandatory")
    @Positive(message = "The amount must be greater than zero")
    private BigDecimal amount;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy="fromAccount")
    @Builder.Default
    private List<Transfer> outgoingTransfers = new ArrayList<>();


    @OneToMany(mappedBy="toAccount")
    @Builder.Default
    private List<Transfer> incomingTransfers = new ArrayList<>();
}
