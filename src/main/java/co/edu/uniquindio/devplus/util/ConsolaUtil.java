package co.edu.uniquindio.devplus.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utilidades para la lectura segura de datos por consola con Scanner,
 * previniendo excepciones por formato incorrecto y buffering residual.
 */
public final class ConsolaUtil {

    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ConsolaUtil() {
        // Constructor privado para evitar instanciacion
    }

    /**
     * Lee un numero entero dentro de un rango especifico [min, max].
     */
    public static int leerEntero(Scanner sc, String mensaje, int min, int max) {
        int valor;
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();
            try {
                valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("  [!] Por favor ingrese un numero entre %d y %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Entrada invalida. Debe digitar un numero entero.");
            }
        }
    }

    /**
     * Lee un numero decimal (double) mayor o igual a un minimo.
     */
    public static double leerDouble(Scanner sc, String mensaje, double min) {
        double valor;
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim().replace(",", ".");
            try {
                valor = Double.parseDouble(input);
                if (valor >= min) {
                    return valor;
                }
                System.out.printf("  [!] El valor debe ser mayor o igual a %.2f.\n", min);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Entrada invalida. Digite un numero decimal valido.");
            }
        }
    }

    /**
     * Lee una cadena obligatoria (no vacia ni solo espacios).
     */
    public static String leerCadenaNoVacia(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] Este campo es obligatorio. No puede quedar vacio.");
        }
    }

    /**
     * Lee una fecha valida en formato AAAA-MM-DD.
     */
    public static LocalDate leerFecha(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DD): ");
            String input = sc.nextLine().trim();
            try {
                return LocalDate.parse(input, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Formato de fecha invalido. Use el formato exacto AAAA-MM-DD (ej: 2026-09-24).");
            }
        }
    }

    /**
     * Pausa la consola hasta que el usuario presione Enter.
     */
    public static void esperarEnter(Scanner sc) {
        System.out.print("\nPresione [ENTER] para continuar...");
        sc.nextLine();
    }
}
