package com.hotelreservation.model;

/**
 * Interfaz base para todas las habitaciones.
 * Responsabilidad única: Definir el contrato básico de una habitación.
 * ISP: Interfaces separadas para diferentes tipos de habitaciones.
 */
public interface Habitacion {
    String getNumero();

    TipoHabitacion getTipo();

    double getPrecioNoche();

    boolean estaDisponible();

    void marcarOcupada();

    void marcarDisponible();
}
