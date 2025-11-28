package com.hotelreservation.service;

import com.hotelreservation.model.Habitacion;
import com.hotelreservation.model.Reserva;
import com.hotelreservation.model.Cliente;
import com.hotelreservation.payment.MetodoPago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestor de Reservas.
 * SRP: Responsabilidad única de gestionar todas las operaciones de reservas.
 * Actúa como un controlador central para crear, modificar y cancelar reservas.
 */
public class GestorReservas {
    private static final Logger logger = LoggerFactory.getLogger(GestorReservas.class);
    private List<Reserva> reservas;
    private List<Habitacion> habitacionesDisponibles;

    public GestorReservas() {
        this.reservas = new ArrayList<>();
        this.habitacionesDisponibles = new ArrayList<>();
    }

    /**
     * Registra una habitación en el sistema.
     */
    public void registrarHabitacion(Habitacion habitacion) {
        habitacionesDisponibles.add(habitacion);
        logger.info("Habitación registrada: " + habitacion);
    }

    /**
     * Obtiene todas las habitaciones disponibles para las fechas especificadas.
     */
    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate checkIn, LocalDate checkOut) {
        return habitacionesDisponibles.stream()
                .filter(Habitacion::estaDisponible)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una habitación específica por número.
     */
    public Optional<Habitacion> obtenerHabitacionPorNumero(String numero) {
        return habitacionesDisponibles.stream()
                .filter(h -> h.getNumero().equals(numero))
                .findFirst();
    }

    /**
     * Crea una nueva reserva.
     */
    public Reserva crearReserva(Cliente cliente, List<Habitacion> habitaciones,
                               LocalDate checkIn, LocalDate checkOut,
                               MetodoPago metodoPago) {
        // Validar fechas
        if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
            throw new IllegalArgumentException("Las fechas de check-in y check-out son inválidas");
        }

        // Validar que las habitaciones estén disponibles
        for (Habitacion habitacion : habitaciones) {
            if (!habitacion.estaDisponible()) {
                throw new IllegalStateException("La habitación " + habitacion.getNumero() +
                        " no está disponible para las fechas seleccionadas");
            }
        }

        Reserva reserva = new Reserva(cliente, habitaciones, checkIn, checkOut, metodoPago);
        reservas.add(reserva);
        logger.info("Reserva creada: " + reserva.getIdReserva());
        return reserva;
    }

    /**
     * Crea una reserva VIP.
     */
    public Reserva crearReservaVIP(Cliente cliente, List<Habitacion> habitaciones,
                                  LocalDate checkIn, LocalDate checkOut,
                                  MetodoPago metodoPago) {
        if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
            throw new IllegalArgumentException("Las fechas de check-in y check-out son inválidas");
        }

        for (Habitacion habitacion : habitaciones) {
            if (!habitacion.estaDisponible()) {
                throw new IllegalStateException("La habitación " + habitacion.getNumero() +
                        " no está disponible para las fechas seleccionadas");
            }
        }

        Reserva reservaVIP = new com.hotelreservation.model.ReservaVIP(cliente, habitaciones,
                checkIn, checkOut, metodoPago);
        reservas.add(reservaVIP);
        logger.info("Reserva VIP creada: " + reservaVIP.getIdReserva());
        return reservaVIP;
    }

    /**
     * Confirma una reserva existente.
     */
    public void confirmarReserva(String idReserva) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(idReserva);
        if (reservaOpt.isPresent()) {
            reservaOpt.get().confirmar();
        } else {
            throw new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva);
        }
    }

    /**
     * Cambia las fechas de una reserva existente.
     */
    public void cambiarFechasReserva(String idReserva, LocalDate nuevaFechaCheckIn,
                                     LocalDate nuevaFechaCheckOut) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(idReserva);
        if (reservaOpt.isPresent()) {
            reservaOpt.get().cambiarFechas(nuevaFechaCheckIn, nuevaFechaCheckOut);
        } else {
            throw new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva);
        }
    }

    /**
     * Cancela una reserva existente.
     */
    public void cancelarReserva(String idReserva) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(idReserva);
        if (reservaOpt.isPresent()) {
            reservaOpt.get().cancelar();
        } else {
            throw new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva);
        }
    }

    /**
     * Obtiene una reserva por su ID.
     */
    public Optional<Reserva> obtenerReservaPorId(String idReserva) {
        return reservas.stream()
                .filter(r -> r.getIdReserva().equals(idReserva))
                .findFirst();
    }

    /**
     * Obtiene todas las reservas de un cliente específico.
     */
    public List<Reserva> obtenerReservasCliente(Cliente cliente) {
        return reservas.stream()
                .filter(r -> r.getCliente().equals(cliente))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las reservas confirmadas.
     */
    public List<Reserva> obtenerReservasConfirmadas() {
        return reservas.stream()
                .filter(r -> r.getEstado() == com.hotelreservation.model.EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las reservas.
     */
    public List<Reserva> obtenerTodasLasReservas() {
        return new ArrayList<>(reservas);
    }

    /**
     * Obtiene el total de ingresos de las reservas confirmadas.
     */
    public double obtenerIngresosTotales() {
        return reservas.stream()
                .filter(r -> r.getEstado() == com.hotelreservation.model.EstadoReserva.CONFIRMADA)
                .mapToDouble(Reserva::getMontoTotal)
                .sum();
    }
}
