package com.barenne.insurance_manager_api.unit.dto;

import com.barenne.insurance_manager_api.dto.TotalCostDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TotalCostDtoTest {
    @Test
    public void testTotalCostDtoConstructor() {
        // Test constructor
        Long clientId = 1L;
        BigDecimal totalCost = new BigDecimal("3000.00");

        TotalCostDto totalCostDto = new TotalCostDto(clientId, totalCost);

        // Assert correct fields
        assertThat(totalCostDto.getClientId()).isEqualTo(clientId);
        assertThat(totalCostDto.getTotalCost()).isEqualByComparingTo(totalCost);
    }

    @Test
    public void testFactoryMethod() {
        // Test the of() factory method
        Long clientId = 1L;
        BigDecimal totalCost = new BigDecimal("3000.00");

        TotalCostDto totalCostDto = TotalCostDto.of(clientId, totalCost);

        // Assert correct mapping
        assertThat(totalCostDto.getClientId()).isEqualTo(clientId);
        assertThat(totalCostDto.getTotalCost()).isEqualByComparingTo(totalCost);
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        TotalCostDto totalCostDto = new TotalCostDto();

        Long clientId = 1L;
        BigDecimal totalCost = new BigDecimal("3000.00");

        totalCostDto.setClientId(clientId);
        totalCostDto.setTotalCost(totalCost);

        // Assert getters return correct values
        assertThat(totalCostDto.getClientId()).isEqualTo(clientId);
        assertThat(totalCostDto.getTotalCost()).isEqualByComparingTo(totalCost);
    }
}
