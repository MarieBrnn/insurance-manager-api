package com.barenne.insurance_manager_api.dto;

import com.barenne.insurance_manager_api.model.Contract;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {
    private Long id;
    private Long clientId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Cost amount cannot be null")
    @Positive(message = "Cost amount must be positive")
    private BigDecimal costAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    //Simple Factory Pattern - Static constructor to simplify creation
    public static ContractDto from(Contract contract) {
        return ContractDto.builder()
                .id(contract.getId())
                .clientId(contract.getClient().getId())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .costAmount(contract.getCostAmount())
                .createdAt(contract.getCreatedAt())
                .build();
    }

}
