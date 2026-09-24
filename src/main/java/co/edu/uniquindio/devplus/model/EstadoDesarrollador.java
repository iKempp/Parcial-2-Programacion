package co.edu.uniquindio.devplus.model;

/**
 * Estados operativos del desarrollador para control de asignacion y disponibilidad.
 */
public enum EstadoDesarrollador {
    DISPONIBLE("Disponible"),
    ASIGNADO("Asignado"),
    OCUPADO("Ocupado"),
    EN_CAPACITACION("En capacitacion");

    private final String descripcion;

    EstadoDesarrollador(String descripcion) {
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
