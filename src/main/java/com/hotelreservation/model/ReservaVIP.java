package com.hotelreservation.model;

import com.hotelreservation.payment.MetodoPago;

import java.time.LocalDate;
import java.util.List;

/**
 * Subclase de Reserva para clientes VIP.
 * LSP: Puede reemplazar a Reserva sin que el sistema falle.
 * Agrega beneficios adicionales sin cambiar el comportamiento esperado.
 * SRP: Responsabilidad única de manejar reservas VIP con beneficios especiales.
 */
public class ReservaVIP extends Reserva {
    private static final double DESCUENTO_VIP = 0.15; // 15% de descuento
    private boolean desayunoIncluido;
    private boolean servicioHabitacion24h;
    private boolean accesoBienvenida;

    public ReservaVIP(Cliente cliente, List<Habitacion> habitaciones,
                     LocalDate fechaCheckIn, LocalDate fechaCheckOut,
                     MetodoPago metodoPago) {
        super(cliente, habitaciones, fechaCheckIn, fechaCheckOut, metodoPago);
        this.desayunoIncluido = true;
        this.servicioHabitacion24h = true;
        this.accesoBienvenida = true;
        calcularMontoTotal(); // Recalcular con descuento VIP
    }

    @Override
    protected void calcularMontoTotal() {
        long noches = java.time.temporal.ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        double subtotal = habitaciones.stream()
                .mapToDouble(Habitacion::getPrecioNoche)
                .sum() * noches;
        this.montoTotal = subtotal * (1 - DESCUENTO_VIP);
    }

    @Override
    public void confirmar() {
        super.confirmar();
        System.out.println("Beneficios VIP activados: Desayuno incluido, Servicio 24h, Acceso a áreas VIP");
    }

    public boolean tieneDesayunoIncluido() {
        return desayunoIncluido;
    }

    public boolean tieneServicioHabitacion24h() {
        return servicioHabitacion24h;
    }

    public boolean tieneAccesoBienvenida() {
        return accesoBienvenida;
    }

    @Override
    public String toString() {
        return String.format("%s [VIP - Descuento: %.0f%%]",
                super.toString(), DESCUENTO_VIP * 100);
    }
}
