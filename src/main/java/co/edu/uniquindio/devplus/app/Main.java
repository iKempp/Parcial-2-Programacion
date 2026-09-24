package co.edu.uniquindio.devplus.app;

import co.edu.uniquindio.devplus.exception.*;
import co.edu.uniquindio.devplus.model.*;
import co.edu.uniquindio.devplus.gui.VentanaPrincipal;
import co.edu.uniquindio.devplus.service.DevPlusService;
import co.edu.uniquindio.devplus.util.ConsolaUtil;
import co.edu.uniquindio.devplus.util.DatosPruebaUtil;
import co.edu.uniquindio.devplus.util.ValidadorUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada principal y Vista de Consola (CLI) del sistema DevPlus.
 * Contiene el menu interactivo y delega toda la logica de negocio a DevPlusService.
 */
public class Main {

    private static DevPlusService service;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        inicializarSistemaConDatosPrueba();

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = ConsolaUtil.leerEntero(scanner, "Seleccione una opcion [0-13]: ", 0, 13);
            System.out.println();

            try {
                switch (opcion) {
                    case 1 -> menuClientes();
                    case 2 -> menuDesarrolladores();
                    case 3 -> menuServicios();
                    case 4 -> crearProyecto();
                    case 5 -> agregarDesarrolladorAProyecto();
                    case 6 -> agregarServicioAProyecto();
                    case 7 -> calcularValorTotalProyecto();
                    case 8 -> confirmarProyecto();
                    case 9 -> cambiarEstadoProyecto();
                    case 10 -> consultarTelefonoNumeroPerfecto();
                    case 11 -> consultarIngresosPorFecha();
                    case 12 -> mostrarResumenEmpresa();
                    case 13 -> abrirInterfazGrafica();
                    case 0 -> System.out.println(">> Gracias por utilizar el sistema DevPlus. ¡Hasta pronto!");
                }
            } catch (RecursoNoEncontradoException | DesarrolladorNoDisponibleException |
                     ServicioNoDisponibleException | TransicionEstadoInvalidaException |
                     ValidacionNegocioException e) {
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
        System.out.println("          DEVPLUS SOFTWARE - SISTEMA DE GESTION DE PROYECTOS          ");
        System.out.println("=======================================================================");
        System.out.println(" 1. Gestion de Clientes (Registrar / Listar)");
        System.out.println(" 2. Gestion de Desarrolladores (Registrar / Listar)");
        System.out.println(" 3. Gestion de Servicios Adicionales (Registrar / Listar)");
        System.out.println(" 4. Crear nuevo proyecto para un cliente");
        System.out.println(" 5. Asignar desarrollador a un proyecto (Valida disponibilidad RN3)");
        System.out.println(" 6. Agregar servicio adicional a un proyecto (Valida disponibilidad RN6)");
        System.out.println(" 7. Liquidar / Consultar valor total de un proyecto (RN1 y RN2)");
        System.out.println(" 8. Confirmar proyecto (Actualiza disponibilidad de desarrolladores RN4)");
        System.out.println(" 9. Cambiar estado de un proyecto (Maquina de estados RN5)");
        System.out.println(" 10. Consultar cliente por telefono y verificar NUMERO PERFECTO (RN7)");
        System.out.println(" 11. Consultar total de ingresos por fecha de solicitud (RN8)");
        System.out.println(" 12. Mostrar informacion institucional de DevPlus y resumen general");
        System.out.println(" 13. [NUEVO] Abrir Interfaz Grafica de Usuario (Ventana Swing)");
        System.out.println(" 0. Salir de la aplicacion");
        System.out.println("=======================================================================");
    }

    // =========================================================================
    // SUBMENUS Y OPCIONES DE INTERACCION
    // =========================================================================

    private static void menuClientes() throws ValidacionNegocioException {
        System.out.println("--- GESTION DE CLIENTES ---");
        System.out.println("1. Listar todos los clientes");
        System.out.println("2. Registrar un nuevo cliente");
        int subOpcion = ConsolaUtil.leerEntero(scanner, "Opcion [1-2]: ", 1, 2);

        if (subOpcion == 1) {
            List<Cliente> clientes = service.listarClientes();
            System.out.println("\n>> CLIENTES REGISTRADOS (" + clientes.size() + "):");
            for (Cliente c : clientes) {
                System.out.println("  * " + c);
            }
        } else {
            System.out.println("\n>> REGISTRO DE NUEVO CLIENTE:");
            String id = ConsolaUtil.leerCadenaNoVacia(scanner, "Documento de Identidad o NIT: ");
            String nombre = ConsolaUtil.leerCadenaNoVacia(scanner, "Nombre completo o Razon Social: ");
            String tel = ConsolaUtil.leerCadenaNoVacia(scanner, "Telefono de contacto (ej: 8589869056): ");
            String email = ConsolaUtil.leerCadenaNoVacia(scanner, "Correo electronico: ");
            String pais = ConsolaUtil.leerCadenaNoVacia(scanner, "Pais de procedencia: ");

            Cliente nuevo = new Cliente(id, nombre, tel, email, pais);
            service.registrarCliente(nuevo);
            System.out.println("  [+] Cliente registrado exitosamente: " + nombre);
        }
    }

    private static void menuDesarrolladores() throws ValidacionNegocioException {
        System.out.println("--- GESTION DE DESARROLLADORES ---");
        System.out.println("1. Listar todos los desarrolladores");
        System.out.println("2. Registrar un nuevo desarrollador");
        int subOpcion = ConsolaUtil.leerEntero(scanner, "Opcion [1-2]: ", 1, 2);

        if (subOpcion == 1) {
            List<Desarrollador> devs = service.listarDesarrolladores();
            System.out.println("\n>> TALENTO HUMANO REGISTRADO (" + devs.size() + "):");
            for (Desarrollador d : devs) {
                System.out.println("  * " + d);
            }
        } else {
            System.out.println("\n>> REGISTRO DE NUEVO DESARROLLADOR:");
            String cod = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del desarrollador (ej: DEV-10): ");
            String equipo = ConsolaUtil.leerCadenaNoVacia(scanner, "Equipo de trabajo (ej: Backend / Frontend): ");

            System.out.println("Seleccione Nivel de experiencia:");
            System.out.println("  1. JUNIOR");
            System.out.println("  2. SEMISENIOR");
            System.out.println("  3. SENIOR");
            int n = ConsolaUtil.leerEntero(scanner, "Opcion [1-3]: ", 1, 3);
            NivelDesarrollador nivel = switch (n) {
                case 1 -> NivelDesarrollador.JUNIOR;
                case 2 -> NivelDesarrollador.SEMISENIOR;
                default -> NivelDesarrollador.SENIOR;
            };

            int maxSim = ConsolaUtil.leerEntero(scanner, "Cantidad maxima de proyectos simultaneos: ", 1, 10);
            double tarifa = ConsolaUtil.leerDouble(scanner, "Tarifa por dia (COP): ", 10000.0);

            Desarrollador dev = new Desarrollador(cod, equipo, nivel, maxSim, tarifa, EstadoDesarrollador.DISPONIBLE);
            service.registrarDesarrollador(dev);
            System.out.println("  [+] Desarrollador registrado exitosamente con estado DISPONIBLE.");
        }
    }

    private static void menuServicios() throws ValidacionNegocioException {
        System.out.println("--- CATALOGO DE SERVICIOS ADICIONALES ---");
        System.out.println("1. Listar catalogo de servicios");
        System.out.println("2. Registrar nuevo servicio adicional");
        int subOpcion = ConsolaUtil.leerEntero(scanner, "Opcion [1-2]: ", 1, 2);

        if (subOpcion == 1) {
            List<ServicioAdicional> servs = service.listarServicios();
            System.out.println("\n>> PORTAFOLIO DE SERVICIOS (" + servs.size() + "):");
            for (ServicioAdicional s : servs) {
                System.out.println("  * " + s);
            }
        } else {
            System.out.println("\n>> REGISTRO DE NUEVO SERVICIO ADICIONAL:");
            String cod = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del servicio (ej: SRV-05): ");
            String nombre = ConsolaUtil.leerCadenaNoVacia(scanner, "Nombre del servicio: ");
            String desc = ConsolaUtil.leerCadenaNoVacia(scanner, "Descripcion: ");
            double precio = ConsolaUtil.leerDouble(scanner, "Precio unitario: ", 0.0);
            System.out.print("¿Se encuentra disponible actualmente? (S/N): ");
            boolean disp = scanner.nextLine().trim().equalsIgnoreCase("S");

            ServicioAdicional servicio = new ServicioAdicional(cod, nombre, desc, precio, disp);
            service.registrarServicioAdicional(servicio);
            System.out.println("  [+] Servicio adicional incorporado al portafolio de DevPlus.");
        }
    }

    private static void crearProyecto() throws RecursoNoEncontradoException, ValidacionNegocioException {
        System.out.println(">> CREACION DE NUEVO PROYECTO DE SOFTWARE:");
        String cod = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo unico del proyecto (ej: PRJ-2026-A): ");
        LocalDate fSol = ConsolaUtil.leerFecha(scanner, "Fecha de solicitud");
        LocalDate fIni = ConsolaUtil.leerFecha(scanner, "Fecha estimada de inicio");
        LocalDate fEnt = ConsolaUtil.leerFecha(scanner, "Fecha pactada de entrega");

        System.out.println("Seleccione el Metodo de Pago acordado:");
        System.out.println("  1. TARJETA DE CREDITO");
        System.out.println("  2. TRANSFERENCIA BANCARIA");
        System.out.println("  3. EFECTIVO");
        int m = ConsolaUtil.leerEntero(scanner, "Opcion [1-3]: ", 1, 3);
        MetodoPago metodo = switch (m) {
            case 1 -> MetodoPago.TARJETA_CREDITO;
            case 2 -> MetodoPago.TRANSFERENCIA_BANCARIA;
            default -> MetodoPago.EFECTIVO;
        };

        String idCliente = ConsolaUtil.leerCadenaNoVacia(scanner, "Identificacion o NIT del cliente contratante: ");

        Proyecto proyecto = service.crearProyecto(cod, fSol, fIni, fEnt, metodo, idCliente);
        System.out.println("\n  [+] Proyecto creado con exito en estado PENDIENTE.");
        System.out.println("      " + proyecto);
    }

    private static void agregarDesarrolladorAProyecto()
            throws RecursoNoEncontradoException, DesarrolladorNoDisponibleException, ValidacionNegocioException {
        System.out.println(">> ASIGNAR DESARROLLADOR A UN PROYECTO (VALIDACION RN3):");
        String codProy = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del proyecto: ");
        String codDev = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del desarrollador: ");

        service.asignarDesarrolladorAProyecto(codProy, codDev);
        Proyecto p = service.buscarProyectoPorCodigo(codProy);

        System.out.println("\n  [+] Desarrollador " + codDev + " asignado exitosamente al proyecto " + codProy + ".");
        System.out.printf("      Nuevo valor total recalculado: $%,.2f COP (Dias de desarrollo: %d)\n",
                p.getValorTotal(), p.calcularDiasDesarrollo());
    }

    private static void agregarServicioAProyecto()
            throws RecursoNoEncontradoException, ServicioNoDisponibleException, ValidacionNegocioException {
        System.out.println(">> AGREGAR SERVICIO ADICIONAL A UN PROYECTO (VALIDACION RN6):");
        String codProy = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del proyecto: ");
        String codSrv = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del servicio adicional: ");

        service.asociarServicioAProyecto(codProy, codSrv);
        Proyecto p = service.buscarProyectoPorCodigo(codProy);

        System.out.println("\n  [+] Servicio " + codSrv + " vinculado exitosamente al proyecto " + codProy + ".");
        System.out.printf("      Nuevo valor total recalculado: $%,.2f COP\n", p.getValorTotal());
    }

    private static void calcularValorTotalProyecto() throws RecursoNoEncontradoException {
        System.out.println(">> LIQUIDAR / CONSULTAR VALOR TOTAL DE UN PROYECTO (RN1 y RN2):");
        String codProy = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del proyecto: ");

        Proyecto p = service.buscarProyectoPorCodigo(codProy);
        double total = service.calcularValorTotalProyecto(codProy);

        System.out.println("\n----------------- DETALLE DE LIQUIDACION FINANCIERA -----------------");
        System.out.println("  Proyecto: " + p.getCodigo() + " | Estado: " + p.getEstado());
        System.out.println("  Cliente: " + p.getContratante().getNombreCompleto() +
                           (p.getContratante().esClienteFrecuente() ? " [CLIENTE FRECUENTE: APLICA 10% DESCUENTO]" : " [CLIENTE ESTANDAR]"));
        System.out.printf("  Periodo: %s hasta %s -> Dias de desarrollo (RN2): %d dias\n",
                p.getFechaInicio(), p.getFechaEntrega(), p.calcularDiasDesarrollo());

        System.out.println("  Desarrolladores asignados:");
        double sumaTarifas = 0;
        for (Desarrollador d : p.getDesarrolladoresAsignados()) {
            System.out.printf("    - %s (%s) | Tarifa/dia: $%,.2f COP\n", d.getCodigo(), d.getNivel(), d.getTarifaPorDia());
            sumaTarifas += d.getTarifaPorDia();
        }
        double costoDevs = sumaTarifas * p.calcularDiasDesarrollo();
        System.out.printf("    Subtotal talento humano (%d dias * $%,.2f/dia): $%,.2f COP\n",
                p.calcularDiasDesarrollo(), sumaTarifas, costoDevs);

        System.out.println("  Servicios adicionales contratados:");
        double costoSrvs = 0;
        if (p.getServiciosUtilizados().isEmpty()) {
            System.out.println("    (Sin servicios adicionales)");
        } else {
            for (ServicioAdicional s : p.getServiciosUtilizados()) {
                System.out.printf("    - %s (%s): $%,.2f COP\n", s.getCodigo(), s.getNombre(), s.getPrecio());
                costoSrvs += s.getPrecio();
            }
        }
        System.out.printf("    Subtotal servicios: $%,.2f COP\n", costoSrvs);

        double bruto = costoDevs + costoSrvs;
        double descuento = p.getContratante().esClienteFrecuente() ? bruto * 0.10 : 0.0;
        System.out.printf("  SUBTOTAL BRUTO:               $%,.2f COP\n", bruto);
        System.out.printf("  DESCUENTO FIDELIZACION (10%%): -$%,.2f COP\n", descuento);
        System.out.printf("  VALOR TOTAL LIQUIDADO (RN1):  $%,.2f COP\n", total);
        System.out.println("---------------------------------------------------------------------");
    }

    private static void confirmarProyecto()
            throws RecursoNoEncontradoException, TransicionEstadoInvalidaException, ValidacionNegocioException {
        System.out.println(">> CONFIRMAR PROYECTO (ACTUALIZA DISPONIBILIDAD DE DESARROLLADORES RN4):");
        String codProy = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del proyecto a confirmar: ");

        service.confirmarProyecto(codProy);
        Proyecto p = service.buscarProyectoPorCodigo(codProy);

        System.out.println("\n  [+] ¡Proyecto " + codProy + " confirmado exitosamente!");
        System.out.println("      Estado del proyecto: " + p.getEstado());
        System.out.println("      Disponibilidad actualizada de los desarrolladores asignados:");
        for (Desarrollador d : p.getDesarrolladoresAsignados()) {
            System.out.printf("        - Dev: %s | Nuevo Estado: %s | Proyectos activos: %d/%d\n",
                    d.getCodigo(), d.getEstado(), d.getProyectosAsignados().size(), d.getCantidadMaximaProyectosSimultaneos());
        }
    }

    private static void cambiarEstadoProyecto()
            throws RecursoNoEncontradoException, TransicionEstadoInvalidaException, ValidacionNegocioException {
        System.out.println(">> TRANSICION DE ESTADO DEL PROYECTO (RN5):");
        String codProy = ConsolaUtil.leerCadenaNoVacia(scanner, "Codigo del proyecto: ");
        Proyecto p = service.buscarProyectoPorCodigo(codProy);
        System.out.println("  Estado actual: " + p.getEstado());

        System.out.println("Seleccione nuevo estado objetivo:");
        System.out.println("  1. CONFIRMADO");
        System.out.println("  2. EN_CURSO");
        System.out.println("  3. FINALIZADO");
        System.out.println("  4. CANCELADO");
        int op = ConsolaUtil.leerEntero(scanner, "Opcion [1-4]: ", 1, 4);
        EstadoProyecto nuevoEstado = switch (op) {
            case 1 -> EstadoProyecto.CONFIRMADO;
            case 2 -> EstadoProyecto.EN_CURSO;
            case 3 -> EstadoProyecto.FINALIZADO;
            default -> EstadoProyecto.CANCELADO;
        };

        service.cambiarEstadoProyecto(codProy, nuevoEstado);
        System.out.println("\n  [+] Transicion ejecutada con exito. Nuevo estado del proyecto: " + p.getEstado());
    }

    private static void consultarTelefonoNumeroPerfecto()
            throws RecursoNoEncontradoException, ValidacionNegocioException {
        System.out.println(">> CONSULTA DE CLIENTE POR TELEFONO Y NUMERO PERFECTO (RN7):");
        String telefono = ConsolaUtil.leerCadenaNoVacia(scanner, "Ingrese el numero de telefono a consultar: ");

        Cliente c = service.buscarClientePorTelefono(telefono);
        boolean esPerfecto = service.verificarTelefonoClienteNumeroPerfecto(telefono);
        long numeroLong = ValidadorUtil.parsearTelefonoALong(c.getTelefono());

        System.out.println("\n----------------- RESULTADO DE LA EVALUACION RN7 -----------------");
        System.out.println("  Datos del cliente encontrado:");
        System.out.println("    * Identificacion: " + c.getIdentificacion());
        System.out.println("    * Razon Social / Nombre: " + c.getNombreCompleto());
        System.out.println("    * Correo Electronico: " + c.getCorreoElectronico());
        System.out.println("    * Telefono registrado: " + c.getTelefono());
        System.out.println("    * Pais de procedencia: " + c.getPaisProcedencia());
        System.out.println("  Analisis matematico de numero perfecto (Algoritmo O(sqrt(N))):");
        System.out.println("    * Valor numerico procesado (long): " + numeroLong);
        if (esPerfecto) {
            System.out.println("    * DICTAMEN: ¡EL TELEFONO ES UN NUMERO PERFECTO! (La suma de sus divisores propios es igual al numero).");
        } else {
            System.out.println("    * DICTAMEN: EL TELEFONO NO ES UN NUMERO PERFECTO.");
        }
        System.out.println("------------------------------------------------------------------");
    }

    private static void consultarIngresosPorFecha() {
        System.out.println(">> CONSULTA DE INGRESOS POR FECHA DE SOLICITUD (RN8):");
        LocalDate fecha = ConsolaUtil.leerFecha(scanner, "Ingrese la fecha de solicitud a consultar");

        double total = service.calcularIngresosPorFecha(fecha);
        System.out.println("\n----------------- REPORTE DE INGRESOS POR FECHA -----------------");
        System.out.println("  Fecha de solicitud consultada: " + fecha);
        System.out.printf("  TOTAL INGRESOS ACUMULADOS:    $%,.2f COP\n", total);
        System.out.println("  Detalle de proyectos con solicitud en esa fecha:");
        boolean huboProyectos = false;
        for (Proyecto p : service.listarProyectos()) {
            if (fecha.equals(p.getFechaSolicitud())) {
                huboProyectos = true;
                System.out.printf("    - Proyecto %s | Cliente: %s | Estado: %s | Valor: $%,.2f COP\n",
                        p.getCodigo(), p.getContratante().getNombreCompleto(), p.getEstado(), p.getValorTotal());
            }
        }
        if (!huboProyectos) {
            System.out.println("    (No se registraron proyectos solicitados en esta fecha).");
        }
        System.out.println("-----------------------------------------------------------------");
    }

    private static void abrirInterfazGrafica() {
        System.out.println(">> Abriendo Interfaz Grafica de Usuario (Swing)...");
        java.awt.EventQueue.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(service);
            ventana.setVisible(true);
        });
        System.out.println("  [+] ¡Ventana grafica iniciada en primer plano!");
    }

    private static void mostrarResumenEmpresa() {
        System.out.println(">> INFORMACION CORPORATIVA Y RESUMEN GENERAL:");
        System.out.println(service.getEmpresa());
        System.out.println("\nRESUMEN DE PROYECTOS:");
        for (Proyecto p : service.listarProyectos()) {
            System.out.println("  - " + p);
        }
    }

    // =========================================================================
    // DATOS DE PRUEBA PRECARGADOS (SEED DATA)
    // =========================================================================

    private static void inicializarSistemaConDatosPrueba() {
        service = DatosPruebaUtil.crearServicioConDatosPrueba();
    }
}
