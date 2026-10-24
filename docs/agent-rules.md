# Reglas del Agente y Contexto del Refactor (`agent-rules.md`)

> **Propósito:** Este archivo almacena las reglas operativas obligatorias para el asistente (Antigravity) y el plan maestro completo de refactorización del proyecto **Project Library** (`refactor.pdf`) para mantener el contexto entre sesiones y pasos.

---

## 1. Reglas Operativas del Agente

### Regla 1: Lectura Obligatoria de Contexto
- Antes de responder o avanzar en cualquier paso del proyecto, el agente **debe leer y respetar** este archivo (`docs/agent-rules.md`).
- Cualquier nueva regla indicada por el usuario en el chat debe integrarse y persistirse en esta sección.

### Regla 2: Registro Continuo y Literal de Prompts (`docs/prompts-stage-2.md`)
- En cada interacción del **Stage 2**, el agente debe registrar el prompt utilizado por el usuario dentro de `docs/prompts-stage-2.md` en orden cronológico.
- Por cada entrada se debe incluir obligatoriamente (según Sección 6.2 y Apéndice A de `refactor.pdf`):
  1. **Número / Paso correspondiente**.
  2. **Nombre exacto del modelo** utilizado.
  3. **Texto exacto del prompt copiado literalmente (*verbatim*)**, sin resumir ni alterar.
  4. **Lista exacta de archivos creados o modificados** en ese paso.

### Regla 3: Metodología de Avance Pedagógico (Modo Diseñador/Ingeniero Senior)
Cada vez que el usuario solicite avanzar a un paso del plan, el agente debe estructurar su respuesta con:
1. **Plan de Avance del Paso**: Objetivo arquitectónico, errores del documento que corrige (`Error #...`), archivos involucrados y dependencias.
2. **Guía "Hazlo tú mismo" (Aprendizaje Activo)**:
   - Explicación clara, paso a paso, de qué cambios debe realizar el estudiante en el código y **por qué** se diseñan así (principios de diseño, cohesión, desacoplamiento, atomicidad transaccional, etc.).
   - Fragmentos de referencia claros (firmas, validaciones y bloques clave) para que pueda implementarlo por su cuenta o pedir al agente que lo aplique tras intentarlo.
3. **Estrategia de Verificación de Errores**:
   - Qué validaciones hará el agente/usuario para comprobar que el paso quedó perfecto (inspección de problemas en el IDE, compilación con `./gradlew compileJava`, pruebas con `./gradlew test`, y checklist del criterio *"Done when"* de la rúbrica 6.2).

---

## 2. Resumen de los 12 Errores Arquitectónicos (`refactor.pdf` Sección 5)

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

---

## 3. Plan Maestro del Refactor en Pasos

### Resumen de Stage 1 (Completado — 38 puntos)
- **Cambio 1a (1 pt):** Headers de licencia y Javadoc en archivos tocados en Stage 1.
- **Cambio 2, 2b, 2c (11 pts):** Fechas tipadas con `LocalDate` en `Loan` y `Reservation`, creación de `db/LocalDatePersister.java`, eliminación de `Loan.isOverdue()` (nota: verificar que no quede en `Loan.java`), adaptación de *call sites* y aprobación de `DueDateDuplicationTest`.
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

### Plan Paso a Paso de Stage 2 (En curso — 42 puntos)

#### Paso 1: Cambio 7 — Limpieza de `MemberService` (2 pts) [COMPLETADO]
- **Archivo:** `src/main/java/cl/ucn/disc/arqsist/library/service/MemberService.java`
- **Corrige:** Error #2 (*God service*).
- **Especificaciones:**
  1. Eliminar `checkout(int, int)` y los campos `BookDao` y `LoanDao`.
  2. Mantener únicamente `private final MemberDao memberDao;` y el constructor `public MemberService(MemberDao memberDao)`.
  3. Eliminar `throws SQLException` de `register()` y `findAll()`.
- **Criterio "Done when":** `MemberService` tiene un solo DAO y `checkout()` ya no existe en esta clase.

#### Paso 2: Cambio 8 — Refactorización de `LoanService` (12 pts) [COMPLETADO]
- **Archivos:** `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java` (más soporte de `transaction` en `CrudDao.java` / `BaseDao.java`).
- **Corrige:** Errores #2, #3, #4, #10, #11 y #12.
- **Especificaciones:**
  1. Eliminar la constante antigua `DUE_DAYS`.
  2. Recibir en el constructor: `LoanDao loanDao, MemberDao memberDao, BookService bookService, Clock clock`.
  3. Obtener siempre la fecha actual mediante `LocalDate.now(clock)`.
  4. `checkout(int memberId, int bookId)`:
     - Validar existencia de `Member` y `Book` (lanzar `NotFoundException` si alguno es `null`).
     - Calcular vencimiento con `LoanPolicy.dueDate(today)`.
     - Ejecutar en una transacción (`loanDao.transaction(...)`): `bookService.borrow(bookId)` y `loanDao.create(loan)`.
  5. `returnLoan(int loanId)`:
     - Si `loan == null` lanzar `NotFoundException`.
     - Si `loan.isReturned()` es `true` lanzar `IllegalStateException`.
     - Marcar como devuelto, asignar `returnDate = today` y calcular mora solo si `today.isAfter(loan.getDueDate())`.
     - Ejecutar en una transacción: `loanDao.update(loan)` y `bookService.returnCopy(bookId)`.
  6. `overdueLoans()`: filtrar préstamos con `!loan.isReturned() && loan.getDueDate().isBefore(today)`.
  7. `findAll()`: retornar `withAccruedFees(loanDao.findAll())`.
  8. `withAccruedFees(List<Loan> loans)`: asignar en memoria la mora a cada préstamo abierto y vencido (sin persistir en BD).
  9. `accruedFee(Loan loan, LocalDate today)`: retornar `ChronoUnit.DAYS.between(loan.getDueDate(), today) * LoanPolicy.FEE_PER_DAY`.
- **Criterio "Done when":** `LoanService` es dueño de `checkout()`, recibe un `Clock`, usa transacciones, y `returnLoan()` rechaza préstamos inexistentes o ya devueltos.

#### Paso 3: Cambio 9 — Refactorización de `ReservationService` (8 pts) [ACTUAL]
- **Archivo:** `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`
- **Corrige:** Errores #4, #7, #8, #9 y #10.
- **Especificaciones:**
  1. Eliminar el campo `BookDao`.
  2. Recibir en el constructor: `ReservationDao reservationDao, MemberDao memberDao, LoanDao loanDao, BookService bookService, Clock clock`.
  3. `reserve(int bookId, int memberId)`:
     - Validar que el libro (`bookService.findById(bookId)`) y el socio (`memberDao.findById(memberId)`) existan; si alguno es `null`, lanzar `NotFoundException`.
     - Crear la reserva usando `new Reservation(member, book, LocalDate.now(clock))`.
  4. `fulfill(int reservationId)`:
     - Si la reserva no existe (`reservation == null`), lanzar `NotFoundException`.
     - Si la reserva ya fue cumplida (`reservation.isFulfilled()`), lanzar `IllegalStateException`.
     - Calcular `today = LocalDate.now(clock)` y `dueDate = LoanPolicy.dueDate(today)`.
     - Ejecutar en una sola transacción (`reservationDao.transaction(...)`):
       1. Marcar `reservation.setFulfilled(true)` y actualizarla (`reservationDao.update(reservation)`).
       2. Descontar copia del inventario mediante `bookService.borrow(reservation.getBook().getId())`.
       3. Crear y persistir el préstamo (`loanDao.create(loan)`).
- **Criterio "Done when":** `fulfill()` usa una transacción y `BookService.borrow()`, y el campo `BookDao` fue eliminado.

#### Paso 4: Cambio 12 — Refactorización de Controladores (3 pts)
- **Archivos:**
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/BookController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/LoanController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/ReservationController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/MemberController.java`
- **Corrige:** Errores #1 y #2.
- **Especificaciones:**
  1. `BookController`: eliminar `BookDao` (campo, import y parámetro si quedara) y asegurar que `GET /books` llame a `service.listAll()`.
  2. `LoanController`: eliminar el campo y parámetro `MemberService`. Recibir solo `LoanService`. En `POST /loans` llamar a `loanService.checkout(memberId, bookId)`. Eliminar `Objects.requireNonNull` si aplica.
  3. `ReservationController`: eliminar las dos llamadas a `Objects.requireNonNull` y el import `java.util.Objects`.
  4. `MemberController`: sin cambios funcionales; asegurar licencia y Javadoc completo.
- **Criterio "Done when":** Los controladores llaman únicamente a los servicios; sin `BookDao`, sin `MemberService` en `LoanController`, y sin `Objects.requireNonNull`.

#### Paso 5: Cambio 11 — Cableado de la Aplicación en `App.java` (5 pts)
- **Archivo:** `src/main/java/cl/ucn/disc/arqsist/library/App.java`
- **Corrige:** Errores #9, #10 y #12 (mapeo HTTP y reloj del sistema).
- **Especificaciones:**
  1. Cambiar la URL de la base de datos a `jdbc:sqlite:database.db` (en lugar de `database.sqlite`).
  2. Crear el reloj con `Clock.system(ZoneId.of("America/Santiago"))`.
  3. Agregar logger: `private static final Logger log = LoggerFactory.getLogger(App.class);` y registrar cada paso con `log.info(...)`.
  4. Actualizar la instanciación de servicios:
     - `MemberService(memberDao)`
     - `LoanService(loanDao, memberDao, bookService, clock)`
     - `ReservationService(reservationDao, memberDao, loanDao, bookService, clock)`
  5. Actualizar la instanciación de controladores:
     - `BookController(bookService)`
     - `LoanController(loanService)`
  6. Configurar `jsonMapper` en Javalin para deshabilitar `SerializationFeature.WRITE_DATES_AS_TIMESTAMPS`.
  7. Registrar los 3 manejadores de excepciones en `config.routes`:
     - `NotFoundException.class` -> HTTP `404` con `Map.of("error", e.getMessage())`
     - `IllegalArgumentException.class` -> HTTP `400` con `Map.of("error", e.getMessage())`
     - `IllegalStateException.class` -> HTTP `409` con `Map.of("error", e.getMessage())`
- **Criterio "Done when":** `App` usa `database.db`, el reloj de Santiago, el nuevo cableado, el date mapper y los 3 exception handlers.

#### Paso 6: Cambio 15 — Actualización y Creación de Tests (5 pts)
- **Archivos:**
  - `src/test/java/cl/ucn/disc/arqsist/library/DueDateDuplicationTest.java`
  - `src/test/java/cl/ucn/disc/arqsist/library/TransactionBugTest.java`
  - `src/test/java/cl/ucn/disc/arqsist/library/AccruedFeeTest.java` (nuevo)
  - `src/test/java/cl/ucn/disc/arqsist/library/DatabaseSeedTest.java` (nuevo)
- **Especificaciones:**
  1. `DueDateDuplicationTest`: reemplazar `MemberService` por `LoanService`; usar reloj fijo `Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC)` para ambos servicios; crear el libro de prueba con 2 copias (`totalCopies = 2`); llamar a `loanService.checkout(...)`.
  2. `TransactionBugTest`: reemplazar `MemberService` por `LoanService`; esperar `NotFoundException.class` en `assertThrows(NotFoundException.class, () -> loanService.checkout(9999, book.getId()))`.
  3. `AccruedFeeTest` (nuevo): base de datos en memoria, libro, socio y reloj fijo (`2026-01-15T10:00:00Z`). Crear un préstamo vencido (con `dueDate` `2025-12-22`) y uno activo. Llamar a `loanService.findAll()` y verificar mora de `24.0` para el vencido y `0.0` para el activo.
  4. `DatabaseSeedTest` (nuevo): base de datos en memoria, llamar a `db.seedIfEmpty()`. Verificar que existan préstamos y reservas, que haya un préstamo vencido, y que todo libro con préstamo abierto tenga menos copias disponibles. Llamar a `db.seedIfEmpty()` por segunda vez y verificar que la cantidad de préstamos y reservas no cambie.
- **Criterio "Done when":** Los 4 tests existen y pasan con `./gradlew test`.

#### Paso 7: Cambio 1b — Licencias y Javadoc Completo en Stage 2 (1 pt)
- **Archivos:** Todos los archivos tocados en Stage 2 (`MemberService`, `LoanService`, `ReservationService`, `BookController`, `LoanController`, `ReservationController`, `MemberController`, `App`, `CrudDao`, `BaseDao`, y los 4 tests).
- **Especificaciones:** Header de copyright 2026 y Javadoc en cada clase, atributo, constructor y método (`@param`, `@return`, `@throws`), además de verificar que `Loan.isOverdue()` no exista en `model/Loan.java`.

#### Paso 8: Cambio 16b — Actualización del Diagrama de Clases (1 pt)
- **Archivo:** `docs/class-diagram.puml`
- **Especificaciones:** Reflejar los nuevos atributos/métodos de `MemberService`, `LoanService` y `ReservationService`, así como las nuevas dependencias de los controladores y servicios.

#### Paso 9: Log de Prompts de Stage 2 (5 pts)
- **Archivo:** `docs/prompts-stage-2.md`
- **Especificaciones:** Verificar que todos los prompts de Stage 2 estén registrados en orden, con modelo exacto, texto literal y archivos modificados.
