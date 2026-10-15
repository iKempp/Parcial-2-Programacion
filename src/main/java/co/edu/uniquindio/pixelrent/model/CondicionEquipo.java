package co.edu.uniquindio.pixelrent.model;

/**
 * Representa la condicion operativa de un equipo audiovisual en PixelRent.
 */
public enum CondicionEquipo {
    DISPONIBLE("Disponible para ser utilizado"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_SERVICIO("Fuera de servicio");

    private final String descripcion;

    CondicionEquipo(String descripcion) {
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
