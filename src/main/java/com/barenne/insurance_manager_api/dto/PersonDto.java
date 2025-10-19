package com.barenne.insurance_manager_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonDto extends ClientDto {

    @NotNull(message = "BirthDate cannot be null")
    @Past (message = "BirthDate must be in the past")
    private LocalDate birthDate;

    //Simple Factory Pattern - Static constructor to simplify creation
    public static PersonDto from(Long id, String name, String phone, String email, LocalDate birthdate) {
       return PersonDto.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .email(email)
                .birthDate(birthdate)
                .clientType("PERSON")
                .build();
    }
}
