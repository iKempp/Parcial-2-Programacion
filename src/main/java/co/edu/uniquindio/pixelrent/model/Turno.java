package co.edu.uniquindio.pixelrent.model;

/**
 * Representa los turnos en los que se pueden alquilar y utilizar los equipos.
 */
public enum Turno {
    MANANA("Mañana"),
    TARDE("Tarde"),
    NOCHE("Noche");

    private final String descripcion;

    Turno(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
