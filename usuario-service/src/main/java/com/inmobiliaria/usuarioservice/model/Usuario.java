package com.inmobiliaria.usuarioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @Column(unique = true, nullable = false)
    private String rut;

    @Column(nullable = false)
    private  String nombre;

    @Column(nullable = false)
    private  String apellido;

    @Column(unique = true,nullable = false)
    private  String email;

    @Column
    private  String telefono;

    @Column
    private  String fotoUrl;

    @Column(nullable = false)
    private  Boolean activo= true;






}
