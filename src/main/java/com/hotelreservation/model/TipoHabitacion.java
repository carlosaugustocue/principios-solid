package com.hotelreservation.model;

/**
 * Enumeración de tipos de habitación.
 * SRP: Responsabilidad única de definir los tipos disponibles.
 */
public enum TipoHabitacion {
    ESTANDAR("Habitación Estándar"),
    DOBLE("Habitación Doble"),
    SUITE("Suite"),
    SUITE_PRESIDENCIAL("Suite Presidencial");

    private final String descripcion;

    TipoHabitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
