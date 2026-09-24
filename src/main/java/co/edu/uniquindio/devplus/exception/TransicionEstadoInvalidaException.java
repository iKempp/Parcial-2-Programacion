package co.edu.uniquindio.devplus.exception;

/**
 * Excepcion lanzada cuando se intenta realizar una transicion no valida en la
 * maquina de estados del proyecto (ej. pasar de PENDIENTE a FINALIZADO directamente).
 */
public class TransicionEstadoInvalidaException extends Exception {

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
