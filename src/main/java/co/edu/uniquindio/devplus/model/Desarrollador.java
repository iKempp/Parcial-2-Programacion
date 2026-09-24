package co.edu.uniquindio.devplus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa a un profesional de desarrollo de software administrado por DevPlus.
 * Controla su disponibilidad, carga operativa y concurrencia de proyectos asignados.
 */
public class Desarrollador {

    private String codigo;
    private String equipoDeTrabajo;
    private NivelDesarrollador nivel;
    private int cantidadMaximaProyectosSimultaneos;
    private double tarifaPorDia;
    private EstadoDesarrollador estado;
    private final List<Proyecto> proyectosAsignados;

    public Desarrollador(String codigo, String equipoDeTrabajo, NivelDesarrollador nivel,
                         int cantidadMaximaProyectosSimultaneos, double tarifaPorDia,
                         EstadoDesarrollador estado) {
        this.codigo = codigo;
        this.equipoDeTrabajo = equipoDeTrabajo;
        this.nivel = nivel;
        this.cantidadMaximaProyectosSimultaneos = cantidadMaximaProyectosSimultaneos;
        this.tarifaPorDia = tarifaPorDia;
        this.estado = estado;
        this.proyectosAsignados = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEquipoDeTrabajo() {
        return equipoDeTrabajo;
    }

    public void setEquipoDeTrabajo(String equipoDeTrabajo) {
        this.equipoDeTrabajo = equipoDeTrabajo;
    }

    public NivelDesarrollador getNivel() {
        return nivel;
    }

    public void setNivel(NivelDesarrollador nivel) {
        this.nivel = nivel;
    }

    public int getCantidadMaximaProyectosSimultaneos() {
        return cantidadMaximaProyectosSimultaneos;
    }

    public void setCantidadMaximaProyectosSimultaneos(int cantidadMaximaProyectosSimultaneos) {
        this.cantidadMaximaProyectosSimultaneos = cantidadMaximaProyectosSimultaneos;
    }

    public double getTarifaPorDia() {
        return tarifaPorDia;
    }

    public void setTarifaPorDia(double tarifaPorDia) {
        this.tarifaPorDia = tarifaPorDia;
    }

    public EstadoDesarrollador getEstado() {
        return estado;
    }

    public void setEstado(EstadoDesarrollador estado) {
        this.estado = estado;
    }

    public List<Proyecto> getProyectosAsignados() {
        return Collections.unmodifiableList(proyectosAsignados);
    }

    /**
     * Vincula un proyecto a la agenda del desarrollador.
     * @param proyecto Proyecto a vincular.
     */
    public void agregarProyecto(Proyecto proyecto) {
        if (proyecto != null && !proyectosAsignados.contains(proyecto)) {
            proyectosAsignados.add(proyecto);
        }
    }

    /**
     * Cuenta cuantos proyectos activos del desarrollador se cruzan o solapan
     * en el intervalo de fechas especificado.
     * Dos intervalos [A_ini, A_fin] y [B_ini, B_fin] se solapan si:
     * !(A_fin < B_ini || A_ini > B_fin)
     *
     * @param fechaInicio Fecha inicial del rango a evaluar.
     * @param fechaEntrega Fecha final del rango a evaluar.
     * @return Cantidad de proyectos concurrentes en conflicto temporal.
     */
    public int contarProyectosEnRango(LocalDate fechaInicio, LocalDate fechaEntrega) {
        int conteo = 0;
        for (Proyecto p : proyectosAsignados) {
            // Solo se consideran proyectos vigentes (no finalizados ni cancelados)
            if (p.getEstado() != EstadoProyecto.FINALIZADO && p.getEstado() != EstadoProyecto.CANCELADO) {
                boolean noHayCruce = p.getFechaEntrega().isBefore(fechaInicio) || p.getFechaInicio().isAfter(fechaEntrega);
                if (!noHayCruce) {
                    conteo++;
                }
            }
        }
        return conteo;
    }

    /**
     * Valida si el desarrollador tiene capacidad disponible para asumir un nuevo proyecto
     * en el intervalo de fechas dado sin exceder su tope maximo de concurrencia.
     *
     * @param fechaInicio Fecha inicial del nuevo proyecto.
     * @param fechaEntrega Fecha final del nuevo proyecto.
     * @return true si el desarrollador puede asumir el proyecto, false en caso contrario.
     */
    public boolean tieneCapacidadEnFechas(LocalDate fechaInicio, LocalDate fechaEntrega) {
        if (this.estado == EstadoDesarrollador.EN_CAPACITACION) {
            return false;
        }
        int proyectosSolapados = contarProyectosEnRango(fechaInicio, fechaEntrega);
        return proyectosSolapados < this.cantidadMaximaProyectosSimultaneos;
    }

    /**
     * Actualiza dinamicamente el estado operativo del desarrollador segun la carga de proyectos activos.
     */
    public void actualizarEstadoSegunCarga() {
        if (this.estado == EstadoDesarrollador.EN_CAPACITACION) {
            return; // Se respeta su estado de capacitacion
        }

        int proyectosActivos = 0;
        for (Proyecto p : proyectosAsignados) {
            if (p.getEstado() == EstadoProyecto.CONFIRMADO || p.getEstado() == EstadoProyecto.EN_CURSO) {
                proyectosActivos++;
            }
        }

        if (proyectosActivos == 0) {
            this.estado = EstadoDesarrollador.DISPONIBLE;
        } else if (proyectosActivos >= this.cantidadMaximaProyectosSimultaneos) {
            this.estado = EstadoDesarrollador.OCUPADO;
        } else {
            this.estado = EstadoDesarrollador.ASIGNADO;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Desarrollador that = (Desarrollador) o;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format(
            "Desarrollador [%s - Nivel: %s | Equipo: %s | Tarifa/dia: $%,.2f | MaxSimultaneos: %d | Estado: %s | ProyectosAsignados: %d]",
            codigo, nivel, equipoDeTrabajo, tarifaPorDia, cantidadMaximaProyectosSimultaneos, estado, proyectosAsignados.size()
        );
    }
}
