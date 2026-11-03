# 08. Estado Actual del Sistema (Current State)

**Última actualización:** 03 de noviembre de 2026
**Commit de referencia:** `a146b9f` (rama `main`)  
**Propósito:** Este documento registra el inventario exhaustivo, auditable y verificable del código existente en el repositorio frente a la línea base oficial de **109 Historias de Usuario (HU-01 a HU-109)**. Sirve como base empírica para la especificación formal del sistema y la priorización del **MVP Inicial**.

---

«"docs/" representa la especificación y el estado conocido del proyecto. La especificación define la intención del sistema; el código representa el estado actual de implementación. Las divergencias entre ambos son defectos de implementación o elementos pendientes, no cambios implícitos de la especificación.»

«La documentación no declara que una funcionalidad esté verificada únicamente porque exista código relacionado con ella.»

Para evitar ambigüedades, los componentes y funcionalidades se clasifican estrictamente en las siguientes categorías:

* **SPECIFIED**: definido por una fuente oficial del proyecto (Ej. 109 HUs, DDL).
* **DESIGNED**: existe una decisión, diseño o arquitectura aprobados.
* **IMPLEMENTED**: existe implementación funcional identificable en el repositorio.
* **VERIFIED**: existe evidencia objetiva (ej. tests automatizados o de ejecución) de que la implementación funciona y cumple lo especificado.

*Modificadores de estado:*
* **PARTIAL**: implementación incompleta o parcial.
* **MISSING**: especificado pero no implementado.
* **DIVERGENT**: la implementación contradice explícitamente la especificación.
* **UNVERIFIABLE**: no hay evidencia suficiente para determinarlo.

*Nota: La ausencia de tests no convierte automáticamente una implementación en PARTIAL. El estado "IMPLEMENTED + NOT VERIFIED" es completamente válido.*

---

## 2. Matriz de Entidades y Capas del Backend

El modelo de datos se definió originalmente mediante base de datos relacional y se importó vía *Database-First Scaffold* en [`PiscidatadbContext.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/PiscidatadbContext.cs). Existen **19 entidades** en el contexto de base de datos.

A continuación se detalla su presencia en cada capa arquitectónica:

| # | Entidad de Dominio | Tabla en MySQL | Modelo EF Core | DTOs | Validador | Servicio | Repositorio (Comportamiento actual) | Controlador REST | Estado SDD Global |
|---|---|---|---|---|---|---|---|---|---|
| 1 | **Farm** (Granjas) | `farm` | Sí | Sí | Sí | Sí | Sí (Implementa Soft Delete) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 2 | **User** (Usuarios) | `user` | Sí | Sí | Sí | Sí | Sí (Implementa Soft Delete) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 3 | **Pond** (Estanques) | `pond` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 4 | **Productioncycle** (Ciclos) | `productioncycle` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 5 | **Feed** (Alimentos) | `feed` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 6 | **Feeding** (Alimentación) | `feeding` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 7 | **Supply** (Insumos) | `supply` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 8 | **Biometric** (Biometrías) | `biometrics` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 9 | **Biometricssample** (Muestras) | `biometricssample` | Sí | Sí | Sí | Sí | Sí (Llama a `_dbSet.Remove()`) | Sí | **IMPLEMENTED / NOT VERIFIED** |
| 10 | **Waterquality** (Calidad de agua) | `waterquality` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 11 | **Mortality** (Mortalidad) | `mortality` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 12 | **Species** (Especies) | `species` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 13 | **Harvest** (Cosechas) | `harvest` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 14 | **Task** (Tareas operativas) | `task` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 15 | **Feedingbyage** (Guía por edad) | `feedingbyage` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 16 | **Feedingbyweight** (Guía por peso) | `feedingbyweight` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 17 | **Supplymovement** (Kardex/Mov.) | `supplymovement` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 18 | **Aiconversation** (Conversaciones) | `aiconversation` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| 19 | **Aimessage** (Mensajes IA) | `aimessage` | Sí | No | No | No | No | No | **SPECIFIED → PARTIAL** |
| - | **Decisión Soft Delete** | `IsActive` presente en las 19 tablas (DDL) | - | - | - | - | Repositorios difieren en su uso. | - | Rationale: **UNKNOWN / REQUIRES HUMAN VALIDATION** |

*\* Nota sobre User:* Aunque tiene todas las capas, no posee lógica de login/autenticación ni hasheo de contraseñas.

---

## 3. Catálogo de Controladores y Endpoints Expuestos

Actualmente el backend expone **10 controladores** (9 de negocio + 1 de plantilla ASP.NET Core) totalizando **46 endpoints HTTP**:

### 3.1. Controladores de Negocio (9 Controladores con patrón uniforme)

Cada uno de los 9 controladores implementa exactamente las siguientes 5 operaciones REST:

1. `GET /api/[recurso]` — Lista todos los registros activos.
2. `GET /api/[recurso]/{id}` — Retorna un registro por ID (404 si no existe).
3. `POST /api/[recurso]` — Valida mediante DTO y crea un registro (400 si hay errores de validación, 201 Created con cabecera `Location`).
4. `PUT /api/[recurso]/{id}` — Valida y actualiza un registro por ID (400 si hay errores, 404 si no existe, 200 OK).
5. `DELETE /api/[recurso]/{id}` — Elimina el registro (404 si no existe, 204 NoContent).

| Controlador | Ruta Base | DTO Entrada (Create) | DTO Entrada (Update) | DTO Salida | Estrategia Delete |
|---|---|---|---|---|---|
| [`FarmsController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FarmsController.cs) | `/api/Farms` | `CreateFarmDto` | `UpdateFarmDto` | `FarmDto` | Soft Delete (`IsActive = false`) |
| [`UsersController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/UsersController.cs) | `/api/Users` | `CreateUserDto` | `UpdateUserDto` | `UserDto` | Soft Delete (`IsActive = false`) |
| [`PondsController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/PondsController.cs) | `/api/Ponds` | `CreatePondDto` | `UpdatePondDto` | `PondDto` | Hard Delete (`DbSet.Remove`) |
| [`ProductioncyclesController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/ProductioncyclesController.cs) | `/api/Productioncycles` | `CreateProductioncycleDto` | `UpdateProductioncycleDto` | `ProductioncycleDto` | Hard Delete (`DbSet.Remove`) |
| [`FeedsController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FeedsController.cs) | `/api/Feeds` | `CreateFeedDto` | `UpdateFeedDto` | `FeedDto` | Hard Delete (`DbSet.Remove`) |
| [`FeedingsController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FeedingsController.cs) | `/api/Feedings` | `CreateFeedingDto` | `UpdateFeedingDto` | `FeedingDto` | Hard Delete (`DbSet.Remove`) |
| [`SuppliesController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/SuppliesController.cs) | `/api/Supplies` | `CreateSupplyDto` | `UpdateSupplyDto` | `SupplyDto` | Hard Delete (`DbSet.Remove`) |
| [`BiometricsController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/BiometricsController.cs) | `/api/Biometrics` | `CreateBiometricDto` | `UpdateBiometricDto` | `BiometricDto` | Hard Delete (`DbSet.Remove`) |
| [`BiometricssamplesController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/BiometricssamplesController.cs) | `/api/Biometricssamples` | `CreateBiometricssampleDto` | `UpdateBiometricssampleDto` | `BiometricssampleDto` | Hard Delete (`DbSet.Remove`) |

### 3.2. Controlador de Plantilla Residual

* [`WeatherForecastController`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/WeatherForecastController.cs):
  * `GET /weatherforecast` — Retorna datos meteorológicos simulados.
  * **Estado:** **[Código residual / Requiere eliminación o confirmación]**.

---

## 4. Estado del Frontend Móvil (`PisciDataFrontend`)

El proyecto frontend se encuentra estructurado en Kotlin Multiplatform (KMP) con Compose Multiplatform:

```text
PisciDataFrontend/
└── PisciDataFrontend/       # Estructura física anidada
    ├── shared/              # Código común (Compose Multiplatform)
    ├── androidApp/          # Punto de entrada para Android
    └── iosApp/              # Proyecto Xcode / Swift para iOS
```

### 4.1. Cliente y conectividad

* El frontend utiliza Ktor y `kotlinx.serialization` para autenticación, granjas y chat.
* Las rutas HTTP están centralizadas en `shared/src/commonMain/kotlin/ucb/piscidata/data/remote/ApiEndpoints.kt`.
* El inicio de sesión conserva el JWT para llamadas protegidas. El registro vuelve al login para obtener ese token antes de entrar a la app.
* Para desarrollo en un teléfono conectado por USB, `adb reverse tcp:5007 tcp:5007` permite conservar la URL local `127.0.0.1:5007`.

### 4.2. Estado de pantallas y chat móvil

* La app incluye pantallas de autenticación, asistente, base de datos, tareas, reportes y perfil; el alcance implementado varía por módulo.
* El chat móvil recupera o crea una conversación en `/api/AiConversations`, envía mensajes al backend y muestra la respuesta de Ollama.
* El backend usa el modelo `piscidata-ai` en Ollama. La respuesta móvil no usa herramientas MCP; por lo tanto, no debe presentarse como consulta de datos vivos de granjas.
* **Verificación manual:** el usuario confirmó una respuesta real de la IA en un teléfono Android el 03 de noviembre de 2026. No hay pruebas automatizadas de esta integración.
* **Estado SDD:** chat móvil **IMPLEMENTED + VERIFIED MANUALLY**; conexión móvil con MCP **MISSING**.

---

## 5. Estado del Servidor MCP (`PisciDataMCP`)

* **Framework:** .NET 10 (`net10.0`), SDK oficial `ModelContextProtocol 2.1.0`, `Microsoft.Extensions.Hosting 10.0.12`, `Microsoft.Extensions.Http 10.0.12`.
* **Transporte:** `stdio` configurado en `Program.cs` con redirección de logs a `stderr` (`LogLevel.Trace`).
* **Arquitectura de Conectividad:** Cliente HTTP tipado (`PisciDataApiClient`) hacia la API REST de `PisciDataBackend` (`http://localhost:5007/api`) con configuración desacoplada en `appsettings.json`.
* **Herramientas de Dominio Implementadas (10 herramientas activas):**
  * [`FarmTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/FarmTools.cs): `GetFarms`, `GetFarmById`.
  * [`PondTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/PondTools.cs): `GetPonds`, `GetPondById`.
  * [`ProductionCycleTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/ProductionCycleTools.cs): `GetProductionCycles`, `GetProductionCycleById`.
  * [`BiometricTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/BiometricTools.cs): `GetBiometrics`, `GetBiometricById`.
  * [`FeedingTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/FeedingTools.cs): `GetFeedings`, `GetFeedingById`.
* **Estado SDD:** **IMPLEMENTED / NOT VERIFIED**. El servidor MCP está implementado por stdio; `PisciDataChat` ofrece una interfaz web de prueba que lo orquesta con Ollama. Este flujo es independiente del chat móvil.

---

## 6. Registro de Deuda Técnica y Hallazgos Críticos

| ID Hallazgo | Componente | Severidad | Descripción del Hallazgo | Evidencia en Código |
|---|---|---|---|---|
| **TECH-SEC-01** | `PisciDataBackend` | **Media** | El registro aplica BCrypt y el login emite JWT; faltan controles de seguridad y gestión de secretos para un despliegue real. | `UserService.cs`, `UsersController.cs`, `Program.cs` |
| **TECH-SEC-02** | `PisciDataBackend` | **Alta** | `appsettings.json` contiene una clave JWT de marcador y una cadena de conexión de desarrollo. Deben configurarse secretos únicos fuera del repositorio. | `PisciDataBackend/appsettings.json` |
| **TECH-SEC-03** | `PisciDataBackend` | **Alta** | Cadena de conexión y credenciales de base de datos (`root:1234`) hardcodeadas en código C# bajo `#warning`. | [`PiscidatadbContext.cs:59`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/PiscidatadbContext.cs#L59) |
| **TECH-ARC-01** | `PisciDataBackend` | **Media** | Inversión de Dependencias (DIP) rota. Existe `IBaseRepository<T>`, pero los servicios inyectan directamente las clases concretas de repositorio. | [`FarmService.cs:12`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Application/Services/FarmService.cs#L12), [`Program.cs:22-41`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Program.cs#L22-L41) |
| **TECH-DAT-01** | `PisciDataBackend` | **Media** | Divergencia DDL vs EF Core. DDL incluye `ON DELETE CASCADE`. EF Core aplica `ClientSetNull` (Restrict implícito). *Riesgo esperado / Requiere verificación en runtime* si causará HTTP 500 al eliminar. | DDL vs [`PiscidatadbContext.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/PiscidatadbContext.cs) |
| **TECH-DAT-02** | `PisciDataBackend` | **Media** | Estrategia de eliminación heterogénea frente a la columna `IsActive`. | Repositorios |
| **TECH-STR-01** | `PisciDataBackend` | **Baja** | Error tipográfico en nombre de directorio y namespace (`Infraestructure` con "e"). | Directorio `PisciDataBackend/Infraestructure` |
| **TECH-STR-02** | `PisciDataFrontend`| **Baja** | Directorio raíz anidado redundantemente (`PisciDataFrontend/PisciDataFrontend/`). | Sistema de archivos |

---

## 7. Estado de Pruebas Automatizadas

* **PisciDataBackend:** **0 pruebas**. No existe proyecto de pruebas unitarias (`PisciDataBackend.Tests`) en la solución `PisciData.slnx`.
* **PisciDataMCP:** **0 pruebas**.
* **PisciDataFrontend:** Solo existen 3 archivos de prueba de plantilla generados por JetBrains ([`SharedCommonTest.kt`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataFrontend/PisciDataFrontend/shared/src/commonTest/kotlin/ucb/piscidata/SharedCommonTest.kt)), cuyo único contenido es `assertEquals(3, 1 + 2)`.
* **Veredicto:** **[Inexistente / 0% de cobertura en lógica de negocio]**.

---

## 8. Trazabilidad Global frente a las 109 HUs (MVP)

Actualmente, las piezas codificadas proporcionan una base técnica (entidades, repositorios, y operaciones CRUD REST). Sin embargo, la lógica core del MVP según las 109 HUs oficiales aún requiere orquestación (autenticación, flujos de reportes/KPIs, y enlace completo del asistente IA).

### Matriz de Estado Global (SDD)

| Elemento Funcional / Arquitectónico | Specified | Designed | Implemented | Verified | Estado Global SDD |
|---|:---:|:---:|:---:|:---:|---|
| **Login / Autenticación** | Sí | Sí | Sí | Sí (manual) | **IMPLEMENTED / VERIFIED MANUALLY** |
| **Backend CRUD REST** | Sí | Sí | Sí | No | **SPECIFIED → IMPLEMENTED / NOT VERIFIED** |
| **UI y chat móvil Android** | Sí | Sí | Parcial | Sí (chat manual) | **IMPLEMENTED / PARTIAL; CHAT VERIFIED MANUALLY** |
| **Servidor MCP** | Sí | Sí | Sí | No | **IMPLEMENTED / NOT VERIFIED; NO INTEGRACIÓN MÓVIL** |
| **LLM (Ollama) desde app móvil** | Sí | Sí | Sí | Sí (manual) | **IMPLEMENTED / VERIFIED MANUALLY** |
| **STT (Whisper)** | Sí | Sí | No | No | **SPECIFIED → MISSING** |
| **Base de Datos DDL vs EF**| Sí | Sí | Sí | No | **SPECIFIED → DIVERGENT** |

La matriz de trazabilidad detallada a nivel de requisitos se mantiene en `02-requirements/system-requirements.md`.
