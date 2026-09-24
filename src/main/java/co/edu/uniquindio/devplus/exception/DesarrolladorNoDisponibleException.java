package co.edu.uniquindio.devplus.exception;

/**
 * Excepcion de negocio lanzada cuando un desarrollador no puede ser asignado a un proyecto
 * debido a que no esta disponible, esta en capacitacion o excede su capacidad maxima
 * de proyectos simultaneos en las fechas solicitadas.
 */
public class DesarrolladorNoDisponibleException extends Exception {

    public DesarrolladorNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
