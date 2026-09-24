package co.edu.uniquindio.devplus.model;

/**
 * Nivel de experiencia profesional del desarrollador de software.
 */
public enum NivelDesarrollador {
    JUNIOR("Junior"),
    SEMISENIOR("Semisenior"),
    SENIOR("Senior");

    private final String descripcion;

    NivelDesarrollador(String descripcion) {
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
