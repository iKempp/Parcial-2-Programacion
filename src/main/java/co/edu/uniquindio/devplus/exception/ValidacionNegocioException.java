package co.edu.uniquindio.devplus.exception;

/**
 * Excepcion para errores generales de validacion de datos o precondiciones del negocio.
 */
public class ValidacionNegocioException extends Exception {

    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
    }
}
