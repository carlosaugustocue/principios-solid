package com.hotelreservation;

import com.hotelreservation.model.*;
import com.hotelreservation.payment.*;
import com.hotelreservation.service.GestorReservas;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Clase principal que demuestra el uso del sistema de reservas con los principios SOLID.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("SISTEMA DE RESERVAS DE HOTEL - Demostración de Principios SOLID");
        System.out.println("=".repeat(80));

        // Crear el gestor de reservas
        GestorReservas gestor = new GestorReservas();

        // === DEMOSTRACIÓN: Registrar habitaciones ===
        System.out.println("\n1. REGISTRANDO HABITACIONES (SRP):");
        System.out.println("-".repeat(80));
        registrarHabitaciones(gestor);

        // === DEMOSTRACIÓN: Crear clientes ===
        System.out.println("\n2. CREANDO CLIENTES (SRP):");
        System.out.println("-".repeat(80));
        Cliente cliente1 = new Cliente("Juan Pérez", "juan@email.com", "1234567890", "12345678");
        Cliente cliente2 = new Cliente("María García", "maria@email.com", "0987654321", "87654321");
        Cliente cliente3 = new Cliente("Carlos López", "carlos@email.com", "5555555555", "55555555");

        System.out.println(cliente1);
        System.out.println(cliente2);
        System.out.println(cliente3);

        // === DEMOSTRACIÓN: Crear reservas con diferentes métodos de pago (DIP, OCP) ===
        System.out.println("\n3. CREANDO RESERVAS CON DIFERENTES MÉTODOS DE PAGO (DIP, OCP):");
        System.out.println("-".repeat(80));

        // Reserva 1: Con tarjeta de crédito
        System.out.println("\n3.1 Reserva con Tarjeta de Crédito:");
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
        System.out.println(reserva1);

        // Reserva 2: Con tarjeta de débito
        System.out.println("\n3.2 Reserva con Tarjeta de Débito:");
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
        System.out.println(reserva2);

        // === DEMOSTRACIÓN: Nuevo método de pago (Criptomoneda) - OCP ===
        System.out.println("\n3.3 Reserva con Criptomoneda (OCP - Extensión sin modificar código):");
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
        System.out.println(reserva3);

        // === DEMOSTRACIÓN: Transferencia Bancaria - OCP ===
        System.out.println("\n3.4 Reserva con Transferencia Bancaria (OCP):");
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
        System.out.println(reserva4);

        // === DEMOSTRACIÓN: Reserva VIP - LSP ===
        System.out.println("\n4. CREANDO RESERVA VIP (LSP - Substitución de Liskov):");
        System.out.println("-".repeat(80));
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
        System.out.println(reservaVIP);
        System.out.println("  Beneficios VIP: Desayuno incluido, Servicio 24h, Acceso a áreas VIP");

        // === DEMOSTRACIÓN: Confirmar reservas ===
        System.out.println("\n5. CONFIRMANDO RESERVAS (SRP - Responsabilidad única del gestor):");
        System.out.println("-".repeat(80));
        confirmadorReserva(gestor, reserva1.getIdReserva());
        confirmadorReserva(gestor, reserva2.getIdReserva());
        confirmadorReserva(gestor, reserva3.getIdReserva());
        confirmadorReserva(gestor, reserva4.getIdReserva());

        // La reserva VIP se confirma y demuestra LSP
        System.out.println("\nConfirmando Reserva VIP (LSP):");
        confirmadorReserva(gestor, reservaVIP.getIdReserva());

        // === DEMOSTRACIÓN: Cambiar fechas de una reserva ===
        System.out.println("\n6. CAMBIANDO FECHAS DE UNA RESERVA:");
        System.out.println("-".repeat(80));
        System.out.println("Reserva original: " + reserva1);
        gestor.cambiarFechasReserva(
                reserva1.getIdReserva(),
                LocalDate.of(2025, 12, 16),
                LocalDate.of(2025, 12, 21)
        );
        System.out.println("Reserva actualizada: " + reserva1);

        // === DEMOSTRACIÓN: Consultar reservas ===
        System.out.println("\n7. CONSULTANDO RESERVAS:");
        System.out.println("-".repeat(80));
        System.out.println("\nReservas confirmadas:");
        gestor.obtenerReservasConfirmadas().forEach(r -> System.out.println("  - " + r));

        System.out.println("\nReservas del cliente Juan Pérez:");
        gestor.obtenerReservasCliente(cliente1).forEach(r -> System.out.println("  - " + r));

        // === DEMOSTRACIÓN: Cancelar una reserva ===
        System.out.println("\n8. CANCELANDO UNA RESERVA:");
        System.out.println("-".repeat(80));
        System.out.println("Cancelando reserva: " + reserva2.getIdReserva());
        gestor.cancelarReserva(reserva2.getIdReserva());

        // === RESUMEN FINAL ===
        System.out.println("\n9. RESUMEN FINAL:");
        System.out.println("-".repeat(80));
        System.out.println("Total de reservas: " + gestor.obtenerTodasLasReservas().size());
        System.out.println("Reservas confirmadas: " + gestor.obtenerReservasConfirmadas().size());
        System.out.println("Ingresos totales: $" + String.format("%.2f", gestor.obtenerIngresosTotales()));

        // === DEMOSTRACIÓN: Principios SOLID ===
        System.out.println("\n" + "=".repeat(80));
        System.out.println("DEMOSTRACIÓN DE PRINCIPIOS SOLID:");
        System.out.println("=".repeat(80));
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
            System.out.println("Error al confirmar reserva: " + e.getMessage());
        }
    }

    private static void demostrarPrincipiosSOLID() {
        System.out.println("\n1. SRP (Single Responsibility Principle):");
        System.out.println("   ✓ Cliente: Responsable solo de datos del cliente");
        System.out.println("   ✓ Habitacion: Responsable solo del estado de la habitación");
        System.out.println("   ✓ Reserva: Responsable solo de los datos de la reserva");
        System.out.println("   ✓ GestorReservas: Responsable solo de gestionar reservas");
        System.out.println("   ✓ MetodoPago (implementaciones): Cada una responsable de su tipo de pago");

        System.out.println("\n2. OCP (Open/Closed Principle):");
        System.out.println("   ✓ Nuevos métodos de pago (PagoCriptomoneda) se pueden agregar");
        System.out.println("     sin modificar las clases existentes");
        System.out.println("   ✓ Nuevos tipos de habitaciones se pueden crear sin modificar Habitacion");

        System.out.println("\n3. LSP (Liskov Substitution Principle):");
        System.out.println("   ✓ ReservaVIP puede reemplazar a Reserva en cualquier contexto");
        System.out.println("   ✓ El sistema funciona con ambas sin cambios en la lógica");

        System.out.println("\n4. ISP (Interface Segregation Principle):");
        System.out.println("   ✓ Interfaz Habitacion solo define métodos necesarios");
        System.out.println("   ✓ Interfaz MetodoPago solo define métodos de pago");
        System.out.println("   ✓ Las implementaciones no están forzadas a implementar métodos innecesarios");

        System.out.println("\n5. DIP (Dependency Inversion Principle):");
        System.out.println("   ✓ Reserva depende de la abstracción MetodoPago");
        System.out.println("   ✓ No depende de implementaciones concretas");
        System.out.println("   ✓ GestorReservas usa abstracciones, no clases concretas");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Sistema completado exitosamente!");
        System.out.println("=".repeat(80));
    }
}
