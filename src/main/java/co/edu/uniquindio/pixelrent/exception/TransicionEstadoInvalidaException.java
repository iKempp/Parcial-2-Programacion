package co.edu.uniquindio.pixelrent.exception;

/**
 * Lanzada cuando se intenta una transicion no permitida en el ciclo de vida de una reserva.
 */
public class TransicionEstadoInvalidaException extends ReglaNegocioException {
    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
