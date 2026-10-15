package co.edu.uniquindio.pixelrent.exception;

/**
 * Lanzada cuando no se encuentra un elemento buscado (cliente, equipo, operador, sede, reserva).
 */
public class ElementoNoEncontradoException extends PixelRentException {
    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
