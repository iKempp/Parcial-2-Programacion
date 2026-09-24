package co.edu.uniquindio.devplus.exception;

/**
 * Excepcion lanzada cuando una entidad solicitada por codigo, nit o identificador
 * no existe dentro de los catalogos de la empresa.
 */
public class RecursoNoEncontradoException extends Exception {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
