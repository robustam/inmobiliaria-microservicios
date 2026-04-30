package com.inmobiliaria.authservice.repository;

import com.inmobiliaria.authservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuario,Long> {


        Optional<Usuario> findByEmail(String email);

        Boolean existsByEmail(String email);
        Boolean existsByRut(String rut);

}
