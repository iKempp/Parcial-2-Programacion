package co.edu.uniquindio.pixelrent.model;

/**
 * Representa un dron audiovisual.
 * Tarifa base: $18.000 por cada hora de utilizacion.
 * Adicional: $20.000 si dispone/solicita bateria adicional.
 * Regla: Su inclusion en una reserva exige la asignacion obligatoria de un operador capacitado.
 */
public class Dron extends EquipoAudiovisual {

    public static final double TARIFA_POR_HORA = 18000.0;
    public static final double RECARGO_BATERIA_ADICIONAL = 20000.0;

    private int autonomiaVueloMinutos;
    private boolean tieneBateriaAdicional;

    public Dron(String codigo, String marca, String modelo, CondicionEquipo condicion,
                int autonomiaVueloMinutos, boolean tieneBateriaAdicional) {
        super(codigo, marca, modelo, condicion);
        this.autonomiaVueloMinutos = Math.max(0, autonomiaVueloMinutos);
        this.tieneBateriaAdicional = tieneBateriaAdicional;
    }

    public Dron(String codigo, String marca, String modelo, CondicionEquipo condicion, Sede sede,
                int autonomiaVueloMinutos, boolean tieneBateriaAdicional) {
        super(codigo, marca, modelo, condicion, sede);
        this.autonomiaVueloMinutos = Math.max(0, autonomiaVueloMinutos);
        this.tieneBateriaAdicional = tieneBateriaAdicional;
    }

    public int getAutonomiaVueloMinutos() {
        return autonomiaVueloMinutos;
    }

    public void setAutonomiaVueloMinutos(int autonomiaVueloMinutos) {
        this.autonomiaVueloMinutos = Math.max(0, autonomiaVueloMinutos);
    }

    public boolean isTieneBateriaAdicional() {
        return tieneBateriaAdicional;
    }

    public void setTieneBateriaAdicional(boolean tieneBateriaAdicional) {
        this.tieneBateriaAdicional = tieneBateriaAdicional;
    }

    @Override
    public double calcularCostoUtilizacion(int horas) {
        if (horas <= 0) {
            return 0.0;
        }
        double costoBase = horas * TARIFA_POR_HORA;
        if (tieneBateriaAdicional) {
            costoBase += RECARGO_BATERIA_ADICIONAL;
        }
        return costoBase;
    }

    @Override
    public String getTipoEquipo() {
        return "Dron";
    }

    @Override
    public String toString() {
        return String.format("Dron [codigo=%s, marca=%s, modelo=%s, autonomia=%d min, batAdicional=%s, condicion=%s]",
                getCodigo(), getMarca(), getModelo(), autonomiaVueloMinutos,
                (tieneBateriaAdicional ? "Si" : "No"), getCondicion());
    }
}
