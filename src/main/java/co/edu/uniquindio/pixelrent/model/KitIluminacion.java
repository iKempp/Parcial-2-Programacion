package co.edu.uniquindio.pixelrent.model;

/**
 * Representa un kit de iluminacion para produccion audiovisual.
 * Tarifa base: $10.000 por cada hora de utilizacion.
 * Incremento: $12.000 cuando disponen de control inalambrico.
 */
public class KitIluminacion extends EquipoAudiovisual {

    public static final double TARIFA_POR_HORA = 10000.0;
    public static final double RECARGO_CONTROL_INALAMBRICO = 12000.0;

    private int cantidadLuces;
    private boolean tieneControlInalambrico;

    public KitIluminacion(String codigo, String marca, String modelo, CondicionEquipo condicion,
                          int cantidadLuces, boolean tieneControlInalambrico) {
        super(codigo, marca, modelo, condicion);
        this.cantidadLuces = Math.max(1, cantidadLuces);
        this.tieneControlInalambrico = tieneControlInalambrico;
    }

    public KitIluminacion(String codigo, String marca, String modelo, CondicionEquipo condicion, Sede sede,
                          int cantidadLuces, boolean tieneControlInalambrico) {
        super(codigo, marca, modelo, condicion, sede);
        this.cantidadLuces = Math.max(1, cantidadLuces);
        this.tieneControlInalambrico = tieneControlInalambrico;
    }

    public int getCantidadLuces() {
        return cantidadLuces;
    }

    public void setCantidadLuces(int cantidadLuces) {
        this.cantidadLuces = Math.max(1, cantidadLuces);
    }

    public boolean isTieneControlInalambrico() {
        return tieneControlInalambrico;
    }

    public void setTieneControlInalambrico(boolean tieneControlInalambrico) {
        this.tieneControlInalambrico = tieneControlInalambrico;
    }

    @Override
    public double calcularCostoUtilizacion(int horas) {
        if (horas <= 0) {
            return 0.0;
        }
        double costoBase = horas * TARIFA_POR_HORA;
        if (tieneControlInalambrico) {
            costoBase += RECARGO_CONTROL_INALAMBRICO;
        }
        return costoBase;
    }

    @Override
    public String getTipoEquipo() {
        return "KitIluminacion";
    }

    @Override
    public String toString() {
        return String.format("KitIluminacion [codigo=%s, marca=%s, modelo=%s, luces=%d, ctrlInalambrico=%s, condicion=%s]",
                getCodigo(), getMarca(), getModelo(), cantidadLuces,
                (tieneControlInalambrico ? "Si" : "No"), getCondicion());
    }
}
