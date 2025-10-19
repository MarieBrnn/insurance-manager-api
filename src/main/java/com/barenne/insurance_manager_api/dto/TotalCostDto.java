package com.barenne.insurance_manager_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalCostDto {
    private Long clientId;
    private BigDecimal totalCost;

    public static TotalCostDto of(Long clientId, BigDecimal totalCost) {
        return new TotalCostDto(clientId, totalCost);
    }
}
