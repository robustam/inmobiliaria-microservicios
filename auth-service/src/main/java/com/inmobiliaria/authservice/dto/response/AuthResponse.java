package com.inmobiliaria.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.ref.SoftReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private  String token;

    private String rut;
    private  String email;
    private  String rol;

    private String mensaje;
}
