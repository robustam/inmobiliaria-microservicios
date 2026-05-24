package com.inmobiliaria.propiedadservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: PROPIEDAD (Propiedad Service)
// ============================================================
// Representa la tabla "propiedades" en la base de datos propiedad_db.
// Una propiedad es una CASA o DEPARTAMENTO disponible para arriendo
// en el territorio chileno.
//
// Este microservicio es el NÚCLEO del sistema inmobiliario:
// casi todos los otros servicios hacen referencia a una propiedad.
//
// Tabla en MySQL: propiedades
// ============================================================

import jakarta.persistence.*;            // Anotaciones JPA: @Entity, @Table, @Id, @Column, etc.
import jakarta.validation.constraints.*; // Validaciones: @NotBlank, @NotNull, @Positive, @Min
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.math.BigDecimal;              // Tipo para dinero (más preciso que double/float)
import java.time.LocalDateTime;           // Fecha y hora sin zona horaria

// @Entity: le dice a Hibernate que esta clase es una ENTIDAD mapeada a tabla de BD.
@Entity

// @Table(name = "propiedades"): nombre exacto de la tabla en MySQL.
@Table(name = "propiedades")

// @Data: genera getters, setters, toString(), equals() y hashCode() automáticamente.
@Data

// @Builder: habilita el patrón de diseño Builder para crear objetos de forma fluente.
// Ejemplo: Propiedad.builder().titulo("Casa en Santiago").precio(500000).build()
@Builder

// @NoArgsConstructor: constructor vacío requerido por JPA/Hibernate.
@NoArgsConstructor

// @AllArgsConstructor: constructor con todos los campos, necesario internamente por @Builder.
@AllArgsConstructor
public class Propiedad {

    // ── Enums internos (tipos fijos de datos) ────────────────
    // Se definen dentro de la entidad para no crear archivos separados.
    // "public enum" = accesible desde fuera con Propiedad.TipoPropiedad

    // TipoPropiedad: el sistema inmobiliario SOLO maneja estos dos tipos.
    // No existen oficinas, bodegas, ni terrenos en este sistema.
    public enum TipoPropiedad {
        CASA,          // Casa independiente con terreno
        DEPARTAMENTO   // Unidad en edificio o condominio
    }

    // EstadoPropiedad: ciclo de vida de una propiedad en el sistema.
    // DISPONIBLE → puede ser reservada
    // ARRENDADA  → tiene una reserva activa/confirmada
    // INACTIVA   → el propietario la quitó temporalmente o fue desactivada
    public enum EstadoPropiedad {
        DISPONIBLE, // visible en búsquedas, puede reservarse
        ARRENDADA,  // actualmente ocupada, no se puede reservar
        INACTIVA    // oculta del sistema, no aparece en búsquedas
    }

    // ── Clave primaria ───────────────────────────────────────

    // @Id: columna de clave primaria.
    @Id
    // @GeneratedValue(IDENTITY): MySQL auto-incrementa el valor (1, 2, 3...).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // identificador único de la propiedad

    // ── Información básica ───────────────────────────────────

    // @NotBlank: no puede ser null, vacío ("") ni solo espacios.
    // El mensaje se muestra al cliente cuando falla la validación.
    @NotBlank(message = "El título es obligatorio")
    // @Column(nullable = false): la columna en MySQL no acepta NULL.
    @Column(nullable = false)
    private String titulo; // nombre descriptivo, ej: "Casa amplia con jardín en Ñuñoa"

    // columnDefinition = "TEXT": usa tipo TEXT en MySQL (hasta 65535 caracteres).
    // Ideal para descripciones largas. Sin esto sería VARCHAR(255) por defecto.
    @Column(columnDefinition = "TEXT")
    private String descripcion; // descripción completa de la propiedad (opcional)

    // ── Precio ───────────────────────────────────────────────

    // @NotNull: el campo no puede ser null (diferente a @NotBlank que es para Strings).
    @NotNull(message = "El precio es obligatorio")
    // @Positive: el valor debe ser estrictamente mayor que 0.
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(nullable = false)
    // BigDecimal: tipo preciso para representar dinero.
    // Evita los errores de redondeo de double/float.
    // Ejemplo: 450000.00 (pesos chilenos) o 12.5 (UF)
    private BigDecimal precio;

    // @Builder.Default: valor por defecto cuando se usa el Builder.
    // Si no se especifica la moneda al crear la propiedad, será "CLP".
    @Builder.Default
    private String moneda = "CLP"; // "CLP" = Peso Chileno, "UF" = Unidad de Fomento

    // ── Ubicación (Chile) ────────────────────────────────────

    @NotBlank(message = "La región es obligatoria")
    @Column(nullable = false)
    private String region; // región de Chile, ej: "Región Metropolitana", "Valparaíso"

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(nullable = false)
    private String ciudad; // ciudad, ej: "Santiago", "Viña del Mar", "Concepción"

    @NotBlank(message = "La comuna es obligatoria")
    @Column(nullable = false)
    private String comuna; // comuna, ej: "Ñuñoa", "Providencia", "Las Condes"

    private String direccion; // dirección exacta (opcional por privacidad)

    // ── Características físicas ──────────────────────────────

    // @Min(value = 1): el número de habitaciones debe ser al menos 1.
    @Min(value = 1, message = "Las habitaciones deben ser al menos 1")
    private Integer habitaciones; // número de dormitorios

    @Min(value = 1, message = "Los baños deben ser al menos 1")
    private Integer banos; // número de baños

    @Positive(message = "Los metros cuadrados deben ser positivos")
    private Double metrosCuadrados; // superficie total en m²

    // ── Clasificación ────────────────────────────────────────

    @NotNull(message = "El tipo de propiedad es obligatorio")
    // @Enumerated(EnumType.STRING): guarda el texto "CASA" o "DEPARTAMENTO" en BD.
    // NO guarda el número (0, 1) — más seguro si cambia el orden del enum.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPropiedad tipo; // CASA o DEPARTAMENTO

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPropiedad estado = EstadoPropiedad.DISPONIBLE; // por defecto DISPONIBLE al crear

    // ── Relación con propietario ─────────────────────────────

    @NotNull(message = "El ID del propietario es obligatorio")
    // propietarioId: referencia al usuario dueño de la propiedad.
    // No usamos @ManyToOne/@JoinColumn porque cada servicio tiene su propia BD.
    // La relación entre microservicios se resuelve a nivel de aplicación, no de BD.
    private Long propietarioId;

    // ── Auditoría (fechas automáticas) ───────────────────────

    private LocalDateTime createdAt; // fecha de creación del registro
    private LocalDateTime updatedAt; // fecha de la última actualización

    // @PrePersist: se ejecuta ANTES del primer INSERT.
    // Asigna valores automáticos que no requieren intervención del usuario.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // registra la fecha/hora actual
        updatedAt = LocalDateTime.now(); // igual al crear, se actualizará después
        if (estado == null) estado = EstadoPropiedad.DISPONIBLE; // garantía de estado inicial
        if (moneda == null) moneda = "CLP"; // garantía de moneda por defecto
    }

    // @PreUpdate: se ejecuta ANTES de cada UPDATE en la BD.
    // Actualiza automáticamente updatedAt cada vez que se guarda un cambio.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // registra cuándo fue la última modificación
    }
}