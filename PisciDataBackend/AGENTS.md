# AGENTS.md (Backend)

# 1. Descripción del proyecto

Este proyecto es el backend de una aplicación de gestión de piscigranjas (PisciData). Expone una API REST consumida por una aplicación Kotlin Multiplatform y, más adelante, por un servidor MCP.

Está desarrollado con:

* C#
* .NET 10.0
* ASP.NET Core Web API
* Entity Framework Core
* MySQL (`utf8mb4`)
* JWT (autenticación)
* BCrypt (hash de contraseñas)
* Swagger / OpenAPI

Características del entorno:

* No se utiliza ningún contenedor ni librería externa de inyección de dependencias: la DI nativa de ASP.NET Core se configura directamente en `Program.cs`.
* El proyecto se ejecuta directamente desde Visual Studio.
* CORS ya está configurado.
* No hay tests por el momento.
* No hay paginación por el momento.

La base de datos está documentada en `DATABASE.md`, que contiene el script SQL completo y es la **fuente de verdad** del esquema.

---

# 2. REGLA DE IDIOMA

```text
CÓDIGO                     → INGLÉS
LO QUE VE EL USUARIO FINAL → ESPAÑOL
```

En inglés:

* nombres de clases, interfaces, métodos, propiedades y variables;
* nombres de archivos y carpetas;
* rutas de la API (`/api/pond`, `/api/farm`);
* nombres de DTOs, validators y repositories;
* comentarios en el código;
* mensajes de commit.

En español:

* mensajes de error y de éxito devueltos por la API que el frontend mostrará al usuario;
* mensajes de validación;
* descripciones de Swagger dirigidas al equipo pueden estar en español o inglés, pero deben ser consistentes con las existentes.

Ejemplo correcto:

```csharp
return NotFound(new { message = "No se encontró el estanque solicitado." });
```

Ejemplo incorrecto:

```csharp
return NotFound(new { mensaje = "No se encontró el estanque solicitado." }); // propiedad en español
```

Los datos guardados en base de datos que son de referencia para el usuario (por ejemplo `RecommendedAction` en `WaterQualityReference`) deben estar en español.

---

# 3. REGLA PRINCIPAL

## NO inventar una arquitectura nueva

Antes de crear o modificar código, el agente DEBE analizar primero la estructura existente del proyecto.

Cuando exista un módulo que sirva como ejemplo, debe utilizarse como referencia.

El módulo de referencia arquitectónica es `Farm` (Controller, Service, DTOs, Model, Validator y Repository de `Farm`). Si el módulo de referencia cambia, el usuario lo indicará.

Todo módulo nuevo debe seguir:

* la misma estructura de carpetas;
* la misma separación de capas;
* el mismo flujo de datos;
* el mismo patrón de Model;
* el mismo patrón de Validator;
* el mismo patrón de Repository;
* el mismo patrón de Service;
* el mismo patrón de DTO;
* el mismo patrón de Controller;
* el mismo patrón de registro de dependencias en `Program.cs`;
* el mismo formato de respuesta y de error.

No reemplazar la arquitectura existente por otra considerada "mejor" (por ejemplo MediatR, CQRS, AutoMapper, Repository genérico alternativo, Minimal APIs, etc.).

---

# 4. Regla de trabajo antes de modificar archivos

Antes de modificar código, el agente debe:

1. Analizar la estructura del proyecto.
2. Buscar el módulo de referencia (`Farm`).
3. Revisar sus archivos.
4. Identificar el patrón utilizado.
5. Comparar la nueva funcionalidad con el módulo de referencia.
6. Determinar qué archivos nuevos son necesarios.
7. Determinar qué archivos existentes necesitan modificación.
8. Evitar modificaciones innecesarias.

Para cambios grandes, primero explicar:

```text
Archivos que se crearán:
- ...

Archivos que se modificarán:
- ...

Archivos que NO necesitan cambios:
- ...

Motivo:
- ...
```

No realizar cambios arquitectónicos importantes sin autorización.

---

# 5. Arquitectura

El proyecto usa **arquitectura hexagonal** con cuatro carpetas principales:

```text
Application/
├── DTOs/
└── Services/

Domain/
├── Models/
├── Validators/
└── Interfaces/
    └── IBaseRepository.cs

Infraestructure/
└── Persistence/
    ├── BaseRepository.cs
    ├── PisciDataDbContext.cs
    └── Repositories/
        └── <Model>Repository.cs  (uno por cada modelo)

Controllers/
└── <Name>Controller.cs
```

IMPORTANTE: la carpeta se llama `Infraestructure` (con esa ortografía). Es el nombre real en el proyecto y NO debe corregirse ni renombrarse.

Los nombres exactos de carpetas, namespaces y archivos deben respetar los que ya existan en el proyecto.

---

# 6. Flujo de arquitectura

```text
Request HTTP
     ↓
 Controller
     ↓
  Service
     ↓
 Repository
     ↓
PisciDataDbContext
     ↓
 Base de datos (MySQL)
```

Reglas:

* El Controller solo habla con el Service.
* El Service solo habla con Repositories (y Validators).
* El Repository es el único que usa `PisciDataDbContext`.
* No saltarse capas sin una razón explícita.
* El Controller no usa Repositories ni `DbContext` directamente.
* El Service no usa `DbContext` directamente.

---

# 7. Capa DOMAIN

La capa `Domain` debe permanecer independiente de detalles de infraestructura, HTTP y DTOs.

```text
Domain/
├── Models/
├── Validators/
└── Interfaces/
```

## 7.1 Models

Los Models representan las entidades de la base de datos (una por tabla).

Reglas:

* Una clase por tabla, con el mismo nombre que la tabla (`Farm`, `Pond`, `ProductionCycle`, ...).
* Las propiedades deben coincidir con las columnas de `DATABASE.md`: mismo nombre, tipo y nulabilidad.
* Incluir las columnas de auditoría (`CreatedAt`, `UpdatedAt`, `IsActive`, `UserId`).
* No utilizar DTOs dentro del dominio.
* No colocar lógica de HTTP ni atributos de Swagger.
* No inventar columnas que no existan en la base de datos.

## 7.2 Validators

Los Validators contienen las reglas de validación de negocio de cada modelo o DTO, siguiendo el patrón existente.

Reglas:

* Mantenerlos en `Domain/Validators`.
* Los mensajes de validación que verá el usuario van en español.
* No acceder a la base de datos dentro de un Validator, salvo que el patrón existente lo haga.

## 7.3 IBaseRepository

`IBaseRepository` define las operaciones comunes de acceso a datos.

Reglas:

* Es una `interface` en `Domain/Interfaces`.
* Los métodos asíncronos deben usar `async`/`Task` y aceptar `CancellationToken` si el patrón existente lo hace.
* No contener implementación de Entity Framework.
* Si un módulo necesita consultas específicas, agregarlas en una interface propia (`I<Model>Repository`) que herede de `IBaseRepository`, siguiendo el patrón existente.

---

# 8. Capa INFRAESTRUCTURE

```text
Infraestructure/Persistence/
├── BaseRepository.cs
├── PisciDataDbContext.cs
└── Repositories/
```

## 8.1 PisciDataDbContext

Reglas:

* Es el único `DbContext` del proyecto.
* Todo `DbSet<T>` nuevo y su configuración se agregan aquí.
* La configuración de entidades (nombres de tabla, claves, relaciones, longitudes) debe coincidir exactamente con `DATABASE.md`.
* **CreatedAt y UpdatedAt los maneja la base de datos.** EF Core no debe asignarlos: configurarlos como `ValueGeneratedOnAdd` / `ValueGeneratedOnAddOrUpdate` según corresponda y no establecerlos desde el código.
* No colocar lógica de negocio en el DbContext.

## 8.2 BaseRepository

Implementación genérica de `IBaseRepository`.

Reglas:

* Contiene las operaciones comunes (obtener, listar, crear, actualizar, eliminar lógicamente).
* Las consultas de lectura deben filtrar `IsActive == true` salvo que se requiera explícitamente lo contrario.
* La "eliminación" nunca ejecuta `DELETE`: marca `IsActive = false` (ver sección 12).

## 8.3 Repositories

Un Repository por cada modelo, en `Infraestructure/Persistence/Repositories`.

Reglas:

* Heredan de `BaseRepository` e implementan su interface.
* Contienen únicamente acceso a datos y consultas específicas del modelo.
* No contienen reglas de negocio, validaciones de usuario ni lógica HTTP.
* Usar `AsNoTracking()` en consultas de solo lectura.
* Evitar el problema N+1: usar `Include` o proyecciones cuando se devuelvan datos relacionados.

---

# 9. Capa APPLICATION

```text
Application/
├── DTOs/
└── Services/
```

## 9.1 DTOs

Reglas:

* Los DTOs de **crear, actualizar y devolver** de una entidad se encuentran **dentro del mismo archivo** (por ejemplo `FarmDto.cs` contiene `FarmCreateDto`, `FarmUpdateDto` y `FarmResponseDto`), siguiendo el patrón existente.
* Cuando una pantalla del frontend necesite una forma de datos distinta, **crear un DTO nuevo** que devuelva exactamente lo que el frontend pide (por ejemplo `CycleCardDto`), en lugar de forzar al frontend a combinar varias respuestas.
* Los DTOs de pantalla se arman en el Service (agregando datos de varios repositories), no en el Controller.
* Los DTOs no exponen campos innecesarios: nunca devolver `PasswordHash`.
* Los nombres de propiedades de los DTOs deben ser coherentes con lo que consume el frontend (revisar el DTO del frontend antes de crear el del backend).
* No devolver Models de dominio directamente desde un Controller.
* No colocar lógica de negocio en los DTOs.

## 9.2 Services

Los Services contienen la lógica de aplicación y de negocio.

Reglas:

* Reciben Repositories y Validators por inyección de dependencias.
* Convierten Model ↔ DTO (siguiendo el patrón de mapeo existente; no introducir AutoMapper sin autorización).
* Aplican validaciones y reglas de negocio.
* Verifican la propiedad de los datos (ver sección 11).
* Asignan `UserId` y `IsActive` en las operaciones de escritura (ver sección 12).
* Realizan los cálculos de negocio (ver sección 14).
* Lanzan o retornan errores según el patrón existente; no devolver códigos HTTP desde el Service si el patrón existente no lo hace.
* No usan `HttpContext` directamente salvo que el patrón existente lo haga. Para obtener el usuario autenticado, seguir el mecanismo ya establecido.

---

# 10. Controllers

Reglas:

* Un Controller por recurso, con atributo `[ApiController]`.
* Convención de rutas: `/api/<controllername>/...`

```csharp
[ApiController]
[Route("api/[controller]")]
public class PondController : ControllerBase
```

* Los Controllers son delgados: reciben el request, llaman al Service y devuelven la respuesta.
* No contienen lógica de negocio ni acceso a datos.
* Usar los códigos HTTP correctamente:

```text
200 OK            → lectura / actualización correcta
201 Created       → creación correcta
204 No Content    → eliminación lógica correcta (si el patrón existente lo usa)
400 Bad Request   → datos inválidos
401 Unauthorized  → sin autenticación o token inválido
403 Forbidden     → autenticado pero sin permiso sobre el recurso
404 Not Found     → recurso inexistente o inactivo
409 Conflict      → conflicto (por ejemplo, código duplicado)
500               → error inesperado
```

* Usar el **mismo formato estándar de respuesta y de error** que ya usan los Controllers existentes. No inventar otro formato.
* Los endpoints deben tener `[ProducesResponseType]` cuando el patrón existente lo haga, para que Swagger quede documentado.
* Los endpoints protegidos llevan `[Authorize]`. Solo `login` y `register` son públicos.
* Aún no hay paginación: no agregarla sin autorización.

---

# 11. Autenticación y autorización

## 11.1 Autenticación

* Mecanismo: **JWT**.
* Hash de contraseñas: **BCrypt**.
* El login se hace por **teléfono** y contraseña (no hay email).
* Nunca guardar ni devolver contraseñas en texto plano.
* Nunca registrar (log) contraseñas, hashes ni tokens.
* Actualizar `LastLoginAt` en un login exitoso.
* El mensaje de login fallido no debe revelar si el teléfono existe o si falló la contraseña.

## 11.2 Autorización

* Por ahora el único rol relevante es `Owner`.
* Un usuario solo puede acceder a **sus propias granjas** y a todo lo que cuelgue de ellas.

La cadena de pertenencia es:

```text
User → Farm (OwnerUserId) → Pond → ProductionCycle → Feeding / WaterQuality / Biometrics / Mortality / Harvest
User → Farm → Feed / Supply → SupplyMovement
User → Farm → Task
User → AIConversation (OwnerUserId) → AIMessage
```

Reglas:

* En **cada** operación, el Service debe verificar que el recurso pertenece a una granja del usuario autenticado.
* Nunca confiar en un `FarmId`, `PondId` o `ProductionCycleId` recibido del cliente sin verificar su pertenencia.
* Si el recurso existe pero pertenece a otro usuario, responder `404 Not Found` (preferido) o `403 Forbidden`, siguiendo el patrón existente. No revelar que el recurso existe.
* El identificador del usuario autenticado se obtiene del token JWT, nunca del body ni de la query.

---

# 12. Auditoría y eliminación lógica

Todas las tablas tienen las columnas de auditoría:

```text
CreatedAt  → la maneja la BASE DE DATOS
UpdatedAt  → la maneja la BASE DE DATOS
IsActive   → DEFAULT TRUE en la base de datos; el BACKEND lo cambia en la eliminación lógica
UserId     → la maneja el BACKEND (último usuario que modificó el registro)
```

Reglas:

* **No asignar `CreatedAt` ni `UpdatedAt` desde el código.**
* En cada creación y en cada actualización, el backend asigna `UserId` con el usuario autenticado (tomado del JWT).
* **El borrado es siempre lógico**: se pone `IsActive = false` y se asigna `UserId`. Nunca ejecutar `DELETE` ni `Remove()` sobre entidades de negocio.
* Las consultas de lectura filtran `IsActive == true`.
* Antes de desactivar un registro, verificar sus dependencias (las claves foráneas usan `ON DELETE RESTRICT` para entidades principales). Por ejemplo, no desactivar una granja con estanques activos sin una regla explícita.
* Excepción conocida: `BiometricsSample` y `AIMessage` usan `ON DELETE CASCADE` en la base de datos, pero igualmente se manejan con eliminación lógica desde el backend.
* La tabla `User` tiene un `UserId` autorreferenciado (último usuario que la modificó).

---

# 13. Base de datos

* El archivo `DATABASE.md` contiene el script SQL completo y es la fuente de verdad.
* El esquema se mantiene mediante el script SQL, no mediante migraciones de EF Core. **No usar `dotnet ef migrations` ni `EnsureCreated()`** sin autorización.
* Los datos semilla (tablas de referencia como `Species`, `FeedingByWeight`, `FeedingByAge`, `WaterQualityReference`) se cargan directamente con sentencias SQL.
* Si cambia el esquema, deben actualizarse en conjunto: el script SQL, `DATABASE.md`, el Model, la configuración en `PisciDataDbContext` y los DTOs afectados.
* Mantener las convenciones: tablas y columnas en PascalCase y en singular, `Id INT AUTO_INCREMENT` como clave primaria.
* Prestar atención a los tipos `DECIMAL`: usar `decimal` en C#, nunca `float` ni `double` para pesos, cantidades o dinero.
* El nombre de tabla `Task` puede chocar con `System.Threading.Tasks.Task`: usar un alias o nombre completo cuando haga falta, sin renombrar el modelo ni la tabla.
* Nunca ejecutar `DROP DATABASE` ni el script completo contra una base con datos reales sin autorización explícita (el script inicial contiene `DROP DATABASE IF EXISTS`).

---

# 14. Reglas de negocio y cálculos

El backend es responsable de los siguientes cálculos:

```text
Biomasa
Alimentación recomendada
Mortalidad acumulada
Calidad del agua (OPTIMAL / WARNING / CRITICAL)
```

Reglas:

* La definición exacta de cada fórmula **todavía no está establecida**. El agente NO debe inventar fórmulas. Si una tarea requiere un cálculo no definido, debe pedir la fórmula al usuario antes de implementarlo.
* Los cálculos viven en los Services (o en una clase de dominio dedicada si el usuario lo autoriza), nunca en el Controller ni en el frontend.
* Referencias disponibles en la base de datos:
  * `FeedingByWeight` y `FeedingByAge` → tasa y cantidad de alimento. En `FeedingByAge`, `DailyAmountKilo` es válido para `ReferenceFishCount` peces y se escala: `recommendedKg = DailyAmountKilo * (currentFish / ReferenceFishCount)`.
  * `WaterQualityReference` → rangos óptimo, de advertencia y críticos por parámetro (`DISSOLVED_OXYGEN`, `PH`, `TEMPERATURE`, `TRANSPARENCY`), con `RecommendedAction`.
* Los valores calculados que la base de datos almacena (`Biometrics.BiomassKg`, `Biometrics.CalculatedFeedKg`, `BiometricsSample.AverageWeightGrams`, etc.) los calcula el backend; no confiar en valores calculados enviados por el cliente.
* Usar `decimal` y definir el redondeo de forma explícita.

## 14.1 Stock

* Al registrar una alimentación (`Feeding`), el stock del alimento (`Feed.StockKg`) debe actualizarse automáticamente restando `QuantityKg`.
* Registrar la alimentación y actualizar el stock debe hacerse en **una sola transacción**.
* Si el stock resultante sería negativo, aplicar la regla acordada con el usuario (no asumir una). Hasta que se defina, informar del problema y no continuar en silencio.
* Para `Supply`, los cambios de cantidad deben pasar por `SupplyMovement` (`IN`, `OUT`, `ADJUSTMENT`) y mantener `Supply.Quantity` consistente en la misma transacción.
* Si se actualiza o desactiva una alimentación, revertir o ajustar el stock correspondiente.

---

# 15. Dependency Injection en Program.cs

La inyección de dependencias se configura directamente en `Program.cs` con el contenedor nativo de ASP.NET Core.

Reglas:

* Registrar `PisciDataDbContext` con el proveedor MySQL existente.
* Registrar Repositories, Services y Validators con `AddScoped`, siguiendo el patrón existente.
* Registrar los nuevos elementos junto a los registros del módulo de referencia.
* No introducir Autofac, Scrutor ni otros contenedores o librerías de registro automático.
* No modificar la configuración de JWT, CORS ni Swagger al agregar un módulo, salvo que sea estrictamente necesario.

Orden de registro de un módulo nuevo:

```text
Repository  → AddScoped<I<Model>Repository, <Model>Repository>()
Validator   → AddScoped (según el patrón existente)
Service     → AddScoped<I<Model>Service, <Model>Service>()   (o la clase directa, según el patrón existente)
```

---

# 16. Agregar un nuevo módulo

Cuando se agregue un módulo (por ejemplo `Pond`), seguir este orden:

```text
1. Verificar la tabla en DATABASE.md
        ↓
2. Crear/verificar el Model (Domain/Models)
        ↓
3. Agregar DbSet y configuración en PisciDataDbContext
        ↓
4. Crear el Validator (Domain/Validators)
        ↓
5. Crear la interface del Repository (si es necesaria)
        ↓
6. Crear el Repository (Infraestructure/Persistence/Repositories)
        ↓
7. Crear los DTOs (Application/DTOs) → create, update y response en un solo archivo
        ↓
8. Crear el Service (Application/Services)
        ↓
9. Crear el Controller (Controllers)
        ↓
10. Registrar en Program.cs
        ↓
11. Compilar
        ↓
12. Corregir errores
        ↓
13. Probar en Swagger
```

Resultado esperado para `Pond`:

```text
Domain/Models/Pond.cs
Domain/Validators/PondValidator.cs
Application/DTOs/PondDto.cs
Application/Services/PondService.cs
Infraestructure/Persistence/Repositories/PondRepository.cs
Controllers/PondController.cs
```

Y las modificaciones mínimas:

```text
Infraestructure/Persistence/PisciDataDbContext.cs   (DbSet + configuración)
Program.cs                                          (registro de dependencias)
```

No cambiar arbitrariamente este flujo.

---

# 17. Respuestas para el frontend

El frontend (Kotlin Multiplatform, Ktor, kotlinx.serialization) consume esta API.

Reglas:

* Antes de crear un endpoint para una pantalla, revisar qué datos necesita el frontend y diseñar el DTO para devolverlos directamente.
* Los nombres de propiedades JSON deben ser estables: no renombrarlos sin avisar, porque rompen los DTOs del frontend.
* Mantener el formato de serialización JSON existente (por ejemplo camelCase por defecto de ASP.NET Core). No cambiarlo.
* Los campos opcionales deben ser `nullable` en el DTO igual que en el frontend.
* Las fechas y horas deben usar un formato consistente (ISO 8601); no mezclar formatos.
* Los mensajes de error visibles para el usuario van en español.

---

# 18. Manejo de errores

Cuando aparezca un error:

```text
1. Leer el error completo.
2. Identificar el archivo.
3. Identificar la línea.
4. Identificar la causa.
5. Corregir la causa.
6. Compilar nuevamente.
7. Verificar que la corrección no haya roto otra parte.
```

En el código:

* No ocultar errores con `catch` vacíos.
* No devolver `500` con la traza de la excepción al cliente.
* No exponer mensajes internos de Entity Framework o de MySQL al usuario.
* No utilizar el operador `!` (null-forgiving) simplemente para hacer desaparecer una advertencia de nulabilidad.
* No eliminar código solamente para evitar un error de compilación.

---

# 19. Validación

Después de implementar una funcionalidad:

1. Compilar la solución.
2. Revisar errores y advertencias de C#.
3. Revisar errores de inyección de dependencias (servicios no registrados).
4. Revisar la coincidencia entre Models, `PisciDataDbContext` y `DATABASE.md`.
5. Probar los endpoints desde Swagger (casos correctos, datos inválidos, sin token, recurso de otro usuario).
6. Verificar que `UserId` e `IsActive` se asignan correctamente.
7. Verificar que el stock y los cálculos son correctos cuando corresponda.

Todavía no hay tests automatizados: no crear proyectos de tests sin autorización. No considerar una funcionalidad terminada solamente porque el código parece correcto.

---

# 20. Entity Framework

Reglas:

* Usar `async`/`await` en todas las operaciones de acceso a datos.
* Usar `AsNoTracking()` para lecturas.
* No ejecutar SQL crudo salvo que no exista alternativa razonable, y siempre parametrizado.
* No usar concatenación de strings para construir consultas.
* No cargar tablas completas en memoria para filtrarlas con LINQ to Objects; filtrar en la consulta.
* Usar transacciones cuando una operación modifique más de una tabla (por ejemplo, alimentación + stock).
* No exponer `IQueryable` fuera de los Repositories.

---

# 21. Dependencias

No modificar dependencias sin necesidad.

No actualizar:

* .NET SDK / versión del framework;
* Entity Framework Core;
* el proveedor de MySQL;
* paquetes de JWT;
* BCrypt;
* Swashbuckle / Swagger;

solamente para solucionar un problema puntual, salvo que sea realmente necesario.

Antes de agregar un paquete NuGet nuevo:

1. Revisar si el proyecto ya tiene una dependencia equivalente.
2. Revisar si puede resolverse con las herramientas existentes.
3. Explicar por qué es necesario.
4. Agregarlo solamente si corresponde.

---

# 22. Archivos globales

Los archivos globales que configuran la aplicación solo deben modificarse cuando sea necesario:

```text
Program.cs
appsettings.json
appsettings.Development.json
PisciDataDbContext.cs
launchSettings.json
```

Para un módulo nuevo, modificar únicamente lo necesario para:

```text
PisciDataDbContext (DbSet y configuración)
Program.cs (registro de dependencias)
```

No tocar la configuración de CORS, JWT ni Swagger al agregar un módulo.

---

# 23. No duplicar lógica

Antes de crear un método, clase o componente:

1. Buscar si ya existe.
2. Revisar si puede reutilizarse.
3. Si existe un patrón equivalente, seguirlo.
4. Evitar duplicación innecesaria.

No crear:

```text
PondRepository2
PondServiceNew
AlternativePondController
```

si el problema puede resolverse con la arquitectura existente. Si una operación es común a todos los repositories, va en `BaseRepository`, no se copia en cada uno.

---

# 24. Cambios mínimos y refactorización

Siempre preferir la solución mínima necesaria.

Si el problema está en `PondService.cs`, no modificar `Program.cs` ni otros módulos sin una razón.

No realizar refactorizaciones generales durante la implementación de una funcionalidad. No:

* cambiar nombres globalmente;
* mover carpetas o namespaces;
* renombrar `Infraestructure`;
* cambiar la arquitectura;
* reemplazar Entity Framework;
* introducir MediatR, AutoMapper, FluentValidation u otras librerías;
* reorganizar el DbContext.

---

# 25. Estilo de código

* Usar nombres descriptivos en inglés.
* Seguir las convenciones de C#: `PascalCase` para tipos, métodos y propiedades; `camelCase` para variables locales y parámetros; interfaces con prefijo `I`; campos privados según la convención ya usada en el proyecto.
* Los métodos asíncronos terminan en `Async` si esa es la convención existente; si no, mantener la existente.
* Respetar el uso de nullable reference types del proyecto.
* Evitar métodos excesivamente grandes.
* Evitar código duplicado.
* No agregar comentarios que simplemente describan código obvio.

---

# 26. Seguridad

* Nunca exponer, inventar o modificar secretos.
* No agregar claves JWT, cadenas de conexión ni API keys directamente en el código si existe una alternativa segura (`appsettings`, User Secrets, variables de entorno).
* Si se encuentra un secreto existente en el proyecto: no publicarlo, no mostrarlo en respuestas, no moverlo innecesariamente e informar si existe un riesgo.
* Nunca subir secretos a Git.
* Validar siempre la entrada del usuario en el backend; no confiar en la validación del frontend.
* Nunca devolver `PasswordHash` ni datos de otros usuarios.

---

# 27. Git

El agente NO debe ejecutar automáticamente operaciones destructivas.

No ejecutar sin autorización:

```bash
git reset --hard
git clean -fd
git push --force
git branch -D
git checkout .
git restore .
```

No eliminar archivos para solucionar errores sin autorización.

No crear commits automáticamente salvo que el usuario lo solicite.

No hacer push automáticamente salvo que el usuario lo solicite.

Antes de una operación que pueda eliminar cambios, informar al usuario.

---

# 28. Regla para respuestas al usuario

El usuario prefiere trabajar paso a paso.

Cuando una tarea sea compleja:

1. Explicar primero qué se hará.
2. Mostrar la estructura.
3. Implementar por partes.
4. Explicar qué archivo corresponde a cada parte.
5. Indicar qué archivo debe modificarse.
6. No proporcionar cambios no relacionados.

Cuando el usuario solicite un archivo concreto, entregar ese archivo completo y listo para copiar.

---

# 29. Si el usuario pide un nuevo módulo o endpoint

El agente debe utilizar este procedimiento:

```text
PASO 1  Analizar el módulo de referencia (Farm).
PASO 2  Comparar el nuevo módulo con la referencia.
PASO 3  Revisar la tabla en DATABASE.md.
PASO 4  Revisar qué necesita el frontend (forma del DTO).
PASO 5  Definir los archivos a crear y modificar.
PASO 6  Crear/verificar Model y DbContext.
PASO 7  Crear Validator.
PASO 8  Crear Repository.
PASO 9  Crear DTOs.
PASO 10 Crear Service.
PASO 11 Crear Controller.
PASO 12 Registrar en Program.cs.
PASO 13 Compilar.
PASO 14 Corregir errores.
PASO 15 Verificar en Swagger.
```

---

# 30. Principio fundamental

La arquitectura existente tiene prioridad sobre las preferencias personales del agente.

La prioridad es:

```text
Arquitectura existente
        ↓
Patrones existentes
        ↓
Requerimientos de la tarea
        ↓
Buenas prácticas
        ↓
Preferencias del agente
```

Nunca invertir este orden sin autorización.

---

# 31. Regla final

ANTES DE CAMBIAR:

```text
Analizar
   ↓
Comparar
   ↓
Planificar
   ↓
Explicar
   ↓
Implementar
   ↓
Compilar
   ↓
Verificar
```

No:

```text
Recibir tarea
   ↓
Modificar todo
   ↓
Esperar que compile
```

El objetivo es que todo módulo nuevo parezca haber sido creado con el mismo patrón que el módulo de referencia.

El nuevo código debe integrarse al proyecto, no cambiar la arquitectura del proyecto.