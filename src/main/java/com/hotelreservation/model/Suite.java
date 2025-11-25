package com.hotelreservation.model;

/**
 * Implementación de una suite.
 * SRP: Responsabilidad única de gestionar el estado de una suite.
 * ISP: Implementa solo los métodos necesarios de la interfaz Habitacion.
 */
public class Suite implements Habitacion {
    private String numero;
    private boolean disponible;
    private static final double PRECIO_NOCHE = 200.0;

    public Suite(String numero) {
        this.numero = numero;
        this.disponible = true;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public TipoHabitacion getTipo() {
        return TipoHabitacion.SUITE;
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
        return String.format("Suite #%s (Precio: $%.2f/noche) - %s",
                numero, PRECIO_NOCHE, disponible ? "Disponible" : "Ocupada");
    }
}
