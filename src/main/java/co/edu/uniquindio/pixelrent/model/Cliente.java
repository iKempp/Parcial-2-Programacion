package co.edu.uniquindio.pixelrent.model;

import java.util.Objects;

/**
 * Representa un cliente de PixelRent.
 * Regla de negocio: La informacion registrada (nombre, identificacion, telefono y correo)
 * no debe ser modificada posteriormente (inmutabilidad).
 */
public class Cliente {

    private final String identificacion;
    private final String nombreCompleto;
    private final String telefono;
    private final String correoElectronico;

    public Cliente(String identificacion, String nombreCompleto, String telefono, String correoElectronico) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de identificacion del cliente no puede ser vacio.");
        }
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser vacio.");
        }
        this.identificacion = identificacion.trim();
        this.nombreCompleto = nombreCompleto.trim();
        this.telefono = telefono != null ? telefono.trim() : "";
        this.correoElectronico = correoElectronico != null ? correoElectronico.trim() : "";
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(identificacion, cliente.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public String toString() {
        return String.format("Cliente [id=%s, nombre=%s, tel=%s, email=%s]",
                identificacion, nombreCompleto, telefono, correoElectronico);
    }
}
