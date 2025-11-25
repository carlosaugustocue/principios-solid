# Sistema de Reservas de Hotel - Implementación de Principios SOLID

## Descripción General

Este proyecto implementa un sistema completo de reservas de hotel en Java 21, enfocándose especialmente en la aplicación correcta de los principios SOLID. El sistema permite a los clientes hacer reservas de una o varias habitaciones y cambiar las fechas de sus reservas.

## Principios SOLID Implementados

### 1. **SRP (Single Responsibility Principle)**

Cada clase tiene una responsabilidad única y bien definida:

- **Cliente** (model/Cliente.java): Solo gestiona datos de clientes
- **Habitacion** (model/Habitacion.java): Define el contrato de una habitación
- **Reserva** (model/Reserva.java): Solo maneja los datos y estado de una reserva
- **ReservaVIP** (model/ReservaVIP.java): Extiende Reserva con funcionalidad VIP
- **GestorReservas** (service/GestorReservas.java): Solo gestiona operaciones de reservas
- **MetodoPago** (payment/MetodoPago.java): Cada implementación es responsable de su tipo de pago

**Ejemplo:**
```java
// GestorReservas solo gestiona reservas, no procesa pagos directamente
public void confirmarReserva(String idReserva) {
    Optional<Reserva> reservaOpt = obtenerReservaPorId(idReserva);
    if (reservaOpt.isPresent()) {
        reservaOpt.get().confirmar(); // Delega a Reserva
    }
}
```

---

### 2. **OCP (Open/Closed Principle)**

El sistema está abierto para extensión pero cerrado para modificación. Nuevos métodos de pago pueden agregarse sin cambiar el código existente:

**Métodos de pago implementados:**
- `PagoTarjetaCredito.java`: Pago con tarjeta de crédito
- `PagoTarjetaDebito.java`: Pago con tarjeta de débito
- `PagoCriptomoneda.java`: **NUEVO** - Pago con criptomonedas (sin modificar código existente)
- `PagoTransferenciaBancaria.java`: Pago por transferencia bancaria

**Características:**
- Todos implementan la interfaz `MetodoPago`
- Se pueden agregar nuevas formas de pago simplemente creando nuevas clases
- No requiere modificación de `Reserva`, `GestorReservas` o cualquier otra clase

**Ejemplo de extensibilidad:**
```java
// Agregar un nuevo método de pago es solo crear una clase nueva
public class PagoPayPal implements MetodoPago {
    @Override
    public boolean procesarPago(double monto) { /* implementación */ }
    @Override
    public String getNombreMetodo() { return "PayPal"; }
    @Override
    public String obtenerDetalles() { /* detalles */ }
}
// El sistema funciona sin cambios adicionales
```

---

### 3. **LSP (Liskov Substitution Principle)**

La clase `ReservaVIP` puede reemplazar a `Reserva` en cualquier contexto sin que el sistema falle:

**Implementación:**
```java
public class ReservaVIP extends Reserva {
    private static final double DESCUENTO_VIP = 0.15;

    @Override
    protected void calcularMontoTotal() {
        // Cálculo diferente pero mantiene el contrato
        // ...
    }
}
```

**Uso transparente:**
```java
Reserva reserva1 = new Reserva(...);       // Reserva normal
Reserva reserva2 = new ReservaVIP(...);    // Reserva VIP

// Ambas se tratan igual en el sistema
gestor.confirmarReserva(reserva1.getIdReserva());
gestor.confirmarReserva(reserva2.getIdReserva());
```

**Beneficios:**
- Sistema polimórfico y flexible
- ReservaVIP agrega descuentos y beneficios sin romper el contrato
- Fácil agregar nuevos tipos de reservas (ReservaGrupo, ReservaEstudiantil, etc.)

---

### 4. **ISP (Interface Segregation Principle)**

Las interfaces no fuerzan la implementación de métodos innecesarios:

**Interfaz Habitacion:**
```java
public interface Habitacion {
    String getNumero();
    TipoHabitacion getTipo();
    double getPrecioNoche();
    boolean estaDisponible();
    void marcarOcupada();
    void marcarDisponible();
}
```

**Implementaciones específicas:**
- `HabitacionEstandar`: Implementa todos los métodos de forma simple
- `HabitacionDoble`: Implementa todos los métodos para habitación doble
- `Suite`: Implementa con características de suite
- `SuitePresidencial`: Implementa con características presidenciales

**Beneficio:** Cada clase implementa exactamente lo que necesita, sin métodos innecesarios.

**Interfaz MetodoPago:**
```java
public interface MetodoPago {
    boolean procesarPago(double monto);
    String getNombreMetodo();
    String obtenerDetalles();
}
```

Cada método de pago implementa exactamente estos tres métodos, nada más.

---

### 5. **DIP (Dependency Inversion Principle)**

Las clases de alto nivel dependen de abstracciones, no de implementaciones concretas:

**Implementación en Reserva:**
```java
public class Reserva {
    protected MetodoPago metodoPago; // Depende de la abstracción

    public Reserva(Cliente cliente, List<Habitacion> habitaciones,
                   LocalDate fechaCheckIn, LocalDate fechaCheckOut,
                   MetodoPago metodoPago) {
        this.metodoPago = metodoPago; // Inyección de dependencia
    }

    public void confirmar() {
        // Usa la abstracción, no sabe que implementación es
        if (metodoPago.procesarPago(montoTotal)) {
            // ...
        }
    }
}
```

**Ventajas:**
- `Reserva` funciona con cualquier `MetodoPago`
- No está acoplada a implementaciones específicas
- Fácil de testear (inyectar mocks)
- Fácil de extender con nuevos métodos de pago

---

## Estructura del Proyecto

```
principios-solid/
├── src/main/java/com/hotelreservation/
│   ├── Main.java                          # Punto de entrada con demostración
│   ├── model/                             # Modelos de dominio
│   │   ├── Habitacion.java               # Interfaz base
│   │   ├── HabitacionEstandar.java       # Implementación
│   │   ├── HabitacionDoble.java          # Implementación
│   │   ├── Suite.java                    # Implementación
│   │   ├── SuitePresidencial.java        # Implementación
│   │   ├── TipoHabitacion.java           # Enumeración
│   │   ├── Cliente.java                  # Cliente del hotel
│   │   ├── Reserva.java                  # Clase base de reservas
│   │   ├── ReservaVIP.java               # Subclase con beneficios VIP
│   │   └── EstadoReserva.java            # Estados posibles
│   ├── payment/                          # Sistema de pagos
│   │   ├── MetodoPago.java              # Interfaz de abstracción
│   │   ├── PagoTarjetaCredito.java      # Implementación
│   │   ├── PagoTarjetaDebito.java       # Implementación
│   │   ├── PagoCriptomoneda.java        # Implementación (OCP)
│   │   └── PagoTransferenciaBancaria.java # Implementación
│   └── service/                          # Servicios de negocio
│       └── GestorReservas.java          # Gestor de reservas
└── README.md
```

## Características Principales

### 1. **Gestión de Habitaciones**
- 4 tipos de habitaciones: Estándar, Doble, Suite, Suite Presidencial
- Control de disponibilidad
- Precios diferenciados por tipo

### 2. **Gestión de Reservas**
- Crear reservas de una o múltiples habitaciones
- Confirmar reservas con procesamiento de pago
- Cambiar fechas de reserva
- Cancelar reservas
- Consultar estado de reservas

### 3. **Sistema de Pagos**
- Múltiples métodos de pago implementados
- Fácil extensión para nuevos métodos
- Validación de datos de pago

### 4. **Tipos de Reservas**
- Reservas estándar
- Reservas VIP con descuentos y beneficios especiales
- Fácil agregar nuevos tipos (Reserva Grupal, etc.)

### 5. **Gestión de Clientes**
- Registro de clientes con información de contacto
- Rastreo de reservas por cliente
- Sistema de identificación mediante documento

## Cómo Compilar y Ejecutar

```bash
# Crear directorio de salida
mkdir -p out

# Compilar todos los archivos Java
javac -d out src/main/java/com/hotelreservation/**/*.java src/main/java/com/hotelreservation/*.java

# Ejecutar el programa
java -cp out com.hotelreservation.Main
```

## Ejemplo de Uso

```java
// Crear gestor de reservas
GestorReservas gestor = new GestorReservas();

// Registrar habitaciones
gestor.registrarHabitacion(new HabitacionEstandar("101"));
gestor.registrarHabitacion(new Suite("301"));

// Crear cliente
Cliente cliente = new Cliente("Juan Pérez", "juan@email.com", "123456", "12345678");

// Crear método de pago
MetodoPago pago = new PagoTarjetaCredito("4111111111111111", "Juan Pérez", "12/25", "123");

// Crear reserva
Reserva reserva = gestor.crearReserva(
    cliente,
    Arrays.asList(
        gestor.obtenerHabitacionPorNumero("101").get(),
        gestor.obtenerHabitacionPorNumero("301").get()
    ),
    LocalDate.of(2025, 12, 15),
    LocalDate.of(2025, 12, 20),
    pago
);

// Confirmar reserva
gestor.confirmarReserva(reserva.getIdReserva());

// Cambiar fechas
gestor.cambiarFechasReserva(
    reserva.getIdReserva(),
    LocalDate.of(2025, 12, 16),
    LocalDate.of(2025, 12, 21)
);

// Crear reserva VIP
Reserva reservaVIP = gestor.crearReservaVIP(
    cliente,
    Arrays.asList(gestor.obtenerHabitacionPorNumero("301").get()),
    LocalDate.of(2026, 1, 10),
    LocalDate.of(2026, 1, 15),
    pago
);
gestor.confirmarReserva(reservaVIP.getIdReserva());
```

## Ventajas del Diseño SOLID

1. **Mantenibilidad**: Código fácil de entender y modificar
2. **Extensibilidad**: Agregar nuevas funcionalidades sin romper existentes
3. **Testabilidad**: Fácil de testear unitariamente
4. **Reutilización**: Componentes desacoplados y reutilizables
5. **Flexibilidad**: Cambios de requisitos se implementan fácilmente

## Posibles Extensiones

1. **Nuevos métodos de pago**: PayPal, Apple Pay, etc.
2. **Nuevos tipos de reservas**: Grupal, Corporativa, etc.
3. **Sistema de puntos de fidelización**
4. **Descuentos y promociones**
5. **Notificaciones a clientes**
6. **Historial de transacciones**
7. **Reportes y análisis**

## Conclusión

Este proyecto demuestra la importancia de aplicar los principios SOLID desde el diseño inicial de una aplicación. El resultado es un sistema:

- ✅ Fácil de mantener
- ✅ Fácil de extender
- ✅ Fácil de testear
- ✅ Flexible y adaptable
- ✅ Profesional y escalable

Los principios SOLID no son solo teoría, sino prácticas concretas que mejoran significativamente la calidad del código.
