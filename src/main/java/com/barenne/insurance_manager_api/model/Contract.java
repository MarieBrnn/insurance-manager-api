package com.barenne.insurance_manager_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Cost amount cannot be null")
    @Positive(message = "Cost amount must be positive")
    private BigDecimal costAmount;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Transient
    private boolean skipAutoUpdate;

    @PrePersist
    protected void onCreate() {

        if (!skipAutoUpdate) {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();

            if (this.startDate == null) {
                this.startDate = LocalDate.now();
            }
        }
    }

    @PreUpdate
    protected void onUpdate(){
        if (!skipAutoUpdate) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public Boolean isActive(){
        return endDate == null || LocalDate.now().isBefore(endDate);
    }

}
