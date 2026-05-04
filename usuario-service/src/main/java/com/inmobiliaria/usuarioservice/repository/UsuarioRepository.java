package com.inmobiliaria.usuarioservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inmobiliaria.usuarioservice.model.Usuario;
import java.util.Optional;


public interface UsuarioRepository extends  JpaRepository<Usuario,long>{

     //busca usuario por rut
    Optional<Usuario>  findByEmail(String email);

    // Busca usuario por rut
    Optional<Usuario> findByRut(String rut);

    // Verifica si existe un email
    Boolean existsByEmail(String email);

    // Verifica si existe un rut
    Boolean existsByRut(String rut);


}
