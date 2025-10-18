package com.barenne.insurance_manager_api.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("PERSON")
public class Person extends Client {

    @NotNull(message = "BirthDate cannot be null")
    @Past (message = "BirthDate must be in the past")
    private LocalDate birthDate;

}
