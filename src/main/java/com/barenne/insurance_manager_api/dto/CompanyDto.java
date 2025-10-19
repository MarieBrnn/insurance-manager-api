package com.barenne.insurance_manager_api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyDto extends ClientDto {

    @NotBlank(message = "Company identifier cannot be null")
    @Pattern(regexp= "^[a-z]{3}-[0-9]{3}$", message="Invalid Company Identifier (example: aaa-123)")
    private String companyIdentifier;

    //Simple Factory Pattern - Static constructor to simplify creation
    public static CompanyDto from(Long id, String name, String phone, String email, String companyIdentifier) {
      return CompanyDto.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .email(email)
                .companyIdentifier(companyIdentifier)
                .clientType("COMPANY")
                .build();

    }

}
