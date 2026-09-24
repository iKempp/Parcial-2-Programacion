package co.edu.uniquindio.devplus.util;

import java.time.LocalDate;

/**
 * Clase utilitaria con metodos estaticos para validacion de datos y algoritmos
 * matematicos del negocio.
 */
public final class ValidadorUtil {

    private ValidadorUtil() {
        // Constructor privado para evitar instanciacion
    }

    /**
     * Valida que una cadena no sea nula ni contenga unicamente espacios en blanco.
     * @param valor Cadena a evaluar.
     * @return true si la cadena contiene texto valido, false en caso contrario.
     */
    public static boolean esTextoValido(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    /**
     * Valida que un rango de fechas sea cronologicamente correcto (fecha inicio menor o igual a fecha fin).
     * @param fechaInicio Fecha inicial.
     * @param fechaFin Fecha final.
     * @return true si ambas fechas no son nulas y la inicial no es posterior a la final.
     */
    public static boolean esRangoFechasValido(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        return !fechaInicio.isAfter(fechaFin);
    }

    /**
     * Implementacion eficiente de verificacion de Numero Perfecto (RN7).
     * Un numero perfecto es aquel igual a la suma de sus divisores propios positivos
     * (excluyendo el propio numero).
     *
     * Algoritmo de complejidad O(sqrt(N)):
     * En lugar de recorrer linealmente desde 1 hasta N-1 (lo que requeriria mas de 8.5 billones
     * de ciclos para numeros telefonicos como 8589869056), iteramos unicamente hasta la raiz cuadrada
     * de N. Al encontrar un divisor i, sumamos simultaneamente su contraparte simetrica (N / i).
     *
     * @param numero Numero a evaluar (tipo long para soportar hasta 10 digitos sin desbordamiento).
     * @return true si el numero es perfecto, false en caso contrario.
     */
    public static boolean esNumeroPerfecto(long numero) {
        // Los numeros menores o iguales a 1 no son perfectos
        if (numero <= 1) {
            return false;
        }

        // El 1 siempre es divisor propio para cualquier N > 1
        long sumaDivisores = 1;
        long limite = (long) Math.sqrt(numero);

        // Iterar desde 2 hasta la raiz cuadrada
        for (long divisor = 2; divisor <= limite; divisor++) {
            if (numero % divisor == 0) {
                sumaDivisores += divisor;
                long pareja = numero / divisor;
                // Si los divisores no son iguales (evitar duplicar en cuadrados perfectos)
                if (pareja != divisor) {
                    sumaDivisores += pareja;
                }
            }
        }

        return sumaDivisores == numero;
    }

    /**
     * Extrae de forma segura el valor numerico de una cadena de telefono,
     * omitiendo guiones, espacios y caracteres especiales.
     * @param telefono Cadena que contiene el telefono.
     * @return El numero en formato long.
     * @throws NumberFormatException Si la cadena no contiene digitos validos.
     */
    public static long parsearTelefonoALong(String telefono) {
        if (telefono == null) {
            throw new NumberFormatException("El telefono no puede ser nulo.");
        }
        String soloDigitos = telefono.replaceAll("\\D", "");
        if (soloDigitos.isEmpty()) {
            throw new NumberFormatException("El telefono no contiene digitos numericos validos.");
        }
        return Long.parseLong(soloDigitos);
    }
}
