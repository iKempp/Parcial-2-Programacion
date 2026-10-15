package co.edu.uniquindio.pixelrent.model;

import java.util.Objects;

/**
 * Representa la asociacion entre un equipo audiovisual y la cantidad de horas
 * que sera utilizado dentro de una reserva determinada.
 */
public class DetalleReserva {

    private final EquipoAudiovisual equipo;
    private int horasUtilizacion;

    public DetalleReserva(EquipoAudiovisual equipo, int horasUtilizacion) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo audiovisual no puede ser nulo.");
        }
        if (horasUtilizacion <= 0) {
            throw new IllegalArgumentException("La cantidad de horas de utilizacion debe ser mayor a cero.");
        }
        this.equipo = equipo;
        this.horasUtilizacion = horasUtilizacion;
    }

    public EquipoAudiovisual getEquipo() {
        return equipo;
    }

    public int getHorasUtilizacion() {
        return horasUtilizacion;
    }

    public void setHorasUtilizacion(int horasUtilizacion) {
        if (horasUtilizacion <= 0) {
            throw new IllegalArgumentException("La cantidad de horas debe ser mayor a cero.");
        }
        this.horasUtilizacion = horasUtilizacion;
    }

    /**
     * Calcula el subtotal correspondiente a este equipo segun su costo horario
     * y condiciones especificas.
     */
    public double calcularSubtotal() {
        return equipo.calcularCostoUtilizacion(horasUtilizacion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleReserva that = (DetalleReserva) o;
        return Objects.equals(equipo, that.equipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipo);
    }

    @Override
    public String toString() {
        return String.format("%s | Horas: %d | Subtotal: $%,.0f",
                equipo.toString(), horasUtilizacion, calcularSubtotal());
    }
}
