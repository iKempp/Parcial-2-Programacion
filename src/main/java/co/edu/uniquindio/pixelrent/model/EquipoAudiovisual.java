package co.edu.uniquindio.pixelrent.model;

import java.util.Objects;

/**
 * Clase base abstracta para todos los equipos audiovisuales registrados en PixelRent.
 */
public abstract class EquipoAudiovisual {

    private final String codigo;
    private String marca;
    private String modelo;
    private CondicionEquipo condicion;
    private Sede sede;

    public EquipoAudiovisual(String codigo, String marca, String modelo, CondicionEquipo condicion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del equipo no puede ser vacio.");
        }
        this.codigo = codigo.trim();
        this.marca = marca != null ? marca.trim() : "";
        this.modelo = modelo != null ? modelo.trim() : "";
        this.condicion = condicion != null ? condicion : CondicionEquipo.DISPONIBLE;
    }

    public EquipoAudiovisual(String codigo, String marca, String modelo, CondicionEquipo condicion, Sede sede) {
        this(codigo, marca, modelo, condicion);
        this.sede = sede;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca != null ? marca.trim() : "";
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo != null ? modelo.trim() : "";
    }

    public CondicionEquipo getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionEquipo condicion) {
        if (condicion != null) {
            this.condicion = condicion;
        }
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public boolean estaDisponible() {
        return this.condicion == CondicionEquipo.DISPONIBLE;
    }

    /**
     * Calcula el costo de utilizacion del equipo segun la cantidad de horas
     * y las condiciones particulares que aplican a cada tipo de dispositivo.
     *
     * @param horas cantidad de horas de uso (debe ser mayor a 0)
     * @return costo total de utilizacion del equipo
     */
    public abstract double calcularCostoUtilizacion(int horas);

    /**
     * Retorna una descripcion breve del tipo especifico de equipo audiovisual.
     */
    public abstract String getTipoEquipo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipoAudiovisual that = (EquipoAudiovisual) o;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("%s [codigo=%s, marca=%s, modelo=%s, condicion=%s, sede=%s]",
                getTipoEquipo(), codigo, marca, modelo, condicion, (sede != null ? sede.getNombre() : "Sin Sede"));
    }
}
