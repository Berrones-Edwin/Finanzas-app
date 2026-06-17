package com.bitly.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bitly.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "accounts")

@SQLDelete(sql = "UPDATE accounts SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")

public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private AccountType accountType = AccountType.BANK;

    @Column(length = 3)
    @Builder.Default
    private String currency = "MXN";

    @Column(length = 7)
    @Builder.Default
    private String color = "#6B7280";

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "account")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

     @OneToMany(mappedBy="fromAccount")
    @Builder.Default
    private List<Transfer> outgoingTransfers = new ArrayList<>();


    @OneToMany(mappedBy="toAccount")
    @Builder.Default
    private List<Transfer> incomingTransfers = new ArrayList<>();

}
