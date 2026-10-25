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
