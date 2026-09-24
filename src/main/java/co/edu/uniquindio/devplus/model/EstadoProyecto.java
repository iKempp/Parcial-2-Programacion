package co.edu.uniquindio.devplus.model;

/**
 * Representa los posibles estados por los que atraviesa un proyecto de software en DevPlus,
 * desde su creacion hasta su cierre o cancelacion.
 */
public enum EstadoProyecto {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    EN_CURSO("En curso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoProyecto(String descripcion) {
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
