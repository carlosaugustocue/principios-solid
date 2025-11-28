package com.hotelreservation.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de pago con tarjeta de débito.
 * SRP: Responsabilidad única de procesar pagos con tarjeta de débito.
 * OCP: Puede extenderse o usarse sin modificar otras clases.
 */
public class PagoTarjetaDebito implements MetodoPago {
    private static final Logger logger = LoggerFactory.getLogger(PagoTarjetaDebito.class);
    private String numeroTarjeta;
    private String nombreTitular;
    private String PIN;

    public PagoTarjetaDebito(String numeroTarjeta, String nombreTitular, String PIN) {
        this.numeroTarjeta = numeroTarjeta;
        this.nombreTitular = nombreTitular;
        this.PIN = PIN;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Simular validación de tarjeta de débito
        if (numeroTarjeta == null || numeroTarjeta.length() < 13) {
            return false;
        }
        if (PIN == null || PIN.length() < 4) {
            return false;
        }
        // En una aplicación real, se conectaría a un banco
        logger.info("Procesando pago de $" + monto + " con tarjeta de débito " +
                          numeroTarjeta.substring(numeroTarjeta.length() - 4));
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Tarjeta de Débito";
    }

    @Override
    public String obtenerDetalles() {
        return String.format("Tarjeta de débito a nombre de %s (Últimos 4 dígitos: %s)",
                nombreTitular, numeroTarjeta.substring(numeroTarjeta.length() - 4));
    }
}
