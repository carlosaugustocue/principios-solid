package com.hotelreservation.payment;

/**
 * Interfaz para métodos de pago.
 * DIP: Dependency Inversion Principle.
 * Las clases dependen de esta abstracción, no de implementaciones concretas.
 * OCP: Nuevos métodos de pago pueden agregarse sin modificar código existente.
 */
public interface MetodoPago {
    /**
     * Procesa un pago.
     * @param monto Monto a pagar.
     * @return true si el pago fue exitoso, false en caso contrario.
     */
    boolean procesarPago(double monto);

    /**
     * Obtiene el nombre del método de pago.
     * @return Nombre del método.
     */
    String getNombreMetodo();

    /**
     * Obtiene los detalles de la transacción.
     * @return Detalles de la transacción.
     */
    String obtenerDetalles();
}
