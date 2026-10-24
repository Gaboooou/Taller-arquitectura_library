# Log de Prompts - Stage 2

> **Modelo utilizado:** Geminis Argon 4 *(Antigravity System)*
> **Fecha de ejecución:** Octubre 2026
> **Propósito:** Registro detallado y literal (*verbatim*) de los prompts e interacciones utilizados durante el Stage 2 de refactorización (Servicios e Integración).

---

## Prompt 1: Paso 2 (Consulta inicial) — `LoanService`
- **Modelo:** `Geminis Argon 4`
- **Prompt literal del usuario:**
> Como diseñador senior realiza el paso 2, y muestra un plan de como lo incluirias

- **Archivos modificados:**
  - *(Inspección inicial del estado de la rama `stage-2`; solicitud de especificaciones del Paso 2 sin modificar archivos aún).*

---

## Prompt 2: Paso 2 — Implementación de `LoanService` (Cambio 8) y limpieza de `MemberService` (Cambio 7)
- **Modelo:** `Geminis Argon 4`
- **Prompt literal del usuario:**
> iv.
> checkout(int memberId, int bookId):
> ▪
> Valida existencia de miembro y libro (lanza NotFoundException si no existen).
> ▪
> Ejecuta dentro de una transacción (loanDao.transaction(...)): bookService.borrow(bookId) y loanDao.create(loan).
> ▪
> Calcula la fecha de vencimiento con LoanPolicy.dueDate(today).
> v.
> returnLoan(int loanId):
> ▪
> Si el préstamo no existe: lanza NotFoundException.
> ▪
> Si loan.isReturned() es true: lanza IllegalStateException.
> ▪
> Ejecuta en una transacción: loanDao.update(loan) y bookService.returnCopy(bookId).
> ▪
> Aplica recargo de mora solo si today.isAfter(loan.getDueDate()).
> vi.
> overdueLoans():
> ▪
> Filtra préstamos con: !loan.isReturned() && loan.getDueDate().isBefore(today).
> vii.
> findAll():
> ▪
> Retorna withAccruedFees(loanDao.findAll()).
> viii.
> withAccruedFees(List<Loan> loans):
> ▪
> Asigna el monto de mora calculado a cada préstamo vencido y abierto (solo en memoria, no persiste en BD).
> ix.
> accruedFee(Loan loan, LocalDate today):
> ▪
> Retorna: ChronoUnit.DAYS.between(loan.getDueDate(), today) * LoanPolicy.FEE_PER_DAY.
> •
> Criterio de Rúbrica 6.2: LoanService owns checkout(), receives a Clock, and uses transactions. returnLoan() rejects a missing loan and a returned loan./C:/Users/Disc/Desktop/arquitectura/refactor.pdf

- **Archivos modificados:**
  - `src/main/java/cl/ucn/disc/arqsist/library/service/LoanService.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/service/MemberService.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/dao/Interface/CrudDao.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/dao/BaseDao.java`

---

## Prompt 3: Configuración de Reglas del Agente (`agent-rules.md`), Registro de Prompts y Guía del Paso 3 (`ReservationService`)
- **Modelo:** `Geminis Argon 4`
- **Prompt literal del usuario:**
> okey, avancemos con el paso tres, pero agrega reglas para ti, primero cree un archivo en docs que se llama agent-rules.md, aqui quiero que integres reglas que te indique, para que la lees como contexto, luego quiero que agregues los prompts que utilice, en prompts-stage-2.md, agrega esta instruccion como regla.
> Cada vez que quiera avanzar un paso quiero que me des una guia para avanzar,tipo plan de avance, y que hacer para hacerlo yo mismo, y aprender, ademas entender que haras para verficiar errores, agregala com orelga, ademas agrega en este archivo de rules, el plan en pasos a realizar de todo el refactor , para guardar contexto.

- **Archivos modificados:**
  - `docs/agent-rules.md`
  - `docs/prompts-stage-2.md`
  - `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`

---

## Prompt 4: Verificación del Paso 3 y Avance al Paso 4 (Controladores — Cambio 12)
- **Modelo:** `Geminis Argon 4`
- **Prompt literal del usuario:**
> Ya realice los cambios, sigamos al siguiente paso

- **Archivos modificados:**
  - `docs/prompts-stage-2.md`

---

## Prompt 5: Revisión del Paso 4 (Controladores — Cambio 12), Corrección de Javadoc/Licencias y Avance al Paso 5 (`App.java` — Cambio 11)
- **Modelo:** `Geminis Argon 4`
- **Prompt literal del usuario:**
> Ya he realizo el paso numero 4, avancemos al siguiente, revisalo para corregir errores y subirlo a git

- **Archivos modificados:**
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/BookController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/LoanController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/ReservationController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/controller/MemberController.java`
  - `src/main/java/cl/ucn/disc/arqsist/library/service/ReservationService.java`
  - `docs/prompts-stage-2.md`
