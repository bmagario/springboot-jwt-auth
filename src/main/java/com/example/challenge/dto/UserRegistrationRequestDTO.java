package com.example.challenge.dto;

import com.example.challenge.validators.ValidPassword;
import java.util.Collections;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    @Valid
    private List<PhoneDTO> phones;
    public List<PhoneDTO> getPhones() {
        return phones != null ? phones : Collections.emptyList();
    }
}
