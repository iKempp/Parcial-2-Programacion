# DevPlus - Sistema de Gestión de Proyectos de Software
**Curso:** Programación I  
**Facultad de Ingeniería - Programa de Ingeniería de Sistemas y Computación**  
**Universidad del Quindío**  
**Fecha:** 24 de septiembre del 2026  

---

## 1. Pensamiento Computacional

### Abstracción
* **¿Qué se solicita finalmente? (Problema):**  
  Construir una solución de software orientada a objetos para la empresa "DevPlus", permitiendo la administración integral de clientes, talentos de desarrollo, servicios adicionales y proyectos contratados. El sistema debe garantizar la no existencia de conflictos en agendas de desarrollo, liquidar valores con descuentos de fidelidad, gestionar transiciones de estado del proyecto y proveer dos analíticas avanzadas: evaluación de números perfectos sobre líneas telefónicas y acumulación de ingresos por fecha de solicitud.

* **¿Qué información es relevante dado el problema anterior?:**  
  * *Empresa:* Nombre comercial, NIT, dirección, teléfono, página web y sus 4 registros maestros.
  * *Cliente:* Identificación/NIT, nombre o razón social, teléfono, correo, país y su historial de proyectos contratados.
  * *Desarrollador:* Código, equipo, nivel (Junior, Semisenior, Senior), capacidad máxima de concurrencia, tarifa diaria, estado y proyectos asignados.
  * *Proyecto:* Código, fechas (solicitud, inicio, entrega), estado del ciclo de vida, método de pago, valor liquidado, cliente contratante, equipo asignado y servicios complementarios.
  * *ServicioAdicional:* Código, nombre, descripción, precio y disponibilidad.

* **¿Cómo se agrupa la información relevante?:**  
  Se agrupa mediante clases modelo fuertemente cohesivas (`Empresa`, `Cliente`, `Desarrollador`, `Proyecto`, `ServicioAdicional`) y enumeraciones para tipificación estricta de estados y categorías (`EstadoProyecto`, `MetodoPago`, `NivelDesarrollador`, `EstadoDesarrollador`).

* **¿Qué funcionalidades se solicitan finalmente?:**  
  1. CRUD y consultas sobre clientes, desarrolladores, servicios y proyectos.
  2. Validación de disponibilidad y solapamiento de fechas con límite de simultaneidad (RN3).
  3. Liquidación automática del valor total con días y descuento por cliente frecuente (RN1 y RN2).
  4. Confirmación de proyectos con reactualización de disponibilidad de desarrolladores (RN4).
  5. Control estricto de transiciones de estado de proyectos (RN5).
  6. Asociación exclusiva de servicios disponibles (RN6).
  7. Consulta de cliente por teléfono y verificación matemática de Número Perfecto en $O(\sqrt{N})$ (RN7).
  8. Consolidación de ingresos por fecha de solicitud (RN8).

### Descomposición
* **¿Cómo se distribuyen las funcionalidades?:**  
  Se aplica una arquitectura en capas con responsabilidades desacopladas:
  * `model`: Datos y comportamientos intrínsecos de las entidades.
  * `service`: Fachada de negocio (`DevPlusService`) que encapsula las 8 reglas de negocio.
  * `exception`: Clases de error para situaciones anómalas del dominio.
  * `util`: Módulos de soporte técnico (`ConsolaUtil` y `ValidadorUtil`).
  * `app`: Interfaz por consola (`Main`) sin lógica de negocio.
* **¿Qué debo hacer para probar las funcionalidades?:**  
  * Ejecutar la suite de pruebas automatizadas con JUnit 5 (`DevPlusServiceTest`).
  * Realizar la demostración guiada a través del menú interactivo en consola probando casos exitosos y casos de error controlado.

### Reconocimiento de Patrones
* **Patrón de Interval Scheduling (Solapamiento Temporal):** Comprobación matemática de cruce de rangos: dos proyectos chocan si no ocurre que uno termine antes que empiece el otro.
* **Patrón Algorítmico de Divisores Simétricos:** Cálculo de divisores propios en $O(\sqrt{N})$ para procesar números grandes (como teléfonos de 10 dígitos) en milisegundos.
* **Patrón Fachada (Facade):** `DevPlusService` expone una interfaz limpia y simplificada para la vista.
* **Patrón Máquina de Estados Finita:** Control determinista del ciclo de vida del proyecto.

### Codificación
* **¿Cómo pruebo la solución en Java?:**  
  A través de pruebas unitarias parametrizadas en JUnit 5 y ejecución interactiva mediante `Main.java` o el script `ejecutar.bat`.
* **¿Cómo escribo la solución en Java?:**  
  En Java 17 LTS, con encapsulamiento estricto (`private` con getters/setters), colecciones `ArrayList`, inmutabilidad controlada (`Collections.unmodifiableList`), API `java.time` (`LocalDate`, `ChronoUnit.DAYS`), y manejo explícito de excepciones personalizadas.

---

## 2. Diagrama de Clases UML

### PlantUML
```plantuml
@startuml
skinparam classAttributeIconSize 0

package "co.edu.uniquindio.devplus.model" {
    enum EstadoProyecto { PENDIENTE, CONFIRMADO, EN_CURSO, FINALIZADO, CANCELADO }
    enum MetodoPago { TARJETA_CREDITO, TRANSFERENCIA_BANCARIA, EFECTIVO }
    enum NivelDesarrollador { JUNIOR, SEMISENIOR, SENIOR }
    enum EstadoDesarrollador { DISPONIBLE, ASIGNADO, OCUPADO, EN_CAPACITACION }

    class Empresa {
        - String nombreComercial
        - String nit
        - String direccion
        - String telefono
        - String paginaWeb
        - List<Cliente> clientes
        - List<Desarrollador> desarrolladores
        - List<Proyecto> proyectos
        - List<ServicioAdicional> serviciosAdicionales
    }

    class Cliente {
        - String identificacion
        - String nombreCompleto
        - String telefono
        - String correoElectronico
        - String paisProcedencia
        - List<Proyecto> proyectosContratados
        + esClienteFrecuente(): boolean
        + agregarProyecto(Proyecto): void
    }

    class Desarrollador {
        - String codigo
        - String equipoDeTrabajo
        - NivelDesarrollador nivel
        - int cantidadMaximaProyectosSimultaneos
        - double tarifaPorDia
        - EstadoDesarrollador estado
        - List<Proyecto> proyectosAsignados
        + contarProyectosEnRango(LocalDate, LocalDate): int
        + tieneCapacidadEnFechas(LocalDate, LocalDate): boolean
        + actualizarEstadoSegunCarga(): void
    }

    class ServicioAdicional {
        - String codigo
        - String nombre
        - String descripcion
        - double precio
        - boolean disponible
    }

    class Proyecto {
        - String codigo
        - LocalDate fechaSolicitud
        - LocalDate fechaInicio
        - LocalDate fechaEntrega
        - EstadoProyecto estado
        - MetodoPago metodoPago
        - double valorTotal
        - Cliente contratante
        - List<Desarrollador> desarrolladoresAsignados
        - List<ServicioAdicional> serviciosUtilizados
        + calcularDiasDesarrollo(): long
        + recalcularValorTotal(): double
    }
}

package "co.edu.uniquindio.devplus.service" {
    class DevPlusService {
        - Empresa empresa
        + crearProyecto(...)
        + asignarDesarrolladorAProyecto(String, String)
        + asociarServicioAProyecto(String, String)
        + confirmarProyecto(String)
        + cambiarEstadoProyecto(String, EstadoProyecto)
        + verificarTelefonoClienteNumeroPerfecto(String): boolean
        + calcularIngresosPorFecha(LocalDate): double
    }
}

Empresa "1" *-- "0..*" Cliente : administra
Empresa "1" *-- "0..*" Desarrollador : gestiona
Empresa "1" *-- "0..*" Proyecto : supervisa
Empresa "1" *-- "0..*" ServicioAdicional : oferta
Cliente "1" o-- "0..*" Proyecto : proyectosContratados
Proyecto "0..*" o-- "1..*" Desarrollador : desarrolladoresAsignados
Proyecto "0..*" o-- "0..*" ServicioAdicional : serviciosUtilizados
DevPlusService --> Empresa : opera
@enduml
```

---

## 3. Instrucciones de Compilación y Ejecución

### Opción 1: Ejecutar la Interfaz Gráfica (GUI Swing)
* **Doble clic en `ejecutar_gui.bat`** en la raíz del proyecto.
* O por consola:
  ```bash
  java -cp target/classes co.edu.uniquindio.devplus.gui.VentanaPrincipal
  ```

### Opción 2: Ejecutar la Aplicación de Consola (CLI interactivo)
* **Doble clic en `ejecutar.bat`** en la raíz del proyecto.
* O por consola:
  ```bash
  java -jar DevPlus.jar
  ```
  *(Nota: Desde el menú de consola, la opción `13` también permite abrir la Interfaz Gráfica).*

### Opción 3: Ejecución de Pruebas Unitarias Automatizadas (JUnit 5)
```bash
java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path "target/classes;target/test-classes" --scan-class-path
```

---

## 4. Guion de Sustentación en Video (~10 Minutos)

* **Formato:** Video en YouTube con audio, pantalla compartida y cámara web activa de ambos integrantes.

| Tiempo | Integrante | Tema a Explicar | Contenido Clave |
| :--- | :--- | :--- | :--- |
| **00:00 - 02:00** | **Integrante 1** | Introducción y Pensamiento Computacional | Explicar Abstracción (qué pide DevPlus, datos relevantes, clases), Descomposición (capas model, service, exception, util, app), y Reconocimiento de Patrones (interval scheduling y algoritmo $O(\sqrt{N})$ para número perfecto). |
| **02:00 - 04:00** | **Integrante 2** | Diagrama de Clases UML | Explicar las 5 entidades, los 4 enums, la justificación de Composición de la Empresa, la Agregación de Cliente a Proyecto, y la relación de asignación con Desarrolladores y Servicios Adicionales. Roles y multiplicidades. |
| **04:00 - 06:30** | **Integrante 1** | Arquitectura y Código Fuente (Service y Modelo) | Mostrar la clase `DevPlusService.java` y `Desarrollador.java`: explicar el método de detección de solapamiento de fechas `tieneCapacidadEnFechas()`, la liquidación con 10% de descuento frecuente (`recalcularValorTotal()`), y la máquina de estados. |
| **06:30 - 08:30** | **Integrante 2** | Demostración Práctica en Consola (Demo) | Ejecutar la aplicación en vivo: Probar la Opción 10 con el teléfono `8589869056` (demostrar que es número perfecto) y `3154879201` (no perfecto). Probar la Opción 11 con ingresos del `2026-09-24`. Probar la Opción 7 mostrando la liquidación detallada con el 10% de descuento. |
| **08:30 - 10:00** | **Ambos** | Pruebas Unitarias, Control de Versiones y Conclusiones | Ejecutar las 18 pruebas de JUnit 5 en verde. Mostrar el historial de commits en GitHub con la participación equitativa de ambos y dar cierre profesional. |

---

## 5. Plan de Commits para Trabajo Colaborativo en Git

Para evidenciar la autoría de ambos estudiantes mediante commits identificables:

* **Estudiante 1 (Arquitectura Base, Modelo y Excepciones):**
  1. `docs: configuracion inicial del proyecto, pom.xml y documentacion de pensamiento computacional`
  2. `feat(model): creacion de enums (EstadoProyecto, MetodoPago, NivelDesarrollador, EstadoDesarrollador)`
  3. `feat(model): implementacion de entidades Cliente, Desarrollador y ServicioAdicional con encapsulamiento`
  4. `feat(exception): definicion de excepciones de dominio (DesarrolladorNoDisponible, ServicioNoDisponible, TransicionInvalida)`
  5. `feat(service): implementacion de DevPlusService con validaciones RN1, RN2 y solapamiento de fechas RN3`

* **Estudiante 2 (Lógica Matemática, Utilidades, UI Consola y Pruebas):**
  6. `feat(util): implementacion de ConsolaUtil para lectura robusta y ValidadorUtil con algoritmo O(sqrt(N))`
  7. `feat(service): implementacion de funcionalidades especiales RN7 (numero perfecto) y RN8 (ingresos por fecha)`
  8. `feat(app): implementacion de Main con menu interactivo y precarga de datos semilla (seed data)`
  9. `test(service): implementacion de suite de pruebas unitarias JUnit 5 para RN1, RN2, RN3, RN6, RN7 y RN8`
  10. `docs: creacion de guia de ejecucion, casos de prueba para sustentacion y diagramas UML finales`
