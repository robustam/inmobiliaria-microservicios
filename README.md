Plataforma de Arriendo Inmobiliario - Arquitectura de Microservicios

Descripción del proyecto

Este proyecto corresponde a una solución integral de arriendo inmobiliario basada en una arquitectura de microservicios distribuidos. La plataforma está diseñada para cubrir de manera funcional y escalable todas las operaciones del negocio: desde la gestión de usuarios y publicación de propiedades, hasta la firma de contratos, control de pagos, agendamiento de visitas y mantenimiento.

El sistema ha sido desarrollado en Java utilizando Spring Boot e implementa el patrón CSR (Controller-Service-Repository). Toda la persistencia de datos se maneja mediante JPA/Hibernate conectados a bases de datos MySQL independientes para cada microservicio. Además, la arquitectura centraliza su enrutamiento a través de un API Gateway y gestiona el descubrimiento de servicios mediante Eureka Server.

Integrantes
Renato Navarrete Moraga
Roberto Pablo Bustamante Letelier

Funcionalidades implementadas (Microservicios)

La arquitectura se compone de la siguiente infraestructura base y 10 microservicios de negocio independientes:

Infraestructura Core:

eureka-server: Servidor de descubrimiento (Service Discovery) donde todos los microservicios se registran para comunicarse entre sí.

api-gateway: Puerta de entrada única (puerto 8080) que enruta de manera segura todas las peticiones REST hacia los microservicios correspondientes.

Microservicios de Negocio (Mínimo 10 exigidos):

usuario-service: Gestión (CRUD) de perfiles de arrendatarios, propietarios y administradores.

propiedad-service: Catálogo de casas y departamentos, filtrando por características y disponibilidad.

contrato-service: Lógica de acuerdos de alquiler, plazos de meses, fechas de inicio/término y estado de firmas.

pago-service: Gestión de mensualidades, registro de garantías y control de estados de morosidad.

seguro-service: Administración de pólizas exigidas (seguro de incendio, sismo o impagos).

visita-service: Agendamiento y coordinación de citas presenciales para visualizar propiedades.

mantenimiento-service: Gestión de tickets y solicitudes de reparación para las propiedades en arriendo.

inventario-service: Registro detallado del estado de la propiedad y sus muebles al momento de entregar o recibir las llaves.

resena-service: Sistema de calificaciones y comentarios sobre el comportamiento de propietarios y arrendatarios.

notificacion-service: Servicio encargado de emitir alertas (simuladas o reales) sobre pagos pendientes o visitas próximas.

(Nota: Todos los microservicios cuentan con validaciones Bean Validation, manejo centralizado de excepciones con @ControllerAdvice, y respuestas JSON estructuradas).

Pasos para ejecutar
Para levantar este proyecto en un entorno local, se deben seguir los siguientes pasos en estricto orden:

1. Inicializar las Bases de Datos (Docker)
Este proyecto requiere 10 bases de datos independientes. Se utiliza Docker para levantar un contenedor unificado de MySQL.

Abrir la terminal y navegar a la carpeta de infraestructura: cd infraestructura

Ejecutar el comando: docker compose up -d

Verificar que el contenedor esté corriendo en el puerto 3306 y que el script init.sql haya creado los esquemas.

2. Levantar Eureka Server (Service Discovery)
Abrir el proyecto eureka-server en el IDE (IntelliJ IDEA).

Ejecutar la clase principal EurekaServerApplication.java.

Validar que el servidor esté activo ingresando a http://localhost:8761 en el navegador.

3. Levantar API Gateway
Abrir el proyecto api-gateway.

Ejecutar la clase principal ApiGatewayApplication.java.

Validar en la interfaz de Eureka (http://localhost:8761) que el servicio api-gateway aparezca registrado.

4. Levantar los Microservicios de Negocio
Abrir cada microservicio (empezando por propiedad-service, usuario-service, etc.).

Asegurarse de que el archivo application.yml de cada uno esté apuntando correctamente a su respectiva base de datos en localhost:3306.

Ejecutar las clases principales de cada microservicio.

Refrescar la página de Eureka (http://localhost:8761) para confirmar que los 10 microservicios se han registrado exitosamente y están listos para recibir peticiones a través del Gateway.
