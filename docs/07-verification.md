# 07. Estrategia de Verificación y Pruebas (Verification & Testing)

**Última actualización:** 06 de octubre de 2026  
**Propósito:** Definir la estrategia de pruebas automatizadas y aseguramiento de calidad técnica para el sistema PisciData, estableciendo la matriz de cobertura hacia los requerimientos formales (`REQ-*`).

---

## 1. Diagnóstico del Estado Actual de Pruebas

A la fecha de esta auditoría, el estado de pruebas automatizadas en el repositorio es el siguiente:

| Proyecto | Proyecto de Tests | Pruebas Implementadas | Cobertura en Lógica de Negocio |
|---|---|---|---|
| **PisciDataBackend** | Inexistente (no hay proyecto `.Tests`) | **0 pruebas** | **0%** |
| **PisciDataMCP** | Inexistente | **0 pruebas** | **0%** |
| **PisciDataFrontend** | `shared/src/commonTest/` | 3 pruebas de plantilla ([`SharedCommonTest.kt`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataFrontend/PisciDataFrontend/shared/src/commonTest/kotlin/ucb/piscidata/SharedCommonTest.kt)) con `assertEquals(3, 1 + 2)` | **0%** |

---

## 2. Estrategia de Verificación por Capas (Pirámide de Pruebas)

Para construir una red de seguridad confiable sin ralentizar el desarrollo, se recomienda abordar las pruebas en el siguiente orden de retorno de inversión:

### Nivel 1: Pruebas Unitarias de Validadores (Prioridad Inmediata)
* **Objetivo:** Probar todas las reglas sintácticas del catálogo `BR-*`.
* **Ventaja técnica:** Las clases en `Domain/Validators/` (`FarmValidator`, `UserValidator`, `PondValidator`, etc.) son **métodos estáticos puros sin dependencias externas** (no requieren base de datos, inyección de dependencias ni mocks).
* **Herramientas:** xUnit / NUnit en .NET.
* **Casos de prueba esenciales:**
  * Rechazar roles de usuario distintos a `"Admin"`, `"Technician"`, `"Worker"`.
  * Rechazar contraseñas de menos de 6 caracteres.
  * Rechazar coordenadas geográficas fuera de rango `[-90, 90]` y `[-180, 180]`.
  * Rechazar dimensiones negativas de estanques.
  * Rechazar ciclos con `EndDate < StartDate`.
  * Rechazar raciones de alimento con `QuantityKg <= 0`.

### Nivel 2: Pruebas Unitarias de Servicios de Aplicación
* **Objetivo:** Probar la orquestación de casos de uso y mapeos DTO <-> Entidad.
* **Prerrequisito técnico:** Resolver la deuda técnica [`TECH-ARC-01`](file:///home/javi/Documentos/uni/taller/PisciData/docs/08-current-state.md#6-registro-de-deuda-t%C3%A9cnica-y-hallazgos-cr%C3%ADticos) inyectando interfaces (`IFarmRepository` o `IBaseRepository<Farm>`) en los servicios en lugar de clases concretas, permitiendo el uso de librerías de simulación como **NSubstitute** o **Moq**.

### Nivel 3: Pruebas de Integración de API
* **Objetivo:** Probar el ciclo de vida HTTP completo, serialización JSON y persistencia en base de datos.
* **Herramientas:** `Microsoft.AspNetCore.Mvc.Testing` (`WebApplicationFactory`) utilizando un contenedor efímero de MySQL (Testcontainers) o base de datos de pruebas local.

---

## 3. Matriz de Trazabilidad de Verificación hacia Requerimientos

| ID Requerimiento | Descripción | Nivel de Prueba Recomendado | Estado Actual |
|---|---|---|---|
| `REQ-AUTH-001` | Registro y validación de usuarios | Unitarias (`UserValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-AUTH-002` | Eliminación lógica de usuarios | Integración / Servicio | **Sin pruebas (0%)** |
| `REQ-FARM-001` | Validación y creación de granjas | Unitarias (`FarmValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-FARM-002` | Dimensiones métricas de estanques | Unitarias (`PondValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-OPS-001` | Consistencia de fechas en ciclos | Unitarias (`ProductioncycleValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-OPS-002` | Control de % proteína en alimentos | Unitarias (`FeedValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-OPS-003` | Raciones estrictamente positivas | Unitarias (`FeedingValidatorTests`) | **Sin pruebas (0%)** |
| `REQ-OPS-004` | Pesajes y muestras de biometría | Unitarias (`BiometricsValidatorTests`) | **Sin pruebas (0%)** |
