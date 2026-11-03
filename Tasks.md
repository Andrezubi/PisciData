# TASKS.md: Conectar Frontend con Backend

## 0. Reglas previas (obligatorio)

* Leer y respetar los `AGENTS.md` de ambos proyectos (frontend y backend) antes de tocar código.
* Leer y respertar la arquitectura de la base de datos en `DATABASE.md`
* No inventar arquitectura nueva: seguir los módulos de referencia (`catalog` en el frontend, `Farm` en el backend).
* Antes de implementar, mostrar el plan con este formato y esperar confirmación:

```text
Archivos que se crearán:
Archivos que se modificarán:
Archivos que NO necesitan cambios:
Motivo:
```

* Trabajar paso a paso: una tarea (T1, T2...) a la vez, compilando al terminar cada una.
* Código en inglés. Todo texto visible para el usuario en español.
* No reutilizar datos de ejemplo (mock) como fuente de verdad: los datos deben venir de la API.

---

## 1. Objetivo general

Conectar el frontend (Kotlin Multiplatform) con el backend (.NET) mediante peticiones HTTP, de modo que el usuario autenticado vea los datos reales de su piscigranja actual.

Utiliza el httpclient creado para la pagina de auth para hacer el resto de httpclients en el frontend
---

## 2. Tareas

### T1. Login y obtención del usuario

* El frontend hace login contra el backend (teléfono y contraseña).
* El backend ya responde con un JWT que contiene el claim `UserId`. No se necesita modificar el login del backend; confirmar el nombre exacto del claim revisando el código que genera el token.
* El frontend decodifica el payload del JWT (Base64 URL-safe + JSON con `kotlinx.serialization`), extrae el `UserId` y lo guarda en una sesión en memoria junto con el token. No agregar una librería de JWT sin explicar por qué es necesaria; primero intentar con las herramientas que ya tiene el proyecto.
* **Almacenamiento del token:** la sesión vive solo en memoria. No se persiste el token en disco (ni Keychain, ni EncryptedSharedPreferences, ni similares). Al cerrar la app, el usuario vuelve a iniciar sesión. Esto es una decisión deliberada para el alcance actual; no "mejorarla" sin autorización.
* El frontend solo lee el payload para obtener datos de la sesión: no valida la firma del token (eso lo hace el backend).
* El frontend envía el token en cada petición protegida mediante el header `Authorization: Bearer <token>`, configurado en un solo lugar (el `HttpClient` de Ktor), sin repetirlo en cada Service.
* **Expiración del token y respuestas `401`:** el manejo se configura en ese mismo `HttpClient`, no en cada Service. Ante un `401` en una petición protegida:
  * Se limpia por completo la sesión (token, `UserId` y `currentFarmId`).
  * Se redirige a la pantalla de login.
  * Se muestra un mensaje en español, por ejemplo: "Tu sesión expiró. Inicia sesión nuevamente."
  * El `401` del propio endpoint de login (credenciales incorrectas) NO debe disparar esta redirección: ahí se muestra el error normal de login.
* El backend NUNCA recibe el `userId` desde el cliente: lo obtiene siempre del JWT.

### T2. Lista de piscigranjas y piscigranja actual

* Backend: endpoint que devuelve las piscigranjas del usuario autenticado (`IsActive = true` y `OwnerUserId` = usuario del JWT). Crear el DTO, la ruta del Controller, la función del Service y la del Repository.
* Frontend: al entrar a la app, pedir la lista de piscigranjas y mostrarla.
* Frontend: guardar la piscigranja seleccionada como `currentFarmId` en un estado compartido y observable (por ejemplo `StateFlow` en la sesión), siguiendo el patrón de DI con Koin.
* `currentFarmId` es nulo (`null`) entre el login y la selección de una granja, y también cuando el usuario no tiene granjas. Mientras sea `null`, las pantallas de T3 y T4 NO deben disparar peticiones al backend; deben mostrar el estado correspondiente (selección pendiente o vacío).
* **Recarga al cambiar de granja:** los ViewModels de las pantallas que dependen de `currentFarmId` lo observan como `StateFlow` y usan `flatMapLatest` (o equivalente) para que, al cambiar de granja, se cancele la petición anterior y solo se procese la respuesta de la granja actual. Debe evitarse que lleguen datos de una granja anterior después de haber cambiado a otra (race condition).
* Si el usuario no tiene piscigranjas, mostrar un estado vacío en español.

### T3. Datos de la piscigranja actual: CycleRecord, Pond e InvItem

Las pantallas que muestran estos datos deben pedirlos al backend usando el `currentFarmId`.

Para cada una:

* Revisar primero el modelo/DTO que espera el frontend.
* Backend: crear un DTO nuevo que devuelva exactamente la forma que pide el frontend (mismos nombres de campos y tipos), la ruta del Controller, la función del Service y la función del Repository.
* Backend: el endpoint debe llevar `[Authorize]`.
* **Validación de propiedad (un solo punto):** crear **un único método reutilizable** que verifique que el `FarmId` recibido pertenece al usuario autenticado (por ejemplo `EnsureFarmOwnedByUser(farmId, userId)`, ubicado donde encaje con la arquitectura existente, siguiendo el módulo `Farm`). Todos los endpoints de T3 y T4 deben usarlo; no se permite repetir la lógica en cada Service o Controller. Si la granja no existe o no pertenece al usuario, responder siempre `404` (no `403`), para no revelar la existencia de granjas ajenas.
* Frontend: DataSource, Service, DTO, Mapper, Repository, UseCase y ViewModel siguiendo `catalog`, y mostrar los estados de carga, error y vacío.

Correspondencia con el backend (confirmada):

| Frontend | Entidad(es) del backend |
|---|---|
| `CycleRecord` | `ProductionCycle` (por `Pond` → `Farm`) |
| `Pond` | `Pond` |
| `InvItem` | `Feed` **y** `Supply` (ambas, filtradas por `FarmId`) |

Notas para `CycleRecord`:

* La pantalla se llama `CycleRecord` en el frontend; no usar el nombre antiguo `CicloRecord`.
* `ProductionCycle` no tiene `FarmId` directo: se llega por `Pond.FarmId`. La consulta del Repository debe unir `ProductionCycle` con `Pond` y filtrar por la granja.

Notas para `InvItem`:

* `Feed` y `Supply` son tablas distintas con campos distintos:
  * `Feed`: `Brand`, `ProductName`, `Phase`, `ProteinPercentage`, `PelletSizeMm`, `StockKg`, `MinimumStockKg`.
  * `Supply`: `Name`, `Category`, `Quantity`, `Unit`, `MinimumStock`.
* El backend debe devolver ambas en un único DTO nuevo con la forma que espera el frontend (por ejemplo, una lista unificada con un campo que indique el tipo: alimento o insumo). Revisar primero el modelo `InvItem` del frontend y respetar sus campos; no inventar campos.
* Si la lista unificada se arma en el Service combinando los resultados de dos Repositories (`Feed` y `Supply`), mantener la lógica de combinación en el Service, no en el Controller ni en el Repository.
* Cada ítem debe indicar si está por debajo del stock mínimo solo si el frontend ya lo muestra. No agregar cálculos que el frontend no pida.
* Las unidades deben ser claras: `Feed` usa kilogramos; `Supply` usa la unidad indicada en `Unit`.

### T4. Pantalla Tasks

* Backend: endpoint que devuelve las tasks de una granja (`Task` filtrada por `FarmId`, `IsActive = true`), con su DTO propio, ruta, Service y Repository. Debe llevar `[Authorize]`.
* Frontend: la pantalla Tasks usa el `currentFarmId` y muestra la lista de tasks de esa granja.
* Mismas reglas de T3: validación de propiedad con el método único (respondiendo `404`), no disparar peticiones si `currentFarmId` es `null`, uso de `flatMapLatest`, y estados de carga, error y vacío.
* Alcance: solo listar. No implementar crear, editar ni completar tasks, salvo que se pida.

---

## 3. Criterios de aceptación

* El login funciona contra el backend y el token se envía en las peticiones protegidas.
* Todos los endpoints nuevos requieren autenticación (`[Authorize]`). En Swagger, llamarlos sin token devuelve `401`.
* Ante un `401` en una petición protegida, la app limpia la sesión, vuelve al login y muestra un mensaje en español.
* Las piscigranjas mostradas son las del usuario autenticado.
* Al cambiar de piscigranja se recargan CycleRecord, Pond, InvItem y Tasks con los datos de la nueva granja, sin mostrar datos de la granja anterior.
* Con `currentFarmId` nulo, las pantallas de T3 y T4 no hacen peticiones.
* Un usuario no puede ver datos de una granja que no es suya: el backend responde `404`, y la validación pasa por un único método reutilizable.
* Los errores de red o del servidor se muestran en español, sin exponer detalles técnicos.
* La solución compila en ambos proyectos y se probó en Swagger (backend) y en la app (frontend).

---

## 4. Restricciones

* No modificar la base de datos ni el esquema sin autorización.
* No cambiar la autenticación, CORS ni la configuración global fuera de lo necesario.
* No persistir el token ni la sesión en disco (la sesión es solo en memoria, ver T1).
* No agregar paginación.
* No agregar dependencias nuevas sin explicar por qué.
* No refactorizar código que no esté relacionado con estas tareas.
* Configurar la URL base del backend en un único lugar del frontend (no repetirla en cada Service). Tener en cuenta que en el emulador de Android `localhost` es `10.0.2.2`.

---

## 5. Preguntas que el agente debe confirmar antes de implementar

1. ¿Cuál es el nombre exacto del claim del `UserId` en el JWT?
2. ¿Qué campos exactos tienen los modelos `CycleRecord`, `Pond` e `InvItem` en el frontend? (los DTOs del backend deben coincidir con ellos)
3. ¿Cómo representa el modelo `InvItem` del frontend la diferencia entre alimento (`Feed`) e insumo (`Supply`)?
4. ¿Qué formato estándar de respuesta y de error usan hoy los Controllers?

---

## 6. Reporte final

Al terminar, generar un archivo `REPORT.md` con:

1. **Resumen:** qué se hizo, en pocas líneas.
2. **Backend:** archivos creados y modificados (con su ruta), endpoints nuevos (método, ruta, request y response) y DTOs nuevos.
3. **Frontend:** archivos creados y modificados (con su ruta), pantallas conectadas y cómo se guarda `currentFarmId`.
4. **Seguridad:** cómo se valida que la granja pertenece al usuario (indicar dónde vive el método único y qué endpoints lo usan) y cómo se aplica `[Authorize]`.
5. **Verificación:** qué se compiló, qué se probó y cómo (incluyendo la prueba sin token → `401` y la de granja ajena → `404`).
6. **Pendientes y problemas:** lo que quedó sin hacer, suposiciones tomadas y riesgos.
7. **Archivos que NO se modificaron** y por qué.