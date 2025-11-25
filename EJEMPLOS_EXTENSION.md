# Ejemplos de Extensión del Sistema

Este documento muestra cómo el sistema SOLID permite agregar nuevas funcionalidades sin modificar el código existente.

## 1. Agregar un Nuevo Método de Pago (OCP)

El sistema está diseñado para ser abierto a extensión pero cerrado a modificación. Agregar un nuevo método de pago es simple:

### Paso 1: Crear una nueva clase que implemente MetodoPago

```java
package com.hotelreservation.payment;

/**
 * Implementación de pago con PayPal.
 * SRP: Responsabilidad única de procesar pagos con PayPal.
 * OCP: Nuevo método de pago agregado sin modificar código existente.
 */
public class PagoPayPal implements MetodoPago {
    private String email;
    private String contrasena;

    public PagoPayPal(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Simular validación de cuenta PayPal
        if (email == null || !email.contains("@")) {
            return false;
        }
        if (contrasena == null || contrasena.length() < 6) {
            return false;
        }

        // En una aplicación real, se conectaría a la API de PayPal
        System.out.println("Procesando pago de $" + monto + " mediante PayPal (" + email + ")");
        return true;
    }

    @Override
    public String getNombreMetodo() {
        return "PayPal";
    }

    @Override
    public String obtenerDetalles() {
        return String.format("Cuenta PayPal: %s", email);
    }
}
```

### Paso 2: Usar el nuevo método en una reserva

```java
// Crear método de pago PayPal
MetodoPago pagoPayPal = new PagoPayPal("cliente@gmail.com", "micontraseña123");

// Crear una reserva con PayPal (sin cambios en el código de Reserva)
Reserva reserva = gestor.crearReserva(
    cliente,
    Arrays.asList(habitacion),
    LocalDate.of(2025, 12, 15),
    LocalDate.of(2025, 12, 20),
    pagoPayPal  // PayPal es usado como cualquier otro MetodoPago
);

// Confirmar la reserva (el sistema funciona igual)
gestor.confirmarReserva(reserva.getIdReserva());
```

**Puntos clave:**
- ✅ No se modificó `Reserva`
- ✅ No se modificó `GestorReservas`
- ✅ No se modificó `Cliente`
- ✅ Solo se agregó una nueva clase
- ✅ El sistema completo funciona sin cambios

---

## 2. Agregar un Nuevo Tipo de Habitación (OCP)

```java
package com.hotelreservation.model;

/**
 * Implementación de una habitación con accesibilidad para discapacitados.
 * SRP: Responsabilidad única de una habitación accesible.
 * OCP: Nuevo tipo agregado sin modificar código existente.
 */
public class HabitacionAccesible implements Habitacion {
    private String numero;
    private boolean disponible;
    private static final double PRECIO_NOCHE = 100.0;

    // Características especiales de accesibilidad
    private boolean rampaAcceso;
    private boolean banioAdaptado;
    private boolean espacioCirculacion;

    public HabitacionAccesible(String numero) {
        this.numero = numero;
        this.disponible = true;
        this.rampaAcceso = true;
        this.banioAdaptado = true;
        this.espacioCirculacion = true;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public TipoHabitacion getTipo() {
        return TipoHabitacion.ESTANDAR; // O crear ACCESIBLE en el enum
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

    public boolean tieneRampaAcceso() {
        return rampaAcceso;
    }

    public boolean tieneBanioAdaptado() {
        return banioAdaptado;
    }

    public boolean tieneEspacioCirculacion() {
        return espacioCirculacion;
    }

    @Override
    public String toString() {
        return String.format("Habitación Accesible #%s (Precio: $%.2f/noche) - %s",
                numero, PRECIO_NOCHE, disponible ? "Disponible" : "Ocupada");
    }
}
```

---

## 3. Agregar un Nuevo Tipo de Reserva (LSP)

```java
package com.hotelreservation.model;

import com.hotelreservation.payment.MetodoPago;
import java.time.LocalDate;
import java.util.List;

/**
 * Reserva grupal para grupos de personas.
 * LSP: Puede reemplazar a Reserva sin que el sistema falle.
 * SRP: Responsabilidad única de manejar reservas grupales.
 */
public class ReservaGrupal extends Reserva {
    private int cantidadPersonas;
    private static final double DESCUENTO_GRUPAL = 0.10; // 10% descuento

    public ReservaGrupal(Cliente cliente, List<Habitacion> habitaciones,
                        LocalDate fechaCheckIn, LocalDate fechaCheckOut,
                        MetodoPago metodoPago, int cantidadPersonas) {
        super(cliente, habitaciones, fechaCheckIn, fechaCheckOut, metodoPago);
        this.cantidadPersonas = cantidadPersonas;
        calcularMontoTotal(); // Recalcular con descuento grupal
    }

    @Override
    protected void calcularMontoTotal() {
        long noches = java.time.temporal.ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        double subtotal = habitaciones.stream()
                .mapToDouble(Habitacion::getPrecioNoche)
                .sum() * noches;

        // Aplicar descuento grupal si hay más de 5 personas
        if (cantidadPersonas > 5) {
            this.montoTotal = subtotal * (1 - DESCUENTO_GRUPAL);
        } else {
            this.montoTotal = subtotal;
        }
    }

    @Override
    public void confirmar() {
        super.confirmar();
        System.out.println("Reserva grupal confirmada para " + cantidadPersonas + " personas");
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    @Override
    public String toString() {
        String descuentoStr = cantidadPersonas > 5 ?
            String.format("[GRUPAL - Descuento: %.0f%%]", DESCUENTO_GRUPAL * 100) :
            "[GRUPAL]";
        return super.toString() + " " + descuentoStr;
    }
}
```

### Uso de Reserva Grupal

```java
// Crear método de pago
MetodoPago pagoPrueba = new PagoTarjetaCredito("4111111111111111", "Test", "12/25", "123");

// Crear una reserva grupal (se trata como Reserva normalmente)
Reserva reservaGrupal = new ReservaGrupal(
    cliente,
    Arrays.asList(
        gestor.obtenerHabitacionPorNumero("101").get(),
        gestor.obtenerHabitacionPorNumero("102").get(),
        gestor.obtenerHabitacionPorNumero("103").get()
    ),
    LocalDate.of(2025, 12, 15),
    LocalDate.of(2025, 12, 20),
    pagoPrueba,
    8  // 8 personas en el grupo
);

// El gestor la maneja como cualquier otra Reserva
gestor.confirmarReserva(reservaGrupal.getIdReserva());
```

---

## 4. Agregar una Nueva Funcionalidad de Consulta

Sin modificar las clases existentes, se puede agregar un nuevo servicio de consulta:

```java
package com.hotelreservation.service;

import com.hotelreservation.model.Habitacion;
import com.hotelreservation.model.Reserva;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de reportes para el hotel.
 * SRP: Responsabilidad única de generar reportes.
 * DIP: Depende de abstracciones (Habitacion, Reserva).
 */
public class ServicioReportes {
    private GestorReservas gestor;

    public ServicioReportes(GestorReservas gestor) {
        this.gestor = gestor;
    }

    /**
     * Genera un reporte de ingresos por tipo de habitación.
     */
    public void reporteIngresosPorTipo() {
        System.out.println("=== REPORTE DE INGRESOS POR TIPO DE HABITACIÓN ===");

        gestor.obtenerReservasConfirmadas().stream()
            .collect(Collectors.groupingBy(
                r -> r.getHabitaciones().get(0).getTipo(),
                Collectors.summingDouble(Reserva::getMontoTotal)
            ))
            .forEach((tipo, total) ->
                System.out.printf("%s: $%.2f%n", tipo.getDescripcion(), total)
            );
    }

    /**
     * Genera un reporte de ocupación para un rango de fechas.
     */
    public void reporteOcupacion(LocalDate inicio, LocalDate fin) {
        System.out.println("\n=== REPORTE DE OCUPACIÓN (" + inicio + " a " + fin + ") ===");

        long reservasEnRango = gestor.obtenerReservasConfirmadas().stream()
            .filter(r -> !r.getFechaCheckOut().isBefore(inicio) &&
                        !r.getFechaCheckIn().isAfter(fin))
            .count();

        System.out.println("Reservas en el período: " + reservasEnRango);
    }

    /**
     * Genera un reporte de clientes frecuentes.
     */
    public void reporteClientesFrecuentes() {
        System.out.println("\n=== CLIENTES MÁS FRECUENTES ===");

        gestor.obtenerReservasConfirmadas().stream()
            .collect(Collectors.groupingBy(Reserva::getCliente, Collectors.counting()))
            .entrySet().stream()
            .filter(e -> e.getValue() >= 2)
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .forEach(e -> System.out.printf("%s: %d reservas%n",
                e.getKey().getNombre(), e.getValue()));
    }
}
```

### Uso del servicio de reportes

```java
// Crear servicio de reportes
ServicioReportes reportes = new ServicioReportes(gestor);

// Generar reportes
reportes.reporteIngresosPorTipo();
reportes.reporteOcupacion(LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 31));
reportes.reporteClientesFrecuentes();
```

---

## 5. Implementación de Sistema de Caché (DIP)

```java
package com.hotelreservation.payment;

/**
 * Wrapper para cachear resultados de pagos (patrón Decorator).
 * DIP: Depende de la abstracción MetodoPago.
 * OCP: Se puede usar sin modificar el código existente.
 */
public class PagoCacheado implements MetodoPago {
    private MetodoPago metodoSubyacente;
    private java.util.Map<Double, Boolean> cache = new java.util.HashMap<>();

    public PagoCacheado(MetodoPago metodoSubyacente) {
        this.metodoSubyacente = metodoSubyacente;
    }

    @Override
    public boolean procesarPago(double monto) {
        // Verificar en caché
        if (cache.containsKey(monto)) {
            System.out.println("Resultado del pago obtenido del caché");
            return cache.get(monto);
        }

        // Procesar pago
        boolean resultado = metodoSubyacente.procesarPago(monto);

        // Guardar en caché
        cache.put(monto, resultado);
        return resultado;
    }

    @Override
    public String getNombreMetodo() {
        return metodoSubyacente.getNombreMetodo() + " (Cacheado)";
    }

    @Override
    public String obtenerDetalles() {
        return metodoSubyacente.obtenerDetalles();
    }
}
```

### Uso del pago cacheado

```java
// Crear un método de pago con caché
MetodoPago pagoCreditoBase = new PagoTarjetaCredito("4111111111111111", "Juan", "12/25", "123");
MetodoPago pagoCacheado = new PagoCacheado(pagoCreditoBase);

// Usar en una reserva
Reserva reserva = gestor.crearReserva(cliente, habitaciones, checkIn, checkOut, pagoCacheado);
```

---

## Conclusión: Por Qué SOLID Hace Esto Posible

Cada extensión en este documento fue **posible sin modificar el código existente** porque:

1. **SRP**: Cada clase tiene una responsabilidad clara, permitiendo cambios localizados
2. **OCP**: Las abstracciones permiten agregar nuevas implementaciones sin cambios
3. **LSP**: Las subclases pueden extender comportamiento manteniendo el contrato
4. **ISP**: Las interfaces pequeñas son fáciles de implementar de nuevas formas
5. **DIP**: Las dependencias en abstracciones permiten intercambiar implementaciones

**Sin SOLID**, agregar cualquiera de estas funcionalidades requeriría modificar múltiples clases existentes, introduciendo riesgo de errores y rompiendo el código.
