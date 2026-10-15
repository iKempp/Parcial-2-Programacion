package co.edu.uniquindio.pixelrent.model;

import java.util.Objects;

/**
 * Representa un operador tecnico especializado de PixelRent.
 */
public class Operador {

    private final String identificacion;
    private String nombreCompleto;
    private String certificacion;
    private int aniosExperiencia;

    public Operador(String identificacion, String nombreCompleto, String certificacion, int aniosExperiencia) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de identificacion del operador no puede ser vacio.");
        }
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del operador no puede ser vacio.");
        }
        this.identificacion = identificacion.trim();
        this.nombreCompleto = nombreCompleto.trim();
        this.certificacion = certificacion != null ? certificacion.trim() : "";
        this.aniosExperiencia = Math.max(0, aniosExperiencia);
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) {
            this.nombreCompleto = nombreCompleto.trim();
        }
    }

    public String getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(String certificacion) {
        this.certificacion = certificacion != null ? certificacion.trim() : "";
    }

    public int getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(int aniosExperiencia) {
        this.aniosExperiencia = Math.max(0, aniosExperiencia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operador operador = (Operador) o;
        return Objects.equals(identificacion, operador.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public String toString() {
        return String.format("Operador [id=%s, nombre=%s, cert=%s, experiencia=%d anios]",
                identificacion, nombreCompleto, certificacion, aniosExperiencia);
    }
}
