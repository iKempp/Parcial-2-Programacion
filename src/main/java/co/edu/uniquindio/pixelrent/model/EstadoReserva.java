package co.edu.uniquindio.pixelrent.model;

/**
 * Estados del ciclo de vida de una reserva de equipos audiovisuales.
 */
public enum EstadoReserva {
    SOLICITADA("Solicitada"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    FINALIZADA("Finalizada");

    private final String descripcion;

    EstadoReserva(String descripcion) {
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
