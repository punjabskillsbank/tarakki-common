package com.tarakki.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "org_name", nullable = false, length = 255)
    private String orgName;

    @Column(name = "org_desc", nullable = false, columnDefinition = "TEXT")
    private String orgDesc;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "org_address", nullable = false, columnDefinition = "TEXT")
    private String orgAddress;

    @Column(name = "org_city", nullable = false, length = 100)
    private String orgCity;

    @Column(name = "org_state", nullable = false, length = 100)
    private String orgState;

    @Column(name = "org_postal_code", nullable = false, length = 20)
    private String orgPostalCode;

    @Column(name = "org_country", nullable = false, length = 100)
    private String orgCountry;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
