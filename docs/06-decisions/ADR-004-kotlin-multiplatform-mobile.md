# ADR-004: Frontend Móvil Multiplataforma con Kotlin Multiplatform y Compose

* **Estado:** Aceptado / En fase de plantilla inicial
* **Fecha:** 06 de octubre de 2026
* **Decisores:** Equipo PisciData (inferido de `libs.versions.toml` y estructura de `PisciDataFrontend`)

---

## 1. Contexto

Los usuarios finales del sistema (técnicos y operarios de campo) realizan sus actividades en las instalaciones de la piscigranja, necesitando acceder y capturar datos directamente desde dispositivos móviles tanto Android como potencialmente iOS.

Se requería seleccionar una tecnología que permitiera construir aplicaciones móviles nativas minimizando la duplicación de código entre plataformas.

## 2. Decisión

Seleccionar **Kotlin Multiplatform (KMP)** versión `2.4.20` junto con **Compose Multiplatform** versión `1.12.1` para compartir tanto la lógica de negocio como la interfaz de usuario declarativa entre Android e iOS.

Estructura de módulos:
* `shared/`: Contiene el código común de UI y lógica en `commonMain`.
* `androidApp/`: Punto de entrada nativo de Android (`MainActivity.kt`).
* `iosApp/`: Proyecto Xcode / Swift para empaquetado y ejecución en iOS.

## 3. Consecuencias

### Positivas
* Capacidad de compilar una sola base de código para Android e iOS.
* Rendimiento nativo en dispositivos móviles.
* Interfaz declarativa moderna con Jetpack Compose / Compose Multiplatform.

### Negativas y Estado Actual
* El módulo se encuentra actualmente en estado de plantilla por defecto generado por el wizard de JetBrains.
* Faltan dependencias operativas fundamentales:
  * Cliente HTTP (Ktor Client) para consumir `PisciDataBackend`.
  * Serialización JSON (`kotlinx.serialization`).
  * Biblioteca de navegación móvil.
* Existe una inconsistencia menor de estructura física de carpetas con doble anidamiento (`PisciDataFrontend/PisciDataFrontend/`).
