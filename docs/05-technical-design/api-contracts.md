# 05. Contratos de Interfaz de API (API Contracts)

**Última actualización:** 06 de octubre de 2026  
**Versión de API:** 1.0 (ASP.NET Core .NET 10)  
**Propósito:** Este documento define formalmente la especificación de los 45 endpoints implementados en el backend. Sirve como contrato vinculante para el desarrollo del cliente móvil (`PisciDataFrontend`), clientes web y agentes de IA.

---

## 1. Convenciones Globales de la API

* **Base URL:** `http://localhost:5007/api` (o puerto configurado por el host).
* **Protocolo y Formato:** HTTP/1.1 y HTTP/2, `Content-Type: application/json`, `Accept: application/json`.
* **Serialización JSON:** Por defecto en ASP.NET Core, los nombres de propiedades se serializan en **`camelCase`** en el payload JSON, mapeándose a las propiedades en `PascalCase` de los DTOs de C#.
* **Autenticación:** **[Actualmente Ausente / Anónimo]**. No se requiere cabecera `Authorization` en este momento (registrado en [`TECH-SEC-02`](file:///home/javi/Documentos/uni/taller/PisciData/docs/08-current-state.md#6-registro-de-deuda-t%C3%A9cnica-y-hallazgos-cr%C3%ADticos)).
* **Estructura Estándar de Códigos de Respuesta:**
  * `200 OK`: Operación de consulta o actualización exitosa.
  * `201 Created`: Recurso creado exitosamente. Incluye cabecera `Location: /api/[Recurso]/{id}`.
  * `204 No Content`: Eliminación exitosa sin cuerpo de respuesta.
  * `400 Bad Request`: Error de validación. Retorna un arreglo JSON de cadenas: `["Error 1", "Error 2"]`.
  * `404 Not Found`: El recurso no existe o fue desactivado mediante borrado lógico.

---

## 2. Módulo: Granjas (`/api/Farms`)

Controlador: [`FarmsController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FarmsController.cs) | Servicio: [`FarmService.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Application/Services/FarmService.cs)

### 2.1. Listar Granjas
* **Ruta:** `GET /api/Farms`
* **Respuesta Exitosa (`200 OK`):**
```json
[
  {
    "id": 1,
    "ownerUserId": 2,
    "name": "Piscigranja Los Andes",
    "description": "Granja principal de trucha",
    "address": "Valle Alto Km 14",
    "latitude": -17.3895,
    "longitude": -66.1568,
    "createdAt": "2026-10-06T12:00:00Z",
    "updatedAt": null,
    "isActive": true
  }
]
```

### 2.2. Obtener Granja por ID
* **Ruta:** `GET /api/Farms/{id}`
* **Parámetros de ruta:** `id` (int, obligatorio).
* **Respuesta (`200 OK`):** Objeto `FarmDto` (ver esquema superior).
* **Respuesta (`404 Not Found`):** Si no existe o `isActive == false`.

### 2.3. Crear Granja
* **Ruta:** `POST /api/Farms`
* **Payload (`CreateFarmDto`):**
```json
{
  "ownerUserId": 2,
  "name": "Piscigranja Los Andes",
  "description": "Granja principal de trucha",
  "address": "Valle Alto Km 14",
  "latitude": -17.3895,
  "longitude": -66.1568
}
```
* **Reglas de Validación ([`FarmValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FarmValidator.cs)):**
  * `ownerUserId`: Obligatorio, `> 0`.
  * `name`: Obligatorio, longitud `<= 150` caracteres.
  * `latitude`: Opcional, rango `[-90, 90]`.
  * `longitude`: Opcional, rango `[-180, 180]`.
* **Respuesta (`201 Created`):** Objeto `FarmDto` creado.
* **Respuesta (`400 Bad Request`):** `["Name is required.", "OwnerUserId is required and must be greater than 0."]`

### 2.4. Actualizar Granja
* **Ruta:** `PUT /api/Farms/{id}`
* **Payload (`UpdateFarmDto`):** Mismo esquema y validaciones que `CreateFarmDto`.
* **Respuesta (`200 OK`):** Objeto `FarmDto` actualizado.
* **Respuesta (`404 Not Found`):** Si la granja no existe.

### 2.5. Eliminar Granja (Soft Delete)
* **Ruta:** `DELETE /api/Farms/{id}`
* **Comportamiento:** Establece `isActive = false` y actualiza `updatedAt`.
* **Respuesta (`204 No Content`):** Éxito.

---

## 3. Módulo: Usuarios (`/api/Users`)

Controlador: [`UsersController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/UsersController.cs) | Servicio: [`UserService.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Application/Services/UserService.cs)

> [!WARNING]
> **Aviso de Seguridad:** Actualmente el endpoint `POST /api/Users` almacena la contraseña sin hashear en la columna `passwordHash`. No existe endpoint de Login.

### 3.1. Listar Usuarios
* **Ruta:** `GET /api/Users`
* **Respuesta (`200 OK`):**
```json
[
  {
    "id": 1,
    "firstName": "Juan",
    "lastName": "Pérez",
    "phone": "+59170012345",
    "role": "Admin",
    "lastLoginAt": null,
    "createdAt": "2026-10-06T10:00:00Z",
    "updatedAt": null,
    "isActive": true
  }
]
```

### 3.2. Obtener Usuario por ID
* **Ruta:** `GET /api/Users/{id}`
* **Respuesta (`200 OK` / `404 Not Found`).**

### 3.3. Crear Usuario
* **Ruta:** `POST /api/Users`
* **Payload (`CreateUserDto`):**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "+59170012345",
  "password": "Password123",
  "role": "Admin"
}
```
* **Reglas de Validación ([`UserValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/UserValidator.cs)):**
  * `firstName`, `lastName`, `phone`: Obligatorios, no vacíos.
  * `password`: Obligatorio, longitud `>= 6` caracteres.
  * `role`: Obligatorio. Valores estrictamente permitidos: `"Admin"`, `"Technician"`, `"Worker"`.
* **Respuesta (`201 Created`):** Objeto `UserDto` (la respuesta no incluye el campo `password`).

### 3.4. Actualizar Usuario
* **Ruta:** `PUT /api/Users/{id}`
* **Payload (`UpdateUserDto`):** `firstName`, `lastName`, `phone`, `role` (no incluye `password`).
* **Respuesta (`200 OK` / `400 Bad Request` / `404 Not Found`).

### 3.5. Eliminar Usuario (Soft Delete)
* **Ruta:** `DELETE /api/Users/{id}`
* **Comportamiento:** Establece `isActive = false` y `updatedAt`.
* **Respuesta (`204 No Content`).**

---

## 4. Módulo: Estanques (`/api/Ponds`)

Controlador: [`PondsController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/PondsController.cs)

### 4.1. Listar y Consultar Estanques
* `GET /api/Ponds` (`200 OK`)
* `GET /api/Ponds/{id}` (`200 OK` / `404 Not Found`)

### 4.2. Crear Estanque
* **Ruta:** `POST /api/Ponds`
* **Payload (`CreatePondDto`):**
```json
{
  "farmId": 1,
  "code": "EST-01",
  "name": "Estanque de Crecimiento 1",
  "shape": "Rectangular",
  "width": 10.5,
  "length": 25.0,
  "diameter": null,
  "depth": 1.8,
  "area": 262.5,
  "description": "Estanque de tierra con fondo enmallado"
}
```
* **Reglas de Validación ([`PondValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/PondValidator.cs)):**
  * `farmId`: Obligatorio, `> 0`.
  * `code`: Obligatorio, no vacío.
  * `width`, `length`, `diameter`, `depth`, `area`: Si se envían, deben ser `>= 0`.
* **Respuesta (`201 Created`):** Objeto `PondDto` con metadatos (`id`, `createdAt`, `updatedAt`, `isActive`).

### 4.3. Actualizar y Eliminar Estanque
* `PUT /api/Ponds/{id}` (`UpdatePondDto` -> `200 OK`)
* `DELETE /api/Ponds/{id}` -> **Hard Delete** (`204 No Content`).

---

## 5. Módulo: Ciclos de Producción (`/api/Productioncycles`)

Controlador: [`ProductioncyclesController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/ProductioncyclesController.cs)

### 5.1. Listar y Consultar Ciclos
* `GET /api/Productioncycles` (`200 OK`)
* `GET /api/Productioncycles/{id}` (`200 OK` / `404 Not Found`)

### 5.2. Crear Ciclo de Producción
* **Ruta:** `POST /api/Productioncycles`
* **Payload (`CreateProductioncycleDto`):**
```json
{
  "pondId": 1,
  "speciesId": 1,
  "startDate": "2026-10-01",
  "endDate": "2027-04-01",
  "initialFishCount": 5000,
  "initialAverageWeightGrams": 15.5,
  "initialAgeDays": 45,
  "observations": "Siembra de alevines trucha arcoíris"
}
```
* **Reglas de Validación ([`ProductioncycleValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/ProductioncycleValidator.cs)):**
  * `pondId`, `speciesId`: Obligatorios, `> 0`.
  * `startDate`: Obligatoria (formato `YYYY-MM-DD`).
  * `endDate`: Opcional, pero si se envía debe ser `>= startDate`.
  * `initialFishCount`, `initialAverageWeightGrams`, `initialAgeDays`: Si se envían, deben ser `>= 0`.
* **Respuesta (`201 Created` / `400 Bad Request`).**

### 5.3. Actualizar y Eliminar Ciclo
* `PUT /api/Productioncycles/{id}` (`UpdateProductioncycleDto` -> `200 OK`)
* `DELETE /api/Productioncycles/{id}` -> **Hard Delete** (`204 No Content`).

---

## 6. Módulo: Alimentos (`/api/Feeds`)

Controlador: [`FeedsController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FeedsController.cs)

### 6.1. Crear / Modificar Alimento
* **Rutas:** `POST /api/Feeds`, `PUT /api/Feeds/{id}`
* **Payload (`CreateFeedDto` / `UpdateFeedDto`):**
```json
{
  "farmId": 1,
  "brand": "NutriFish",
  "productName": "Trucha Inicio 45%",
  "phase": "Inicio",
  "proteinPercentage": 45.0,
  "pelletSizeMm": 1.5,
  "stockKg": 500.0,
  "minimumStockKg": 100.0
}
```
* **Validaciones ([`FeedValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FeedValidator.cs)):**
  * `farmId`: `> 0`, `brand`: no vacío.
  * `proteinPercentage`: rango `[0, 100]`.
  * `pelletSizeMm`, `stockKg`, `minimumStockKg`: `>= 0`.
* **Consultas:** `GET /api/Feeds`, `GET /api/Feeds/{id}`, `DELETE /api/Feeds/{id}` (Hard delete).

---

## 7. Módulo: Registros de Alimentación (`/api/Feedings`)

Controlador: [`FeedingsController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/FeedingsController.cs)

### 7.1. Registrar Alimentación
* **Ruta:** `POST /api/Feedings`
* **Payload (`CreateFeedingDto`):**
```json
{
  "productionCycleId": 1,
  "feedId": 2,
  "feedingDate": "2026-10-06",
  "feedingTime": "08:30:00",
  "quantityKg": 12.5,
  "mealNumber": 1,
  "behavior": "Activo y voraz",
  "observations": "Consumo total en 15 minutos"
}
```
* **Validaciones ([`FeedingValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/FeedingValidator.cs)):**
  * `productionCycleId`, `feedId`: `> 0`.
  * `feedingDate`: Obligatoria.
  * `quantityKg`: Obligatoria y `> 0`.
  * `mealNumber`: Si se envía, debe ser `> 0`.
* **Consultas:** `GET /api/Feedings`, `GET /api/Feedings/{id}`, `PUT /api/Feedings/{id}`, `DELETE /api/Feedings/{id}`.

---

## 8. Módulo: Insumos de Almacén (`/api/Supplies`)

Controlador: [`SuppliesController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/SuppliesController.cs)

### 8.1. Registrar Insumo
* **Ruta:** `POST /api/Supplies`
* **Payload (`CreateSupplyDto`):**
```json
{
  "farmId": 1,
  "name": "Cal viva para desinfección",
  "category": "Químicos y Tratamiento",
  "quantity": 250.0,
  "unit": "Kg",
  "minimumStock": 50.0
}
```
* **Validaciones ([`SupplyValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/SupplyValidator.cs)):**
  * `farmId`: `> 0`, `name`: no vacío, `unit`: no vacío.
  * `quantity`: `>= 0`, `minimumStock`: `>= 0`.
* **Consultas:** `GET /api/Supplies`, `GET /api/Supplies/{id}`, `PUT /api/Supplies/{id}`, `DELETE /api/Supplies/{id}`.

---

## 9. Módulo: Biometrías (`/api/Biometrics`)

Controlador: [`BiometricsController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/BiometricsController.cs)

### 9.1. Registrar Muestreo Biométrico General
* **Ruta:** `POST /api/Biometrics`
* **Payload (`CreateBiometricDto`):**
```json
{
  "productionCycleId": 1,
  "measurementDate": "2026-10-06",
  "averageWeightGrams": 125.4,
  "biomassKg": 614.5,
  "calculatedFeedKg": 18.4,
  "healthStatus": "Saludable",
  "observations": "Buen crecimiento, aletas intactas"
}
```
* **Validaciones ([`BiometricValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/BiometricValidator.cs)):**
  * `productionCycleId`: `> 0`, `measurementDate`: Obligatoria.
  * `averageWeightGrams`, `biomassKg`, `calculatedFeedKg`: `>= 0`.
* **Consultas:** `GET /api/Biometrics`, `GET /api/Biometrics/{id}`, `PUT /api/Biometrics/{id}`, `DELETE /api/Biometrics/{id}`.

---

## 10. Módulo: Muestras Individuales de Biometría (`/api/Biometricssamples`)

Controlador: [`BiometricssamplesController.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Controllers/BiometricssamplesController.cs)

### 10.1. Registrar Muestra Específica (Pesaje por cubeta)
* **Ruta:** `POST /api/Biometricssamples`
* **Payload (`CreateBiometricssampleDto`):**
```json
{
  "biometricsId": 1,
  "sampleNumber": 1,
  "bucketWaterWeightKg": 10.0,
  "bucketWithFishWeightKg": 15.2,
  "fishCount": 42,
  "totalFishWeightKg": 5.2,
  "averageWeightGrams": 123.8
}
```
* **Validaciones ([`BiometricssampleValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/BiometricssampleValidator.cs)):**
  * `biometricsId`: `> 0`, `sampleNumber`: `> 0`, `fishCount`: `>= 0`.
  * `bucketWaterWeightKg`, `bucketWithFishWeightKg`, `totalFishWeightKg`, `averageWeightGrams`: `>= 0`.
* **Consultas:** `GET /api/Biometricssamples`, `GET /api/Biometricssamples/{id}`, `PUT /api/Biometricssamples/{id}`, `DELETE /api/Biometricssamples/{id}`.

---

## 11. Entidades No Expuestas en la API (Pendientes)

Las siguientes 10 entidades existen en MySQL y en `Domain/Models/`, pero **carecen de controladores y endpoints**:

1. `Waterquality` (`/api/Waterqualities` - No implementado)
2. `Mortality` (`/api/Mortalities` - No implementado)
3. `Species` (`/api/Species` - No implementado)
4. `Harvest` (`/api/Harvests` - No implementado)
5. `Task` (`/api/Tasks` - No implementado)
6. `Feedingbyage` (`/api/Feedingbyages` - No implementado)
7. `Feedingbyweight` (`/api/Feedingbyweights` - No implementado)
8. `Supplymovement` (`/api/Supplymovements` - No implementado)
9. `Aiconversation` (`/api/Aiconversations` - No implementado)
10. `Aimessage` (`/api/Aimessages` - No implementado)
