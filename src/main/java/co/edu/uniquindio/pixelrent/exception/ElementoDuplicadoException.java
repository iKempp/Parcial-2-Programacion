package co.edu.uniquindio.pixelrent.exception;

/**
 * Lanzada cuando se intenta registrar un elemento con un identificador o codigo ya existente.
 */
public class ElementoDuplicadoException extends PixelRentException {
    public ElementoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
