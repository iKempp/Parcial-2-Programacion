package co.edu.uniquindio.pixelrent.exception;

/**
 * Excepcion base para errores del dominio de negocio en PixelRent.
 */
public class PixelRentException extends Exception {
    public PixelRentException(String mensaje) {
        super(mensaje);
    }
}
