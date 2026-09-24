package co.edu.uniquindio.devplus.util;

import co.edu.uniquindio.devplus.model.*;
import co.edu.uniquindio.devplus.service.DevPlusService;

import java.time.LocalDate;

/**
 * Clase utilitaria para la inicializacion y precarga de datos semilla (Seed Data)
 * en el sistema DevPlus, reutilizable tanto en la interfaz de consola como en la grafica.
 */
public final class DatosPruebaUtil {

    private DatosPruebaUtil() {
        // Constructor privado para evitar instanciacion
    }

    /**
     * Instancia una nueva Empresa con sus datos corporativos y carga el catalogo
     * inicial de clientes, desarrolladores, servicios adicionales y proyectos de prueba.
     *
     * @return Instancia configurada de DevPlusService con datos semilla.
     */
    public static DevPlusService crearServicioConDatosPrueba() {
        Empresa devPlus = new Empresa(
            "DevPlus Software Solutions",
            "901.458.789-3",
            "Cra 15 # 12N-45, Armenia, Quindio",
            "6067490000",
            "https://www.devplus.co"
        );
        DevPlusService service = new DevPlusService(devPlus);
        cargarDatosSemilla(service);
        return service;
    }

    /**
     * Carga el lote de datos semilla en el servicio suministrado.
     * @param service Servicio sobre el cual cargar los datos.
     */
    public static void cargarDatosSemilla(DevPlusService service) {
        try {
            // 1. Clientes
            // Cliente 1: Telefono "8589869056" -> ES NUMERO PERFECTO (RN7) y califica como CLIENTE FRECUENTE (RN1)
            Cliente c1 = new Cliente("NIT-900111222", "Tech Global Corporation", "8589869056", "gerencia@techglobal.com", "Colombia");
            // Cliente 2: Telefono estandar no perfecto
            Cliente c2 = new Cliente("CC-1094857211", "Alejandro Mejia Rios", "3154879201", "alejandro.mejia@gmail.com", "Colombia");
            // Cliente 3: Internacional con telefono 6 (numero perfecto base)
            Cliente c3 = new Cliente("TAX-8849201", "Andes Logistics Inc.", "6", "it@andeslogistics.com", "Chile");

            service.registrarCliente(c1);
            service.registrarCliente(c2);
            service.registrarCliente(c3);

            // 2. Desarrolladores
            Desarrollador d1 = new Desarrollador("DEV-01", "Backend Core", NivelDesarrollador.SENIOR, 2, 180000.0, EstadoDesarrollador.DISPONIBLE);
            Desarrollador d2 = new Desarrollador("DEV-02", "Frontend Web", NivelDesarrollador.SEMISENIOR, 2, 130000.0, EstadoDesarrollador.DISPONIBLE);
            Desarrollador d3 = new Desarrollador("DEV-03", "Mobile Apps", NivelDesarrollador.JUNIOR, 1, 85000.0, EstadoDesarrollador.DISPONIBLE);
            Desarrollador d4 = new Desarrollador("DEV-04", "Cloud Ops", NivelDesarrollador.SENIOR, 2, 200000.0, EstadoDesarrollador.EN_CAPACITACION);

            service.registrarDesarrollador(d1);
            service.registrarDesarrollador(d2);
            service.registrarDesarrollador(d3);
            service.registrarDesarrollador(d4);

            // 3. Servicios Adicionales
            ServicioAdicional s1 = new ServicioAdicional("SRV-01", "Soporte Tecnico 24/7", "Mantenimiento preventivo y correctivo continuo", 1500000.0, true);
            ServicioAdicional s2 = new ServicioAdicional("SRV-02", "Capacitacion de Usuarios", "Talleres practicos y manuales de operacion", 800000.0, true);
            ServicioAdicional s3 = new ServicioAdicional("SRV-03", "Despliegue Cloud AWS", "Configuracion de infraestructura y balanceadores", 2200000.0, true);
            ServicioAdicional s4 = new ServicioAdicional("SRV-04", "Migracion de Datos Legacy", "Limpieza y ETL de bases de datos antiguas", 1900000.0, false);

            service.registrarServicioAdicional(s1);
            service.registrarServicioAdicional(s2);
            service.registrarServicioAdicional(s3);
            service.registrarServicioAdicional(s4);

            // 4. Proyectos semilla
            LocalDate hoy = LocalDate.of(2026, 9, 24);

            // Proyectos historicos para Cliente 1 (para que sume >= 3 proyectos y sea frecuente)
            Proyecto pPrev1 = service.crearProyecto("PRJ-001", hoy.minusDays(60), hoy.minusDays(50), hoy.minusDays(20), MetodoPago.TRANSFERENCIA_BANCARIA, "NIT-900111222");
            pPrev1.setEstado(EstadoProyecto.FINALIZADO);

            Proyecto pPrev2 = service.crearProyecto("PRJ-002", hoy.minusDays(40), hoy.minusDays(30), hoy.minusDays(10), MetodoPago.TARJETA_CREDITO, "NIT-900111222");
            pPrev2.setEstado(EstadoProyecto.FINALIZADO);

            // Proyecto activo para Cliente 1 (frecuente) solicitado el 2026-09-24
            Proyecto pActivo1 = service.crearProyecto("PRJ-2026-01", hoy, hoy.plusDays(5), hoy.plusDays(25), MetodoPago.TRANSFERENCIA_BANCARIA, "NIT-900111222");
            service.asignarDesarrolladorAProyecto("PRJ-2026-01", "DEV-01");
            service.asignarDesarrolladorAProyecto("PRJ-2026-01", "DEV-02");
            service.asociarServicioAProyecto("PRJ-2026-01", "SRV-01");
            service.confirmarProyecto("PRJ-2026-01");

            // Proyecto activo para Cliente 2 solicitado el 2026-09-24
            Proyecto pActivo2 = service.crearProyecto("PRJ-2026-02", hoy, hoy.plusDays(10), hoy.plusDays(20), MetodoPago.EFECTIVO, "CC-1094857211");
            service.asignarDesarrolladorAProyecto("PRJ-2026-02", "DEV-03");
            service.asociarServicioAProyecto("PRJ-2026-02", "SRV-02");

        } catch (Exception e) {
            System.err.println("Advertencia al cargar datos semilla: " + e.getMessage());
        }
    }
}
