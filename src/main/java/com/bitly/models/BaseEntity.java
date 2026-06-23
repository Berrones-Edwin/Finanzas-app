package com.bitly.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {


    @CreatedDate
    @Column(name = "created_at",nullable = false,updatable=false)
    private LocalDateTime createdAt;
    

    @Column(name="updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public boolean isDeleted(){
        return this.deletedAt !=null;
    }
}
