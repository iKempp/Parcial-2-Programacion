package co.edu.uniquindio.pixelrent.model;

import co.edu.uniquindio.pixelrent.exception.ReglaNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una solicitud o reserva de alquiler de equipos audiovisuales.
 */
public class Reserva {

    private final String codigo;
    private final Cliente cliente;
    private final LocalDate fecha;
    private final Turno turno;
    private EstadoReserva estado;
    private Operador operador;
    private final List<DetalleReserva> detalles;

    public Reserva(String codigo, Cliente cliente, LocalDate fecha, Turno turno) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la reserva no puede ser vacio.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente de la reserva no puede ser nulo.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la reserva no puede ser nula.");
        }
        if (turno == null) {
            throw new IllegalArgumentException("El turno de la reserva no puede ser nulo.");
        }

        this.codigo = codigo.trim();
        this.cliente = cliente;
        this.fecha = fecha;
        this.turno = turno;
        this.estado = EstadoReserva.SOLICITADA; // Toda nueva reserva inicia como solicitada
        this.operador = null;
        this.detalles = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Turno getTurno() {
        return turno;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        if (estado != null) {
            this.estado = estado;
        }
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public List<DetalleReserva> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    /**
     * Agrega un equipo audiovisual a la reserva con sus horas estimadas de uso.
     * Valida que el equipo este disponible y que no se encuentre ya incluido.
     */
    public void agregarEquipo(EquipoAudiovisual equipo, int horas) throws ReglaNegocioException {
        if (equipo == null) {
            throw new ReglaNegocioException("El equipo audiovisual no puede ser nulo.");
        }
        if (horas <= 0) {
            throw new ReglaNegocioException("La cantidad de horas debe ser mayor a cero.");
        }
        if (this.estado != EstadoReserva.SOLICITADA) {
            throw new ReglaNegocioException("Solo se pueden agregar equipos a reservas en estado SOLICITADA.");
        }
        if (!equipo.estaDisponible()) {
            throw new ReglaNegocioException("El equipo '" + equipo.getCodigo() + "' no se encuentra en condiciones de ser utilizado (" + equipo.getCondicion() + ").");
        }
        if (contieneEquipo(equipo.getCodigo())) {
            throw new ReglaNegocioException("El equipo con codigo '" + equipo.getCodigo() + "' ya forma parte de esta reserva.");
        }

        detalles.add(new DetalleReserva(equipo, horas));
    }

    /**
     * Verifica si un equipo ya esta contenido en los detalles de la reserva.
     */
    public boolean contieneEquipo(String codigoEquipo) {
        if (codigoEquipo == null) return false;
        return detalles.stream()
                .anyMatch(d -> d.getEquipo().getCodigo().equalsIgnoreCase(codigoEquipo.trim()));
    }

    /**
     * Determina si la reserva incluye al menos un dron.
     */
    public boolean contieneDron() {
        return detalles.stream()
                .anyMatch(d -> d.getEquipo() instanceof Dron);
    }

    /**
     * Obtiene el valor total de la reserva sumando el subtotal de cada equipo seleccionado.
     */
    public double calcularValorTotal() {
        return detalles.stream()
                .mapToDouble(DetalleReserva::calcularSubtotal)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(codigo, reserva.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("Reserva [codigo=%s, cliente=%s (%s), fecha=%s, turno=%s, estado=%s, operador=%s, equipos=%d, total=$%,.0f]",
                codigo, cliente.getNombreCompleto(), cliente.getIdentificacion(),
                fecha, turno, estado,
                (operador != null ? operador.getNombreCompleto() : "Ninguno"),
                detalles.size(), calcularValorTotal());
    }
}
