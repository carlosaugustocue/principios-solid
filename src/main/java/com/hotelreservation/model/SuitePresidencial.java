package com.hotelreservation.model;

/**
 * Implementación de una suite presidencial.
 * SRP: Responsabilidad única de gestionar el estado de una suite presidencial.
 * ISP: Implementa solo los métodos necesarios de la interfaz Habitacion.
 */
public class SuitePresidencial implements Habitacion {
    private String numero;
    private boolean disponible;
    private static final double PRECIO_NOCHE = 500.0;

    public SuitePresidencial(String numero) {
        this.numero = numero;
        this.disponible = true;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public TipoHabitacion getTipo() {
        return TipoHabitacion.SUITE_PRESIDENCIAL;
    }

    @Override
    public double getPrecioNoche() {
        return PRECIO_NOCHE;
    }

    @Override
    public boolean estaDisponible() {
        return disponible;
    }

    @Override
    public void marcarOcupada() {
        this.disponible = false;
    }

    @Override
    public void marcarDisponible() {
        this.disponible = true;
    }

    @Override
    public String toString() {
        return String.format("Suite Presidencial #%s (Precio: $%.2f/noche) - %s",
                numero, PRECIO_NOCHE, disponible ? "Disponible" : "Ocupada");
    }
}
