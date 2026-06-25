package com.inmobiliaria.reservaservice.scheduler;

import com.inmobiliaria.reservaservice.client.PropiedadClient;
import com.inmobiliaria.reservaservice.model.Reserva;
import com.inmobiliaria.reservaservice.model.Reserva.EstadoReserva;
import com.inmobiliaria.reservaservice.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// ============================================================
// SCHEDULER: LIBERACIÓN AUTOMÁTICA DE PROPIEDADES VENCIDAS
// ============================================================
// Se ejecuta automáticamente todos los días a medianoche.
// Busca reservas cuya fechaFin ya pasó y que siguen en estado
// PENDIENTE o CONFIRMADA (es decir, la propiedad sigue ARRENDADA).
//
// Por cada reserva vencida:
//   1. Cambia el estado de la reserva a COMPLETADA
//   2. Llama a propiedad-service para liberar la propiedad (→ DISPONIBLE)
//
// Esto garantiza que ninguna propiedad quede "atrapada" en
// estado ARRENDADA después de que termine el período de arriendo.
// ============================================================
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaRepository reservaRepository;
    private final PropiedadClient propiedadClient;

    // Estados que indican que la propiedad todavía está ocupada.
    // Una reserva CANCELADA o COMPLETADA ya liberó la propiedad en su momento.
    private static final List<EstadoReserva> ESTADOS_ACTIVOS =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    // ── Tarea programada ─────────────────────────────────────
    // cron = "0 0 0 * * *" → se ejecuta a las 00:00:00 de cada día.
    // Formato: segundos minutos horas día-del-mes mes día-de-semana
    //
    // Se puede cambiar en application.properties con:
    //   reserva.scheduler.cron=0 0 0 * * *
    // Ejemplos:
    //   "0 0 * * * *"     → cada hora
    //   "0 */30 * * * *"  → cada 30 minutos (para pruebas)
    //   "0 0 0 * * *"     → diario a medianoche (producción)
    @Scheduled(cron = "${reserva.scheduler.cron:0 0 0 * * *}")
    public void liberarPropiedadesVencidas() {
        LocalDate hoy = LocalDate.now();
        log.info("=== Scheduler: revisando reservas vencidas al {} ===", hoy);

        // Busca reservas activas cuya fechaFin sea anterior a hoy.
        // fechaFin < hoy significa que el período de arriendo ya terminó.
        List<Reserva> vencidas = reservaRepository.findByEstadoInAndFechaFinBefore(ESTADOS_ACTIVOS, hoy);

        if (vencidas.isEmpty()) {
            log.info("Scheduler: no hay reservas vencidas hoy.");
            return;
        }

        log.info("Scheduler: se encontraron {} reserva(s) vencida(s) para procesar.", vencidas.size());

        int procesadas = 0;
        int errores = 0;

        for (Reserva reserva : vencidas) {
            try {
                Long propiedadId = reserva.getPropiedadId();

                // 1. Marcar la reserva como COMPLETADA en la BD local.
                reserva.setEstado(EstadoReserva.COMPLETADA);
                reservaRepository.save(reserva);

                // 2. Liberar la propiedad en propiedad-service (→ DISPONIBLE).
                // Si falla esta llamada Feign, la reserva ya quedó COMPLETADA en la BD,
                // pero la propiedad podría quedar en ARRENDADA. El log de error lo indica.
                propiedadClient.cambiarEstado(propiedadId, "DISPONIBLE");

                log.info("Reserva #{} completada automáticamente. Propiedad #{} liberada (fechaFin: {}).",
                        reserva.getId(), propiedadId, reserva.getFechaFin());
                procesadas++;

            } catch (Exception e) {
                // Si falla una reserva, continúa con las demás (no para todo el proceso).
                log.error("Error al procesar reserva #{}: {}", reserva.getId(), e.getMessage());
                errores++;
            }
        }

        log.info("=== Scheduler finalizado: {} procesadas, {} errores. ===", procesadas, errores);
    }
}