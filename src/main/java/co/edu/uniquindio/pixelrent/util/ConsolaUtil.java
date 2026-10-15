package co.edu.uniquindio.pixelrent.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utilitario para facilitar la lectura validada de entradas desde la consola (CLI).
 */
public class ConsolaUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String leerCadenaNoVacia(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] El dato no puede ser vacio. Intente nuevamente.");
        }
    }

    public static int leerEntero(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("  [!] Ingrese un valor entre %d y %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Entrada invalida. Debe ingresar un numero entero.");
            }
        }
    }

    public static boolean leerBooleano(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (s/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("true") || input.equals("1")) {
                return true;
            }
            if (input.equals("n") || input.equals("no") || input.equals("false") || input.equals("0")) {
                return false;
            }
            System.out.println("  [!] Responda con 's' (si) o 'n' (no).");
        }
    }

    public static LocalDate leerFecha(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (formato AAAA-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Formato de fecha invalido. Use AAAA-MM-DD (ej: 2026-10-25).");
            }
        }
    }

    public static void esperarEnter(Scanner scanner) {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }
}
