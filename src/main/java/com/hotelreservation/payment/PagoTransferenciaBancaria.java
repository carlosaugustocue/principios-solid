package com.hotelreservation.payment;

/**
 * Implementación de pago por transferencia bancaria.
 * SRP: Responsabilidad única de procesar pagos por transferencia.
 * OCP: Puede agregarse sin modificar código existente.
 */
public class PagoTransferenciaBancaria implements MetodoPago {
    private String numeroCuenta;
    private String nombreBanco;
    private String codigoBanco;

    public PagoTransferenciaBancaria(String numeroCuenta, String nombreBanco, String codigoBanco) {
        this.numeroCuenta = numeroCuenta;
        this.nombreBanco = nombreBanco;
        this.codigoBanco = codigoBanco;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Simular validación de datos bancarios
        if (numeroCuenta == null || numeroCuenta.length() < 8) {
            return false;
        }
        if (codigoBanco == null || codigoBanco.length() < 4) {
            return false;
        }
        // En una aplicación real, se conectaría al sistema bancario
        System.out.println("Procesando transferencia de $" + monto + " al banco " + nombreBanco);
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "Transferencia Bancaria";
    }

    @Override
    public String obtenerDetalles() {
        return String.format("Transferencia al banco %s (Código: %s, Cuenta: %s)",
                nombreBanco, codigoBanco, numeroCuenta.substring(numeroCuenta.length() - 4));
    }
}
