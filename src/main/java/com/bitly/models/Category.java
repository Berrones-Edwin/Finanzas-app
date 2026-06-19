package com.bitly.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bitly.enums.CategoryType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uq_category_user", columnNames = { "user_id", "name","deleted_at" })
})

@SQLDelete(sql = "UPDATE categories SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private CategoryType categoryType = CategoryType.INCOME;

    @Column(length = 7)
    @Builder.Default
    private String color = "#6B7280";

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();


    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Budget> budgets = new ArrayList<>();

}
