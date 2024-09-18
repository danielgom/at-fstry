package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank(message = "Name is required")
    @Schema(example = "John")
    private String name;

    @JsonProperty("last_name")
    @NotBlank(message = "last_name is required")
    @Schema(example = "Jones")
    private String lastName;

    @NotBlank(message = "email is required")
    @Email
    @Schema(example = "example@hotmail.com")
    private String email;

    @NotBlank(message = "Password must be at least 8 characters long, contain one uppercase letter, one" +
            "lower case letter, one number and one special character")
    @Schema(example = "Superpassword1234@")
    private String password;
}
