package com.tarakki.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;
    @Column(name = "org_name", nullable = false)
    private String orgName;
    @Column(name = "org_desc", columnDefinition = "TEXT")
    private String orgDesc;
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;
    @Column(name = "org_address", nullable = false)
    private String orgAddress;
    @Column(name = "org_city", nullable = false)
    private String orgCity;
    @Column(name = "org_state", nullable = false)
    private String orgState;
    @Column(name = "org_postal_code", nullable = false)
    private String orgPostalCode;
    @Column(name = "org_country", nullable = false)
    private String orgCountry;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}