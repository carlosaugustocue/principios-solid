package com.hotelreservation.model;

/**
 * Clase Cliente.
 * SRP: Responsabilidad única de representar un cliente del hotel.
 */
public class Cliente {
    private String nombre;
    private String email;
    private String telefono;
    private String numeroDocumento;

    public Cliente(String nombre, String email, String telefono, String numeroDocumento) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    @Override
    public String toString() {
        return String.format("Cliente: %s (Email: %s, Teléfono: %s, Documento: %s)",
                nombre, email, telefono, numeroDocumento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return numeroDocumento.equals(cliente.numeroDocumento);
    }

    @Override
    public int hashCode() {
        return numeroDocumento.hashCode();
    }
}
