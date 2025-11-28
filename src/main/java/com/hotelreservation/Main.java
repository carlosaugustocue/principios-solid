package com.hotelreservation;

import com.hotelreservation.model.*;
import com.hotelreservation.payment.*;
import com.hotelreservation.service.GestorReservas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Clase principal que demuestra el uso del sistema de reservas con los principios SOLID.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=".repeat(80));
        logger.info("SISTEMA DE RESERVAS DE HOTEL - Demostración de Principios SOLID");
        logger.info("=".repeat(80));

        // Crear el gestor de reservas
        GestorReservas gestor = new GestorReservas();

        // === DEMOSTRACIÓN: Registrar habitaciones ===
        logger.info("\n1. REGISTRANDO HABITACIONES (SRP):");
        logger.info("-".repeat(80));
        registrarHabitaciones(gestor);

        // === DEMOSTRACIÓN: Crear clientes ===
        logger.info("\n2. CREANDO CLIENTES (SRP):");
        logger.info("-".repeat(80));
        Cliente cliente1 = new Cliente("Juan Pérez", "juan@email.com", "1234567890", "12345678");
        Cliente cliente2 = new Cliente("María García", "maria@email.com", "0987654321", "87654321");
        Cliente cliente3 = new Cliente("Carlos López", "carlos@email.com", "5555555555", "55555555");

        logger.info(cliente1.toString());
        logger.info(cliente2.toString());
        logger.info(cliente3.toString());

        // === DEMOSTRACIÓN: Crear reservas con diferentes métodos de pago (DIP, OCP) ===
        logger.info("\n3. CREANDO RESERVAS CON DIFERENTES MÉTODOS DE PAGO (DIP, OCP):");
        logger.info("-".repeat(80));

        // Reserva 1: Con tarjeta de crédito
        logger.info("\n3.1 Reserva con Tarjeta de Crédito:");
        MetodoPago pagoCreditoCliente1 = new PagoTarjetaCredito(
                "4111111111111111", "Juan Pérez", "12/25", "123"
        );
        Habitacion hab101 = gestor.obtenerHabitacionPorNumero("101").get();
        Habitacion hab102 = gestor.obtenerHabitacionPorNumero("102").get();

        Reserva reserva1 = gestor.crearReserva(
                cliente1,
                Arrays.asList(hab101, hab102),
                LocalDate.of(2025, 12, 15),
                LocalDate.of(2025, 12, 20),
                pagoCreditoCliente1
        );
        logger.info(reserva1.toString());

        // Reserva 2: Con tarjeta de débito
        logger.info("\n3.2 Reserva con Tarjeta de Débito:");
        MetodoPago pagoDebitoCliente2 = new PagoTarjetaDebito(
                "5555555555555555", "María García", "1234"
        );
        Habitacion hab201 = gestor.obtenerHabitacionPorNumero("201").get();

        Reserva reserva2 = gestor.crearReserva(
                cliente2,
                Arrays.asList(hab201),
                LocalDate.of(2025, 12, 18),
                LocalDate.of(2025, 12, 22),
                pagoDebitoCliente2
        );
        logger.info(reserva2.toString());

        // === DEMOSTRACIÓN: Nuevo método de pago (Criptomoneda) - OCP ===
        logger.info("\n3.3 Reserva con Criptomoneda (OCP - Extensión sin modificar código):");
        MetodoPago pagoCriptoCliente3 = new PagoCriptomoneda(
                "Bitcoin",
                "1A1z7agoat2TP3z4JwHbqjK8Fs5P5xH3Z1"
        );
        Habitacion hab301 = gestor.obtenerHabitacionPorNumero("301").get();

        Reserva reserva3 = gestor.crearReserva(
                cliente3,
                Arrays.asList(hab301),
                LocalDate.of(2025, 12, 25),
                LocalDate.of(2025, 12, 27),
                pagoCriptoCliente3
        );
        logger.info(reserva3.toString());

        // === DEMOSTRACIÓN: Transferencia Bancaria - OCP ===
        logger.info("\n3.4 Reserva con Transferencia Bancaria (OCP):");
        MetodoPago pagoTransferenciaCliente1 = new PagoTransferenciaBancaria(
                "12345678901234567890", "Banco Nacional", "0001"
        );
        Habitacion hab103 = gestor.obtenerHabitacionPorNumero("103").get();

        Reserva reserva4 = gestor.crearReserva(
                cliente1,
                Arrays.asList(hab103),
                LocalDate.of(2025, 12, 30),
                LocalDate.of(2026, 1, 5),
                pagoTransferenciaCliente1
        );
        logger.info(reserva4.toString());

        // === DEMOSTRACIÓN: Reserva VIP - LSP ===
        logger.info("\n4. CREANDO RESERVA VIP (LSP - Substitución de Liskov):");
        logger.info("-".repeat(80));
        MetodoPago pagoVIP = new PagoTarjetaCredito(
                "4111111111111111", "Juan Pérez", "12/25", "123"
        );
        Habitacion hab401 = gestor.obtenerHabitacionPorNumero("401").get();
        Habitacion hab402 = gestor.obtenerHabitacionPorNumero("402").get();

        Reserva reservaVIP = gestor.crearReservaVIP(
                cliente1,
                Arrays.asList(hab401, hab402),
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 15),
                pagoVIP
        );
        logger.info(reservaVIP.toString());
        logger.info("  Beneficios VIP: Desayuno incluido, Servicio 24h, Acceso a áreas VIP");

        // === DEMOSTRACIÓN: Confirmar reservas ===
        logger.info("\n5. CONFIRMANDO RESERVAS (SRP - Responsabilidad única del gestor):");
        logger.info("-".repeat(80));
        confirmadorReserva(gestor, reserva1.getIdReserva());
        confirmadorReserva(gestor, reserva2.getIdReserva());
        confirmadorReserva(gestor, reserva3.getIdReserva());
        confirmadorReserva(gestor, reserva4.getIdReserva());

        // La reserva VIP se confirma y demuestra LSP
        logger.info("\nConfirmando Reserva VIP (LSP):");
        confirmadorReserva(gestor, reservaVIP.getIdReserva());

        // === DEMOSTRACIÓN: Cambiar fechas de una reserva ===
        logger.info("\n6. CAMBIANDO FECHAS DE UNA RESERVA:");
        logger.info("-".repeat(80));
        logger.info("Reserva original: " + reserva1);
        gestor.cambiarFechasReserva(
                reserva1.getIdReserva(),
                LocalDate.of(2025, 12, 16),
                LocalDate.of(2025, 12, 21)
        );
        logger.info("Reserva actualizada: " + reserva1);

        // === DEMOSTRACIÓN: Consultar reservas ===
        logger.info("\n7. CONSULTANDO RESERVAS:");
        logger.info("-".repeat(80));
        logger.info("\nReservas confirmadas:");
        gestor.obtenerReservasConfirmadas().forEach(r -> logger.info("  - " + r));

        logger.info("\nReservas del cliente Juan Pérez:");
        gestor.obtenerReservasCliente(cliente1).forEach(r -> logger.info("  - " + r));

        // === DEMOSTRACIÓN: Cancelar una reserva ===
        logger.info("\n8. CANCELANDO UNA RESERVA:");
        logger.info("-".repeat(80));
        logger.info("Cancelando reserva: " + reserva2.getIdReserva());
        gestor.cancelarReserva(reserva2.getIdReserva());

        // === RESUMEN FINAL ===
        logger.info("\n9. RESUMEN FINAL:");
        logger.info("-".repeat(80));
        logger.info("Total de reservas: " + gestor.obtenerTodasLasReservas().size());
        logger.info("Reservas confirmadas: " + gestor.obtenerReservasConfirmadas().size());
        logger.info("Ingresos totales: $" + String.format("%.2f", gestor.obtenerIngresosTotales()));

        // === DEMOSTRACIÓN: Principios SOLID ===
        logger.info("\n" + "=".repeat(80));
        logger.info("DEMOSTRACIÓN DE PRINCIPIOS SOLID:");
        logger.info("=".repeat(80));
        demostrarPrincipiosSOLID();
    }

    private static void registrarHabitaciones(GestorReservas gestor) {
        // Habitaciones estándar
        gestor.registrarHabitacion(new HabitacionEstandar("101"));
        gestor.registrarHabitacion(new HabitacionEstandar("102"));
        gestor.registrarHabitacion(new HabitacionEstandar("103"));

        // Habitaciones dobles
        gestor.registrarHabitacion(new HabitacionDoble("201"));
        gestor.registrarHabitacion(new HabitacionDoble("202"));

        // Suites
        gestor.registrarHabitacion(new Suite("301"));
        gestor.registrarHabitacion(new Suite("302"));

        // Suites presidenciales
        gestor.registrarHabitacion(new SuitePresidencial("401"));
        gestor.registrarHabitacion(new SuitePresidencial("402"));
    }

    private static void confirmadorReserva(GestorReservas gestor, String idReserva) {
        try {
            gestor.confirmarReserva(idReserva);
        } catch (Exception e) {
            logger.error("Error al confirmar reserva: " + e.getMessage());
        }
    }

    private static void demostrarPrincipiosSOLID() {
        logger.info("\n1. SRP (Single Responsibility Principle):");
        logger.info("   ✓ Cliente: Responsable solo de datos del cliente");
        logger.info("   ✓ Habitacion: Responsable solo del estado de la habitación");
        logger.info("   ✓ Reserva: Responsable solo de los datos de la reserva");
        logger.info("   ✓ GestorReservas: Responsable solo de gestionar reservas");
        logger.info("   ✓ MetodoPago (implementaciones): Cada una responsable de su tipo de pago");

        logger.info("\n2. OCP (Open/Closed Principle):");
        logger.info("   ✓ Nuevos métodos de pago (PagoCriptomoneda) se pueden agregar");
        logger.info("     sin modificar las clases existentes");
        logger.info("   ✓ Nuevos tipos de habitaciones se pueden crear sin modificar Habitacion");

        logger.info("\n3. LSP (Liskov Substitution Principle):");
        logger.info("   ✓ ReservaVIP puede reemplazar a Reserva en cualquier contexto");
        logger.info("   ✓ El sistema funciona con ambas sin cambios en la lógica");

        logger.info("\n4. ISP (Interface Segregation Principle):");
        logger.info("   ✓ Interfaz Habitacion solo define métodos necesarios");
        logger.info("   ✓ Interfaz MetodoPago solo define métodos de pago");
        logger.info("   ✓ Las implementaciones no están forzadas a implementar métodos innecesarios");

        logger.info("\n5. DIP (Dependency Inversion Principle):");
        logger.info("   ✓ Reserva depende de la abstracción MetodoPago");
        logger.info("   ✓ No depende de implementaciones concretas");
        logger.info("   ✓ GestorReservas usa abstracciones, no clases concretas");

        logger.info("\n" + "=".repeat(80));
        logger.info("Sistema completado exitosamente!");
        logger.info("=".repeat(80));
    }
}
