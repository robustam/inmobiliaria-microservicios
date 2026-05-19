package com.inmobiliaria.authservice.dto.request;
import com.inmobiliaria.authservice.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class RegisterRequest {
    @NotBlank(message = "el rut es obligatorio")
    @Pattern(
            regexp = "^[0-9]{7,8}-[0-9Kk]$",
            message = "el formato de rut invalido : ejemplo 12345678-9"
    )
    private String rut;

    @Email(message = "el mail no tiene formato valido")
    @NotBlank(message = "el mail es obligatorio")
    private  String email;

    @NotBlank(message = "el password es obligatorio")
    private String password;

    // Dentro de RegisterRequest.java
    @NotBlank(message = "El rol es obligatorio")
    private String rol; // Ahora es String. Jackson y el Gateway lo leerán sin colapsar.


}
