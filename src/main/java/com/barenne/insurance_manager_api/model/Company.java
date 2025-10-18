package com.barenne.insurance_manager_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("COMPANY")
public class Company extends Client {

    @NotBlank(message = "Company identifier cannot be null")
    @Pattern(regexp= "^[a-z]{3}-[0-9]{3}$", message="Invalid Company Identifier (example: aaa-123)")
    @Column(unique = true)
    private String companyIdentifier;

}
