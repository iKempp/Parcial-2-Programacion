package co.edu.uniquindio.pixelrent.service;

import co.edu.uniquindio.pixelrent.exception.ElementoDuplicadoException;
import co.edu.uniquindio.pixelrent.exception.ReglaNegocioException;
import co.edu.uniquindio.pixelrent.exception.TransicionEstadoInvalidaException;
import co.edu.uniquindio.pixelrent.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PixelRentServiceTest {

    private PixelRent empresa;
    private PixelRentService service;

    @BeforeEach
    void setUp() {
        empresa = new PixelRent("PixelRent Test");
        service = new PixelRentService(empresa);
    }

    @Test
    @DisplayName("Cálculo de costo de Cámara sin y con estabilizador")
    void testCalculoCostoCamara() {
        Camara camSinEst = new Camara("C-1", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        Camara camConEst = new Camara("C-2", "Sony", "FX3", CondicionEquipo.DISPONIBLE, "4K", true);

        // 3 horas * 12.000 = 36.000
        assertEquals(36000.0, camSinEst.calcularCostoUtilizacion(3), 0.001);
        // 3 horas * 12.000 + 15.000 = 51.000
        assertEquals(51000.0, camConEst.calcularCostoUtilizacion(3), 0.001);
    }

    @Test
    @DisplayName("Cálculo de costo de Dron sin y con batería adicional")
    void testCalculoCostoDron() {
        Dron dronSinBat = new Dron("D-1", "DJI", "Mini", CondicionEquipo.DISPONIBLE, 30, false);
        Dron dronConBat = new Dron("D-2", "DJI", "Mavic", CondicionEquipo.DISPONIBLE, 45, true);

        // 2 horas * 18.000 = 36.000
        assertEquals(36000.0, dronSinBat.calcularCostoUtilizacion(2), 0.001);
        // 2 horas * 18.000 + 20.000 = 56.000
        assertEquals(56000.0, dronConBat.calcularCostoUtilizacion(2), 0.001);
    }

    @Test
    @DisplayName("Cálculo de costo de Kit de Iluminación sin y con control inalámbrico")
    void testCalculoCostoKitIluminacion() {
        KitIluminacion kitSinCtrl = new KitIluminacion("I-1", "Godox", "SL60", CondicionEquipo.DISPONIBLE, 2, false);
        KitIluminacion kitConCtrl = new KitIluminacion("I-2", "Godox", "SL60 Pro", CondicionEquipo.DISPONIBLE, 3, true);

        // 4 horas * 10.000 = 40.000
        assertEquals(40000.0, kitSinCtrl.calcularCostoUtilizacion(4), 0.001);
        // 4 horas * 10.000 + 12.000 = 52.000
        assertEquals(52000.0, kitConCtrl.calcularCostoUtilizacion(4), 0.001);
    }

    @Test
    @DisplayName("No permitir registro de dos equipos con el mismo código")
    void testNoPermitirEquiposConMismoCodigo() throws Exception {
        Camara cam1 = new Camara("EQ-01", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        service.registrarEquipo(cam1, null);

        Dron dronRepetido = new Dron("EQ-01", "DJI", "Mini", CondicionEquipo.DISPONIBLE, 30, false);
        assertThrows(ElementoDuplicadoException.class, () -> service.registrarEquipo(dronRepetido, null));
    }

    @Test
    @DisplayName("No permitir registro de dos reservas con el mismo código")
    void testNoPermitirReservasConMismoCodigo() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);

        service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);
        assertThrows(ElementoDuplicadoException.class, () ->
                service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 2), Turno.TARDE));
    }

    @Test
    @DisplayName("No permitir agregar equipos en mantenimiento o fuera de servicio a una reserva")
    void testNoAgregarEquiposNoDisponibles() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);

        Camara camMant = new Camara("CAM-M", "Canon", "R5", CondicionEquipo.EN_MANTENIMIENTO, "8K", false);
        service.registrarEquipo(camMant, null);

        Reserva res = service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);

        assertThrows(ReglaNegocioException.class, () ->
                service.agregarEquipoAReserva("RES-1", "CAM-M", 3));
    }

    @Test
    @DisplayName("No permitir agregar el mismo equipo más de una vez a la misma reserva")
    void testNoAgregarMismoEquipoDosVeces() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);

        Camara cam = new Camara("CAM-1", "Sony", "FX3", CondicionEquipo.DISPONIBLE, "4K", true);
        service.registrarEquipo(cam, null);

        service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);
        service.agregarEquipoAReserva("RES-1", "CAM-1", 2);

        assertThrows(ReglaNegocioException.class, () ->
                service.agregarEquipoAReserva("RES-1", "CAM-1", 4));
    }

    @Test
    @DisplayName("Confirmar reserva falla si no tiene equipos")
    void testConfirmarReservaSinEquiposFalla() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);
        service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);

        assertThrows(ReglaNegocioException.class, () -> service.confirmarReserva("RES-1"));
    }

    @Test
    @DisplayName("Reserva con dron solo puede confirmarse si cuenta con operador asignado")
    void testConfirmarReservaConDronRequiereOperador() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);

        Dron dron = new Dron("DRN-1", "DJI", "Air 3", CondicionEquipo.DISPONIBLE, 40, true);
        service.registrarEquipo(dron, null);

        service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);
        service.agregarEquipoAReserva("RES-1", "DRN-1", 2);

        // Sin operador debe fallar
        assertThrows(ReglaNegocioException.class, () -> service.confirmarReserva("RES-1"));

        // Se registra y asigna operador
        Operador op = new Operador("OP-1", "Mario Silva", "Certificado Dron", 4);
        service.registrarOperador(op);
        service.asignarOperadorAReserva("RES-1", "OP-1");

        // Ahora si debe confirmar exitosamente
        service.confirmarReserva("RES-1");
        assertEquals(EstadoReserva.CONFIRMADA, empresa.buscarReservaPorCodigo("RES-1").getEstado());
    }

    @Test
    @DisplayName("Equipo ya reservado en otra confirmada para misma fecha y turno no puede usarse")
    void testConflictoEquipoMismaFechaYTurno() throws Exception {
        Cliente c1 = new Cliente("111", "Cliente 1", "3001", "c1@test.com");
        Cliente c2 = new Cliente("222", "Cliente 2", "3002", "c2@test.com");
        service.registrarCliente(c1);
        service.registrarCliente(c2);

        Camara cam = new Camara("CAM-1", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        service.registrarEquipo(cam, null);

        LocalDate fecha = LocalDate.of(2026, 11, 5);
        Turno turno = Turno.TARDE;

        // Reserva 1 confirmada con CAM-1
        service.registrarReserva("RES-1", "111", fecha, turno);
        service.agregarEquipoAReserva("RES-1", "CAM-1", 3);
        service.confirmarReserva("RES-1");

        // Reserva 2 en misma fecha y turno con CAM-1
        service.registrarReserva("RES-2", "222", fecha, turno);
        service.agregarEquipoAReserva("RES-2", "CAM-1", 2);

        // Al intentar confirmar Reserva 2, debe arrojar excepcion de conflicto
        assertThrows(ReglaNegocioException.class, () -> service.confirmarReserva("RES-2"));
    }

    @Test
    @DisplayName("Reservas canceladas o finalizadas no pueden volver al estado confirmado")
    void testReservaCanceladaOFinalizadaNoPuedeVolverAConfirmada() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);
        Camara cam = new Camara("CAM-1", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        service.registrarEquipo(cam, null);

        service.registrarReserva("RES-1", "123", LocalDate.of(2026, 11, 1), Turno.MANANA);
        service.agregarEquipoAReserva("RES-1", "CAM-1", 2);
        service.confirmarReserva("RES-1");

        // Finalizar
        service.finalizarReserva("RES-1");
        assertEquals(EstadoReserva.FINALIZADA, empresa.buscarReservaPorCodigo("RES-1").getEstado());

        // Intentar regresar a CONFIRMADA
        assertThrows(TransicionEstadoInvalidaException.class, () -> service.confirmarReserva("RES-1"));
    }

    @Test
    @DisplayName("Consultar equipos disponibles para fecha y turno excluye confirmados y no disponibles")
    void testConsultarEquiposDisponibles() throws Exception {
        Cliente c = new Cliente("123", "Juan Perez", "300123", "juan@test.com");
        service.registrarCliente(c);

        Camara camDisp = new Camara("CAM-D", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        Camara camMant = new Camara("CAM-M", "Canon", "R5", CondicionEquipo.EN_MANTENIMIENTO, "8K", false);
        Camara camOcupada = new Camara("CAM-O", "Nikon", "Z6", CondicionEquipo.DISPONIBLE, "4K", false);

        service.registrarEquipo(camDisp, null);
        service.registrarEquipo(camMant, null);
        service.registrarEquipo(camOcupada, null);

        LocalDate fecha = LocalDate.of(2026, 11, 10);
        Turno turno = Turno.NOCHE;

        // Ocupamos camOcupada en una reserva confirmada
        service.registrarReserva("RES-OCUPADA", "123", fecha, turno);
        service.agregarEquipoAReserva("RES-OCUPADA", "CAM-O", 2);
        service.confirmarReserva("RES-OCUPADA");

        List<EquipoAudiovisual> disponibles = service.consultarEquiposDisponibles(fecha, turno);

        assertTrue(disponibles.contains(camDisp));
        assertFalse(disponibles.contains(camMant), "No debe incluir equipos en mantenimiento");
        assertFalse(disponibles.contains(camOcupada), "No debe incluir equipos ya ocupados en reserva confirmada");
    }

    @Test
    @DisplayName("Consultar reservas confirmadas por identificación del cliente")
    void testConsultarReservasConfirmadasPorCliente() throws Exception {
        Cliente c = new Cliente("CLI-777", "Pedro Gomez", "3111", "pg@test.com");
        service.registrarCliente(c);

        Camara cam = new Camara("CAM-1", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", false);
        service.registrarEquipo(cam, null);

        // Reserva 1: Solicitada
        service.registrarReserva("R-1", "CLI-777", LocalDate.of(2026, 12, 1), Turno.MANANA);
        // Reserva 2: Confirmada
        service.registrarReserva("R-2", "CLI-777", LocalDate.of(2026, 12, 2), Turno.TARDE);
        service.agregarEquipoAReserva("R-2", "CAM-1", 3);
        service.confirmarReserva("R-2");

        List<Reserva> confirmadas = service.consultarReservasConfirmadasPorCliente("CLI-777");
        assertEquals(1, confirmadas.size());
        assertEquals("R-2", confirmadas.get(0).getCodigo());
    }

    @Test
    @DisplayName("Obtener valor total de una reserva con varios equipos")
    void testObtenerValorTotalReserva() throws Exception {
        Cliente c = new Cliente("CLI-1", "Carlos", "3111", "c@test.com");
        service.registrarCliente(c);

        Camara cam = new Camara("C-1", "Sony", "A7", CondicionEquipo.DISPONIBLE, "4K", true); // 3 * 12.000 + 15.000 = 51.000
        KitIluminacion kit = new KitIluminacion("K-1", "Godox", "SL60", CondicionEquipo.DISPONIBLE, 2, false); // 2 * 10.000 = 20.000
        service.registrarEquipo(cam, null);
        service.registrarEquipo(kit, null);

        service.registrarReserva("R-TOTAL", "CLI-1", LocalDate.of(2026, 12, 5), Turno.MANANA);
        service.agregarEquipoAReserva("R-TOTAL", "C-1", 3);
        service.agregarEquipoAReserva("R-TOTAL", "K-1", 2);

        double total = service.obtenerValorTotalReserva("R-TOTAL");
        assertEquals(71000.0, total, 0.001);
    }
}
