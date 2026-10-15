package co.edu.uniquindio.pixelrent.service;

import co.edu.uniquindio.pixelrent.exception.ElementoDuplicadoException;
import co.edu.uniquindio.pixelrent.exception.ElementoNoEncontradoException;
import co.edu.uniquindio.pixelrent.exception.ReglaNegocioException;
import co.edu.uniquindio.pixelrent.exception.TransicionEstadoInvalidaException;
import co.edu.uniquindio.pixelrent.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fachada del servicio y orquestador central de la logica de negocio de PixelRent.
 * Aplica todas las validaciones e invariantes requeridas por el negocio.
 */
public class PixelRentService {

    private final PixelRent empresa;

    public PixelRentService(PixelRent empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("La entidad PixelRent no puede ser nula.");
        }
        this.empresa = empresa;
    }

    public PixelRent getEmpresa() {
        return empresa;
    }

    // =========================================================================
    // 1. REGISTRO DE ENTIDADES MAESTRAS (Sedes, Clientes, Operadores, Equipos)
    // =========================================================================

    public void registrarSede(Sede sede) throws ElementoDuplicadoException, ReglaNegocioException {
        if (sede == null) {
            throw new ReglaNegocioException("La informacion de la sede no puede ser nula.");
        }
        if (empresa.buscarSedePorCodigo(sede.getCodigo()) != null) {
            throw new ElementoDuplicadoException("Ya existe una sede registrada con el codigo: " + sede.getCodigo());
        }
        empresa.agregarSede(sede);
    }

    public void registrarCliente(Cliente cliente) throws ElementoDuplicadoException, ReglaNegocioException {
        if (cliente == null) {
            throw new ReglaNegocioException("La informacion del cliente no puede ser nula.");
        }
        if (empresa.buscarClientePorIdentificacion(cliente.getIdentificacion()) != null) {
            throw new ElementoDuplicadoException("Ya existe un cliente con el numero de identificacion: " + cliente.getIdentificacion());
        }
        empresa.agregarCliente(cliente);
    }

    public void registrarOperador(Operador operador) throws ElementoDuplicadoException, ReglaNegocioException {
        if (operador == null) {
            throw new ReglaNegocioException("La informacion del operador no puede ser nula.");
        }
        if (empresa.buscarOperadorPorIdentificacion(operador.getIdentificacion()) != null) {
            throw new ElementoDuplicadoException("Ya existe un operador con el numero de identificacion: " + operador.getIdentificacion());
        }
        empresa.agregarOperador(operador);
    }

    public void registrarEquipo(EquipoAudiovisual equipo, String codigoSede)
            throws ElementoDuplicadoException, ElementoNoEncontradoException, ReglaNegocioException {
        if (equipo == null) {
            throw new ReglaNegocioException("La informacion del equipo no puede ser nula.");
        }
        if (empresa.buscarEquipoPorCodigo(equipo.getCodigo()) != null) {
            throw new ElementoDuplicadoException("No se permite registrar dos equipos con el mismo codigo: " + equipo.getCodigo());
        }

        if (codigoSede != null && !codigoSede.trim().isEmpty()) {
            Sede sede = empresa.buscarSedePorCodigo(codigoSede);
            if (sede == null) {
                throw new ElementoNoEncontradoException("No se encontro la sede con codigo: " + codigoSede);
            }
            equipo.setSede(sede);
            sede.asignarEquipo(equipo);
        }

        empresa.agregarEquipo(equipo);
    }

    // =========================================================================
    // 2. GESTION DE RESERVAS
    // =========================================================================

    /**
     * Registra una nueva reserva en el sistema iniciando en estado SOLICITADA.
     * La fecha y el turno quedan fijados y no deben ser modificados posteriormente.
     */
    public Reserva registrarReserva(String codigo, String idCliente, LocalDate fecha, Turno turno)
            throws ElementoDuplicadoException, ElementoNoEncontradoException, ReglaNegocioException {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El codigo de la reserva no puede ser vacio.");
        }
        if (empresa.buscarReservaPorCodigo(codigo) != null) {
            throw new ElementoDuplicadoException("No se permite registrar dos reservas con el mismo codigo: " + codigo);
        }
        if (fecha == null) {
            throw new ReglaNegocioException("La fecha de utilizacion de la reserva no puede ser nula.");
        }
        if (turno == null) {
            throw new ReglaNegocioException("El turno de la reserva no puede ser nulo.");
        }

        Cliente cliente = empresa.buscarClientePorIdentificacion(idCliente);
        if (cliente == null) {
            throw new ElementoNoEncontradoException("No existe un cliente registrado con la identificacion: " + idCliente);
        }

        Reserva nuevaReserva = new Reserva(codigo, cliente, fecha, turno);
        empresa.agregarReserva(nuevaReserva);
        return nuevaReserva;
    }

    /**
     * Agrega un equipo audiovisual a una reserva existente en estado SOLICITADA.
     */
    public void agregarEquipoAReserva(String codigoReserva, String codigoEquipo, int horas)
            throws ElementoNoEncontradoException, ReglaNegocioException {

        Reserva reserva = buscarReservaOError(codigoReserva);
        EquipoAudiovisual equipo = empresa.buscarEquipoPorCodigo(codigoEquipo);
        if (equipo == null) {
            throw new ElementoNoEncontradoException("No existe un equipo audiovisual con el codigo: " + codigoEquipo);
        }

        // Delega las validaciones de estado, disponibilidad y no duplicidad a la reserva
        reserva.agregarEquipo(equipo, horas);
    }

    /**
     * Asigna un operador tecnico a una reserva.
     */
    public void asignarOperadorAReserva(String codigoReserva, String idOperador)
            throws ElementoNoEncontradoException, ReglaNegocioException {

        Reserva reserva = buscarReservaOError(codigoReserva);
        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("No se puede asignar un operador a una reserva que ya ha sido cancelada o finalizada.");
        }

        Operador operador = empresa.buscarOperadorPorIdentificacion(idOperador);
        if (operador == null) {
            throw new ElementoNoEncontradoException("No existe un operador con la identificacion: " + idOperador);
        }

        reserva.setOperador(operador);
    }

    // =========================================================================
    // 3. CAMBIO DE ESTADO Y VALIDACIONES DE CONFIRMACION
    // =========================================================================

    /**
     * Confirma una reserva verificando rigurosamente todas las reglas de negocio:
     * 1. Que contenga al menos un equipo.
     * 2. Que los equipos puedan ser utilizados en la fecha y turno solicitados (sin conflicto con otra confirmada).
     * 3. Que si incluye un dron, cuente con un operador asignado.
     * 4. Que no provenga de un estado cancelado o finalizado.
     */
    public void confirmarReserva(String codigoReserva)
            throws ElementoNoEncontradoException, ReglaNegocioException, TransicionEstadoInvalidaException {

        Reserva reserva = buscarReservaOError(codigoReserva);

        // Regla 4: Las reservas que hayan sido canceladas o finalizadas no podran regresar a confirmadas
        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new TransicionEstadoInvalidaException(
                    "Una reserva en estado " + reserva.getEstado() + " no puede regresar al estado CONFIRMADA.");
        }

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("La reserva ya se encuentra en estado CONFIRMADA.");
        }

        // Regla 1: Debe contener al menos un equipo
        if (reserva.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("La reserva no puede ser confirmada porque no contiene ningun equipo.");
        }

        // Regla 2: Un equipo que ya forme parte de otra reserva confirmada para la misma fecha y turno no podra ser utilizado
        for (DetalleReserva detalle : reserva.getDetalles()) {
            EquipoAudiovisual equipo = detalle.getEquipo();
            if (estaEquipoEnOtraReservaConfirmada(equipo.getCodigo(), reserva.getFecha(), reserva.getTurno(), reserva.getCodigo())) {
                throw new ReglaNegocioException("El equipo '" + equipo.getCodigo() + " (" + equipo.getMarca() + " " + equipo.getModelo() +
                        ")' ya se encuentra reservado y confirmado para la fecha " + reserva.getFecha() + " y turno " + reserva.getTurno() + ".");
            }
        }

        // Regla 3: Si contiene un dron, requiere obligatoriamente un operador asignado
        if (reserva.contieneDron() && reserva.getOperador() == null) {
            throw new ReglaNegocioException("La reserva incluye un dron y requiere obligatoriamente tener asignado un operador capacitado para ser confirmada.");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
    }

    /**
     * Cancela una reserva (permitido desde SOLICITADA o CONFIRMADA).
     */
    public void cancelarReserva(String codigoReserva)
            throws ElementoNoEncontradoException, TransicionEstadoInvalidaException {

        Reserva reserva = buscarReservaOError(codigoReserva);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new TransicionEstadoInvalidaException("La reserva ya se encuentra CANCELADA.");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new TransicionEstadoInvalidaException("No es posible cancelar una reserva que ya fue FINALIZADA.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
    }

    /**
     * Finaliza una reserva (solo permitido desde CONFIRMADA).
     */
    public void finalizarReserva(String codigoReserva)
            throws ElementoNoEncontradoException, TransicionEstadoInvalidaException {

        Reserva reserva = buscarReservaOError(codigoReserva);
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede finalizar una reserva que se encuentre CONFIRMADA. Estado actual: " + reserva.getEstado());
        }

        reserva.setEstado(EstadoReserva.FINALIZADA);
    }

    /**
     * Cambia el estado de una reserva aplicando las reglas de transicion permitidas.
     */
    public void cambiarEstadoReserva(String codigoReserva, EstadoReserva nuevoEstado)
            throws ElementoNoEncontradoException, ReglaNegocioException, TransicionEstadoInvalidaException {

        if (nuevoEstado == null) {
            throw new ReglaNegocioException("El nuevo estado no puede ser nulo.");
        }

        switch (nuevoEstado) {
            case CONFIRMADA -> confirmarReserva(codigoReserva);
            case CANCELADA -> cancelarReserva(codigoReserva);
            case FINALIZADA -> finalizarReserva(codigoReserva);
            case SOLICITADA -> throw new TransicionEstadoInvalidaException("Una reserva no puede retornar al estado SOLICITADA.");
        }
    }

    // =========================================================================
    // 4. CONSULTAS ADMINISTRATIVAS REQUERIDAS
    // =========================================================================

    /**
     * Conoce cuales equipos se encuentran disponibles para una fecha y turno determinados.
     * Un equipo esta disponible si su condicion operativa es DISPONIBLE y no esta
     * asignado a ninguna otra reserva confirmada para la misma fecha y turno.
     */
    public List<EquipoAudiovisual> consultarEquiposDisponibles(LocalDate fecha, Turno turno) {
        if (fecha == null || turno == null) {
            return new ArrayList<>();
        }

        return empresa.getEquipos().stream()
                .filter(EquipoAudiovisual::estaDisponible)
                .filter(e -> !estaEquipoEnOtraReservaConfirmada(e.getCodigo(), fecha, turno, null))
                .collect(Collectors.toList());
    }

    /**
     * Consulta las reservas confirmadas realizadas por un cliente a partir de su identificacion.
     */
    public List<Reserva> consultarReservasConfirmadasPorCliente(String idCliente)
            throws ElementoNoEncontradoException {

        Cliente cliente = empresa.buscarClientePorIdentificacion(idCliente);
        if (cliente == null) {
            throw new ElementoNoEncontradoException("No existe un cliente registrado con la identificacion: " + idCliente);
        }

        return empresa.getReservas().stream()
                .filter(r -> r.getCliente().getIdentificacion().equalsIgnoreCase(idCliente.trim()))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el valor total de una reserva especifica.
     */
    public double obtenerValorTotalReserva(String codigoReserva) throws ElementoNoEncontradoException {
        Reserva reserva = buscarReservaOError(codigoReserva);
        return reserva.calcularValorTotal();
    }

    // =========================================================================
    // METODOS AUXILIARES
    // =========================================================================

    private Reserva buscarReservaOError(String codigoReserva) throws ElementoNoEncontradoException {
        if (codigoReserva == null) {
            throw new ElementoNoEncontradoException("El codigo de reserva no puede ser nulo.");
        }
        Reserva reserva = empresa.buscarReservaPorCodigo(codigoReserva);
        if (reserva == null) {
            throw new ElementoNoEncontradoException("No existe una reserva registrada con el codigo: " + codigoReserva);
        }
        return reserva;
    }

    private boolean estaEquipoEnOtraReservaConfirmada(String codigoEquipo, LocalDate fecha, Turno turno, String codigoReservaExcluida) {
        return empresa.getReservas().stream()
                .filter(r -> codigoReservaExcluida == null || !r.getCodigo().equalsIgnoreCase(codigoReservaExcluida))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> r.getFecha().equals(fecha) && r.getTurno() == turno)
                .anyMatch(r -> r.contieneEquipo(codigoEquipo));
    }
}
