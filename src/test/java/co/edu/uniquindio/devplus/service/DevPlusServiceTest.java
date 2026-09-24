package co.edu.uniquindio.devplus.service;

import co.edu.uniquindio.devplus.exception.*;
import co.edu.uniquindio.devplus.model.*;
import co.edu.uniquindio.devplus.util.ValidadorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar las reglas de negocio criticas:
 * - Algoritmo eficiente de Numero Perfecto (RN7).
 * - Liquidacion financiera y descuento de cliente frecuente (RN1 y RN2).
 * - Validacion de disponibilidad y concurrencia de desarrolladores (RN3).
 * - Restriccion de servicios adicionales no disponibles (RN6).
 * - Acumulacion de ingresos por fecha de solicitud (RN8).
 */
public class DevPlusServiceTest {

    private Empresa empresa;
    private DevPlusService service;

    @BeforeEach
    void setUp() {
        empresa = new Empresa("DevPlus", "901458789-3", "Armenia", "6067490000", "www.devplus.co");
        service = new DevPlusService(empresa);
    }

    // =========================================================================
    // PRUEBAS RN7: ALGORITMO NUMERO PERFECTO O(sqrt(N))
    // =========================================================================

    @ParameterizedTest
    @ValueSource(longs = {6L, 28L, 496L, 8128L, 33550336L, 8589869056L})
    @DisplayName("RN7: Debe identificar correctamente los numeros perfectos conocidos incluyendo telefonos de 10 digitos")
    void testNumerosPerfectosValidos(long numero) {
        assertTrue(ValidadorUtil.esNumeroPerfecto(numero),
                () -> "El numero " + numero + " deberia ser reconocido como perfecto.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 10L, 100L, 3154879201L, 9999999999L})
    @DisplayName("RN7: Debe rechazar numeros que no son perfectos")
    void testNumerosNoPerfectos(long numero) {
        assertFalse(ValidadorUtil.esNumeroPerfecto(numero),
                () -> "El numero " + numero + " NO deberia ser reconocido como perfecto.");
    }

    // =========================================================================
    // PRUEBAS RN1 Y RN2: CALCULO DE VALOR TOTAL Y DESCUENTO CLIENTE FRECUENTE
    // =========================================================================

    @Test
    @DisplayName("RN1 y RN2: Calculo de valor total sin descuento para cliente nuevo (< 3 proyectos)")
    void testCalculoValorTotalSinDescuento() throws Exception {
        Cliente cliente = new Cliente("C1", "Cliente Estandar", "3100000000", "c1@mail.com", "Colombia");
        service.registrarCliente(cliente);

        Desarrollador dev = new Desarrollador("D1", "Backend", NivelDesarrollador.SENIOR, 2, 100000.0, EstadoDesarrollador.DISPONIBLE);
        service.registrarDesarrollador(dev);

        ServicioAdicional srv = new ServicioAdicional("S1", "Cloud", "Deploy", 500000.0, true);
        service.registrarServicioAdicional(srv);

        LocalDate fSol = LocalDate.of(2026, 9, 20);
        LocalDate fIni = LocalDate.of(2026, 9, 21);
        LocalDate fEnt = LocalDate.of(2026, 9, 26); // 5 dias de diferencia
        Proyecto p = service.crearProyecto("P-TEST-1", fSol, fIni, fEnt, MetodoPago.EFECTIVO, "C1");

        service.asignarDesarrolladorAProyecto("P-TEST-1", "D1");
        service.asociarServicioAProyecto("P-TEST-1", "S1");

        // Calculo esperado: (1 dev * 100000/dia * 5 dias) + 500000 servicio = 500000 + 500000 = 1000000.
        // Descuento: 0 (cliente nuevo tiene 1 proyecto).
        double total = service.calcularValorTotalProyecto("P-TEST-1");
        assertEquals(1000000.0, total, 0.001);
    }

    @Test
    @DisplayName("RN1: Aplica 10% de descuento cuando el cliente es frecuente (>= 3 proyectos)")
    void testCalculoValorTotalConDescuentoClienteFrecuente() throws Exception {
        Cliente cliente = new Cliente("C_FREC", "Empresa Frecuente", "8589869056", "frec@mail.com", "Colombia");
        service.registrarCliente(cliente);

        // Simulamos 2 proyectos anteriores
        Proyecto p1 = service.crearProyecto("P-ANT-1", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), MetodoPago.EFECTIVO, "C_FREC");
        Proyecto p2 = service.crearProyecto("P-ANT-2", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(1), MetodoPago.EFECTIVO, "C_FREC");

        // Tercer proyecto: ahora califica como cliente frecuente (size >= 3)
        Proyecto p3 = service.crearProyecto("P-ACTUAL", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(10), MetodoPago.TRANSFERENCIA_BANCARIA, "C_FREC");

        Desarrollador dev = new Desarrollador("D_TEST", "Core", NivelDesarrollador.SENIOR, 2, 200000.0, EstadoDesarrollador.DISPONIBLE);
        service.registrarDesarrollador(dev);

        service.asignarDesarrolladorAProyecto("P-ACTUAL", "D_TEST");

        // Bruto: 10 dias * 200000 = 2000000.
        // Descuento (10%): 200000.
        // Total esperado: 1800000.
        double total = service.calcularValorTotalProyecto("P-ACTUAL");
        assertTrue(cliente.esClienteFrecuente());
        assertEquals(1800000.0, total, 0.001);
    }

    // =========================================================================
    // PRUEBAS RN3: VALIDACION DE DISPONIBILIDAD Y CONCURRENCIA
    // =========================================================================

    @Test
    @DisplayName("RN3: Debe rechazar la asignacion de un desarrollador en estado EN_CAPACITACION")
    void testDesarrolladorEnCapacitacionRechazado() throws Exception {
        Cliente cliente = new Cliente("C2", "Cliente Test", "3111111111", "c2@mail.com", "Colombia");
        service.registrarCliente(cliente);

        Desarrollador devCap = new Desarrollador("D_CAP", "QA", NivelDesarrollador.JUNIOR, 2, 80000.0, EstadoDesarrollador.EN_CAPACITACION);
        service.registrarDesarrollador(devCap);

        Proyecto p = service.crearProyecto("P-CAP", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(5), MetodoPago.EFECTIVO, "C2");

        assertThrows(DesarrolladorNoDisponibleException.class, () -> {
            service.asignarDesarrolladorAProyecto("P-CAP", "D_CAP");
        });
    }

    @Test
    @DisplayName("RN3: Debe rechazar la asignacion si el desarrollador excede su capacidad maxima simultanea por solapamiento")
    void testDesarrolladorExcedeCapacidadSimultanea() throws Exception {
        Cliente cliente = new Cliente("C3", "Cliente Test", "3222222222", "c3@mail.com", "Colombia");
        service.registrarCliente(cliente);

        // Desarrollador con capacidad maxima de 1 proyecto simultaneo
        Desarrollador dev = new Desarrollador("D_TOPE", "Fullstack", NivelDesarrollador.SEMISENIOR, 1, 120000.0, EstadoDesarrollador.DISPONIBLE);
        service.registrarDesarrollador(dev);

        LocalDate fIni = LocalDate.of(2026, 10, 1);
        LocalDate fFin = LocalDate.of(2026, 10, 20);

        Proyecto p1 = service.crearProyecto("P-SOLAP-1", LocalDate.now(), fIni, fFin, MetodoPago.EFECTIVO, "C3");
        service.asignarDesarrolladorAProyecto("P-SOLAP-1", "D_TOPE");

        // Intentar asignarlo a otro proyecto en el mismo periodo solapado
        Proyecto p2 = service.crearProyecto("P-SOLAP-2", LocalDate.now(), fIni.plusDays(5), fFin.minusDays(2), MetodoPago.EFECTIVO, "C3");

        assertThrows(DesarrolladorNoDisponibleException.class, () -> {
            service.asignarDesarrolladorAProyecto("P-SOLAP-2", "D_TOPE");
        });
    }

    // =========================================================================
    // PRUEBAS RN6: ASOCIACION DE SERVICIOS
    // =========================================================================

    @Test
    @DisplayName("RN6: Debe rechazar asociacion de un servicio no disponible")
    void testServicioNoDisponibleRechazado() throws Exception {
        Cliente cliente = new Cliente("C4", "Cliente Test", "3333333333", "c4@mail.com", "Colombia");
        service.registrarCliente(cliente);

        ServicioAdicional srvInactivo = new ServicioAdicional("S_OFF", "Migracion", "Legacy", 1000000.0, false);
        service.registrarServicioAdicional(srvInactivo);

        Proyecto p = service.crearProyecto("P-SRV-OFF", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(2), MetodoPago.EFECTIVO, "C4");

        assertThrows(ServicioNoDisponibleException.class, () -> {
            service.asociarServicioAProyecto("P-SRV-OFF", "S_OFF");
        });
    }

    // =========================================================================
    // PRUEBAS RN8: INGRESOS POR FECHA DE SOLICITUD
    // =========================================================================

    @Test
    @DisplayName("RN8: Acumulacion exacta de ingresos por fecha de solicitud")
    void testIngresosPorFecha() throws Exception {
        LocalDate fechaObjetivo = LocalDate.of(2026, 9, 24);
        LocalDate otraFecha = LocalDate.of(2026, 9, 25);

        Cliente cliente = new Cliente("C5", "Cliente Test", "3444444444", "c5@mail.com", "Colombia");
        service.registrarCliente(cliente);

        Desarrollador dev = new Desarrollador("D_ING", "Dev", NivelDesarrollador.SENIOR, 5, 100000.0, EstadoDesarrollador.DISPONIBLE);
        service.registrarDesarrollador(dev);

        // Proyecto 1 solicitado el 24 (1 dia * 100000 = 100000)
        Proyecto p1 = service.crearProyecto("P-ING-1", fechaObjetivo, fechaObjetivo, fechaObjetivo.plusDays(1), MetodoPago.EFECTIVO, "C5");
        service.asignarDesarrolladorAProyecto("P-ING-1", "D_ING");

        // Proyecto 2 solicitado el 24 (2 dias * 100000 = 200000)
        Proyecto p2 = service.crearProyecto("P-ING-2", fechaObjetivo, fechaObjetivo.plusDays(2), fechaObjetivo.plusDays(4), MetodoPago.EFECTIVO, "C5");
        service.asignarDesarrolladorAProyecto("P-ING-2", "D_ING");

        // Proyecto 3 solicitado en otra fecha
        Proyecto p3 = service.crearProyecto("P-OTRA", otraFecha, otraFecha, otraFecha.plusDays(5), MetodoPago.EFECTIVO, "C5");
        service.asignarDesarrolladorAProyecto("P-OTRA", "D_ING");

        double ingresos24 = service.calcularIngresosPorFecha(fechaObjetivo);
        assertEquals(300000.0, ingresos24, 0.001);
    }
}
