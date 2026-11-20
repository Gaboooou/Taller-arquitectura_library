# Reglas del Agente y Contexto del Refactor (`agent-rules.md`)

> **Propósito:** Este archivo almacena las reglas operativas obligatorias para el asistente (Antigravity) y el plan maestro completo de refactorización del proyecto **Project Library** (`refactor.pdf`) para mantener el contexto entre sesiones y pasos.

---

## 1. Reglas Operativas del Agente

### Regla 1: Lectura Obligatoria de Contexto
- Antes de responder o avanzar en cualquier paso del proyecto, el agente **debe leer y respetar** este archivo (`docs/agent-rules.md`).
- Cualquier nueva regla indicada por el usuario en el chat debe integrarse y persistirse en esta sección.

### Regla 2: Registro Continuo y Literal de Prompts (`docs/prompts-stage-3.md`)
- En cada interacción del **Stage 3**, el agente debe registrar el prompt utilizado por el usuario dentro de `docs/prompts-stage-3.md` en orden cronológico (los prompts de etapas previas permanecen cerrados en `docs/prompts-stage-1.md` y `docs/prompts-stage-2.md`).
- Por cada entrada se debe incluir obligatoriamente (según Sección 6 y Apéndice A de `refactor.stage-3.pdf`):
  1. **Número** correlativo de la interacción.
  2. **ID exacto del modelo** utilizado.
  3. **Texto exacto del prompt copiado literalmente (*verbatim*)**, sin resumir ni alterar.
  4. **Lista exacta de archivos creados o modificados** en ese paso (el log debe coincidir exactamente con el código entregado; de lo contrario se pierden los 5 puntos).

### Regla 3: Metodología de Avance Pedagógico (Modo Arquitecto e Ingeniero Senior)
Cada vez que el usuario solicite avanzar a un paso del plan, el agente debe estructurar su respuesta con:
1. **Plan de Avance del Paso**: Objetivo arquitectónico, errores del documento que corrige (`Error #...`), archivos involucrados, adaptaciones necesarias al código actual del repositorio y dependencias.
2. **Guía "Hazlo tú mismo" (Aprendizaje Activo)**:
   - Explicación clara, paso a paso, de qué cambios debe realizar el estudiante en el código y **por qué** se diseñan así (contratos explícitos, límites remotos, mapeo de códigos de estado gRPC, deadlines, cohesión, desacoplamiento, etc.).
   - En los **Cambios Guiados (1 al 10 de Stage 3)**: entregar los fragmentos exactos y advertir las adaptaciones requeridas por la estructura DAO actual (`dao/Interface/MemberDao.java` y `dao/OrmliteMemberDao.java`), dejando que el estudiante los implemente y luego revisándolos.
   - En el **Cambio 11 (Ejercicio abierto: `SearchMembers`)**: **NO entregar una solución cerrada para copiar y pegar**; guiar con arquitectura, firmas, pistas técnicas y casos de aceptación para que el estudiante escriba su propia implementación y luego el agente la revise exhaustivamente contra la rúbrica.
3. **Estrategia de Verificación y Revisión de Errores**:
   - Comandos exactos de verificación (`./gradlew generateProto`, `./gradlew build`, `./gradlew test --tests ...`, `./gradlew run`, `./gradlew runClient`) usando JDK 21+.
   - Checklist del criterio *"Done when"* de la rúbrica (Sección 6) y la *Definition of Done* (Sección 3.3).
   - Al avisar el estudiante que completó un paso, el agente inspeccionará los archivos modificados, ejecutará compilación/pruebas y entregará retroalimentación puntual antes de avanzar al siguiente paso.

### Regla 4: Restricciones Técnicas y Contexto del Repositorio (Stage 3)
- **Entorno Java y Rutas:** El proyecto requiere **Java 21+** (JDK disponible en `C:\Users\Gabriel Briones\.jdks\openjdk-21.0.2`), Gradle wrapper `9.7.1`, y una **ruta de proyecto solo ASCII** en Windows (sin caracteres como `°` o `ñ`, ya que `protoc` falla con rutas no-ASCII). Con el plugin `0.10.1`, los fuentes generados quedan en `build/generated/sources/proto/main/` (`java/` y `grpc/`).
- **Versiones fijadas (NO alterar):**
  - Todos los artefactos `io.grpc:*` en versión `1.85.1` (`grpc-protobuf`, `grpc-stub`, `grpc-netty-shaded`, `grpc-inprocess`, `protoc-gen-grpc-java`).
  - `com.google.protobuf:protoc` en versión `3.25.9` (**nunca** `4.x`).
  - Plugin `com.google.protobuf` versión `0.10.1`.
  - `org.apache.tomcat:annotations-api:6.0.53` en scope `compileOnly`.
- **Arquitectura DAO local del repositorio:** A diferencia de los listados base del PDF que usan una clase concreta `dao.MemberDao`, este repositorio utiliza interfaces en `cl.ucn.disc.arqsist.library.dao.Interface.*` (`MemberDao`, `BookDao`, `LoanDao`, `ReservationDao`, `CrudDao`) e implementaciones ORMLite en `cl.ucn.disc.arqsist.library.dao.Ormlite*Dao`. Todas las referencias en `MemberService`, `App`, `MemberGrpcTest`, `MemberSearchTest` y el Cambio 11 deben respetar este diseño.
- **Límite de capas en gRPC:** `MemberGrpcService` nunca toca el DAO directamente (solo habla con `MemberService`). `io.grpc.Status` nunca entra al servicio ni al DAO. El *blocking stub* solo se usa en `MemberClient` / `MemberClientApp`, nunca dentro de un handler REST de Javalin.
- **Fuera de alcance en Stage 3:** Streaming RPCs, TLS/autenticación/autorización, reflection/health checks/load balancing, gRPC-Web, gRPC para book/loan/reservation, nuevo comportamiento REST.

---

## 2. Resumen de Errores Arquitectónicos

### 2.1 Errores de Stages 1 y 2 (`refactor.pdf` Sección 5 — Cerrados)

| # | Error | Categoría | Impacto | Corregido por Cambios | Etapa |
|---|-------|-----------|---------|-----------------------|-------|
| 1 | **Layering violation** | Layering | `GET /books` salta la capa de servicio llamando directo al DAO | Cambio 12 | Stage 2 |
| 2 | **God service** | Cohesión | `MemberService` maneja tanto ciclo de vida de socios como préstamos (`checkout`) | Cambios 7, 8, 12 | Stage 2 |
| 3 | **Missing transaction** | Integridad de datos | Un fallo a mitad de `checkout` o `returnLoan` deja `availableCopies` inconsistente | Cambios 5, 8 | Stage 1, 2 |
| 4 | **Cross-module coupling** | Acoplamiento | Dos servicios modifican `availableCopies` directamente vía `BookDao` saltándose `BookService` | Cambios 6, 8, 9 | Stage 1, 2 |
| 5 | **Duplicated loan period** | Duplicación | `checkout` usa 14 días y `fulfill` usa 21 días (`LoanService.DUE_DAYS` sin usar) | Cambios 3, 8, 9 | Stage 1 |
| 6 | **No availability guard** | Invariante de dominio | `availableCopies` puede volverse negativo al prestar sin copias disponibles | Cambios 6, 8 | Stage 1, 2 |
| 7 | **Fulfill skips inventory** | Invariante de dominio | `ReservationService.fulfill` crea el préstamo pero nunca descuenta `availableCopies` | Cambio 9 | Stage 2 |
| 8 | **Missing transaction in fulfill** | Integridad de datos | Si falla la creación del préstamo en `fulfill`, la reserva queda marcada como `fulfilled` sin préstamo | Cambios 5, 9 | Stage 1, 2 |
| 9 | **No trust-boundary checks** | Validación | IDs inexistentes producen error 500 o escritura parcial en vez de 404 (`NotFoundException`) | Cambios 4, 6, 8, 9, 11 | Stage 2 |
| 10 | **String dates & wall clock** | Modelo de dominio | Fechas como `String` y uso de `LocalDate.now()` directo impiden tests deterministas | Cambios 2, 8, 9, 11 | Stage 1, 2 |
| 11 | **Hardcoded fee rate** | Política | Multa mágica (`1.0`) y préstamos vencidos abiertos muestran `0.00` en listados | Cambios 3, 8 | Stage 2 |
| 12 | **Silent return** | Contrato API | Devolver un préstamo inexistente o ya devuelto responde 200 en vez de 404 / 409 | Cambios 4, 8, 11 | Stage 2 |

### 2.2 Errores de Stage 3 (`refactor.stage-3.pdf` Sección 5 — En curso)

| # | Error | Categoría | Impacto | Corregido por Cambios | Test |
|---|-------|-----------|---------|-----------------------|------|
| 13 | **No explicit contract** | Contrato | Un cliente depende de una forma JSON implícita sin esquema validado en compilación | Cambio 2 | `MemberGrpcTest` |
| 14 | **No remote boundary** | Acoplamiento | El módulo de miembros solo es alcanzable dentro del mismo proceso JVM | Cambios 4, 5, 6, 7 | `MemberGrpcTest` |
| 15 | **No status mapping** | Contrato API | Un miembro inexistente o argumento inválido llega al cliente como fallo genérico (`UNKNOWN`) | Cambios 4, 6 | `MemberGrpcTest` |
| 16 | **No deadline** | Confiabilidad | Un servidor colgado bloquea indefinidamente al cliente | Cambio 6 | *(ninguno)* |
| 17 | **No round trip test** | Verificación | El contrato de red y el mapeo de estados quedan sin verificar de extremo a extremo | Cambio 8 | `MemberGrpcTest` |
| 18 | **No search path** | Contrato | El llamador debe conocer el ID de BD para leer un miembro; no puede buscar por nombre | Cambio 11 | `MemberSearchTest` |

---

## 3. Plan Maestro del Refactor en Pasos

### Resumen de Stage 1 (Completado — 38 puntos)
- **Cambio 1a (1 pt):** Headers de licencia y Javadoc en archivos tocados en Stage 1.
- **Cambio 2, 2b, 2c (11 pts):** Fechas tipadas con `LocalDate` en `Loan` y `Reservation`, creación de `db/LocalDatePersister.java`, eliminación de `Loan.isOverdue()`, adaptación de *call sites* y aprobación de `DueDateDuplicationTest`.
- **Cambio 3 (1 pt):** Creación de `service/LoanPolicy.java` (`DUE_DAYS = 21`, `FEE_PER_DAY = 1.0`, `dueDate(LocalDate)`).
- **Cambio 4 (1 pt):** Creación de `service/NotFoundException.java` extendiendo `RuntimeException`.
- **Cambio 5 (8 pts):** Creación de `dao/BaseDao.java` con CRUD genérico y `transaction(Callable<R>)`.
- **Cambio 6 (3 pts):** Guardia de inventario en `BookService.borrow()` (`NotFoundException` si no existe, `IllegalStateException` si `availableCopies <= 0`) y `BookService.returnCopy()`.
- **Cambio 10 (5 pts):** Nuevo seed en `db/Database.java` (3 socios, 1 reserva, 3 préstamos: devuelto, activo, atrasado; logging con SLF4J; sin helper `createLoan`).
- **Cambio 13 (1 pt):** Cálculo de `isOverdue(l)` en el navegador (`app.js`).
- **Cambio 14 (1 pt):** Nivel `DEBUG` para `cl.ucn.disc` en `logback.xml`.
- **Cambio 16a (1 pt):** Actualización de `docs/class-diagram.puml` para Stage 1.
- **Log IA Stage 1 (5 pts):** `docs/prompts-stage-1.md`.

---

### Plan Paso a Paso de Stage 2 (Completado — 42 puntos)

- **Paso 1: Cambio 7 — Limpieza de `MemberService` (2 pts) [COMPLETADO]**
- **Paso 2: Cambio 8 — Refactorización de `LoanService` (12 pts) [COMPLETADO]**
- **Paso 3: Cambio 9 — Refactorización de `ReservationService` (8 pts) [COMPLETADO]**
- **Paso 4: Cambio 12 — Refactorización de Controladores (3 pts) [COMPLETADO]**
- **Paso 5: Cambio 11 — Cableado de la Aplicación en `App.java` (5 pts) [COMPLETADO]**
- **Paso 6: Cambio 15 — Actualización y Creación de Tests (5 pts) [COMPLETADO]**
- **Paso 7: Cambio 1b — Licencias y Javadoc Completo en Stage 2 (1 pt) [COMPLETADO]**
- **Paso 8: Cambio 16b — Actualización del Diagrama de Clases (1 pt) [COMPLETADO]**
- **Paso 9: Log de Prompts de Stage 2 (`docs/prompts-stage-2.md`) (5 pts) [COMPLETADO]**

---

### Plan Paso a Paso de Stage 3: The gRPC Member Service (En curso — 40 puntos)

> **Rama de trabajo:** `stage-3` | **Documento fuente:** `refactor.stage-3.pdf` (v1.6)

- **Paso 1: Cambio 1 — Configuración de Build (`build.gradle`) (2 pts) [COMPLETADO]**
  - Plugin `com.google.protobuf` `0.10.1`, dependencias `io.grpc:*:1.85.1`, `protoc:3.25.9`, `annotations-api:6.0.53`, `grpc-inprocess:1.85.1` y tarea `runClient`.
- **Paso 2: Cambio 2 — Contrato Protocol Buffers (`src/main/proto/member.proto`) (3 pts) [COMPLETADO]**
  - Cierra **Error #13**. Define `MemberService` con 3 RPCs unarios (`RegisterMember`, `ListMembers`, `GetMember`) y sus mensajes. Verificado con `./gradlew generateProto build` (fuentes generados en `build/generated/sources/proto/main/`).
- **Paso 3: Cambio 3 — Agregar `findById(int)` en `MemberService` (1 pt) [COMPLETADO]**
  - Lanza `NotFoundException` cuando `memberDao.findById(id)` retorna `null`.
- **Paso 4: Cambio 4 — Adaptador Servidor gRPC (`grpc/MemberGrpcService.java`) (5 pts) [COMPLETADO]**
  - Cierra **Errores #14 y #15** (lado servidor). Extiende `MemberServiceGrpc.MemberServiceImplBase`, define `HOST = "127.0.0.1"` y `PORT = 50051`, implementa los 3 RPCs, `toError(...)` y `toMessage(...)`.
- **Paso 5: Cambio 5 — Iniciar Servidor gRPC en `App.java` (3 pts) [COMPLETADO]**
  - Cierra **Error #14** (cableado). Inicia `NettyServerBuilder` en `127.0.0.1:50051` con shutdown hook gracioso (`stop(Server)` con 5s) + shutdown hook para Javalin, conservando los DAOs `Ormlite*Dao` del proyecto.
- **Paso 6: Cambio 6 — Cliente gRPC (`grpc/MemberClient.java`) (5 pts) [COMPLETADO]**
  - Cierra **Errores #14, #15 (lado cliente) y #16**. Implementa `AutoCloseable`, gestiona `ManagedChannel` y `MemberServiceBlockingStub`, aplica `withDeadlineAfter(5, TimeUnit.SECONDS)` en cada llamada, mapea errores con `toException(...)` y convierte con `toMember(...)`.
- **Paso 7: Cambio 7 — Cliente CLI (`MemberClientApp.java`) (1 pt) [PENDIENTE]**
  - Cierra **Error #14** (punto de entrada cliente). Lista todos los miembros y busca IDs pasados como argumentos; maneja errores y sale con código `1`.
- **Paso 8: Cambio 8 — Test Round-Trip (`MemberGrpcTest.java`) (3 pts) [PENDIENTE]**
  - Cierra **Error #17** (y verifica #13, #14, #15). Usa SQLite en memoria (`jdbc:sqlite::memory:`) y transporte in-process (`InProcessServerBuilder` / `InProcessChannelBuilder`) con `OrmliteMemberDao`.
- **Paso 9: Cambio 9 — Documentación gRPC en `README.md` (1 pt) [PENDIENTE]**
  - Agrega sección `## gRPC API` tras la sección REST API con tabla de RPCs, límites de seguridad y comandos de ejecución.
- **Paso 10: Cambio 10 — Actualizar Diagrama de Clases (`docs/class-diagram.puml`) (1 pt) [PENDIENTE]**
  - Agrega `skinparam package<<grpc>>`, paquete `grpc` (clases del proyecto y generadas con nota) y sus relaciones.
- **Paso 11: Cambio 11 — Ejercicio Abierto: Búsqueda de Miembros (`SearchMembers`) (10 pts) [PENDIENTE]**
  - Cierra **Error #18**. Contrato `SearchMembers` en `member.proto`, consulta literal case-insensitive con `SelectArg` y orden por `id ASC` en `dao/Interface/MemberDao.java` + `dao/OrmliteMemberDao.java`, trim y validación en `MemberService.search(String)`, adaptador en `MemberGrpcService`, método `search(String)` con deadline en `MemberClient`, y los 7 casos de aceptación en `MemberSearchTest.java`.
- **Log IA Stage 3 (`docs/prompts-stage-3.md`) (5 pts) [EN CURSO]**
  - Actualizado en cada interacción del Stage 3 con el formato exigido en la rúbrica y Apéndice A.

