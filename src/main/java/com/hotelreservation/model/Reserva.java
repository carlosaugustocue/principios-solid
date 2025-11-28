package com.hotelreservation.model;

import com.hotelreservation.payment.MetodoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase base para reservas de hotel.
 * SRP: Responsabilidad única de representar una reserva.
 * LSP: Puede ser reemplazada por subclases sin que el sistema falle.
 * DIP: Depende de la abstracción MetodoPago, no de implementaciones concretas.
 */
public class Reserva {
    private static final Logger logger = LoggerFactory.getLogger(Reserva.class);
    protected String idReserva;
    protected Cliente cliente;
    protected List<Habitacion> habitaciones;
    protected LocalDate fechaCheckIn;
    protected LocalDate fechaCheckOut;
    protected MetodoPago metodoPago;
    protected EstadoReserva estado;
    protected double montoTotal;

    public Reserva(Cliente cliente, List<Habitacion> habitaciones,
                   LocalDate fechaCheckIn, LocalDate fechaCheckOut,
                   MetodoPago metodoPago) {
        this.idReserva = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.habitaciones = new ArrayList<>(habitaciones);
        this.fechaCheckIn = fechaCheckIn;
        this.fechaCheckOut = fechaCheckOut;
        this.metodoPago = metodoPago;
        this.estado = EstadoReserva.PENDIENTE;
        calcularMontoTotal();
    }

    /**
     * Calcula el monto total de la reserva.
     */
    protected void calcularMontoTotal() {
        long noches = java.time.temporal.ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        this.montoTotal = habitaciones.stream()
                .mapToDouble(Habitacion::getPrecioNoche)
                .sum() * noches;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Habitacion> getHabitaciones() {
        return new ArrayList<>(habitaciones);
    }

    public LocalDate getFechaCheckIn() {
        return fechaCheckIn;
    }

    public LocalDate getFechaCheckOut() {
        return fechaCheckOut;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    /**
     * Cambia la fecha de la reserva.
     */
    public void cambiarFechas(LocalDate nuevaFechaCheckIn, LocalDate nuevaFechaCheckOut) {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("No se puede cambiar una reserva cancelada");
        }
        if (nuevaFechaCheckIn.isAfter(nuevaFechaCheckOut)) {
            throw new IllegalArgumentException("La fecha de check-in debe ser anterior a check-out");
        }
        this.fechaCheckIn = nuevaFechaCheckIn;
        this.fechaCheckOut = nuevaFechaCheckOut;
        calcularMontoTotal();
        logger.info("Fechas de la reserva " + idReserva + " actualizadas exitosamente");
    }

    /**
     * Confirma la reserva y procesa el pago.
     */
    public void confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("La reserva ya ha sido confirmada o cancelada");
        }

        // Marcar habitaciones como ocupadas
        for (Habitacion habitacion : habitaciones) {
            if (!habitacion.estaDisponible()) {
                throw new IllegalStateException("Una o más habitaciones no están disponibles");
            }
            habitacion.marcarOcupada();
        }

        // Procesar pago
        if (metodoPago.procesarPago(montoTotal)) {
            this.estado = EstadoReserva.CONFIRMADA;
            logger.info("Reserva " + idReserva + " confirmada exitosamente");
        } else {
            // Liberar habitaciones si el pago falla
            for (Habitacion habitacion : habitaciones) {
                habitacion.marcarDisponible();
            }
            throw new RuntimeException("Falló el procesamiento del pago");
        }
    }

    /**
     * Cancela la reserva.
     */
    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }

        // Liberar habitaciones
        for (Habitacion habitacion : habitaciones) {
            habitacion.marcarDisponible();
        }

        this.estado = EstadoReserva.CANCELADA;
        logger.info("Reserva " + idReserva + " cancelada exitosamente");
    }

    @Override
    public String toString() {
        return String.format("Reserva ID: %s | Cliente: %s | Habitaciones: %d | " +
                "Check-in: %s | Check-out: %s | Estado: %s | Total: $%.2f",
                idReserva, cliente.getNombre(), habitaciones.size(),
                fechaCheckIn, fechaCheckOut, estado, montoTotal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return idReserva.equals(reserva.idReserva);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva);
    }
}
