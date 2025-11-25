package com.hotelreservation.model;

/**
 * Enumeración de estados de una reserva.
 * SRP: Responsabilidad única de definir los estados disponibles.
 */
public enum EstadoReserva {
    PENDIENTE("Pendiente de confirmación"),
    CONFIRMADA("Confirmada y pagada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoReserva(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
