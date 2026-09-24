package co.edu.uniquindio.devplus.exception;

/**
 * Excepcion de negocio lanzada al intentar asociar a un proyecto un servicio adicional
 * que se encuentra inactivo o no disponible en el catalogo de la empresa.
 */
public class ServicioNoDisponibleException extends Exception {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
