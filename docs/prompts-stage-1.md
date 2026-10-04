# Log de Prompts - Stage 1

> **Modelo utilizado:** Gemini 3.1 Pro y Claude sonnet 5.5 *(Antigravity System)*
> **Fecha de ejecución:** Octubre 2026
> **Propósito:** Registro detallado de prompts e interacciones para completar el Stage 1 de refactorización y estandarización.

---
## Prompt Inicial - Plan de mejora
**Prompt literal del usuario:**
>Adjunto el documento "Refactoring Plan: Project Library" (refactor.pdf). Necesito realizar únicamente la Parte 1 (Stage 1, sección 6.1 de la rúbrica, 38 puntos).

>Por favor, organiza las especificaciones de Stage 1 como una guía paso a paso, siguiendo un orden de trabajo recomendado que respete las dependencias entre cambios (por ejemplo, crear primero las clases que otros cambios necesitan, y dejar la documentación y el log de prompts para el final).

>Para cada paso incluye:
>1. El número del cambio del documento y su nombre.
>2. Los archivos que se crean o modifican.
>3. Las especificaciones exactas que indica el documento, incluyendo los fragmentos de código cuando los haya.
>4. El criterio "Hecho cuando" de la rúbrica.
>5. Los puntos que vale.

>Condiciones:
>- Usa solo lo que dice el documento. Si agregas una interpretación propia, márcala como "Nota mía".
>- No incluyas cambios de Stage 2.
>- Cierra con una tabla resumen de puntos por paso que sume 38.

## Paso 6: Cambio 3 - Política de Préstamos (`LoanPolicy`)
**Prompt literal del usuario:**
> "Me ayudas con esto, es una parte del plan para este codigo: Cambio 3: LoanPolicy Ubicación: paquete service. Contenido exacto: Dos constantes públicas y estáticas: DUE_DAYS (entero, valor 21) y FEE_PER_DAY (double, valor 1.0). Un método estático dueDate(LocalDate loanDate) que retorna loanDate.plusDays(DUE_DAYS). Un constructor privado vacío (para que nadie instancie la clase). Dónde usarlo en esta etapa: busca en tu código actual las líneas que dicen plusDays(14) (en MemberService.checkout) y plusDays(21) (en ReservationService.fulfill). Reemplázalas por LoanPolicy.dueDate(...). No muevas todavía el método checkout() de lugar — eso es la Etapa 2. Cómo saber si quedó bien: corre DueDateDuplicationTest; debe pasar al final de esta etapa porque ambos sitios ahora usan la misma regla."

**Interacciones adicionales:**
> "me das un plan estructurado para llevar los cambios a realizar, y revisar si son correctos"

**Archivos modificados:**
* *(Evaluación del entorno de pruebas. Errores de compilación Java 8 vs Java 17 detectados, sin modificaciones estructurales aún).*

---

## Paso 3: Cambio 4 - ORMLite Persister
**Prompt literal del usuario:**
> "Actua como un senior en programación y realiza la siguiente tarea, se debe extender BaseDataType de ORMLite. Constructor privado, con un campo estático SINGLETON y un método público getSingleton(). Implementa los 4 métodos exactamente como indica el plan: parseDefaultString, resultToSqlArg, sqlArgToJava, javaToSqlArg. Revisa bien las firmas — ORMLite es estricto con eso."

**Archivos modificados:**
* `cl/ucn/disc/arqsist/library/db/LocalDatePersister.java`


## Paso 6 (Continuación): Cambio 2b, 2c y 3 - Migración a LocalDate
**Prompt literal del usuario:**
> "Puedes darme un plan de como llevaras este paso como un ingeniero senior, Paso 6: Cambio 2b y 2c, Call sites y test (3 pts) MemberService, LoanService, ReservationService y Database deben compilar contra los campos LocalDate (2 pts). Aquí aplicas el Cambio 3: plusDays(14) y plusDays(21) pasan a LoanPolicy.dueDate(…). DueDateDuplicationTest debe pasar. TransactionBugTest puede seguir fallando (1 pt)."


**Archivos modificados:**
* `cl/ucn/disc/arqsist/library/service/LoanPolicy.java`
* `cl/ucn/disc/arqsist/library/service/MemberService.java`
* `cl/ucn/disc/arqsist/library/service/ReservationService.java`
* `cl/ucn/disc/arqsist/library/service/LoanService.java`
* `cl/ucn/disc/arqsist/library/model/Loan.java`
* `cl/ucn/disc/arqsist/library/model/Reservation.java`
* `cl/ucn/disc/arqsist/library/db/Database.java`

---

## Paso 7: Cambio 10 - Database Seeding
**Prompt literal del usuario:**
> "Actua como programador senior y hazme el plan para las siguientes indicaciones: Paso 7: Cambio 10, Seed (5 pts) Archivo: db/Database.java. Se mantiene la creación de tablas. 3 miembros (no 6). 1 reserva (no 2). 3 préstamos: devuelto, activo y atrasado. availableCopies baja solo por el activo y el atrasado. Cada paso con log.debug(…). Eliminar el helper createLoan. Logger: private static final Logger log = LoggerFactory.getLogger(Database.class); Préstamo devuelto: Loan returned = new Loan(members.getFirst(), books.getFirst(), today.minusDays(30), today.minusDays(9)); returned.setReturned(true); returned.setReturnDate(today.minusDays(10)); loanDao.create(returned);"

**Archivos modificados:**
* `cl/ucn/disc/arqsist/library/db/Database.java`
* `build.gradle` (Inyección de Jackson JSR310)
* `database.sqlite` (Reseteada)


## Paso 10: Limpieza Javadoc y Licencias
**Prompt literal del usuario:**
> "El Paso 10 se aplica a todo archivo que toques en Stage 1. Según los pasos anteriores, esta es la lista. (Lista de archivos Java nuevos y modificados detallada, más archivos no Java). Every type, field, method, and parameter has javadoc. SI lo pide lo puedes agregar porfavor."

**Archivos modificados:**
* _Los 15 archivos `.java` fueron reescritos con Headers, licencias y Javadoc completo en clases, métodos, campos y constructores._
* _Los archivos `index.html`, `app.js` y `logback.xml` recibieron su Header de Licencia._


## Paso 11: Log de prompts (Documentación Final)
**Prompt literal del usuario:**
> "Paso 11: Ayudame a recopilar los spronts utilizados, siguiendo las siguientes indicaciones,Log de prompts (5 pts) Archivo: docs/prompts-stage-1.md. Se crea desde el inicio y se llena en el momento de cada consulta. Todos los prompts del Stage 1, en orden. Por cada prompt: nombre exacto del modelo, texto exacto copiado literal y archivos que cambiaron. Si falta el archivo o no coincide con el código, se pierden los 5 puntos."

**Archivos modificados:**
* `docs/prompts-stage-1.md`

