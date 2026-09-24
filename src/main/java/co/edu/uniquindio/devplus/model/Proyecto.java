package co.edu.uniquindio.devplus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad central que representa un proyecto de desarrollo informatico a la medida
 * contratado por un cliente en la empresa DevPlus.
 */
public class Proyecto {

    private String codigo;
    private LocalDate fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaEntrega;
    private EstadoProyecto estado;
    private MetodoPago metodoPago;
    private double valorTotal;
    private Cliente contratante;
    private final List<Desarrollador> desarrolladoresAsignados;
    private final List<ServicioAdicional> serviciosUtilizados;

    public Proyecto(String codigo, LocalDate fechaSolicitud, LocalDate fechaInicio,
                    LocalDate fechaEntrega, MetodoPago metodoPago, Cliente contratante) {
        this.codigo = codigo;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaEntrega = fechaEntrega;
        this.estado = EstadoProyecto.PENDIENTE;
        this.metodoPago = metodoPago;
        this.contratante = contratante;
        this.valorTotal = 0.0;
        this.desarrolladoresAsignados = new ArrayList<>();
        this.serviciosUtilizados = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Cliente getContratante() {
        return contratante;
    }

    public void setContratante(Cliente contratante) {
        this.contratante = contratante;
    }

    public List<Desarrollador> getDesarrolladoresAsignados() {
        return Collections.unmodifiableList(desarrolladoresAsignados);
    }

    public List<ServicioAdicional> getServiciosUtilizados() {
        return Collections.unmodifiableList(serviciosUtilizados);
    }

    /**
     * Calcula la duracion neta en dias del proyecto segun RN2.
     * Si la fecha de entrega es el mismo dia de inicio, se computa como 1 dia de trabajo.
     * @return Cantidad de dias de desarrollo.
     */
    public long calcularDiasDesarrollo() {
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaEntrega);
        return dias <= 0 ? 1 : dias;
    }

    /**
     * Agrega un desarrollador al equipo de trabajo del proyecto.
     * @param dev Desarrollador a vincular.
     */
    public void agregarDesarrollador(Desarrollador dev) {
        if (dev != null && !desarrolladoresAsignados.contains(dev)) {
            desarrolladoresAsignados.add(dev);
        }
    }

    /**
     * Agrega un servicio adicional complementario al proyecto segun RN6.
     * @param servicio Servicio a vincular.
     */
    public void agregarServicio(ServicioAdicional servicio) {
        if (servicio != null && !serviciosUtilizados.contains(servicio)) {
            serviciosUtilizados.add(servicio);
        }
    }

    /**
     * Liquida el valor total del proyecto con base en las reglas de negocio RN1 y RN2:
     * valorTotal = (suma de tarifas/dia * dias) + suma(servicios) - descuento por cliente frecuente (10%).
     * @return El valor total liquidado.
     */
    public double recalcularValorTotal() {
        long dias = calcularDiasDesarrollo();

        double sumaTarifas = 0.0;
        for (Desarrollador d : desarrolladoresAsignados) {
            sumaTarifas += d.getTarifaPorDia();
        }
        double costoDesarrollo = sumaTarifas * dias;

        double costoServicios = 0.0;
        for (ServicioAdicional s : serviciosUtilizados) {
            costoServicios += s.getPrecio();
        }

        double subtotal = costoDesarrollo + costoServicios;

        double descuento = 0.0;
        if (contratante != null && contratante.esClienteFrecuente()) {
            descuento = subtotal * 0.10; // 10% de descuento por cliente frecuente
        }

        this.valorTotal = subtotal - descuento;
        return this.valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(codigo, proyecto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format(
            "Proyecto [%s | Solicitud: %s | Periodo: %s a %s (%d dias) | Estado: %s | Pago: %s | Cliente: %s | Devs: %d | Servicios: %d | Total: $%,.2f]",
            codigo, fechaSolicitud, fechaInicio, fechaEntrega, calcularDiasDesarrollo(),
            estado, metodoPago, (contratante != null ? contratante.getNombreCompleto() : "N/A"),
            desarrolladoresAsignados.size(), serviciosUtilizados.size(), valorTotal
        );
    }
}
