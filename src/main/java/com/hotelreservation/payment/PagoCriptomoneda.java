package com.hotelreservation.payment;

/**
 * Implementación de pago con criptomoneda.
 * SRP: Responsabilidad única de procesar pagos con criptomoneda.
 * OCP: Nuevo método de pago agregado sin modificar código existente.
 * Demuestra la extensibilidad del sistema a nuevos métodos de pago.
 */
public class PagoCriptomoneda implements MetodoPago {
    private String tipoMoneda; // Bitcoin, Ethereum, etc.
    private String billetera;

    public PagoCriptomoneda(String tipoMoneda, String billetera) {
        this.tipoMoneda = tipoMoneda;
        this.billetera = billetera;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Simular validación de billetera
        if (billetera == null || billetera.length() < 20) {
            return false;
        }
        // En una aplicación real, se conectaría a la blockchain
        System.out.println("Procesando pago de $" + monto + " en " + tipoMoneda +
                          " desde billetera " + billetera.substring(0, 10) + "...");
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Criptomoneda (" + tipoMoneda + ")";
    }

    @Override
    public String obtenerDetalles() {
        return String.format("Billetera %s en %s (Primeros 10 caracteres: %s)",
                tipoMoneda, tipoMoneda, billetera.substring(0, 10) + "...");
    }
}
