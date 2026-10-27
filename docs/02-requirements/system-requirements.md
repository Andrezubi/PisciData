# 02. Catálogo de Requerimientos y Reglas de Negocio (System Requirements)

**Última actualización:** 06 de octubre de 2026  
**Propósito:** Especificación autoritativa de requerimientos y reglas de negocio del sistema PisciData para el flujo *Spec-Driven Development*. Cada requerimiento cuenta con identificador estable y matriz de trazabilidad auditable hacia el código.

---

## 1. Catálogo de Reglas de Negocio Verificables (Business Rules)

Las siguientes reglas provienen de la lógica de validación codificada en `Domain/Validators/` y las restricciones de base de datos en `PiscidatadbContext.cs`:

| ID Regla | Entidad Afectada | Definición de la Regla de Negocio | Evidencia en Código |
|---|---|---|---|
| **BR-USR-01** | `User` | El rol del usuario debe pertenecer obligatoriamente a uno de los tres valores predefinidos: `"Admin"`, `"Technician"`, `"Worker"`. | [`UserValidator.cs:7`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/UserValidator.cs#L7) |
| **BR-USR-02** | `User` | La contraseña de acceso debe tener una longitud mínima de 6 caracteres al momento de la creación. | [`UserValidator.cs:22`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/UserValidator.cs#L22) |
| **BR-USR-03** | `User` | El teléfono celular es el campo de contacto obligatorio para la cuenta de usuario. | [`UserValidator.cs:19`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/UserValidator.cs#L19) |
| **BR-FRM-01** | `Farm` | Toda granja debe estar asociada a un propietario válido (`OwnerUserId > 0`). | [`FarmValidator.cs:11`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FarmValidator.cs#L11) |
| **BR-FRM-02** | `Farm` | El nombre de la granja es obligatorio y no puede exceder los 150 caracteres. | [`FarmValidator.cs:16`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FarmValidator.cs#L16) |
| **BR-FRM-03** | `Farm` | Si se registran coordenadas, la latitud debe estar en el rango `[-90, 90]` y la longitud en `[-180, 180]`. | [`FarmValidator.cs:19-23`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FarmValidator.cs#L19-L23) |
| **BR-PND-01** | `Pond` | Todo estanque debe pertenecer a una granja válida (`FarmId > 0`) y poseer un código identificador obligatorio no vacío. | [`PondValidator.cs:11-15`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/PondValidator.cs#L11-L15) |
| **BR-PND-02** | `Pond` | Las dimensiones físicas del estanque (ancho, largo, diámetro, profundidad y área) deben ser valores numéricos no negativos (`>= 0`). | [`PondValidator.cs:17-31`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/PondValidator.cs#L17-L31) |
| **BR-CYC-01** | `Productioncycle` | Todo ciclo debe asociarse a un estanque (`PondId > 0`) y a una especie (`SpeciesId > 0`), con fecha de inicio obligatoria. | [`ProductioncycleValidator.cs:11-18`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/ProductioncycleValidator.cs#L11-L18) |
| **BR-CYC-02** | `Productioncycle` | La fecha de fin del ciclo (`EndDate`), si se define, debe ser mayor o igual a la fecha de inicio (`StartDate`). | [`ProductioncycleValidator.cs:20`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/ProductioncycleValidator.cs#L20) |
| **BR-CYC-03** | `Productioncycle` | Las métricas de siembra inicial (cantidad de peces, peso promedio en gramos y edad en días) deben ser `>= 0`. | [`ProductioncycleValidator.cs:23-30`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/ProductioncycleValidator.cs#L23-L30) |
| **BR-FED-01** | `Feed` | El porcentaje de proteína cruda del alimento balanceado debe ubicarse entre 0% y 100%. | [`FeedValidator.cs:21`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FeedValidator.cs#L21) |
| **BR-FED-02** | `Feed` | El tamaño del pellet, el stock actual y el stock mínimo de alimento deben ser valores `>= 0`. | [`FeedValidator.cs:24-31`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FeedValidator.cs#L24-L31) |
| **BR-FDG-01** | `Feeding` | La cantidad de ración servida (`QuantityKg`) en una alimentación debe ser estrictamente mayor a 0 (`QuantityKg > 0`). | [`FeedingValidator.cs:24`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FeedingValidator.cs#L24) |
| **BR-BIO-01** | `Biometric` | Los valores de peso promedio, biomasa total estimada y ración calculada en un muestreo deben ser `>= 0`. | [`BiometricValidator.cs:21-29`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/BiometricValidator.cs#L21-L29) |
| **BR-SMP-01** | `Biometricssample` | En el muestreo por cubetas, el número de muestra debe ser `> 0`, el conteo de peces `>= 0` y los pesos netos y de cubeta `>= 0`. | [`BiometricssampleValidator.cs:18-35`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/BiometricssampleValidator.cs#L18-L35) |
| **BR-SUP-01** | `Supply` | Todo insumo de almacén debe tener nombre, unidad de medida y cantidad en stock `>= 0`. | [`SupplyValidator.cs:17-26`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/SupplyValidator.cs#L17-L26) |
| **BR-DEL-01** | `Farm`, `User` | La eliminación de Granjas y Usuarios debe realizarse mediante borrado lógico (*Soft Delete*), estableciendo `IsActive = false` y `UpdatedAt = UtcNow`. | [`FarmRepository.cs:20-25`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Repositories/FarmRepository.cs#L20-L25), [`UserRepository.cs:24-30`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Repositories/UserRepository.cs#L24-L30) |

---

## 2. Especificación de Requerimientos y Product Backlog (109 HUs)

El alcance funcional oficial del proyecto está definido por un **Product Backlog de 109 Historias de Usuario (HU-01 a HU-109)**. Para el enfoque de *Spec-Driven Development*, se ha delimitado un **MVP Inicial** (Producto Mínimo Viable) compuesto por un subconjunto específico de estas historias.

### 2.1. Matriz de Trazabilidad (MVP vs Implementación Actual)

A continuación se mapea el estado de los requerimientos correspondientes a los módulos principales evaluados frente a las HUs:

### Módulo: Usuarios y Acceso (REQ-AUTH)

#### `REQ-AUTH-001`: Registro y Gestión de Usuarios
* **Descripción:** El sistema debe permitir registrar usuarios asignando nombres, apellidos, teléfono, rol y credencial.
* **Reglas:** `BR-USR-01`, `BR-USR-02`, `BR-USR-03`.
* **Criterio de Aceptación:** Si se envían datos válidos con rol permitido y contraseña `>= 6` caracteres, el sistema persiste el usuario con `IsActive = true` y retorna `201 Created`. Si el rol o contraseña son inválidos, retorna `400 Bad Request` con el detalle de errores.
* **Trazabilidad de Código:** `CreateUserDto` -> `UserValidator` -> `UserService` -> `UserRepository` -> `UsersController.Create`.
* **Trazabilidad HU (MVP):** Rango de HUs de gestión de usuarios (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED** (con deuda crítica de seguridad: contraseña en texto plano y falta de endpoint login).
* **Evidencia:** 0 pruebas (Sin cobertura automatizada).

#### `REQ-AUTH-002`: Eliminación Lógica de Usuarios
* **Descripción:** La eliminación de un usuario no debe borrar físicamente su fila en la base de datos para preservar el rastro de auditoría de las operaciones que realizó.
* **Reglas:** `BR-DEL-01`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

---

### Módulo: Granjas y Estanques (REQ-FARM)

#### `REQ-FARM-001`: Administración de Granjas Acuícolas
* **Descripción:** Permitir el registro, consulta, modificación y baja lógica de predios acuícolas con geolocalización.
* **Reglas:** `BR-FRM-01`, `BR-FRM-02`, `BR-FRM-03`, `BR-DEL-01`.
* **Criterio de Aceptación:** Se rechaza cualquier granja con coordenadas fuera de rango o con nombre mayor a 150 caracteres.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

#### `REQ-FARM-002`: Catálogo y Dimensionamiento de Estanques
* **Descripción:** Permitir dar de alta estanques asociados a una granja, registrando sus dimensiones métricas y código identificador.
* **Reglas:** `BR-PND-01`, `BR-PND-02`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

---

### Módulo: Producción y Nutrición (REQ-OPS)

#### `REQ-OPS-001`: Gestión de Ciclos de Siembra
* **Descripción:** Permitir la apertura de ciclos de producción asignando estanque, especie, fecha de siembra y parámetros biométricos iniciales de alevines.
* **Reglas:** `BR-CYC-01`, `BR-CYC-02`, `BR-CYC-03`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

#### `REQ-OPS-002`: Control de Alimentos Balanceados
* **Descripción:** Mantener el inventario de marcas de alimento, porcentaje proteico y existencias en almacén.
* **Reglas:** `BR-FED-01`, `BR-FED-02`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

#### `REQ-OPS-003`: Registro de Alimentación Diaria
* **Descripción:** Registrar el suministro de comida por estanque/ciclo, registrando ración en kilogramos, hora y comportamiento del pez.
* **Reglas:** `BR-FDG-01`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

#### `REQ-OPS-004`: Muestreos Biométricos y Pesaje por Cubetas
* **Descripción:** Registrar pesajes muestrales volumétricos con conteo de peces para estimar biomasa y ración diaria recomendada.
* **Reglas:** `BR-BIO-01`, `BR-SMP-01`.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → IMPLEMENTED / NOT VERIFIED**.
* **Evidencia:** 0 pruebas.

---

### Módulo: Monitoreo y Operaciones Pendientes (REQ-MON / REQ-AI)

#### `REQ-MON-001`: Monitoreo Fisicoquímico de Calidad de Agua
* **Descripción:** Registrar mediciones periódicas de temperatura, oxígeno disuelto, pH y transparencia Secchi.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → PARTIAL** (Solo esquema BD existente, sin código ejecutable).

#### `REQ-MON-002`: Registro de Mortalidad y Diagnóstico
* **Descripción:** Registrar número de peces muertos retirados del estanque y signos clínicos observados.
* **Trazabilidad HU (MVP):** (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → PARTIAL** (Solo esquema BD existente).

#### `REQ-AI-001`: Asistencia y Herramientas Inteligentes para Piscicultores
* **Descripción:** Proveer un servidor de herramientas bajo Model Context Protocol para interactuar con agentes de IA externos y consultar métricas de la granja.
* **Trazabilidad de Código:** Proyecto `PisciDataMCP` y tablas `aiconversation`/`aimessage`.
* **Trazabilidad HU (MVP):** Rango del asistente IA (Especificación exacta: REQUIRES HUMAN VALIDATION).
* **Estado SDD:** **SPECIFIED → PARTIAL** (En desarrollo / Plantilla inicial sin herramientas del dominio).
