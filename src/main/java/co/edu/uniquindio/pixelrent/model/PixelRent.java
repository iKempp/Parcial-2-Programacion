package co.edu.uniquindio.pixelrent.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad principal de la empresa PixelRent.
 * Centraliza la administracion de sedes, equipos audiovisuales,
 * clientes, operadores tecnicos y reservas.
 */
public class PixelRent {

    private String nombre;
    private final List<Sede> sedes;
    private final List<EquipoAudiovisual> equipos;
    private final List<Cliente> clientes;
    private final List<Operador> operadores;
    private final List<Reserva> reservas;

    public PixelRent(String nombre) {
        this.nombre = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "PixelRent";
        this.sedes = new ArrayList<>();
        this.equipos = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.operadores = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Sede> getSedes() {
        return Collections.unmodifiableList(sedes);
    }

    public List<EquipoAudiovisual> getEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    public List<Operador> getOperadores() {
        return Collections.unmodifiableList(operadores);
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // --- Metodos de busqueda por clave unica ---

    public Sede buscarSedePorCodigo(String codigo) {
        if (codigo == null) return null;
        return sedes.stream()
                .filter(s -> s.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst()
                .orElse(null);
    }

    public EquipoAudiovisual buscarEquipoPorCodigo(String codigo) {
        if (codigo == null) return null;
        return equipos.stream()
                .filter(e -> e.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarClientePorIdentificacion(String identificacion) {
        if (identificacion == null) return null;
        return clientes.stream()
                .filter(c -> c.getIdentificacion().equalsIgnoreCase(identificacion.trim()))
                .findFirst()
                .orElse(null);
    }

    public Operador buscarOperadorPorIdentificacion(String identificacion) {
        if (identificacion == null) return null;
        return operadores.stream()
                .filter(o -> o.getIdentificacion().equalsIgnoreCase(identificacion.trim()))
                .findFirst()
                .orElse(null);
    }

    public Reserva buscarReservaPorCodigo(String codigo) {
        if (codigo == null) return null;
        return reservas.stream()
                .filter(r -> r.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst()
                .orElse(null);
    }

    // --- Metodos directos de registro en memoria ---

    public void agregarSede(Sede sede) {
        if (sede != null && buscarSedePorCodigo(sede.getCodigo()) == null) {
            sedes.add(sede);
        }
    }

    public void agregarEquipo(EquipoAudiovisual equipo) {
        if (equipo != null && buscarEquipoPorCodigo(equipo.getCodigo()) == null) {
            equipos.add(equipo);
            if (equipo.getSede() != null) {
                equipo.getSede().asignarEquipo(equipo);
            }
        }
    }

    public void agregarCliente(Cliente cliente) {
        if (cliente != null && buscarClientePorIdentificacion(cliente.getIdentificacion()) == null) {
            clientes.add(cliente);
        }
    }

    public void agregarOperador(Operador operador) {
        if (operador != null && buscarOperadorPorIdentificacion(operador.getIdentificacion()) == null) {
            operadores.add(operador);
        }
    }

    public void agregarReserva(Reserva reserva) {
        if (reserva != null && buscarReservaPorCodigo(reserva.getCodigo()) == null) {
            reservas.add(reserva);
        }
    }
}
