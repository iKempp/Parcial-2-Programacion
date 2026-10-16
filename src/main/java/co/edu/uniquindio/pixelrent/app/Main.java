package co.edu.uniquindio.pixelrent.app;

import co.edu.uniquindio.pixelrent.exception.PixelRentException;
import co.edu.uniquindio.pixelrent.model.*;
import co.edu.uniquindio.pixelrent.service.PixelRentService;
import co.edu.uniquindio.pixelrent.util.ConsolaUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada principal y Vista de Consola (CLI) interactiva de PixelRent.
 * Permite probar y evidenciar todas las funcionalidades requeridas en el parcial,
 * iniciando con el sistema limpio y vacio para registrar todo manualmente.
 */
public class Main {

    private static PixelRentService service;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        PixelRent empresa = new PixelRent("PixelRent S.A.S.");
        service = new PixelRentService(empresa);

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = ConsolaUtil.leerEntero(scanner, ">> Seleccione una opcion [0-13]: ", 0, 13);
            System.out.println();

            try {
                switch (opcion) {
                    case 1 -> registrarNuevaSede();
                    case 2 -> registrarNuevoEquipo();
                    case 3 -> registrarNuevoCliente();
                    case 4 -> registrarNuevoOperador();
                    case 5 -> registrarNuevaReserva();
                    case 6 -> agregarEquipoAReserva();
                    case 7 -> asignarOperadorAReserva();
                    case 8 -> confirmarReserva();
                    case 9 -> cambiarEstadoReserva();
                    case 10 -> consultarEquiposDisponibles();
                    case 11 -> consultarReservasConfirmadasCliente();
                    case 12 -> consultarValorTotalReserva();
                    case 13 -> listarInventarioGeneral();
                    case 0 -> System.out.println(">> ¡Gracias por utilizar el Sistema de Gestion PixelRent! Hasta pronto.");
                }
            } catch (PixelRentException e) {
                System.out.println("\n  [ERROR DE NEGOCIO] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n  [ERROR INESPERADO] " + e.getMessage());
            }

            if (opcion != 0) {
                ConsolaUtil.esperarEnter(scanner);
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=======================================================================");
        System.out.println("          PIXELRENT - SISTEMA DE GESTION DE ALQUILER AUDIOVISUAL       ");
        System.out.println("=======================================================================");
        System.out.println(" 1. Registrar nueva Sede");
        System.out.println(" 2. Registrar nuevo Equipo (Camara, Dron o Kit de Iluminacion)");
        System.out.println(" 3. Registrar nuevo Cliente");
        System.out.println(" 4. Registrar nuevo Operador especializado");
        System.out.println(" 5. Registrar nueva Reserva (Inicia en SOLICITADA)");
        System.out.println(" 6. Agregar Equipo a una Reserva");
        System.out.println(" 7. Asignar Operador a una Reserva");
        System.out.println(" 8. Confirmar Reserva (Verifica equipos, disponibilidad y operador)");
        System.out.println(" 9. Cambiar estado de una Reserva (Transiciones controladas)");
        System.out.println(" 10. Consultar Equipos disponibles para una Fecha y Turno determinados");
        System.out.println(" 11. Consultar Reservas CONFIRMADAS por Identificacion de Cliente");
        System.out.println(" 12. Consultar Valor Total y Detalle de una Reserva especifica");
        System.out.println(" 13. Listar inventario general (Sedes, Equipos, Clientes, Operadores, Reservas)");
        System.out.println(" 0. Salir de la aplicacion");
        System.out.println("=======================================================================");
    }

    // =========================================================================
    // CASOS DE USO DEL MENU
    // =========================================================================

    private static void registrarNuevaSede() throws PixelRentException {
        System.out.println(">> REGISTRO DE NUEVA SEDE");
        String codigo = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo unico de la sede (ej: SED-01): ");
        String nombre = ConsolaUtil.leerCadenaNoVacia(scanner, "Nombre de la sede: ");
        String direccion = ConsolaUtil.leerCadenaNoVacia(scanner, "Direccion de la sede: ");

        Sede sede = new Sede(codigo, nombre, direccion);
        service.registrarSede(sede);
        System.out.println("\n  [+] ¡Sede registrada con exito!");
        System.out.println("      " + sede);
    }

    private static void registrarNuevoEquipo() throws PixelRentException {
        System.out.println(">> REGISTRO DE NUEVO EQUIPO AUDIOVISUAL");
        System.out.println("  1. Camara");
        System.out.println("  2. Dron");
        System.out.println("  3. Kit de Iluminacion");
        int tipo = ConsolaUtil.leerEntero(scanner, "Seleccione el tipo de equipo [1-3]: ", 1, 3);

        String codigo = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo unico del equipo: ");
        String marca = ConsolaUtil.leerCadenaNoVacia(scanner, "Marca: ");
        String modelo = ConsolaUtil.leerCadenaNoVacia(scanner, "Modelo: ");

        System.out.println("Condicion inicial:");
        System.out.println("  1. Disponible");
        System.out.println("  2. En mantenimiento");
        System.out.println("  3. Fuera de servicio");
        int condOp = ConsolaUtil.leerEntero(scanner, "Seleccione la condicion [1-3]: ", 1, 3);
        CondicionEquipo condicion = switch (condOp) {
            case 1 -> CondicionEquipo.DISPONIBLE;
            case 2 -> CondicionEquipo.EN_MANTENIMIENTO;
            default -> CondicionEquipo.FUERA_DE_SERVICIO;
        };

        System.out.print("Codigo de sede asignada (dejar vacio si no desea asignar sede): ");
        String codigoSede = scanner.nextLine().trim();

        EquipoAudiovisual nuevoEquipo;
        switch (tipo) {
            case 1 -> {
                String resolucion = ConsolaUtil.leerCadenaNoVacia(scanner, "Resolucion maxima (ej: 4K, 8K): ");
                boolean estabilizador = ConsolaUtil.leerBooleano(scanner, "¿Posee estabilizador? (+ $15.000)");
                nuevoEquipo = new Camara(codigo, marca, modelo, condicion, resolucion, estabilizador);
            }
            case 2 -> {
                int autonomia = ConsolaUtil.leerEntero(scanner, "Autonomia de vuelo (minutos): ", 1, 600);
                boolean bateria = ConsolaUtil.leerBooleano(scanner, "¿Dispone / solicita bateria adicional? (+ $20.000)");
                nuevoEquipo = new Dron(codigo, marca, modelo, condicion, autonomia, bateria);
            }
            default -> {
                int luces = ConsolaUtil.leerEntero(scanner, "Cantidad de luces: ", 1, 100);
                boolean control = ConsolaUtil.leerBooleano(scanner, "¿Cuenta con control inalambrico? (+ $12.000)");
                nuevoEquipo = new KitIluminacion(codigo, marca, modelo, condicion, luces, control);
            }
        }

        service.registrarEquipo(nuevoEquipo, codigoSede.isEmpty() ? null : codigoSede);
        System.out.println("\n  [+] ¡Equipo audiovisual registrado con exito!");
        System.out.println("      " + nuevoEquipo);
    }

    private static void registrarNuevoCliente() throws PixelRentException {
        System.out.println(">> REGISTRO DE NUEVO CLIENTE");
        String id = ConsolaUtil.leerCadenaNoVacia(scanner, "Numero de identificacion (Cedula/NIT): ");
        String nombre = ConsolaUtil.leerCadenaNoVacia(scanner, "Nombre completo: ");
        String telefono = ConsolaUtil.leerCadenaNoVacia(scanner, "Telefono de contacto: ");
        String correo = ConsolaUtil.leerCadenaNoVacia(scanner, "Correo electronico: ");

        Cliente nuevoCliente = new Cliente(id, nombre, telefono, correo);
        service.registrarCliente(nuevoCliente);
        System.out.println("\n  [+] ¡Cliente registrado correctamente! (Informacion guardada e inmutable)");
        System.out.println("      " + nuevoCliente);
    }

    private static void registrarNuevoOperador() throws PixelRentException {
        System.out.println(">> REGISTRO DE NUEVO OPERADOR ESPECIALIZADO");
        String id = ConsolaUtil.leerCadenaNoVacia(scanner, "Numero de identificacion: ");
        String nombre = ConsolaUtil.leerCadenaNoVacia(scanner, "Nombre completo: ");
        String certificacion = ConsolaUtil.leerCadenaNoVacia(scanner, "Certificacion tecnica: ");
        int experiencia = ConsolaUtil.leerEntero(scanner, "Años de experiencia: ", 0, 70);

        Operador nuevoOperador = new Operador(id, nombre, certificacion, experiencia);
        service.registrarOperador(nuevoOperador);
        System.out.println("\n  [+] ¡Operador registrado exitosamente!");
        System.out.println("      " + nuevoOperador);
    }

    private static void registrarNuevaReserva() throws PixelRentException {
        System.out.println(">> REGISTRO DE NUEVA RESERVA");
        String codigo = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo unico para la reserva (ej: RES-101): ");
        String idCliente = ConsolaUtil.leerCadenaNoVacia(scanner, "Identificacion del cliente que reserva: ");
        LocalDate fecha = ConsolaUtil.leerFecha(scanner, "Fecha requerida para los equipos");

        System.out.println("Turnos disponibles:");
        System.out.println("  1. Mañana");
        System.out.println("  2. Tarde");
        System.out.println("  3. Noche");
        int opTurno = ConsolaUtil.leerEntero(scanner, "Seleccione el turno [1-3]: ", 1, 3);
        Turno turno = switch (opTurno) {
            case 1 -> Turno.MANANA;
            case 2 -> Turno.TARDE;
            default -> Turno.NOCHE;
        };

        Reserva reserva = service.registrarReserva(codigo, idCliente, fecha, turno);
        System.out.println("\n  [+] ¡Reserva creada exitosamente en estado SOLICITADA!");
        System.out.println("      " + reserva);
        System.out.println("      (Recuerde agregarle equipos y asignarle operador si incluye drones antes de confirmarla)");
    }

    private static void agregarEquipoAReserva() throws PixelRentException {
        System.out.println(">> AGREGAR EQUIPO A RESERVA");
        String codReserva = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo de la reserva: ");
        String codEquipo = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del equipo a agregar: ");
        int horas = ConsolaUtil.leerEntero(scanner, "Cantidad de horas durante las cuales sera utilizado: ", 1, 720);

        service.agregarEquipoAReserva(codReserva, codEquipo, horas);
        System.out.println("\n  [+] ¡Equipo agregado correctamente a la reserva!");
        double total = service.obtenerValorTotalReserva(codReserva);
        System.out.printf("      Nuevo valor total acumulado de la reserva: $%,.0f%n", total);
    }

    private static void asignarOperadorAReserva() throws PixelRentException {
        System.out.println(">> ASIGNAR OPERADOR A RESERVA");
        String codReserva = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo de la reserva: ");
        String idOperador = ConsolaUtil.leerCadenaNoVacia(scanner, "Identificacion del operador: ");

        service.asignarOperadorAReserva(codReserva, idOperador);
        System.out.println("\n  [+] ¡Operador asignado correctamente a la reserva!");
    }

    private static void confirmarReserva() throws PixelRentException {
        System.out.println(">> CONFIRMAR RESERVA");
        String codReserva = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo de la reserva a confirmar: ");

        service.confirmarReserva(codReserva);
        System.out.println("\n  [+] ¡Reserva confirmada con exito! Se validaron todas las condiciones:");
        System.out.println("      - Contiene al menos un equipo.");
        System.out.println("      - Ninguno de sus equipos colisiona con otra reserva confirmada en la misma fecha y turno.");
        System.out.println("      - Cuenta con operador asignado si incluye un dron.");
        System.out.println("      - La reserva no proviene de estado cancelado o finalizado.");
    }

    private static void cambiarEstadoReserva() throws PixelRentException {
        System.out.println(">> CAMBIAR ESTADO DE UNA RESERVA");
        String codReserva = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo de la reserva: ");

        System.out.println("Seleccione el nuevo estado deseado:");
        System.out.println("  1. CONFIRMADA (aplica validaciones completas)");
        System.out.println("  2. CANCELADA");
        System.out.println("  3. FINALIZADA (solo valida desde CONFIRMADA)");
        int op = ConsolaUtil.leerEntero(scanner, "Opcion [1-3]: ", 1, 3);
        EstadoReserva nuevoEstado = switch (op) {
            case 1 -> EstadoReserva.CONFIRMADA;
            case 2 -> EstadoReserva.CANCELADA;
            default -> EstadoReserva.FINALIZADA;
        };

        service.cambiarEstadoReserva(codReserva, nuevoEstado);
        System.out.println("\n  [+] ¡Estado de la reserva actualizado a: " + nuevoEstado + "!");
    }

    private static void consultarEquiposDisponibles() {
        System.out.println(">> CONSULTAR EQUIPOS DISPONIBLES POR FECHA Y TURNO");
        LocalDate fecha = ConsolaUtil.leerFecha(scanner, "Fecha de consulta");

        System.out.println("Turno:");
        System.out.println("  1. Mañana");
        System.out.println("  2. Tarde");
        System.out.println("  3. Noche");
        int op = ConsolaUtil.leerEntero(scanner, "Seleccione turno [1-3]: ", 1, 3);
        Turno turno = switch (op) {
            case 1 -> Turno.MANANA;
            case 2 -> Turno.TARDE;
            default -> Turno.NOCHE;
        };

        List<EquipoAudiovisual> disponibles = service.consultarEquiposDisponibles(fecha, turno);
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("Equipos disponibles para la fecha %s en el turno %s (%d encontrados):%n",
                fecha, turno, disponibles.size());
        System.out.println("--------------------------------------------------------------------------------");
        if (disponibles.isEmpty()) {
            System.out.println("  No hay equipos disponibles para la fecha y turno seleccionados.");
        } else {
            for (EquipoAudiovisual e : disponibles) {
                System.out.println("  - " + e);
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void consultarReservasConfirmadasCliente() throws PixelRentException {
        System.out.println(">> CONSULTAR RESERVAS CONFIRMADAS POR CLIENTE");
        String idCliente = ConsolaUtil.leerCadenaNoVacia(scanner, "Numero de identificacion del cliente: ");

        List<Reserva> confirmadas = service.consultarReservasConfirmadasPorCliente(idCliente);
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("Reservas CONFIRMADAS para el cliente con ID %s (%d encontradas):%n",
                idCliente, confirmadas.size());
        System.out.println("--------------------------------------------------------------------------------");
        if (confirmadas.isEmpty()) {
            System.out.println("  El cliente no registra ninguna reserva en estado CONFIRMADA.");
        } else {
            for (Reserva r : confirmadas) {
                System.out.println("  - " + r);
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void consultarValorTotalReserva() throws PixelRentException {
        System.out.println(">> CONSULTAR VALOR TOTAL Y DESGLOSE DE UNA RESERVA");
        String codReserva = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo de la reserva: ");

        Reserva reserva = service.getEmpresa().buscarReservaPorCodigo(codReserva);
        if (reserva == null) {
            System.out.println("  [!] No existe una reserva con el codigo: " + codReserva);
            return;
        }

        System.out.println("\n================================================================================");
        System.out.println("                        DETALLE DE RESERVA: " + reserva.getCodigo());
        System.out.println("================================================================================");
        System.out.println("Cliente : " + reserva.getCliente().getNombreCompleto() + " (ID: " + reserva.getCliente().getIdentificacion() + ")");
        System.out.println("Fecha   : " + reserva.getFecha() + " | Turno: " + reserva.getTurno());
        System.out.println("Estado  : " + reserva.getEstado());
        System.out.println("Operador: " + (reserva.getOperador() != null ? reserva.getOperador().getNombreCompleto() + " (" + reserva.getOperador().getCertificacion() + ")" : "Sin operador asignado"));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("EQUIPOS INCLUIDOS:");
        if (reserva.getDetalles().isEmpty()) {
            System.out.println("  (No tiene equipos asignados aun)");
        } else {
            for (DetalleReserva d : reserva.getDetalles()) {
                System.out.println("  * " + d.getEquipo().getTipoEquipo() + " [" + d.getEquipo().getCodigo() + "] "
                        + d.getEquipo().getMarca() + " " + d.getEquipo().getModelo());
                System.out.printf("    Horas uso: %d | Subtotal calculado: $%,.0f%n",
                        d.getHorasUtilizacion(), d.calcularSubtotal());
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("VALOR TOTAL DE LA RESERVA: $%,.0f%n", reserva.calcularValorTotal());
        System.out.println("================================================================================");
    }

    private static void listarInventarioGeneral() {
        PixelRent empresa = service.getEmpresa();
        System.out.println("\n================================================================================");
        System.out.println("              INVENTARIO GENERAL DE PIXELRENT (" + empresa.getNombre() + ")");
        System.out.println("================================================================================");

        System.out.println("\n--- SEDES REGISTRADAS (" + empresa.getSedes().size() + ") ---");
        if (empresa.getSedes().isEmpty()) System.out.println("  (No hay sedes registradas)");
        empresa.getSedes().forEach(s -> System.out.println("  * " + s));

        System.out.println("\n--- CLIENTES REGISTRADOS (" + empresa.getClientes().size() + ") ---");
        if (empresa.getClientes().isEmpty()) System.out.println("  (No hay clientes registrados)");
        empresa.getClientes().forEach(c -> System.out.println("  * " + c));

        System.out.println("\n--- OPERADORES ESPECIALIZADOS (" + empresa.getOperadores().size() + ") ---");
        if (empresa.getOperadores().isEmpty()) System.out.println("  (No hay operadores registrados)");
        empresa.getOperadores().forEach(o -> System.out.println("  * " + o));

        System.out.println("\n--- EQUIPOS AUDIOVISUALES (" + empresa.getEquipos().size() + ") ---");
        if (empresa.getEquipos().isEmpty()) System.out.println("  (No hay equipos registrados)");
        empresa.getEquipos().forEach(e -> System.out.println("  * " + e));

        System.out.println("\n--- RESERVAS REGISTRADAS (" + empresa.getReservas().size() + ") ---");
        if (empresa.getReservas().isEmpty()) System.out.println("  (No hay reservas registradas)");
        empresa.getReservas().forEach(r -> System.out.println("  * " + r));
        System.out.println("================================================================================");
    }
}
