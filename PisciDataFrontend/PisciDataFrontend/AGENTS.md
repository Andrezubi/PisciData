# AGENTS.md
# 1. Descripción del proyecto

Este proyecto es una aplicación Kotlin Multiplatform desarrollada con:

* Kotlin
* Kotlin Multiplatform
* Compose Multiplatform
* Clean Architecture
* Ktor
* kotlinx.serialization
* Koin
* ViewModel
* StateFlow
* SharedFlow
* Navigation Compose

El proyecto tiene una estructura organizada por funcionalidades/features.

Cada funcionalidad debe mantener separadas las capas:

```text
feature/
├── data/
├── domain/
└── presentation/
```

La arquitectura debe mantenerse consistente en todo el proyecto.

---

# 2. REGLA PRINCIPAL

## NO inventar una arquitectura nueva

Antes de crear o modificar código, el agente DEBE analizar primero la estructura existente del proyecto.

Cuando exista una funcionalidad que sirva como ejemplo, debe utilizarse como referencia.

Actualmente la funcionalidad `catalog` es el ejemplo principal y debe considerarse la referencia arquitectónica para crear nuevas funcionalidades.

La nueva funcionalidad debe seguir:

* la misma estructura de carpetas;
* la misma separación de capas;
* el mismo flujo de datos;
* el mismo patrón de DataSource;
* el mismo patrón de DTO;
* el mismo patrón de Mapper;
* el mismo patrón de Repository;
* el mismo patrón de Service;
* el mismo patrón de UseCase;
* el mismo patrón de Screen;
* el mismo patrón de ViewModel;
* el mismo patrón de State;
* el mismo patrón de Event;
* el mismo patrón de Effect;
* el mismo patrón de navegación;
* el mismo patrón de Dependency Injection con Koin.

No reemplazar la arquitectura existente por otra arquitectura considerada "mejor".

---

# 3. Regla de trabajo antes de modificar archivos

Antes de modificar código, el agente debe:

1. Analizar la estructura del proyecto.
2. Buscar la funcionalidad `catalog`.
3. Revisar sus archivos.
4. Identificar el patrón utilizado.
5. Comparar la nueva funcionalidad con `catalog`.
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

# 4. Arquitectura de referencia

La funcionalidad `catalog` representa la arquitectura que deben seguir las nuevas funcionalidades.

Su estructura es:

```text
catalog/
│
├── data/
│   │
│   ├── datasource/
│   │   └── CatalogRemoteDataSource.kt
│   │
│   ├── dto/
│   │   ├── CatalogDto.kt
│   │   └── MovieDto.kt
│   │
│   ├── mapper/
│   │   └── MovieMapper.kt
│   │
│   ├── repository/
│   │   └── CatalogRepositoryImpl.kt
│   │
│   └── service/
│       └── CatalogService.kt
│
├── domain/
│   │
│   ├── model/
│   │   └── MovieModel.kt
│   │
│   ├── repository/
│   │   └── CatalogRepository.kt
│   │
│   └── usecase/
│       └── GetMoviesUseCase.kt
│
└── presentation/
    │
    ├── screen/
    │   └── CatalogScreen.kt
    │
    └── viewmodel/
        ├── CatalogViewModel.kt
        ├── CatalogState.kt
        ├── CatalogEvent.kt
        └── CatalogEffect.kt
```

Las nuevas funcionalidades deben seguir esta misma estructura.

Los nombres deben adaptarse a la funcionalidad, pero la estructura debe mantenerse.

---

# 5. Flujo de arquitectura

El flujo esperado es:

```text
API / Fuente externa
        ↓
    Service
        ↓
   DataSource
        ↓
   Repository
        ↓
    UseCase
        ↓
   ViewModel
        ↓
     State
        ↓
     Screen
```

La navegación y Dependency Injection se conectan posteriormente:

```text
Screen
   ↓
AppNavHost
   ↓
NavRoute
   ↓
dataModule
   ↓
domainModule
   ↓
presentationModule
```

No saltarse capas sin una razón explícita.

---

# 6. Capa DATA

La capa `data` contiene la implementación concreta de acceso a datos.

Debe contener:

```text
data/
├── datasource/
├── dto/
├── mapper/
├── repository/
└── service/
```

---

## 6.1 DataSource

El DataSource define la operación que permite obtener los datos.

Ejemplo de referencia:

```kotlin
package ucb.vargasn.project.catalog.data.datasource

import ucb.vargasn.project.catalog.domain.model.MovieModel

interface CatalogRemoteDataSource {

    suspend fun fetchData(): Result<List<MovieModel>>
}
```

Reglas:

* Debe ser una `interface`.
* Debe utilizar `suspend` cuando corresponda.
* Debe retornar `Result`.
* Debe seguir el patrón existente.
* No colocar código HTTP concreto dentro de la interface.

Para una nueva funcionalidad se debe cambiar solamente el nombre y los modelos correspondientes.

Ejemplo:

```kotlin
interface EarthquakeRemoteDataSource {

    suspend fun fetchData(): Result<List<EarthquakeModel>>
}
```

---

# 7. DTOs

Los DTO representan exactamente la estructura recibida desde la API.

Ejemplo:

```kotlin
@Serializable
data class CatalogDto(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int,
    val total_results: Int
)
```

Y:

```kotlin
@Serializable
data class MovieDto(
    val title: String,

    @SerialName("poster_path")
    val posterPath: String?
)
```

Reglas:

* Utilizar `@Serializable`.
* Respetar los nombres reales de la API.
* Utilizar `@SerialName` cuando el nombre del JSON sea diferente al nombre Kotlin.
* No inventar campos.
* No asumir la estructura de una API.
* Revisar primero la respuesta real de la API.
* Utilizar `ignoreUnknownKeys = true` cuando corresponda.

Es especialmente importante no confundir nombres similares.

Por ejemplo, si la API devuelve:

```json
{
    "page": 1
}
```

no crear:

```kotlin
val pageInfo: Int
```

a menos que la API realmente devuelva `pageInfo`.

---

# 8. Mapper

Los DTO deben convertirse al modelo correspondiente mediante una función de extensión.

Ejemplo:

```kotlin
package ucb.vargasn.project.catalog.data.mapper

import ucb.vargasn.project.catalog.data.dto.MovieDto
import ucb.vargasn.project.catalog.domain.model.MovieModel

fun MovieDto.toModel(): MovieModel {
    return MovieModel(
        title = title,
        posterPath = posterPath
    )
}
```

Reglas:

* Mantener los mappers en `data/mapper`.
* Utilizar funciones de extensión cuando siga el patrón existente.
* No colocar lógica de UI en el mapper.
* No colocar llamadas HTTP en el mapper.
* El mapper transforma DTO → Domain Model.

---

# 9. Repository de DATA

La implementación concreta del Repository debe estar en:

```text
data/repository/
```

Ejemplo:

```kotlin
class CatalogRepositoryImpl(
    val dataSource: CatalogRemoteDataSource
) : CatalogRepository {

    override suspend fun getMovies(): Result<List<MovieModel>> {
        return dataSource.fetchData()
    }
}
```

Reglas:

* Implementar la interface definida en `domain/repository`.
* Recibir el DataSource mediante inyección.
* No acceder directamente a la API desde el Repository.
* No colocar código de Compose.
* No colocar lógica de presentación.

---

# 10. Service

El Service implementa el DataSource.

Ejemplo de referencia:

```kotlin
class CatalogService : CatalogRemoteDataSource {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun fetchData(): Result<List<MovieModel>> {
        return try {
            val response = client.get("API_URL")

            val catalog = response.body<CatalogDto>()

            Result.success(
                catalog.results.map {
                    it.toModel()
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

Reglas:

* Utilizar Ktor.
* Utilizar `HttpClient`.
* Utilizar `ContentNegotiation`.
* Utilizar `kotlinx.serialization`.
* Utilizar `Result`.
* Capturar excepciones.
* Convertir DTO a Model mediante Mapper.
* No colocar lógica de UI.
* No modificar otras capas desde el Service.

El Service es responsable de comunicarse con la fuente remota.

---

# 11. DOMAIN

La capa `domain` debe permanecer independiente de detalles concretos de infraestructura.

Su estructura:

```text
domain/
├── model/
├── repository/
└── usecase/
```

---

# 12. Domain Model

El Model representa la información que utilizarán las capas superiores.

Ejemplo:

```kotlin
data class MovieModel(
    val title: String,
    val posterPath: String?
)
```

Reglas:

* No utilizar DTOs dentro del dominio.
* No colocar `@Serializable` salvo que exista una razón real.
* No utilizar clases específicas de Ktor.
* No colocar componentes de Compose.
* El Model debe representar el dominio.

---

# 13. Domain Repository

El Repository del dominio es una interface.

Ejemplo:

```kotlin
interface CatalogRepository {

    suspend fun getMovies(): Result<List<MovieModel>>
}
```

Reglas:

* Definir las operaciones que necesita el dominio.
* No contener implementación HTTP.
* No depender del Service.
* No depender directamente de Ktor.

La implementación pertenece a:

```text
data/repository/
```

---

# 14. UseCase

Cada operación importante del dominio debe utilizar un UseCase cuando siga el patrón existente.

Ejemplo:

```kotlin
class GetMoviesUseCase(
    val repository: CatalogRepository
) {

    suspend fun invoke(): Result<List<MovieModel>> {
        return repository.getMovies()
    }
}
```

Reglas:

* El UseCase utiliza el Repository del dominio.
* El ViewModel utiliza el UseCase.
* El ViewModel no debe llamar directamente al Service.
* El ViewModel no debe llamar directamente al DataSource.

Flujo correcto:

```text
ViewModel
    ↓
UseCase
    ↓
Repository
    ↓
DataSource
    ↓
Service
    ↓
API
```

---

# 15. PRESENTATION

La presentación debe tener:

```text
presentation/
├── screen/
└── viewmodel/
```

El ViewModel debe encargarse del estado y eventos.

La Screen debe encargarse principalmente de representar el estado.

---

# 16. Screen

Ejemplo de referencia:

```kotlin
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // UI
}
```

Reglas:

* Utilizar Compose.
* Obtener el ViewModel mediante Koin.
* Observar el StateFlow.
* Enviar eventos al ViewModel.
* No realizar llamadas HTTP directamente.
* No utilizar Services directamente.
* No utilizar Repository directamente.
* No colocar lógica de negocio compleja en la Screen.

El flujo debe ser:

```text
Usuario
   ↓
Screen
   ↓
Event
   ↓
ViewModel
   ↓
UseCase
```

---

# 17. State

Cada Screen debe tener un State cuando siga el patrón existente.

Ejemplo:

```kotlin
data class CatalogState(
    val movies: List<MovieModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

El State representa todo lo necesario para dibujar la UI.

Debe contener, cuando corresponda:

```text
datos
isLoading
error
otros estados de UI
```

No colocar lógica de negocio dentro del State.

---

# 18. Event

Los eventos representan acciones de usuario o eventos de la Screen.

Ejemplo:

```kotlin
sealed interface CatalogEvent {

    object OnLoadMovies : CatalogEvent

    object OnRetry : CatalogEvent
}
```

Reglas:

* Las acciones deben pasar por el ViewModel.
* No realizar directamente la lógica dentro de la Screen.
* Mantener nombres claros.

Ejemplo para otra funcionalidad:

```kotlin
sealed interface EarthquakeEvent {

    object OnLoadEarthquakes : EarthquakeEvent

    object OnRetry : EarthquakeEvent
}
```

---

# 19. Effect

Los efectos representan acciones que no forman parte del estado persistente de la Screen.

Ejemplo:

```kotlin
sealed interface CatalogEffect {

    data class ShowToast(
        val message: String
    ) : CatalogEffect
}
```

El ViewModel utiliza `SharedFlow` para emitir efectos.

La Screen los observa mediante `LaunchedEffect`.

---

# 20. ViewModel

El ViewModel debe seguir el patrón utilizado por `CatalogViewModel`.

Ejemplo conceptual:

```text
ViewModel
│
├── MutableStateFlow
│       ↓
│    StateFlow
│
├── MutableSharedFlow
│       ↓
│    SharedFlow
│
├── emitEvent()
│
└── UseCase
```

El patrón de referencia es:

```kotlin
class CatalogViewModel(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CatalogEffect>()
    val effect = _effect.asSharedFlow()

    init {
        emitEvent(CatalogEvent.OnLoadMovies)
    }

    fun emitEvent(event: CatalogEvent) {
        when (event) {
            CatalogEvent.OnLoadMovies -> {
                getMovies()
            }

            CatalogEvent.OnRetry -> {
                getMovies()
            }
        }
    }
}
```

Reglas:

* Utilizar `ViewModel`.
* Utilizar `viewModelScope`.
* Utilizar `MutableStateFlow` internamente.
* Exponer `StateFlow`.
* Utilizar `MutableSharedFlow` internamente para Effects.
* Exponer `SharedFlow`.
* Utilizar UseCases.
* No llamar directamente al Service.
* No llamar directamente al DataSource.
* No colocar componentes Compose en el ViewModel.

---

# 21. Manejo de Loading, Success y Error

El estado debe manejar como mínimo:

```text
Loading
Success
Error
```

Ejemplo:

```kotlin
_state.update {
    it.copy(
        isLoading = true,
        error = null
    )
}
```

En éxito:

```kotlin
_state.update {
    it.copy(
        movies = movies,
        isLoading = false,
        error = null
    )
}
```

En error:

```kotlin
_state.update {
    it.copy(
        isLoading = false,
        error = exception.message
    )
}
```

La Screen debe reaccionar al estado.

---

# 22. Dependency Injection con Koin

La inyección de dependencias debe utilizar Koin.

Existen tres módulos principales:

```text
dataModule
domainModule
presentationModule
```

---

# 23. dataModule

El patrón de referencia es:

```kotlin
val dataModule = module {

    single<CatalogRemoteDataSource> {
        CatalogService()
    }

    single<CatalogRepository> {
        CatalogRepositoryImpl(get())
    }
}
```

Reglas:

* Registrar el DataSource.
* Registrar el Repository.
* Utilizar `single` siguiendo el patrón existente.
* Utilizar `get()` para resolver dependencias.
* No crear otra solución de Dependency Injection.

---

# 24. domainModule

El patrón de referencia:

```kotlin
val domainModule = module {

    factory {
        GetMoviesUseCase(get())
    }
}
```

Los nuevos UseCases deben registrarse siguiendo este patrón.

---

# 25. presentationModule

El patrón de referencia:

```kotlin
val presentationModule = module {

    viewModelOf(::CatalogViewModel)
}
```

Los nuevos ViewModels deben registrarse siguiendo este patrón.

---

# 26. Agregar una nueva Screen

Cuando se agregue una nueva Screen, seguir este orden:

```text
1. Crear DataSource
        ↓
2. Crear DTOs
        ↓
3. Crear Mapper
        ↓
4. Crear RepositoryImpl
        ↓
5. Crear Service
        ↓
6. Crear Domain Model
        ↓
7. Crear Domain Repository
        ↓
8. Crear UseCase
        ↓
9. Crear State
        ↓
10. Crear Event
        ↓
11. Crear Effect
        ↓
12. Crear ViewModel
        ↓
13. Crear Screen
        ↓
14. Registrar dataModule
        ↓
15. Registrar domainModule
        ↓
16. Registrar presentationModule
        ↓
17. Crear/actualizar NavRoute
        ↓
18. Actualizar AppNavHost
        ↓
19. Compilar
        ↓
20. Corregir errores
```

No cambiar arbitrariamente este flujo.

---

# 27. Navigation

La navegación utiliza `NavRoute` y `AppNavHost`.

Ejemplo:

```kotlin
@Serializable
sealed class NavRoute {

    @Serializable
    object Catalog : NavRoute()
}
```

Y:

```kotlin
@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Catalog
    ) {

        composable<NavRoute.Catalog> {
            CatalogScreen()
        }
    }
}
```

Cuando se agregue una nueva Screen:

1. Agregar la nueva ruta a `NavRoute`.
2. Agregar el `composable` correspondiente en `AppNavHost`.
3. Mostrar la Screen correspondiente.
4. No modificar el sistema de navegación completo.
5. No crear otro sistema de navegación.

---

# 28. Ejemplo completo de integración

Para agregar una funcionalidad llamada `Earthquake`, el resultado esperado debe ser conceptualmente:

```text
earthquake/
│
├── data/
│   ├── datasource/
│   │   └── EarthquakeRemoteDataSource.kt
│   │
│   ├── dto/
│   │   ├── EarthquakeDto.kt
│   │   └── EarthquakeResponseDto.kt
│   │
│   ├── mapper/
│   │   └── EarthquakeMapper.kt
│   │
│   ├── repository/
│   │   └── EarthquakeRepositoryImpl.kt
│   │
│   └── service/
│       └── EarthquakeService.kt
│
├── domain/
│   ├── model/
│   │   └── EarthquakeModel.kt
│   │
│   ├── repository/
│   │   └── EarthquakeRepository.kt
│   │
│   └── usecase/
│       └── GetEarthquakesUseCase.kt
│
└── presentation/
    ├── screen/
    │   └── EarthquakeScreen.kt
    │
    └── viewmodel/
        ├── EarthquakeViewModel.kt
        ├── EarthquakeState.kt
        ├── EarthquakeEvent.kt
        └── EarthquakeEffect.kt
```

Además:

```text
di/
├── DataModule.kt
├── DomainModule.kt
└── PresentationModule.kt
```

y:

```text
presentation/navigation/
├── NavRoute.kt
└── AppNavHost.kt
```

Los nombres exactos deben adaptarse a la convención que ya exista en el proyecto.

---

# 29. APIs externas

Antes de implementar una API:

1. Revisar el endpoint.
2. Revisar la respuesta real.
3. Identificar el objeto raíz.
4. Identificar las listas.
5. Identificar los campos necesarios.
6. Revisar nombres exactos.
7. Revisar tipos.
8. Revisar campos nullable.
9. Crear los DTO correspondientes.
10. Crear los Mappers.

No inventar la estructura JSON.

Si la API devuelve:

```json
{
    "features": []
}
```

no asumir que devuelve:

```json
{
    "results": []
}
```

Si existe documentación o una respuesta real de la API, utilizarla como fuente de verdad.

---

# 30. Ktor

Utilizar Ktor para las llamadas HTTP cuando el proyecto ya utilice Ktor.

Mantener el patrón:

```kotlin
private val client = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}
```

No crear múltiples implementaciones diferentes de HttpClient sin necesidad.

No introducir Retrofit, OkHttp u otra biblioteca HTTP si el proyecto ya utiliza Ktor, salvo autorización explícita.

---

# 31. Compose

La UI debe utilizar Compose Multiplatform siguiendo los componentes y patrones existentes.

La Screen debe:

* observar el State;
* emitir Events;
* observar Effects;
* mostrar Loading;
* mostrar Error;
* mostrar Success.

No colocar llamadas de red directamente dentro de una `@Composable`.

No crear ViewModels dentro de la Screen manualmente si Koin ya es utilizado.

Seguir el patrón:

```kotlin
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = koinViewModel()
)
```

---

# 32. Imágenes

Si una funcionalidad necesita imágenes y el proyecto ya utiliza Coil, mantener Coil.

No agregar otra librería de imágenes sin necesidad.

Seguir el patrón existente:

```kotlin
AsyncImage(
    model = imageUrl,
    contentDescription = description
)
```

---

# 33. Dependencias

No modificar dependencias sin necesidad.

No actualizar:

* Kotlin;
* Compose;
* Ktor;
* Koin;
* Gradle;
* Android Gradle Plugin;
* plugins;

solamente para solucionar un problema puntual, salvo que sea realmente necesario.

Antes de agregar una dependencia nueva:

1. Revisar si el proyecto ya tiene una dependencia equivalente.
2. Revisar si puede resolverse con las herramientas existentes.
3. Explicar por qué es necesaria.
4. Agregarla solamente si corresponde.

---

# 34. Archivos globales

Los archivos globales que configuran la aplicación solamente deben modificarse cuando sea necesario.

Ejemplos:

```text
App.kt
MainActivity.kt
MainApplication.kt
AppNavHost.kt
NavRoute.kt
```

No modificar estos archivos simplemente porque se está agregando una nueva funcionalidad.

Para nuevas Screens, modificar únicamente lo necesario para:

```text
Navigation
DI
```

---

# 35. Regla específica para MainActivity y App

`MainActivity`, `MainApplication` y la configuración general de la aplicación deben considerarse archivos de configuración global.

Si ya están correctamente configurados:

```text
NO modificar.
```

Una nueva Screen normalmente no requiere modificar:

```text
MainActivity
MainApplication
App
```

salvo que exista una razón concreta.

---

# 36. Regla específica para DI

Cuando se agregue una nueva Screen, normalmente deben revisarse:

```text
dataModule
domainModule
presentationModule
```

El agente debe comprobar si las dependencias nuevas están registradas.

El flujo esperado es:

```text
Service
   ↓
DataSource
   ↓
RepositoryImpl
   ↓
Repository
   ↓
UseCase
   ↓
ViewModel
```

---

# 37. No duplicar lógica

Antes de crear una función, clase o componente:

1. Buscar si ya existe.
2. Revisar si puede reutilizarse.
3. Si existe un patrón equivalente, seguirlo.
4. Evitar duplicación innecesaria.

No crear:

```text
MovieRepository2
MovieService2
NewMovieViewModel
AlternativeCatalogService
```

si el problema puede resolverse utilizando la arquitectura existente.

---

# 38. Manejo de errores

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

No ocultar errores.

No utilizar soluciones como:

```kotlin
!! 
```

simplemente para hacer desaparecer un error de nullability.

No eliminar código solamente para evitar un error de compilación.

---

# 39. Validación

Después de implementar una funcionalidad:

1. Compilar.
2. Revisar errores de Kotlin.
3. Revisar errores de Gradle.
4. Revisar errores de Koin.
5. Revisar errores de navegación.
6. Revisar errores de serialización.
7. Revisar errores de Compose.
8. Ejecutar los tests existentes cuando corresponda.

No considerar una funcionalidad terminada solamente porque el código parece correcto.

---

# 40. Git

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

# 41. Cambios mínimos

Siempre preferir la solución mínima necesaria.

Si el problema está en:

```text
CatalogService.kt
```

no modificar:

```text
MainActivity.kt
App.kt
NavRoute.kt
```

sin una razón.

Si solamente se necesita crear una nueva Screen, no reorganizar todo el proyecto.

---

# 42. No refactorizar sin autorización

No realizar refactorizaciones generales durante la implementación de una funcionalidad.

No:

* cambiar nombres globalmente;
* mover paquetes;
* cambiar arquitectura;
* reemplazar Koin;
* reemplazar Ktor;
* reemplazar Compose;
* cambiar navegación;
* reorganizar todos los módulos.

La tarea solicitada debe resolverse con el menor cambio posible.

---

# 43. Estilo de código

Utilizar nombres descriptivos.

Seguir las convenciones existentes del proyecto.

Preferir:

```kotlin
val
```

sobre:

```kotlin
var
```

cuando sea posible.

Evitar:

```kotlin
!!
```

cuando exista una alternativa segura.

Evitar código duplicado.

Evitar funciones excesivamente grandes.

No agregar comentarios que simplemente describan código obvio.

---

# 45. Seguridad

Nunca exponer, inventar o modificar secretos.

No agregar API keys directamente al código si existe una alternativa segura.

Si se encuentra una API key existente en el proyecto:

* no publicarla;
* no mostrarla en respuestas;
* no moverla innecesariamente;
* informar si existe un riesgo de seguridad.

Nunca subir secretos a Git.

---

# 46. Regla para respuestas al usuario

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

# 47. Si el usuario pide una nueva Screen

El agente debe utilizar este procedimiento:

```text
PASO 1
Analizar catalog.

PASO 2
Comparar la nueva funcionalidad con catalog.

PASO 3
Definir la estructura de carpetas.

PASO 4
Crear DataSource.

PASO 5
Crear DTOs.

PASO 6
Crear Mapper.

PASO 7
Crear RepositoryImpl.

PASO 8
Crear Service.

PASO 9
Crear Domain Model.

PASO 10
Crear Domain Repository.

PASO 11
Crear UseCase.

PASO 12
Crear State.

PASO 13
Crear Event.

PASO 14
Crear Effect.

PASO 15
Crear ViewModel.

PASO 16
Crear Screen.

PASO 17
Actualizar dataModule.

PASO 18
Actualizar domainModule.

PASO 19
Actualizar presentationModule.

PASO 20
Actualizar NavRoute.

PASO 21
Actualizar AppNavHost.

PASO 22
Compilar.

PASO 23
Corregir errores.

PASO 24
Verificar funcionamiento.
```

---
# 50. Principio fundamental

La arquitectura existente tiene prioridad sobre las preferencias personales del agente.

Aunque el agente conozca otra arquitectura, patrón o biblioteca que considere mejor, debe respetar la arquitectura existente.

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

# 51. Regla final

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

El objetivo es que todas las nuevas funcionalidades parezcan haber sido creadas utilizando el mismo patrón que `catalog`.

La nueva funcionalidad debe integrarse al proyecto, no cambiar la arquitectura del proyecto.
