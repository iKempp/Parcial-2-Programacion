package co.edu.uniquindio.pixelrent.exception;

/**
 * Lanzada cuando se vulnera una restriccion o regla de negocio del sistema de alquiler.
 */
public class ReglaNegocioException extends PixelRentException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
