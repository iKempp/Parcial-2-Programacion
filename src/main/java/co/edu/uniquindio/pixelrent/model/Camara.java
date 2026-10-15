package co.edu.uniquindio.pixelrent.model;

/**
 * Representa una camara de video/fotografia.
 * Tarifa base: $12.000 por cada hora de utilizacion.
 * Adicional: $15.000 al valor resultante si posee estabilizador.
 */
public class Camara extends EquipoAudiovisual {

    public static final double TARIFA_POR_HORA = 12000.0;
    public static final double RECARGO_ESTABILIZADOR = 15000.0;

    private String resolucionMaxima;
    private boolean tieneEstabilizador;

    public Camara(String codigo, String marca, String modelo, CondicionEquipo condicion,
                  String resolucionMaxima, boolean tieneEstabilizador) {
        super(codigo, marca, modelo, condicion);
        this.resolucionMaxima = resolucionMaxima != null ? resolucionMaxima.trim() : "";
        this.tieneEstabilizador = tieneEstabilizador;
    }

    public Camara(String codigo, String marca, String modelo, CondicionEquipo condicion, Sede sede,
                  String resolucionMaxima, boolean tieneEstabilizador) {
        super(codigo, marca, modelo, condicion, sede);
        this.resolucionMaxima = resolucionMaxima != null ? resolucionMaxima.trim() : "";
        this.tieneEstabilizador = tieneEstabilizador;
    }

    public String getResolucionMaxima() {
        return resolucionMaxima;
    }

    public void setResolucionMaxima(String resolucionMaxima) {
        this.resolucionMaxima = resolucionMaxima != null ? resolucionMaxima.trim() : "";
    }

    public boolean isTieneEstabilizador() {
        return tieneEstabilizador;
    }

    public void setTieneEstabilizador(boolean tieneEstabilizador) {
        this.tieneEstabilizador = tieneEstabilizador;
    }

    @Override
    public double calcularCostoUtilizacion(int horas) {
        if (horas <= 0) {
            return 0.0;
        }
        double costoBase = horas * TARIFA_POR_HORA;
        if (tieneEstabilizador) {
            costoBase += RECARGO_ESTABILIZADOR;
        }
        return costoBase;
    }

    @Override
    public String getTipoEquipo() {
        return "Camara";
    }

    @Override
    public String toString() {
        return String.format("Camara [codigo=%s, marca=%s, modelo=%s, resolucion=%s, estabilizador=%s, condicion=%s]",
                getCodigo(), getMarca(), getModelo(), resolucionMaxima,
                (tieneEstabilizador ? "Si" : "No"), getCondicion());
    }
}
