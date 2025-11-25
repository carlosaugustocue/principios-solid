# Comparación: Diseño SIN SOLID vs Diseño CON SOLID

Este documento muestra cómo el mismo sistema se vería sin aplicar los principios SOLID, y por qué es problemático.

## Problema 1: Agregar un Nuevo Método de Pago

### ❌ SIN SOLID (Violación de OCP)

```java
// Clase monolítica que contiene toda la lógica de pagos
public class SistemaReservas {
    // ... otros métodos ...

    public void procesarPago(Reserva reserva, String tipoPago, String datos) {
        if (tipoPago.equals("TARJETA_CREDITO")) {
            // Validar tarjeta de crédito
            String[] partes = datos.split(",");
            String numero = partes[0];
            String cvv = partes[1];

            if (numero.length() < 13) return false;
            if (cvv.length() < 3) return false;

            System.out.println("Pagando con tarjeta: " + numero);
            reserva.confirmar();

        } else if (tipoPago.equals("DEBITO")) {
            // Validar tarjeta de débito
            String[] partes = datos.split(",");
            String numero = partes[0];
            String pin = partes[1];

            if (numero.length() < 13) return false;
            if (pin.length() < 4) return false;

            System.out.println("Pagando con débito: " + numero);
            reserva.confirmar();

        } else if (tipoPago.equals("TRANSFERENCIA")) {
            // Validar transferencia
            String[] partes = datos.split(",");
            String cuenta = partes[0];
            String banco = partes[1];

            System.out.println("Transferencia a " + banco);
            reserva.confirmar();

        } else if (tipoPago.equals("CRIPTO")) {
            // Validar criptomoneda
            String[] partes = datos.split(",");
            String billetera = partes[0];

            System.out.println("Pagando con cripto en " + billetera);
            reserva.confirmar();

        } else {
            System.out.println("Tipo de pago no soportado");
        }
    }
}
```

**Problemas:**
- ❌ Para agregar un nuevo método de pago (PayPal, Apple Pay, etc.) se debe modificar `SistemaReservas`
- ❌ Violación de OCP (Open/Closed Principle)
- ❌ Método muy largo y complejo
- ❌ Difícil de testear
- ❌ Alto riesgo de introducir bugs al modificar

### ✅ CON SOLID (Aplicación de OCP)

```java
// Interfaz abstracta
public interface MetodoPago {
    boolean procesarPago(double monto);
    String getNombreMetodo();
    String obtenerDetalles();
}

// Cada método de pago en su propia clase
public class PagoTarjetaCredito implements MetodoPago {
    // Implementación específica
}

public class PagoCriptomoneda implements MetodoPago {
    // Implementación específica
}

// Para agregar PayPal, solo crear una nueva clase:
public class PagoPayPal implements MetodoPago {
    // Nueva implementación sin tocar código existente
}

// Reserva solo depende de la abstracción
public class Reserva {
    protected MetodoPago metodoPago;

    public void confirmar() {
        if (metodoPago.procesarPago(montoTotal)) {
            // Confirmar
        }
    }
}
```

**Ventajas:**
- ✅ Agregar nuevos métodos de pago sin modificar código existente
- ✅ Respeta OCP (Open/Closed Principle)
- ✅ Código limpio y modular
- ✅ Fácil de testear
- ✅ Bajo riesgo al extender

---

## Problema 2: Múltiples Responsabilidades en una Clase

### ❌ SIN SOLID (Violación de SRP)

```java
// Clase con demasiadas responsabilidades
public class GestorHotel {
    private List<Habitacion> habitaciones;
    private List<Cliente> clientes;
    private List<Reserva> reservas;

    // Responsabilidad 1: Gestionar habitaciones
    public void registrarHabitacion(String numero, String tipo, double precio) {
        Habitacion h = new Habitacion();
        h.setNumero(numero);
        h.setTipo(tipo);
        h.setPrecio(precio);
        habitaciones.add(h);
    }

    public List<Habitacion> obtenerHabitacionesDisponibles() {
        return habitaciones.stream()
            .filter(h -> h.isDisponible())
            .collect(Collectors.toList());
    }

    // Responsabilidad 2: Gestionar clientes
    public void registrarCliente(String nombre, String email, String documento) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setEmail(email);
        c.setDocumento(documento);
        clientes.add(c);
    }

    public Cliente buscarCliente(String documento) {
        return clientes.stream()
            .filter(c -> c.getDocumento().equals(documento))
            .findFirst()
            .orElse(null);
    }

    // Responsabilidad 3: Procesar pagos
    public boolean procesarPago(double monto, String tipoPago, String datos) {
        if (tipoPago.equals("CREDITO")) {
            // Validar tarjeta
            return true;
        } else if (tipoPago.equals("DEBITO")) {
            // Validar débito
            return true;
        }
        return false;
    }

    // Responsabilidad 4: Gestionar reservas
    public void crearReserva(Cliente cliente, List<Habitacion> habitaciones,
                            LocalDate checkIn, LocalDate checkOut) {
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setHabitaciones(habitaciones);
        reserva.setCheckIn(checkIn);
        reserva.setCheckOut(checkOut);
        reservas.add(reserva);
    }

    // Responsabilidad 5: Generar reportes
    public void reporteIngresos() {
        double total = 0;
        for (Reserva r : reservas) {
            total += r.getMontoTotal();
        }
        System.out.println("Ingresos: " + total);
    }

    // Responsabilidad 6: Validar datos
    public boolean validarEmail(String email) {
        return email.contains("@");
    }

    public boolean validarFechas(LocalDate inicio, LocalDate fin) {
        return inicio.isBefore(fin);
    }

    // ... más responsabilidades ...
}
```

**Problemas:**
- ❌ Clase con 6+ responsabilidades diferentes
- ❌ Difícil de testear (debe testear todo junto)
- ❌ Cambios en una responsabilidad afectan otras
- ❌ Reutilización imposible
- ❌ Violación severa de SRP

### ✅ CON SOLID (Aplicación de SRP)

```java
// Cada responsabilidad en su propia clase

// Responsabilidad 1: Gestionar habitaciones
public class RepositorioHabitaciones {
    public void registrar(Habitacion h) { /* ... */ }
    public List<Habitacion> obtenerDisponibles() { /* ... */ }
}

// Responsabilidad 2: Gestionar clientes
public class RepositorioClientes {
    public void registrar(Cliente c) { /* ... */ }
    public Cliente buscar(String documento) { /* ... */ }
}

// Responsabilidad 3: Procesar pagos (delegado a implementations)
public interface MetodoPago {
    boolean procesarPago(double monto);
}

// Responsabilidad 4: Gestionar reservas
public class GestorReservas {
    public Reserva crear(Cliente c, List<Habitacion> h,
                        LocalDate checkIn, LocalDate checkOut,
                        MetodoPago pago) { /* ... */ }
}

// Responsabilidad 5: Generar reportes
public class ServicioReportes {
    public void reporteIngresos(GestorReservas gestor) { /* ... */ }
}

// Responsabilidad 6: Validaciones
public class ValidadorDatos {
    public boolean validarEmail(String email) { /* ... */ }
    public boolean validarFechas(LocalDate i, LocalDate f) { /* ... */ }
}
```

**Ventajas:**
- ✅ Cada clase tiene una única responsabilidad
- ✅ Fácil de testear unitariamente
- ✅ Cambios localizados en una responsabilidad
- ✅ Clases reutilizables
- ✅ Código mantenible y escalable

---

## Problema 3: Acoplamiento Fuerte a Implementaciones Concretas

### ❌ SIN SOLID (Violación de DIP)

```java
// Alto acoplamiento a implementaciones concretas
public class Reserva {
    private Cliente cliente;
    private List<Habitacion> habitaciones;
    private PagoTarjetaCredito metodoPago; // Acoplado a una implementación específica!

    public Reserva(Cliente cliente, List<Habitacion> habitaciones,
                   PagoTarjetaCredito metodoPago) { // Solo acepta un tipo
        this.cliente = cliente;
        this.habitaciones = habitaciones;
        this.metodoPago = metodoPago;
    }

    public void confirmar() {
        // Acoplado específicamente a PagoTarjetaCredito
        if (metodoPago.validarTarjeta()) {
            metodoPago.procesarPago(getMontoTotal());
            // ...
        }

        // ¿Qué si el cliente quiere pagar con PayPal? Hay que reescribir todo
    }
}

// Para usar con otro método de pago, hay que crear nuevas clases:
public class ReservaConPayPal {
    private PagoPayPal metodoPago;
    // Duplicación de código!
}

public class ReservaConCripto {
    private PagoCriptomoneda metodoPago;
    // Más duplicación!
}
```

**Problemas:**
- ❌ Acoplamiento fuerte a `PagoTarjetaCredito`
- ❌ No se puede cambiar el método de pago después
- ❌ Violación de DIP
- ❌ Necesita múltiples clases Reserva para cada método de pago
- ❌ Duplicación masiva de código

### ✅ CON SOLID (Aplicación de DIP)

```java
// Bajo acoplamiento mediante inyección de dependencias
public class Reserva {
    private Cliente cliente;
    private List<Habitacion> habitaciones;
    private MetodoPago metodoPago; // Depende de la abstracción!

    public Reserva(Cliente cliente, List<Habitacion> habitaciones,
                   MetodoPago metodoPago) { // Acepta cualquier implementación
        this.cliente = cliente;
        this.habitaciones = habitaciones;
        this.metodoPago = metodoPago;
    }

    public void confirmar() {
        // Funciona con cualquier MetodoPago
        if (metodoPago.procesarPago(getMontoTotal())) {
            // Confirmar reserva
        }
    }
}

// Uso flexible
Reserva reserva1 = new Reserva(cliente, habitaciones, new PagoTarjetaCredito(...));
Reserva reserva2 = new Reserva(cliente, habitaciones, new PagoPayPal(...));
Reserva reserva3 = new Reserva(cliente, habitaciones, new PagoCriptomoneda(...));

// Todas funcionan exactamente igual
```

**Ventajas:**
- ✅ Bajo acoplamiento a abstracciones
- ✅ Flexible para cualquier método de pago
- ✅ Fácil de extender
- ✅ Una sola clase `Reserva` funciona para todos
- ✅ Fácil testear con mocks

---

## Problema 4: Violación de LSP

### ❌ SIN SOLID (Violación de LSP)

```java
public class Reserva {
    protected double montoTotal;

    public double getMontoTotal() {
        return montoTotal;
    }

    public void confirmar() {
        // Procesa el pago normalmente
        metodoPago.procesarPago(montoTotal);
    }
}

public class ReservaVIP extends Reserva {
    private static final double DESCUENTO = 0.15;

    @Override
    public double getMontoTotal() {
        // Viola el contrato: devuelve un valor diferente
        return montoTotal * (1 - DESCUENTO);
    }

    @Override
    public void confirmar() {
        // Se comporta diferente sin explicación clara
        // Aplica el descuento aquí, pero getMontoTotal() también lo aplica
        // Resultados impredecibles!
        super.confirmar();
        applyVIPBenefits();
    }
}

// Problema: No se puede reemplazar Reserva por ReservaVIP sin problemas
Reserva reserva = new ReservaVIP(...);
double monto = reserva.getMontoTotal(); // ¿Es el precio normal o con descuento?
```

**Problemas:**
- ❌ Contrato violado: getMontoTotal() devuelve cosas diferentes
- ❌ Comportamiento impredecible
- ❌ No se puede sustituir seguramente
- ❌ Violación de LSP

### ✅ CON SOLID (Aplicación de LSP)

```java
public class Reserva {
    protected double montoTotal;

    protected void calcularMontoTotal() {
        long noches = ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        this.montoTotal = habitaciones.stream()
            .mapToDouble(Habitacion::getPrecioNoche)
            .sum() * noches;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void confirmar() {
        // Procesa el pago con el monto calculado
        if (metodoPago.procesarPago(montoTotal)) {
            estado = EstadoReserva.CONFIRMADA;
        }
    }
}

public class ReservaVIP extends Reserva {
    private static final double DESCUENTO_VIP = 0.15;

    @Override
    protected void calcularMontoTotal() {
        // Calcula diferente
        long noches = ChronoUnit.DAYS.between(fechaCheckIn, fechaCheckOut);
        double subtotal = habitaciones.stream()
            .mapToDouble(Habitacion::getPrecioNoche)
            .sum() * noches;
        this.montoTotal = subtotal * (1 - DESCUENTO_VIP);
    }

    @Override
    public void confirmar() {
        super.confirmar();
        // Beneficios adicionales
        System.out.println("Beneficios VIP activados");
    }
}

// Ahora se puede sustituir seguramente
List<Reserva> reservas = new ArrayList<>();
reservas.add(new Reserva(...));
reservas.add(new ReservaVIP(...));

// Ambas funcionan correctamente
for (Reserva r : reservas) {
    r.confirmar(); // Contrato mantenido
}
```

**Ventajas:**
- ✅ Contrato claramente mantenido
- ✅ Comportamiento predecible
- ✅ Se puede sustituir de forma segura
- ✅ Respeta LSP

---

## Resumen Comparativo

| Aspecto | SIN SOLID | CON SOLID |
|---------|-----------|-----------|
| **Agregar nuevo método de pago** | Modificar clase existente | Crear nueva clase |
| **Riesgo de bugs** | Alto | Bajo |
| **Reutilización de código** | Difícil | Fácil |
| **Testabilidad** | Complicada | Simple |
| **Mantenibilidad** | Difícil | Fácil |
| **Escalabilidad** | Limitada | Excelente |
| **Cantidad de clases** | Pocas, grandes | Muchas, pequeñas |
| **Acoplamiento** | Fuerte | Débil |
| **Cambios futuros** | Peligrosos | Seguros |

---

## Conclusión

Los principios SOLID parecen hacer el código más complejo inicialmente, con más clases y abstracciones. **Sin embargo:**

1. **Cada clase es más simple** (una responsabilidad)
2. **Los cambios son más seguros** (bajo acoplamiento)
3. **La extensión es más fácil** (abierto a extensión)
4. **El código es más mantenible** (reutilizable)
5. **A largo plazo, hay MENOS código duplicado**

En resumen: **SOLID no es solo teoría, es inversión en mantenibilidad y escalabilidad.**
