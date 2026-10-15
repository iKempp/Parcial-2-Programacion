package co.edu.uniquindio.pixelrent.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una sede fisica de PixelRent desde la cual se administran
 * los equipos disponibles y las solicitudes de los clientes.
 */
public class Sede {

    private final String codigo;
    private String nombre;
    private String direccion;
    private final List<EquipoAudiovisual> equiposAsignados;

    public Sede(String codigo, String nombre, String direccion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la sede no puede ser vacio.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre != null ? nombre.trim() : "";
        this.direccion = direccion != null ? direccion.trim() : "";
        this.equiposAsignados = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : "";
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion != null ? direccion.trim() : "";
    }

    public List<EquipoAudiovisual> getEquiposAsignados() {
        return Collections.unmodifiableList(equiposAsignados);
    }

    public void asignarEquipo(EquipoAudiovisual equipo) {
        if (equipo != null && !equiposAsignados.contains(equipo)) {
            equiposAsignados.add(equipo);
            equipo.setSede(this);
        }
    }

    public void removerEquipo(EquipoAudiovisual equipo) {
        if (equipo != null && equiposAsignados.remove(equipo)) {
            if (equipo.getSede() == this) {
                equipo.setSede(null);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sede sede = (Sede) o;
        return Objects.equals(codigo, sede.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("Sede [codigo=%s, nombre=%s, direccion=%s, equipos=%d]",
                codigo, nombre, direccion, equiposAsignados.size());
    }
}
