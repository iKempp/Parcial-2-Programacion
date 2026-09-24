package co.edu.uniquindio.devplus.service;

import co.edu.uniquindio.devplus.exception.*;
import co.edu.uniquindio.devplus.model.*;
import co.edu.uniquindio.devplus.util.ValidadorUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Fachada del dominio y orquestador central de la logica de negocio de DevPlus.
 * Implementa las reglas de negocio RN1 a RN8, coordinando el modelo y aislandolo
 * de la capa de presentacion (Consola/UI).
 */
public class DevPlusService {

    private final Empresa empresa;

    public DevPlusService(Empresa empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa no puede ser nula.");
        }
        this.empresa = empresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    // =========================================================================
    // REGISTRO Y GESTION DE CATALOGOS MAESTROS
    // =========================================================================

    public void registrarCliente(Cliente cliente) throws ValidacionNegocioException {
        if (cliente == null) {
            throw new ValidacionNegocioException("Los datos del cliente no pueden ser nulos.");
        }
        if (empresa.buscarClientePorIdentificacion(cliente.getIdentificacion()) != null) {
            throw new ValidacionNegocioException("Ya existe un cliente con la identificacion: " + cliente.getIdentificacion());
        }
        empresa.registrarCliente(cliente);
    }

    public void registrarDesarrollador(Desarrollador desarrollador) throws ValidacionNegocioException {
        if (desarrollador == null) {
            throw new ValidacionNegocioException("Los datos del desarrollador no pueden ser nulos.");
        }
        if (empresa.buscarDesarrolladorPorCodigo(desarrollador.getCodigo()) != null) {
            throw new ValidacionNegocioException("Ya existe un desarrollador con el codigo: " + desarrollador.getCodigo());
        }
        empresa.registrarDesarrollador(desarrollador);
    }

    public void registrarServicioAdicional(ServicioAdicional servicio) throws ValidacionNegocioException {
        if (servicio == null) {
            throw new ValidacionNegocioException("Los datos del servicio adicional no pueden ser nulos.");
        }
        if (empresa.buscarServicioPorCodigo(servicio.getCodigo()) != null) {
            throw new ValidacionNegocioException("Ya existe un servicio con el codigo: " + servicio.getCodigo());
        }
        empresa.registrarServicioAdicional(servicio);
    }

    // =========================================================================
    // GESTION DE PROYECTOS Y REGLAS DE NEGOCIO (RN1 a RN6)
    // =========================================================================

    /**
     * Crea un nuevo proyecto en estado PENDIENTE asociado a un cliente existente.
     */
    public Proyecto crearProyecto(String codigo, LocalDate fechaSolicitud, LocalDate fechaInicio,
                                  LocalDate fechaEntrega, MetodoPago metodoPago, String idCliente)
            throws RecursoNoEncontradoException, ValidacionNegocioException {

        if (!ValidadorUtil.esRangoFechasValido(fechaInicio, fechaEntrega)) {
            throw new ValidacionNegocioException("La fecha de entrega debe ser igual o posterior a la fecha de inicio.");
        }
        if (fechaSolicitud != null && fechaInicio != null && fechaInicio.isBefore(fechaSolicitud)) {
            throw new ValidacionNegocioException("La fecha de inicio no puede ser anterior a la fecha de solicitud.");
        }
        if (empresa.buscarProyectoPorCodigo(codigo) != null) {
            throw new ValidacionNegocioException("Ya existe un proyecto registrado con el codigo: " + codigo);
        }

        Cliente cliente = empresa.buscarClientePorIdentificacion(idCliente);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("No existe ningun cliente registrado con ID/NIT: " + idCliente);
        }

        Proyecto nuevoProyecto = new Proyecto(codigo, fechaSolicitud, fechaInicio, fechaEntrega, metodoPago, cliente);
        cliente.agregarProyecto(nuevoProyecto);
        empresa.registrarProyecto(nuevoProyecto);
        return nuevoProyecto;
    }

    /**
     * Agrega un desarrollador a un proyecto validando su disponibilidad y tope de concurrencia (RN3).
     */
    public void asignarDesarrolladorAProyecto(String codigoProyecto, String codigoDesarrollador)
            throws RecursoNoEncontradoException, DesarrolladorNoDisponibleException, ValidacionNegocioException {

        Proyecto proyecto = buscarProyectoPorCodigo(codigoProyecto);
        Desarrollador dev = buscarDesarrolladorPorCodigo(codigoDesarrollador);

        if (proyecto.getEstado() == EstadoProyecto.FINALIZADO || proyecto.getEstado() == EstadoProyecto.CANCELADO) {
            throw new ValidacionNegocioException("No se pueden asignar desarrolladores a un proyecto finalizado o cancelado.");
        }

        // RN3: Validar disponibilidad y solapamiento de fechas con otros proyectos
        if (dev.getEstado() == EstadoDesarrollador.EN_CAPACITACION) {
            throw new DesarrolladorNoDisponibleException(
                "El desarrollador " + dev.getCodigo() + " no esta disponible: se encuentra EN CAPACITACION."
            );
        }

        if (!dev.tieneCapacidadEnFechas(proyecto.getFechaInicio(), proyecto.getFechaEntrega())) {
            int cruzados = dev.contarProyectosEnRango(proyecto.getFechaInicio(), proyecto.getFechaEntrega());
            throw new DesarrolladorNoDisponibleException(
                String.format("El desarrollador %s excede su capacidad maxima simultanea (%d proyectos). " +
                              "Tiene actualmente %d proyectos solapados en las fechas %s a %s.",
                              dev.getCodigo(), dev.getCantidadMaximaProyectosSimultaneos(), cruzados,
                              proyecto.getFechaInicio(), proyecto.getFechaEntrega())
            );
        }

        // Asignacion bidireccional
        proyecto.agregarDesarrollador(dev);
        dev.agregarProyecto(proyecto);

        // Recalcular valor total segun RN1 y RN2
        proyecto.recalcularValorTotal();
    }

    /**
     * Asocia un servicio adicional a un proyecto validando que este disponible (RN6).
     */
    public void asociarServicioAProyecto(String codigoProyecto, String codigoServicio)
            throws RecursoNoEncontradoException, ServicioNoDisponibleException, ValidacionNegocioException {

        Proyecto proyecto = buscarProyectoPorCodigo(codigoProyecto);
        ServicioAdicional servicio = buscarServicioPorCodigo(codigoServicio);

        if (proyecto.getEstado() == EstadoProyecto.FINALIZADO || proyecto.getEstado() == EstadoProyecto.CANCELADO) {
            throw new ValidacionNegocioException("No se pueden agregar servicios a proyectos finalizados o cancelados.");
        }

        // RN6: Solo si esta disponible
        if (!servicio.isDisponible()) {
            throw new ServicioNoDisponibleException(
                "El servicio adicional '" + servicio.getNombre() + "' (" + servicio.getCodigo() + ") NO esta disponible actualmente."
            );
        }

        proyecto.agregarServicio(servicio);
        proyecto.recalcularValorTotal();
    }

    /**
     * Calcula o recalcula el valor total de un proyecto segun RN1 y RN2.
     */
    public double calcularValorTotalProyecto(String codigoProyecto) throws RecursoNoEncontradoException {
        Proyecto proyecto = buscarProyectoPorCodigo(codigoProyecto);
        return proyecto.recalcularValorTotal();
    }

    /**
     * Confirma un proyecto en estado PENDIENTE y actualiza la disponibilidad de sus desarrolladores (RN4 y RN5).
     */
    public void confirmarProyecto(String codigoProyecto)
            throws RecursoNoEncontradoException, TransicionEstadoInvalidaException, ValidacionNegocioException {

        Proyecto proyecto = buscarProyectoPorCodigo(codigoProyecto);

        if (proyecto.getDesarrolladoresAsignados().isEmpty()) {
            throw new ValidacionNegocioException("No es posible confirmar un proyecto sin al menos un desarrollador asignado.");
        }

        if (proyecto.getEstado() != EstadoProyecto.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                "Solo se puede confirmar un proyecto en estado PENDIENTE. Estado actual: " + proyecto.getEstado()
            );
        }

        proyecto.setEstado(EstadoProyecto.CONFIRMADO);
        proyecto.recalcularValorTotal();

        // RN4: Actualizar la disponibilidad/estado de los desarrolladores asignados
        for (Desarrollador d : proyecto.getDesarrolladoresAsignados()) {
            d.actualizarEstadoSegunCarga();
        }
    }

    /**
     * Gestiona las transiciones permitidas del estado de un proyecto (RN5).
     */
    public void cambiarEstadoProyecto(String codigoProyecto, EstadoProyecto nuevoEstado)
            throws RecursoNoEncontradoException, TransicionEstadoInvalidaException, ValidacionNegocioException {

        Proyecto proyecto = buscarProyectoPorCodigo(codigoProyecto);
        EstadoProyecto actual = proyecto.getEstado();

        if (actual == nuevoEstado) {
            return; // Sin cambios
        }

        // Evaluacion estricta de la maquina de estados
        boolean transicionPermitida = false;
        switch (actual) {
            case PENDIENTE:
                if (nuevoEstado == EstadoProyecto.CONFIRMADO) {
                    confirmarProyecto(codigoProyecto);
                    return;
                } else if (nuevoEstado == EstadoProyecto.CANCELADO) {
                    transicionPermitida = true;
                }
                break;
            case CONFIRMADO:
                if (nuevoEstado == EstadoProyecto.EN_CURSO || nuevoEstado == EstadoProyecto.CANCELADO) {
                    transicionPermitida = true;
                }
                break;
            case EN_CURSO:
                if (nuevoEstado == EstadoProyecto.FINALIZADO || nuevoEstado == EstadoProyecto.CANCELADO) {
                    transicionPermitida = true;
                }
                break;
            case FINALIZADO:
            case CANCELADO:
                throw new TransicionEstadoInvalidaException(
                    "El proyecto se encuentra en estado terminal (" + actual + ") y no admite mas transiciones."
                );
        }

        if (!transicionPermitida) {
            throw new TransicionEstadoInvalidaException(
                String.format("Transicion no permitida: no se puede pasar de %s a %s.", actual, nuevoEstado)
            );
        }

        proyecto.setEstado(nuevoEstado);

        // Si finaliza o se cancela, se recalcula la carga y estado de sus desarrolladores
        if (nuevoEstado == EstadoProyecto.FINALIZADO || nuevoEstado == EstadoProyecto.CANCELADO) {
            for (Desarrollador d : proyecto.getDesarrolladoresAsignados()) {
                d.actualizarEstadoSegunCarga();
            }
        }
    }

    // =========================================================================
    // FUNCIONALIDADES ESPECIALES (RN7 y RN8)
    // =========================================================================

    /**
     * RN7: Consulta un cliente por su telefono y determina si dicho numero corresponde
     * a un numero perfecto usando algoritmo eficiente O(sqrt(N)).
     *
     * @param telefono Numero telefonico a consultar.
     * @return true si el numero es perfecto, false en caso contrario.
     * @throws RecursoNoEncontradoException Si no existe un cliente con ese telefono.
     * @throws ValidacionNegocioException Si el telefono no contiene digitos validos.
     */
    public boolean verificarTelefonoClienteNumeroPerfecto(String telefono)
            throws RecursoNoEncontradoException, ValidacionNegocioException {

        Cliente cliente = empresa.buscarClientePorTelefono(telefono);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("No se encontro ningun cliente con el telefono: " + telefono);
        }

        long numero;
        try {
            numero = ValidadorUtil.parsearTelefonoALong(cliente.getTelefono());
        } catch (NumberFormatException e) {
            throw new ValidacionNegocioException("El telefono del cliente no posee un formato numerico valido: " + cliente.getTelefono());
        }

        return ValidadorUtil.esNumeroPerfecto(numero);
    }

    /**
     * RN8: Recorre los proyectos registrados, filtra aquellos cuya fechaSolicitud
     * coincida con la fecha consultada y acumula su valor total.
     *
     * @param fechaConsulta Fecha de solicitud a consultar.
     * @return El total acumulado de ingresos por proyectos solicitados en esa fecha.
     */
    public double calcularIngresosPorFecha(LocalDate fechaConsulta) {
        if (fechaConsulta == null) {
            return 0.0;
        }

        double totalIngresos = 0.0;
        for (Proyecto p : empresa.getProyectos()) {
            if (p.getEstado() != EstadoProyecto.CANCELADO && fechaConsulta.equals(p.getFechaSolicitud())) {
                totalIngresos += p.getValorTotal();
            }
        }
        return totalIngresos;
    }

    // =========================================================================
    // METODOS DE BUSQUEDA AUXILIARES
    // =========================================================================

    public Cliente buscarClientePorIdentificacion(String id) throws RecursoNoEncontradoException {
        Cliente c = empresa.buscarClientePorIdentificacion(id);
        if (c == null) throw new RecursoNoEncontradoException("Cliente no encontrado: " + id);
        return c;
    }

    public Cliente buscarClientePorTelefono(String tel) throws RecursoNoEncontradoException {
        Cliente c = empresa.buscarClientePorTelefono(tel);
        if (c == null) throw new RecursoNoEncontradoException("Cliente con telefono " + tel + " no encontrado.");
        return c;
    }

    public Desarrollador buscarDesarrolladorPorCodigo(String cod) throws RecursoNoEncontradoException {
        Desarrollador d = empresa.buscarDesarrolladorPorCodigo(cod);
        if (d == null) throw new RecursoNoEncontradoException("Desarrollador no encontrado: " + cod);
        return d;
    }

    public Proyecto buscarProyectoPorCodigo(String cod) throws RecursoNoEncontradoException {
        Proyecto p = empresa.buscarProyectoPorCodigo(cod);
        if (p == null) throw new RecursoNoEncontradoException("Proyecto no encontrado: " + cod);
        return p;
    }

    public ServicioAdicional buscarServicioPorCodigo(String cod) throws RecursoNoEncontradoException {
        ServicioAdicional s = empresa.buscarServicioPorCodigo(cod);
        if (s == null) throw new RecursoNoEncontradoException("Servicio adicional no encontrado: " + cod);
        return s;
    }

    public List<Cliente> listarClientes() {
        return empresa.getClientes();
    }

    public List<Desarrollador> listarDesarrolladores() {
        return empresa.getDesarrolladores();
    }

    public List<Proyecto> listarProyectos() {
        return empresa.getProyectos();
    }

    public List<ServicioAdicional> listarServicios() {
        return empresa.getServiciosAdicionales();
    }
}
