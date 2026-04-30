package com.inmobiliaria.authservice.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "el mail no tiene formato valido ")
    @NotBlank(message = "el mail es obligatorio")
    private  String email;

    @NotBlank(message = "el password es obligatorio")
    private String password;


}
