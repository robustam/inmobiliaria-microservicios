# Guía de Implementación — Inmobiliaria Microservicios

## Tabla de contenidos
1. [Resumen de cambios](#1-resumen-de-cambios)
2. [Probar con Swagger](#2-probar-con-swagger)
3. [Probar con Docker](#3-probar-con-docker)
4. [Probar los tests JUnit + Mockito](#4-probar-los-tests-junit--mockito)
5. [Probar el scheduler automático](#5-probar-el-scheduler-automático)

---

## 1. Resumen de cambios

### 1.1 Swagger (springdoc-openapi 2.6.0)

**¿Qué es?** Genera una interfaz web interactiva para probar los endpoints REST sin necesidad de Postman.

**Archivos modificados:**

| Archivo | Cambio |
|---|---|
| `pom.xml` (raíz) | Agregada propiedad `springdoc.version=2.6.0` y dependencia en `<dependencyManagement>` |
| `propiedad-service/pom.xml` | Agregada dependencia `springdoc-openapi-starter-webmvc-ui` |
| `usuario-service/pom.xml` | Ídem |
| `reserva-service/pom.xml` | Ídem |
| `resena-service/pom.xml` | Ídem |
| `reporte-service/pom.xml` | Ídem |
| `notificacion-service/pom.xml` | Ídem |
| `imagen-service/pom.xml` | Ídem |
| `busqueda-service/pom.xml` | Ídem |
| `auth-service/pom.xml` | Agregada dependencia con versión explícita `2.6.0` |

**Archivos creados (uno por servicio):**

| Archivo | Descripción |
|---|---|
| `auth-service/.../config/SwaggerConfig.java` | Info de la API + esquema de seguridad JWT Bearer |
| `usuario-service/.../config/SwaggerConfig.java` | Título, descripción y servidores |
| `propiedad-service/.../config/SwaggerConfig.java` | Ídem |
| `reserva-service/.../config/SwaggerConfig.java` | Ídem |
| `resena-service/.../config/SwaggerConfig.java` | Ídem |
| `reporte-service/.../config/SwaggerConfig.java` | Ídem |
| `notificacion-service/.../config/SwaggerConfig.java` | Ídem |
| `imagen-service/.../config/SwaggerConfig.java` | Ídem |
| `busqueda-service/.../config/SwaggerConfig.java` | Ídem |

**application.properties de cada servicio** — Agregadas las líneas:
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

---

### 1.2 JUnit 5 + Mockito

**¿Qué es?** Tests unitarios que prueban la lógica de negocio de cada servicio aisladamente, sin base de datos ni red.

**Archivos modificados:**

| Archivo | Cambio |
|---|---|
| Cada `pom.xml` de servicio | Agregada dependencia `spring-boot-starter-test` (scope test) |

**Archivos de test creados:**

| Archivo de test | Clase probada | Tests |
|---|---|---|
| `propiedad-service/.../PropiedadServiceTest.java` | `PropiedadService` | 9 tests |
| `auth-service/.../AuthServiceTest.java` | `AuthService` | 7 tests |
| `usuario-service/.../UsuarioServiceTest.java` | `UsuarioService` | 8 tests |
| `reserva-service/.../ReservaServiceTest.java` | `ReservaService` | 7 tests |
| `resena-service/.../ResenaServiceTest.java` | `ResenaService` | 7 tests |
| `notificacion-service/.../NotificacionServiceTest.java` | `NotificacionService` | 7 tests |
| `imagen-service/.../ImagenServiceTest.java` | `ImagenService` | 7 tests |
| `reserva-service/.../ReservaSchedulerTest.java` | `ReservaScheduler` | 6 tests |

---

### 1.3 Docker

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `docker-compose.yml` | Orquesta MySQL + los 11 microservicios |
| `init-db.sql` | Crea las 9 bases de datos al iniciar MySQL |
| `propiedad-service/Dockerfile` | Build multi-etapa Maven → JRE 17 Alpine |
| `usuario-service/Dockerfile` | Ídem |
| `reserva-service/Dockerfile` | Ídem |
| `resena-service/Dockerfile` | Ídem |
| `reporte-service/Dockerfile` | Ídem |
| `notificacion-service/Dockerfile` | Ídem |
| `imagen-service/Dockerfile` | Ídem |
| `busqueda-service/Dockerfile` | Ídem |
| `auth-service/Dockerfile` | Build multi-etapa Maven → JRE **21** Alpine |
| `server_eureka/Dockerfile` | Ídem JRE 21 |
| `api-gateway/Dockerfile` | Build multi-etapa Maven → JRE 17 Alpine |

---

### 1.4 Scheduler automático (Liberación de propiedades)

**¿Qué hace?** Cada día a medianoche busca reservas vencidas (`fechaFin` pasada con estado PENDIENTE o CONFIRMADA) y automáticamente las marca como COMPLETADA y libera la propiedad.

**Archivos modificados:**

| Archivo | Cambio |
|---|---|
| `ReservaServiceApplication.java` | Agregado `@EnableScheduling` |
| `ReservaRepository.java` | Nuevo método `findByEstadoInAndFechaFinBefore()` |
| `application.properties` (reserva) | Nueva propiedad `reserva.scheduler.cron=0 0 0 * * *` |

**Archivos creados:**

| Archivo | Descripción |
|---|---|
| `reserva-service/.../scheduler/ReservaScheduler.java` | Tarea programada con `@Scheduled` |
| `reserva-service/.../scheduler/ReservaSchedulerTest.java` | 6 tests unitarios del scheduler |

---

## 2. Probar con Swagger

### Prerrequisitos
- MySQL corriendo en `localhost:3306` con usuario `root` / contraseña `root`
- Eureka corriendo en `localhost:8761`
- El servicio que quieres probar iniciado desde IntelliJ

### Paso 1 — Iniciar los servicios en orden
Arranca cada uno desde IntelliJ en este orden:
1. `server_eureka` → espera hasta ver `Started ServerEurekaApplication`
2. El servicio que quieres probar (ej: `propiedad-service`)

### Paso 2 — Abrir Swagger UI

Accede a la URL según el servicio:

| Servicio | URL de Swagger UI |
|---|---|
| auth-service | http://localhost:8081/swagger-ui.html |
| usuario-service | http://localhost:8082/swagger-ui.html |
| reserva-service | http://localhost:8083/swagger-ui.html |
| resena-service | http://localhost:8084/swagger-ui.html |
| reporte-service | http://localhost:8085/swagger-ui.html |
| propiedad-service | http://localhost:8086/swagger-ui.html |
| notificacion-service | http://localhost:8087/swagger-ui.html |
| imagen-service | http://localhost:8088/swagger-ui.html |
| busqueda-service | http://localhost:8089/swagger-ui.html |

### Paso 3 — Probar propiedad-service (ejemplo completo)

**3.1 — Verificar que el servicio está vivo**
1. En Swagger, busca el endpoint `GET /api/v1/propiedades/health`
2. Haz clic en `Try it out` → `Execute`
3. Resultado esperado: HTTP 200 con body `"Propiedad Service is UP! ✅"`

**3.2 — Crear una propiedad**
1. Busca `POST /api/v1/propiedades`
2. Haz clic en `Try it out`
3. Pega este JSON en el body:
```json
{
  "titulo": "Casa amplia en Ñuñoa",
  "descripcion": "Casa con jardín y estacionamiento",
  "precio": 550000,
  "moneda": "CLP",
  "region": "Región Metropolitana",
  "ciudad": "Santiago",
  "comuna": "Ñuñoa",
  "direccion": "Av. Irarrázaval 1234",
  "habitaciones": 3,
  "banos": 2,
  "metrosCuadrados": 120.0,
  "tipo": "CASA",
  "propietarioId": 1
}
```
4. Haz clic en `Execute`
5. Resultado esperado: HTTP 201 con la propiedad creada (incluye `id` asignado)

**3.3 — Listar propiedades disponibles**
1. Busca `GET /api/v1/propiedades`
2. `Try it out` → `Execute`
3. Resultado esperado: HTTP 200 con array de propiedades en estado DISPONIBLE

**3.4 — Obtener propiedad por ID**
1. Busca `GET /api/v1/propiedades/{id}`
2. `Try it out` → ingresa el `id` obtenido en el paso 3.2
3. Resultado esperado: HTTP 200 con los datos de esa propiedad

**3.5 — Buscar con filtros**
1. Busca `GET /api/v1/propiedades/buscar`
2. `Try it out` → completa los filtros opcionales:
   - `region`: `Región Metropolitana`
   - `tipo`: `CASA`
   - `precioMax`: `600000`
3. Resultado esperado: HTTP 200 con propiedades que cumplen los filtros

### Paso 4 — Probar auth-service con JWT

**4.1 — Registrar usuario**
1. Abre http://localhost:8081/swagger-ui.html
2. Busca `POST /api/v1/auth/register`
3. Body:
```json
{
  "username": "juan123",
  "email": "juan@mail.cl",
  "password": "miPass123",
  "nombre": "Juan Pérez",
  "role": "USER"
}
```
4. Resultado esperado: HTTP 201 con un `token` JWT

**4.2 — Login**
1. Busca `POST /api/v1/auth/login`
2. Body:
```json
{
  "username": "juan123",
  "password": "miPass123"
}
```
3. Resultado esperado: HTTP 200 con el token JWT

**4.3 — Validar token**
1. Copia el token del paso anterior
2. Busca `GET /api/v1/auth/validate`
3. Parámetro `token`: pega el token
4. Resultado esperado: HTTP 200 con `"valid": true`

---

## 3. Probar con Docker

### Prerrequisitos
- Docker Desktop instalado y corriendo
- Puerto 3306, 8761, 8080–8089 libres

### Paso 1 — Construir y levantar todos los servicios

Abre una terminal en la **raíz del proyecto** y ejecuta:

```bash
docker compose up --build
```

La primera vez tarda varios minutos porque descarga imágenes y compila todos los servicios.

### Paso 2 — Verificar que los contenedores están corriendo

En otra terminal:
```bash
docker compose ps
```

Debes ver todos los contenedores en estado `Up`:
```
inmobiliaria-mysql        Up (healthy)
eureka-server             Up (healthy)
api-gateway               Up
auth-service              Up
usuario-service           Up
propiedad-service         Up
reserva-service           Up
resena-service            Up
reporte-service           Up
notificacion-service      Up
imagen-service            Up
busqueda-service          Up
```

### Paso 3 — Verificar Eureka

Abre en el navegador: http://localhost:8761

Debes ver el panel de Eureka con todos los microservicios registrados en la sección **"Instances currently registered with Eureka"**.

### Paso 4 — Probar a través del API Gateway

Con Docker, todos los servicios están disponibles a través del **puerto 8080** (API Gateway):

```bash
# Health check de propiedad-service a través del Gateway
curl http://localhost:8080/api/v1/propiedades/health

# Listar propiedades
curl http://localhost:8080/api/v1/propiedades

# Crear propiedad
curl -X POST http://localhost:8080/api/v1/propiedades \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Departamento en Providencia",
    "precio": 400000,
    "region": "Región Metropolitana",
    "ciudad": "Santiago",
    "comuna": "Providencia",
    "habitaciones": 2,
    "banos": 1,
    "metrosCuadrados": 65.0,
    "tipo": "DEPARTAMENTO",
    "propietarioId": 1
  }'
```

### Paso 5 — Probar Swagger desde Docker

Con los contenedores corriendo, Swagger también está disponible:
- http://localhost:8086/swagger-ui.html (propiedad-service directo)
- http://localhost:8082/swagger-ui.html (usuario-service directo)

### Paso 6 — Ver logs de un servicio

```bash
# Ver logs en tiempo real del scheduler de reservas
docker compose logs -f reserva-service

# Ver logs de todos los servicios
docker compose logs -f
```

### Paso 7 — Detener los contenedores

```bash
# Detener sin borrar datos
docker compose stop

# Detener y borrar contenedores (conserva el volumen de MySQL)
docker compose down

# Detener, borrar contenedores Y borrar datos de MySQL
docker compose down -v
```

---

## 4. Probar los tests JUnit + Mockito

Los tests son **unitarios puros**: no necesitan MySQL, Eureka ni ningún servidor corriendo.

### Opción A — Desde IntelliJ IDEA

**Correr todos los tests del proyecto:**
1. Clic derecho sobre la carpeta raíz del proyecto en el panel de Project
2. `Run 'All Tests'`

**Correr tests de un servicio:**
1. Clic derecho sobre la carpeta `src/test/java` de ese servicio
2. `Run 'Tests in ...'`

**Correr una clase de test:**
1. Abre el archivo de test (ej: `PropiedadServiceTest.java`)
2. Clic en el ícono ▶ verde junto al nombre de la clase
3. O clic derecho → `Run 'PropiedadServiceTest'`

**Correr un test individual:**
1. Haz clic en el ícono ▶ verde junto al método `@Test`

### Opción B — Desde la terminal con Maven

```bash
# Correr TODOS los tests del proyecto
mvn test

# Correr tests de un servicio específico
mvn test -pl propiedad-service
mvn test -pl reserva-service
mvn test -pl usuario-service

# Correr una clase de test específica
mvn test -pl propiedad-service -Dtest=PropiedadServiceTest

# Correr un método de test específico
mvn test -pl propiedad-service -Dtest=PropiedadServiceTest#findById_retornaPropiedadExistente
```

### Resumen de tests y qué verifican

**PropiedadServiceTest** (9 tests)
| Test | Verifica |
|---|---|
| `findAll_retornaListaCompleta` | Retorna todas las propiedades |
| `findDisponibles_retornaSoloDisponibles` | Filtra por estado DISPONIBLE |
| `findById_retornaPropiedadExistente` | Retorna propiedad cuando existe |
| `findById_lanzaExcepcionCuandoNoExiste` | Lanza 404 si el ID no existe |
| `create_guardaYRetornaPropiedad` | Guarda y retorna la propiedad nueva |
| `update_actualizaCamposYGuarda` | Actualiza los campos correctamente |
| `cambiarEstado_actualizaEstado` | Cambia el estado de la propiedad |
| `cambiarEstado_lanzaExcepcionConEstadoInvalido` | Rechaza estados que no existen en el enum |
| `delete_cambiaEstadoAInactiva` | Borrado lógico: cambia a INACTIVA |

**AuthServiceTest** (7 tests)
| Test | Verifica |
|---|---|
| `login_retornaTokenConCredencialesCorrectas` | Login exitoso genera token JWT |
| `login_lanzaExcepcionCuandoUsuarioNoExiste` | 404 si el usuario no existe |
| `login_lanzaExcepcionConContrasenaIncorrecta` | 400 si la contraseña es incorrecta |
| `register_creaUsuarioYRetornaToken` | Registro exitoso genera token |
| `register_lanzaExcepcionConUsernameDuplicado` | Rechaza username ya registrado |
| `register_lanzaExcepcionConEmailDuplicado` | Rechaza email ya registrado |
| `validateToken_retornaTrueCuandoTokenEsValido` | Valida token correcto |

**ReservaServiceTest** (7 tests)
| Test | Verifica |
|---|---|
| `create_creaReservaCuandoPropiedadDisponible` | Crea reserva y llama Feign para cambiar estado |
| `create_lanzaExcepcionConFechasInvalidas` | Rechaza fechaFin < fechaInicio |
| `create_lanzaExcepcionCuandoPropiedadNoDisponible` | Rechaza propiedad no DISPONIBLE |
| `cambiarEstado_canceladaLiberaPropiedad` | Al CANCELAR llama Feign para liberar propiedad |
| `cambiarEstado_confirmadaNoLiberaPropiedad` | Al CONFIRMAR NO llama Feign |
| `delete_eliminaReservaYLiberaPropiedad` | Elimina y libera la propiedad |

**ReservaSchedulerTest** (6 tests)
| Test | Verifica |
|---|---|
| `procesaReservasVencidas` | Procesa todas las reservas vencidas correctamente |
| `noHaceNadaSinVencidas` | No hace nada si no hay vencidas |
| `cambiaEstadoACompletada` | El estado queda en COMPLETADA |
| `continuaSiUnaFalla` | Si una falla, continúa con las demás |
| `consultaEstadosCorrectos` | Solo consulta PENDIENTE y CONFIRMADA |
| `consultaConFechaHoy` | Usa la fecha actual como referencia |

### Resultado esperado en IntelliJ

Al correr los tests debes ver en la ventana de resultados:
```
Tests run: 51, Failures: 0, Errors: 0, Skipped: 0
✅ BUILD SUCCESS
```

---

## 5. Probar el scheduler automático

El scheduler corre a medianoche. Para probarlo sin esperar:

### Paso 1 — Cambiar el cron a cada minuto

En `reserva-service/src/main/resources/application.properties`:
```properties
# Cambiar temporalmente para pruebas (cada minuto)
reserva.scheduler.cron=0 */1 * * * *
```

### Paso 2 — Crear una reserva con fecha vencida

Con reserva-service corriendo, usa Swagger en http://localhost:8083/swagger-ui.html:

`POST /api/v1/reservas`
```json
{
  "propiedadId": 1,
  "usuarioId": 1,
  "fechaInicio": "2025-01-01",
  "fechaFin": "2025-01-31",
  "comentario": "Prueba scheduler"
}
```

> **Nota:** Las fechas deben ser pasadas para que el scheduler las detecte como vencidas.

### Paso 3 — Observar los logs

En la consola de IntelliJ (o con `docker compose logs -f reserva-service`) verás:

```
=== Scheduler: revisando reservas vencidas al 2026-06-13 ===
Scheduler: se encontraron 1 reserva(s) vencida(s) para procesar.
Reserva #1 completada automáticamente. Propiedad #1 liberada (fechaFin: 2025-01-31).
=== Scheduler finalizado: 1 procesadas, 0 errores. ===
```

### Paso 4 — Verificar el resultado

En Swagger de propiedad-service (http://localhost:8086/swagger-ui.html):

`GET /api/v1/propiedades/1`

El campo `estado` debe haber cambiado de `"ARRENDADA"` a `"DISPONIBLE"` automáticamente.

### Paso 5 — Restaurar el cron a producción

```properties
# Volver a medianoche diaria
reserva.scheduler.cron=0 0 0 * * *
```

---

## Puertos de referencia rápida

| Servicio | Puerto | Swagger UI |
|---|---|---|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | — |
| Auth Service | 8081 | http://localhost:8081/swagger-ui.html |
| Usuario Service | 8082 | http://localhost:8082/swagger-ui.html |
| Reserva Service | 8083 | http://localhost:8083/swagger-ui.html |
| Reseña Service | 8084 | http://localhost:8084/swagger-ui.html |
| Reporte Service | 8085 | http://localhost:8085/swagger-ui.html |
| Propiedad Service | 8086 | http://localhost:8086/swagger-ui.html |
| Notificación Service | 8087 | http://localhost:8087/swagger-ui.html |
| Imagen Service | 8088 | http://localhost:8088/swagger-ui.html |
| Búsqueda Service | 8089 | http://localhost:8089/swagger-ui.html |
| MySQL | 3306 | — |