package co.edu.uniquindio.devplus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad que representa a un cliente (persona natural o juridica) que contrata
 * proyectos de desarrollo de software en DevPlus.
 */
public class Cliente {

    private String identificacion;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private String paisProcedencia;
    private final List<Proyecto> proyectosContratados;

    public Cliente(String identificacion, String nombreCompleto, String telefono,
                   String correoElectronico, String paisProcedencia) {
        this.identificacion = identificacion;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.paisProcedencia = paisProcedencia;
        this.proyectosContratados = new ArrayList<>();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getPaisProcedencia() {
        return paisProcedencia;
    }

    public void setPaisProcedencia(String paisProcedencia) {
        this.paisProcedencia = paisProcedencia;
    }

    public List<Proyecto> getProyectosContratados() {
        return Collections.unmodifiableList(proyectosContratados);
    }

    /**
     * Asocia un nuevo proyecto a la lista historica de proyectos contratados por el cliente.
     * @param proyecto Proyecto a asociar.
     */
    public void agregarProyecto(Proyecto proyecto) {
        if (proyecto != null && !proyectosContratados.contains(proyecto)) {
            proyectosContratados.add(proyecto);
        }
    }

    /**
     * Determina si el cliente califica como frecuente segun la regla de negocio RN1:
     * contar con 3 o mas proyectos previos contratados.
     * @return true si tiene 3 o mas proyectos contratados, false en caso contrario.
     */
    public boolean esClienteFrecuente() {
        return proyectosContratados.size() >= 3;
    }

    @Override
    public String toString() {
        return String.format(
            "Cliente [ID/NIT: %s | Nombre: %s | Tel: %s | Email: %s | Pais: %s | Proyectos: %d | Frecuente: %s]",
            identificacion, nombreCompleto, telefono, correoElectronico, paisProcedencia,
            proyectosContratados.size(), esClienteFrecuente() ? "SI" : "NO"
        );
    }
}
