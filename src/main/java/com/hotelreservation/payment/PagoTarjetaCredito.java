package com.hotelreservation.payment;

/**
 * Implementación de pago con tarjeta de crédito.
 * SRP: Responsabilidad única de procesar pagos con tarjeta de crédito.
 * OCP: Puede extenderse o usarse sin modificar otras clases.
 */
public class PagoTarjetaCredito implements MetodoPago {
    private String numeroTarjeta;
    private String nombreTitular;
    private String fechaExpiracion;
    private String cvv;

    public PagoTarjetaCredito(String numeroTarjeta, String nombreTitular,
                              String fechaExpiracion, String cvv) {
        this.numeroTarjeta = numeroTarjeta;
        this.nombreTitular = nombreTitular;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Simular validación de tarjeta
        if (numeroTarjeta == null || numeroTarjeta.length() < 13) {
            return false;
        }
        if (cvv == null || cvv.length() < 3) {
            return false;
        }
        // En una aplicación real, se conectaría a un gateway de pagos
        System.out.println("Procesando pago de $" + monto + " con tarjeta " +
                          numeroTarjeta.substring(numeroTarjeta.length() - 4));
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Tarjeta de Crédito";
    }

    @Override
    public String obtenerDetalles() {
        return String.format("Tarjeta a nombre de %s (Últimos 4 dígitos: %s)",
                nombreTitular, numeroTarjeta.substring(numeroTarjeta.length() - 4));
    }
}
