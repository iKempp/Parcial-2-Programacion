package co.edu.uniquindio.devplus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad principal de la organizacion DevPlus. Conserva sus datos corporativos
 * y centraliza los catalogos maestros de clientes, desarrolladores, servicios adicionales
 * y proyectos gestionados.
 */
public class Empresa {

    private String nombreComercial;
    private String nit;
    private String direccion;
    private String telefono;
    private String paginaWeb;

    private final List<Cliente> clientes;
    private final List<Desarrollador> desarrolladores;
    private final List<Proyecto> proyectos;
    private final List<ServicioAdicional> serviciosAdicionales;

    public Empresa(String nombreComercial, String nit, String direccion, String telefono, String paginaWeb) {
        this.nombreComercial = nombreComercial;
        this.nit = nit;
        this.direccion = direccion;
        this.telefono = telefono;
        this.paginaWeb = paginaWeb;
        this.clientes = new ArrayList<>();
        this.desarrolladores = new ArrayList<>();
        this.proyectos = new ArrayList<>();
        this.serviciosAdicionales = new ArrayList<>();
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    public List<Desarrollador> getDesarrolladores() {
        return Collections.unmodifiableList(desarrolladores);
    }

    public List<Proyecto> getProyectos() {
        return Collections.unmodifiableList(proyectos);
    }

    public List<ServicioAdicional> getServiciosAdicionales() {
        return Collections.unmodifiableList(serviciosAdicionales);
    }

    // --- Metodos de administracion de catalogos en memoria ---

    public void registrarCliente(Cliente cliente) {
        if (cliente != null && buscarClientePorIdentificacion(cliente.getIdentificacion()) == null) {
            clientes.add(cliente);
        }
    }

    public void registrarDesarrollador(Desarrollador desarrollador) {
        if (desarrollador != null && buscarDesarrolladorPorCodigo(desarrollador.getCodigo()) == null) {
            desarrolladores.add(desarrollador);
        }
    }

    public void registrarProyecto(Proyecto proyecto) {
        if (proyecto != null && buscarProyectoPorCodigo(proyecto.getCodigo()) == null) {
            proyectos.add(proyecto);
        }
    }

    public void registrarServicioAdicional(ServicioAdicional servicio) {
        if (servicio != null && buscarServicioPorCodigo(servicio.getCodigo()) == null) {
            serviciosAdicionales.add(servicio);
        }
    }

    // --- Metodos de busqueda unívoca ---

    public Cliente buscarClientePorIdentificacion(String identificacion) {
        if (identificacion == null) return null;
        for (Cliente c : clientes) {
            if (c.getIdentificacion().equalsIgnoreCase(identificacion.trim())) {
                return c;
            }
        }
        return null;
    }

    public Cliente buscarClientePorTelefono(String telefono) {
        if (telefono == null) return null;
        String telLimpio = telefono.replaceAll("\\D", "");
        for (Cliente c : clientes) {
            String cTelLimpio = c.getTelefono().replaceAll("\\D", "");
            if (cTelLimpio.equals(telLimpio)) {
                return c;
            }
        }
        return null;
    }

    public Desarrollador buscarDesarrolladorPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (Desarrollador d : desarrolladores) {
            if (d.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return d;
            }
        }
        return null;
    }

    public Proyecto buscarProyectoPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (Proyecto p : proyectos) {
            if (p.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return p;
            }
        }
        return null;
    }

    public ServicioAdicional buscarServicioPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (ServicioAdicional s : serviciosAdicionales) {
            if (s.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format(
            "Empresa: %s | NIT: %s | Direccion: %s | Tel: %s | Web: %s\n" +
            "Resumen: %d Clientes, %d Desarrolladores, %d Servicios, %d Proyectos Registrados",
            nombreComercial, nit, direccion, telefono, paginaWeb,
            clientes.size(), desarrolladores.size(), serviciosAdicionales.size(), proyectos.size()
        );
    }
}
