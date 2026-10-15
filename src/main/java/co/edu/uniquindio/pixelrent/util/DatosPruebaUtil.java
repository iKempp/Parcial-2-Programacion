package co.edu.uniquindio.pixelrent.util;

import co.edu.uniquindio.pixelrent.model.*;
import co.edu.uniquindio.pixelrent.service.PixelRentService;

import java.time.LocalDate;

/**
 * Utilitario para precargar datos iniciales de prueba y permitir
 * una demostracion inmediata del sistema.
 */
public class DatosPruebaUtil {

    public static void cargarDatosPrueba(PixelRentService service) {
        try {
            // 1. Registrar Sedes
            Sede sedeCentro = new Sede("SED-01", "Sede Central Centro", "Carrera 14 # 20-30, Armenia");
            Sede sedeNorte = new Sede("SED-02", "Sede Norte Produccion", "Avenida Bolivar # 12N-45, Armenia");
            service.registrarSede(sedeCentro);
            service.registrarSede(sedeNorte);

            // 2. Registrar Clientes
            Cliente c1 = new Cliente("1094901001", "Carlos Andres Gomez", "3104567890", "carlos.gomez@gmail.com");
            Cliente c2 = new Cliente("1094902002", "Mariana Rios Sanchez", "3158901234", "mariana.rios@outlook.com");
            Cliente c3 = new Cliente("1094903003", "Juan David Morales", "3201122334", "juan.morales@productora.co");
            service.registrarCliente(c1);
            service.registrarCliente(c2);
            service.registrarCliente(c3);

            // 3. Registrar Operadores
            Operador op1 = new Operador("75100200", "Luis Fernando Castro", "Piloto Profesional de Dron y Asistente Tecnico", 5);
            Operador op2 = new Operador("75200300", "Andrea Valencia Perez", "Tecnica Certificada en Iluminacion y Camaras", 3);
            service.registrarOperador(op1);
            service.registrarOperador(op2);

            // 4. Registrar Equipos
            // Camaras ($12.000/h + $15.000 con estabilizador)
            Camara cam1 = new Camara("CAM-01", "Sony", "Alpha 7 IV", CondicionEquipo.DISPONIBLE, "4K 60fps", true);
            Camara cam2 = new Camara("CAM-02", "Canon", "EOS R5", CondicionEquipo.DISPONIBLE, "8K RAW", false);
            Camara cam3 = new Camara("CAM-03", "Blackmagic", "Pocket 6K Pro", CondicionEquipo.EN_MANTENIMIENTO, "6K", true);
            service.registrarEquipo(cam1, "SED-02");
            service.registrarEquipo(cam2, "SED-01");
            service.registrarEquipo(cam3, "SED-01");

            // Drones ($18.000/h + $20.000 con bateria adicional)
            Dron dron1 = new Dron("DRN-01", "DJI", "Mavic 3 Pro Cine", CondicionEquipo.DISPONIBLE, 43, true);
            Dron dron2 = new Dron("DRN-02", "DJI", "Mini 4 Pro", CondicionEquipo.DISPONIBLE, 34, false);
            Dron dron3 = new Dron("DRN-03", "Autel", "EVO II Pro", CondicionEquipo.FUERA_DE_SERVICIO, 40, true);
            service.registrarEquipo(dron1, "SED-02");
            service.registrarEquipo(dron2, "SED-01");
            service.registrarEquipo(dron3, "SED-02");

            // Kits de Iluminacion ($10.000/h + $12.000 con control inalambrico)
            KitIluminacion kit1 = new KitIluminacion("ILU-01", "Godox", "SL60W Trio", CondicionEquipo.DISPONIBLE, 3, true);
            KitIluminacion kit2 = new KitIluminacion("ILU-02", "Aputure", "Amaran 200d Duo", CondicionEquipo.DISPONIBLE, 2, false);
            service.registrarEquipo(kit1, "SED-01");
            service.registrarEquipo(kit2, "SED-02");

            // 5. Registrar Reservas iniciales de prueba
            // Reserva 1: Carlos Gomez para 2026-10-25 en la Mañana (CONFIRMADA con CAM-01 y ILU-02)
            service.registrarReserva("RES-001", "1094901001", LocalDate.of(2026, 10, 25), Turno.MANANA);
            service.agregarEquipoAReserva("RES-001", "CAM-01", 4); // 4 * 12.000 + 15.000 = 63.000
            service.agregarEquipoAReserva("RES-001", "ILU-02", 4); // 4 * 10.000 + 0 = 40.000. Total = 103.000
            service.confirmarReserva("RES-001");

            // Reserva 2: Mariana Rios para 2026-10-25 en la Tarde (SOLICITADA con DRN-01 y CAM-02, Operador asignado)
            service.registrarReserva("RES-002", "1094902002", LocalDate.of(2026, 10, 25), Turno.TARDE);
            service.agregarEquipoAReserva("RES-002", "DRN-01", 3); // 3 * 18.000 + 20.000 = 74.000
            service.agregarEquipoAReserva("RES-002", "CAM-02", 3); // 3 * 12.000 = 36.000. Total = 110.000
            service.asignarOperadorAReserva("RES-002", "75100200");

            // Reserva 3: Juan David Morales para 2026-10-26 en la Mañana (SOLICITADA con ILU-01)
            service.registrarReserva("RES-003", "1094903003", LocalDate.of(2026, 10, 26), Turno.MANANA);
            service.agregarEquipoAReserva("RES-003", "ILU-01", 5); // 5 * 10.000 + 12.000 = 62.000

        } catch (Exception e) {
            System.err.println("Error precargando datos de prueba: " + e.getMessage());
        }
    }
}
