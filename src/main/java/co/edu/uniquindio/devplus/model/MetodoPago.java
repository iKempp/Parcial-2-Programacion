package co.edu.uniquindio.devplus.model;

/**
 * Metodos de pago aceptados por la empresa DevPlus para la contratacion de proyectos.
 */
public enum MetodoPago {
    TARJETA_CREDITO("Tarjeta de Credito"),
    TRANSFERENCIA_BANCARIA("Transferencia Bancaria"),
    EFECTIVO("Efectivo");

    private final String descripcion;

    MetodoPago(String descripcion) {
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
