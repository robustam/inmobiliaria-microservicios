package com.inmobiliaria.usuarioservice.controller;

import com.inmobiliaria.usuarioservice.dto.request.UsuarioRequest;
import com.inmobiliaria.usuarioservice.dto.rresponse.UsuarioResponse;
import com.inmobiliaria.usuarioservice.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRestClientHttpServiceGroupConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
// logger registra la info que llega y peticon y cada respuesta que sale del controller
    public static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

// este service contiene la logica del controller
    //el controller no ase nada de logica ,solo delega al service
    private final UsuarioService usuarioService;
    private final LoadBalancerRestClientHttpServiceGroupConfigurer loadBalancerRestClientHttpServiceGroupConfigurer;
// post con esta ruta /api/v1/usuarios
    //crea nuevo  usuario en el sistema
    //y recibe{ rut, nombre, apellido, email, telefono, fotoUrl }
    // retorna el usuario con ID generado
    @PostMapping
    // la anotacion valid activa las validaciones de usuarioRequest
    // si falla lansa el methodargumentnotvalidexception
    //que lo captura el globalexception
    //requestbody convierte el json en objeto java
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request){
        log.info("request crear usuario-> emai{}",
        request.getEmail());
       // delega la logica al service
        UsuarioResponse response = usuarioService.crearUsuario(request);

        log.info("RESPONSE crea usuario == status : 201 create");
        //201 created el el codigo correcto cuando se crea nuevo usuario exitoso
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    //  GET /api/v1/usuarios/{id}
    //  obtiene un usuario por su ID
    //   recibe: id en la URL ejemplo: /api/v1/usuarios/1
    //   retorna: datos del usuario encontrado
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(
            //@PathVariable extra el id de la url
            // ejemplo /api/v1/usuarios/5 id= 5
            @PathVariable Long id
    ){
        log.info("REQUEST obtener el usuario == id: {}",id);

        UsuarioResponse response = usuarioService.obtenerUsuario(id);

        log.info("RESPONSE obtener usuario == status :200 ok");

        //200 ok codigo correcto para la consulta exitosa
        return ResponseEntity.ok(response);

    }
    // GET /api/v1/usuarios
    // lista todos los usuarios del sistema
    // retorna lista de todos los usuarios
    @GetMapping
    public  ResponseEntity<List<UsuarioResponse>> listarUsuarios(){

        log.info("REQUEST listar todos los usuarios ");

        List<UsuarioResponse> response = usuarioService.listarUsuarios();
        log.info("RESPONSE listar usuarios == total{}",response.size());
        return ResponseEntity.ok(response);

    }



    // PUT /api/v1/usuarios/{id}
    // Actualiza los datos de un usuario existente
    // Recibe: id en la URL y nuevos datos en el body
    // Retorna: usuario con los datos actualizados
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            // @PathVariable extrae el id de la URL
            // @Valid valida los datos del body
            // @RequestBody convierte el JSON a objeto Java
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request){

        log.info("RESPONSE actualizar usuario == status :200 ok", id);
        UsuarioResponse response = usuarioService.actualizarUsuario(id, request);

        log.info("RESPONSE actualizar usuario == status : 200 ok ");

        return ResponseEntity.ok(response);




    }

    // DELETE /api/v1/usuarios/{id}
    // Elimina un usuario del sistema
    // Recibe: id del usuario en la URL
    // Retorna: mensaje de confirmacion
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable Long id) {

        log.info("REQUEST eliminar usuario → id: {}", id);

        usuarioService.eliminarUsuario(id);

        log.info("RESPONSE eliminar usuario → status: 200 OK");

        // Retornamos mensaje de confirmacion
        return ResponseEntity.ok(
                "Usuario con id " + id + " eliminado correctamente");
    }
}
