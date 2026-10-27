# Tasks.md — Implementación Backend de Ciclos Productivos (PisciData)

## 1. Objetivo general

Implementar toda la lógica necesaria en el **backend de PisciData** para el apartado actualmente denominado `Ciclos activos` (`Production Cycle`), utilizando el nombre **Ciclos productivos** cuando corresponda para mantener una nomenclatura coherente entre la API, los DTOs y los conceptos del dominio.

Antes de realizar cambios, analizar detalladamente la estructura, la arquitectura, las entidades, los repositorios, los servicios, los controladores, la configuración de autenticación y el acceso a datos del proyecto existente.

La implementación debe respetar la arquitectura y las convenciones actuales del proyecto. No se debe crear una arquitectura paralela ni duplicar funcionalidades que ya existan.

**Alcance exclusivo:** backend. El frontend se debe consultar únicamente para identificar los campos que necesita mostrar, los nombres de propiedades esperados y la forma en que consume los endpoints. No modificar componentes, pantallas, estilos ni lógica del frontend.

## 2. Análisis inicial obligatorio

Antes de programar:

- [ ] Revisar la estructura completa del backend y determinar las capas existentes.
- [ ] Identificar las entidades, DTOs, interfaces, repositorios, servicios y controladores relacionados con `ProductionCycle`.
- [ ] Revisar cómo se implementa la autenticación JWT y cómo se obtiene el identificador del usuario autenticado.
- [ ] Analizar el controlador de granjas (`FarmController`), especialmente el endpoint `GET /api/farm/my`, y utilizarlo como referencia para la extracción del identificador del usuario.
- [ ] Revisar cómo se relacionan las entidades `Farm`, `Pond`, `Species` y `ProductionCycle`.
- [ ] Examinar los repositorios y servicios existentes para `Biometrics`, `Harvest`, `Mortality` y `FeedingByAge`.
- [ ] Comprobar si las entidades y relaciones de Entity Framework Core representan correctamente el esquema de la base de datos.
- [ ] Revisar los endpoints y los DTOs utilizados por el frontend para este apartado.
- [ ] Identificar errores, duplicaciones, relaciones faltantes o cálculos que deban corregirse.
- [ ] Presentar los hallazgos relevantes antes de implementar cambios que puedan afectar otras funcionalidades.

La sentencia SQL proporcionada por el usuario es una **referencia del esquema y de las reglas de negocio**, no un script que se deba ejecutar. No crear ni recrear la base de datos, no insertar datos de prueba y no modificar el esquema sin que sea estrictamente necesario y esté justificado.

## 3. Autenticación y autorización

La seguridad es un requisito obligatorio para todos los endpoints de ciclos productivos.

### 3.1. Verificación de identidad

- [ ] Proteger los endpoints correspondientes mediante `[Authorize]`, siguiendo las convenciones del proyecto.
- [ ] Obtener el identificador del usuario autenticado a partir de los claims disponibles en el token.
- [ ] Tomar como referencia la lógica existente en `FarmController.GetMyFarms()`:

```csharp
var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)
    ?? User.FindFirst("UserId")
    ?? User.FindFirst("sub");

if (userIdClaim == null || !int.TryParse(userIdClaim.Value, out int userId))
{
    return Unauthorized(new
    {
        message = "No se pudo identificar al usuario."
    });
}
```

Adaptar el ejemplo a la estructura real del proyecto y evitar duplicar innecesariamente esta lógica si ya existe un mecanismo compartido.

- [ ] No confiar en un `UserId` enviado desde el frontend para determinar la identidad del usuario.
- [ ] Responder con `401 Unauthorized` cuando la solicitud no esté autenticada o no sea posible identificar correctamente al usuario.

### 3.2. Validación de propiedad de la granja

Cada consulta de ciclos debe estar limitada a la granja seleccionada por el usuario autenticado.

- [ ] Recibir y validar el identificador de la granja cuando corresponda.
- [ ] Verificar que la granja exista y pertenezca al usuario autenticado.
- [ ] Utilizar `Farm.OwnerUserId` como referencia principal de propiedad, conforme al modelo de datos existente y a la lógica de `FarmController`.
- [ ] No considerar que una granja pertenece al usuario únicamente porque el identificador fue enviado en la solicitud.
- [ ] Verificar la cadena de relaciones `ProductionCycle → Pond → Farm`.
- [ ] Obtener los ciclos productivos únicamente de estanques asociados a la granja autorizada.
- [ ] Evitar que un usuario pueda consultar ciclos de otra granja modificando el identificador de la solicitud.
- [ ] Aplicar la misma validación en consultas individuales y en cualquier otro endpoint del apartado que utilice un identificador de granja o de ciclo.
- [ ] Respetar la convención de respuestas de error que ya utilice el proyecto. Si una granja o un ciclo no existe o no es accesible, no devolver información de terceros.

La consulta debe aplicar las restricciones de autorización también en el acceso a datos. No basta con validar la granja y después recuperar un ciclo únicamente por su ID.

## 4. Información general de la granja

Cada ciclo productivo debe proporcionar la información de la granja a la que pertenece, utilizando las relaciones reales de la base de datos.

Campos requeridos:

- [ ] Código de la granja, si existe en el modelo actual.
- [ ] Nombre de la granja.
- [ ] Estado de la granja: activa o inactiva.
- [ ] Identificador de la granja, cuando sea necesario para identificarla en la respuesta.
- [ ] Identificación del estanque y su código, si el frontend los requiere.
- [ ] Especie asociada al ciclo productivo.

**Importante:** en el esquema SQL proporcionado, la tabla `Farm` contiene `Name` e `IsActive`, pero no contiene una columna `Code`. No inventar un código ni asignar arbitrariamente el código del estanque como código de granja. Revisar las entidades, migraciones y el esquema real para determinar si el código existe en otra versión del modelo. Si no existe, documentar el hallazgo y mantener la respuesta coherente con la base de datos actual.

La información debe corresponder exclusivamente a la granja seleccionada y autorizada.

## 5. Datos de los ciclos productivos

Implementar la recuperación correcta de los datos almacenados en `ProductionCycle`.

Los campos que deben contemplarse son:

| Campo | Origen |
|---|---|
| Identificador del ciclo | `ProductionCycle.Id` |
| Fecha de inicio | `ProductionCycle.StartDate` |
| Fecha de finalización | `ProductionCycle.EndDate` |
| Edad inicial en días | `ProductionCycle.InitialAgeDays` |
| Peso promedio inicial en gramos | `ProductionCycle.InitialAverageWeightGrams` |
| Cantidad inicial de peces | `ProductionCycle.InitialFishCount` |
| Observaciones | `ProductionCycle.Observations` |
| Estado del ciclo | `ProductionCycle.IsActive` y los datos de finalización, según las reglas existentes |
| Especie | Relación con `Species` |
| Información de granja y estanque | Relaciones `Pond` y `Farm` |

Requisitos:

- [ ] Mapear correctamente las entidades hacia los DTOs de respuesta.
- [ ] Mantener los nombres y tipos de las propiedades compatibles con el contrato que espera el frontend.
- [ ] Tratar los campos nullable de acuerdo con el esquema real.
- [ ] No sustituir datos inexistentes por valores ficticios.
- [ ] No confundir el estado de la granja con el estado del ciclo.
- [ ] Revisar cómo se define actualmente un ciclo activo y respetar esa regla. No asumir que `IsActive` y `EndDate == null` son equivalentes sin analizar el comportamiento existente.
- [ ] Evitar mostrar ciclos inactivos o finalizados como ciclos en curso si la definición funcional actual del apartado exige únicamente ciclos activos.
- [ ] No modificar ni eliminar registros existentes para conseguir que los datos se muestren.

## 6. Cálculo de la cantidad actual de peces

Implementar el cálculo en la capa de servicio, utilizando los datos registrados para cada ciclo productivo.

### Fórmula requerida

Cantidad actual estimada de peces:

**Peces actuales = cantidad inicial − peces cosechados − peces muertos.**

Fuentes:

- Cantidad inicial: `ProductionCycle.InitialFishCount`.
- Peces cosechados: suma de `Harvest.FishCount`.
- Peces muertos: suma de `Mortality.DeadFishCount`.

### Reglas de implementación

- [ ] Consultar los registros de `Harvest` asociados al ciclo mediante `ProductionCycleId`.
- [ ] Consultar los registros de `Mortality` asociados al ciclo mediante `ProductionCycleId`.
- [ ] Sumar todos los registros válidos de cosecha y mortalidad correspondientes al ciclo.
- [ ] Revisar cómo se utiliza `IsActive` en estas entidades y excluir los registros anulados o inactivos cuando corresponda a las reglas de negocio existentes.
- [ ] Evitar duplicaciones causadas por joins entre las tablas de cosecha y mortalidad. Agregar cada conjunto por separado o utilizar una estrategia equivalente que garantice sumas correctas.
- [ ] Manejar correctamente la ausencia de registros: una suma sin resultados debe tratarse como cero.
- [ ] Definir y documentar el comportamiento cuando `InitialFishCount` sea nulo.
- [ ] Evitar devolver cantidades negativas. Si los datos registrados producen un resultado negativo, identificar y manejar la inconsistencia sin ocultar silenciosamente los datos que la causaron.
- [ ] No utilizar las cantidades de `BiometricsSample.FishCount` para descontar peces, ya que representan muestras y no necesariamente pérdidas de población.
- [ ] Asegurar que los cálculos se realicen exclusivamente con los registros del ciclo solicitado.

El resultado debe representar una estimación de los peces vivos disponibles según los movimientos registrados, no una cantidad inventada ni una suma global de toda la granja.

## 7. Cálculo del peso promedio actual

El peso promedio actual debe obtenerse utilizando la siguiente prioridad.

### 7.1. Primera opción: registros biométricos

- [ ] Consultar `Biometrics` mediante `ProductionCycleId`.
- [ ] Considerar únicamente registros válidos según el uso de `IsActive` en el proyecto.
- [ ] Ordenar por `MeasurementDate` de forma descendente.
- [ ] Obtener el registro biométrico más reciente que contenga un `AverageWeightGrams` válido.
- [ ] Utilizar ese valor como peso promedio actual.
- [ ] No calcular un promedio simple de todos los registros históricos, porque el campo solicitado representa el peso actual y no el promedio de todas las mediciones realizadas.

Si el registro biométrico más reciente no contiene un peso válido, buscar el registro anterior más reciente que sí lo tenga.

### 7.2. Segunda opción: tabla `FeedingByAge`

Si no existe ningún registro biométrico con un peso promedio válido, estimar el peso utilizando la tabla `FeedingByAge`.

- [ ] Calcular la edad actual del ciclo según la sección 8.
- [ ] Identificar la especie del ciclo.
- [ ] Buscar el registro de `FeedingByAge` cuyo intervalo `MinimumAgeDays`–`MaximumAgeDays` corresponda a la edad actual.
- [ ] Priorizar el registro específico de la especie (`SpeciesId`) y considerar los registros generales (`SpeciesId IS NULL`) únicamente como alternativa cuando no exista un registro específico aplicable.
- [ ] Utilizar `ApproximatedFishWeightGrams` como peso promedio estimado.
- [ ] Si existen varios registros aplicables, seleccionar uno de forma determinista, respetando la especificidad de la especie y los intervalos de edad.
- [ ] Comprobar `IsActive` y los criterios de validez de los registros según el modelo existente.
- [ ] Si no existe un registro válido, devolver un valor nulo o la representación que corresponda al DTO actual, en lugar de inventar un peso.

**Importante:** `DailyAmountKilo` representa una cantidad de alimento diaria de referencia, no el peso promedio de un pez. No utilizar ese campo como sustituto de `ApproximatedFishWeightGrams`.

La estimación debe ser coherente con la especie y la edad del ciclo. No modificar los datos de biometría ni insertar registros artificiales para producir un resultado.

## 8. Cálculo de la edad actual en días

La edad actual se debe calcular utilizando la edad inicial y el tiempo transcurrido desde el inicio del ciclo.

### Fórmula requerida

**Edad actual en días = edad inicial en días + días transcurridos desde la fecha de inicio.**

Equivalente a:

`InitialAgeDays + (fecha actual − StartDate).TotalDays`

Requisitos:

- [ ] Consultar `ProductionCycle.InitialAgeDays` y `ProductionCycle.StartDate`.
- [ ] Calcular la diferencia entre la fecha actual y la fecha de inicio utilizando días naturales completos.
- [ ] Para evitar problemas con las horas, realizar la operación con fechas normalizadas a fecha, de acuerdo con los tipos del modelo.
- [ ] Evitar resultados negativos cuando existan fechas futuras o datos inconsistentes.
- [ ] Definir el comportamiento cuando `InitialAgeDays` sea nulo, respetando los tipos del DTO y las convenciones del proyecto.
- [ ] Si el ciclo tiene una fecha de finalización y la edad se solicita como edad al finalizar, utilizar esa fecha de acuerdo con las reglas del negocio. Si se solicita la edad actual de un ciclo en curso, utilizar la fecha actual.
- [ ] Utilizar una fuente de fecha consistente y fácil de probar, preferiblemente mediante las abstracciones de fecha que ya existan en el proyecto.
- [ ] Evitar utilizar la fecha de creación del registro en lugar de `StartDate`.

El cálculo debe realizarse en el backend y el resultado debe llegar listo para mostrar en el frontend.

## 9. Integración de los cálculos en los servicios

La lógica de negocio debe residir en la capa de servicio o en las abstracciones de dominio que correspondan a la arquitectura actual.

- [ ] Revisar si existe `ProductionCycleService` o un servicio equivalente y reutilizarlo.
- [ ] Crear o ajustar los métodos necesarios para obtener los ciclos de una granja autorizada.
- [ ] Centralizar los cálculos de peces actuales, peso promedio actual y edad actual en la capa adecuada.
- [ ] Mantener los controladores delgados: deben validar la solicitud, obtener la identidad del usuario, invocar el servicio y devolver la respuesta HTTP correspondiente.
- [ ] Evitar colocar consultas complejas, agregaciones o cálculos de negocio directamente en el controlador.
- [ ] Evitar duplicar las mismas fórmulas en distintos endpoints.
- [ ] Mantener la separación de responsabilidades entre servicios y repositorios.
- [ ] Si se requieren nuevas operaciones de acceso a datos, agregarlas a las interfaces y a sus implementaciones correspondientes.
- [ ] Utilizar consultas eficientes y asíncronas, siguiendo las convenciones del proyecto.
- [ ] Evitar el problema N+1 al consultar biometrías, cosechas y mortalidades para múltiples ciclos.
- [ ] Revisar el uso de `AsNoTracking`, proyecciones y agregaciones cuando corresponda.
- [ ] Registrar correctamente las dependencias en la inyección de dependencias existente.
- [ ] Mantener la compatibilidad con la versión de .NET, Entity Framework Core y el proveedor de base de datos que utiliza el proyecto.

No introducir patrones, paquetes ni abstracciones adicionales si la implementación existente ya proporciona una solución adecuada.

## 10. Controladores y comunicación con el frontend

Implementar o adaptar los endpoints necesarios para que el frontend pueda recuperar la información de los ciclos productivos.

- [ ] Revisar las rutas existentes antes de crear nuevas.
- [ ] Mantener la convención de rutas, nombres y respuestas HTTP del backend.
- [ ] Permitir consultar los ciclos asociados a una granja concreta y autorizada.
- [ ] Permitir recuperar el detalle de un ciclo cuando la funcionalidad existente lo requiera.
- [ ] Asegurar que todas las respuestas estén filtradas por la granja autorizada.
- [ ] Devolver los datos calculados mediante los DTOs correspondientes.
- [ ] Validar los identificadores recibidos y devolver respuestas HTTP coherentes.
- [ ] No exponer entidades de Entity Framework directamente como respuesta de la API si el proyecto utiliza DTOs.
- [ ] Mantener la compatibilidad con los contratos actuales siempre que sea posible.

### Revisión del frontend como referencia

Consultar el código del frontend únicamente para verificar:

- [ ] Qué endpoint consume el apartado de ciclos productivos.
- [ ] Cómo se envía o selecciona el identificador de la granja.
- [ ] Qué propiedades espera recibir para mostrar la granja, el estanque, la especie y el ciclo.
- [ ] Qué formato espera para fechas, cantidades, pesos, edades y estados.
- [ ] Qué valores admite cuando una propiedad no tiene datos.
- [ ] Si existen nombres de propiedades diferentes entre los DTOs del backend y los modelos utilizados por el frontend.

Si se detectan incompatibilidades, corregir el contrato o el mapeo en el backend de manera justificada. No modificar el frontend como parte de esta tarea.

## 11. Estados y consistencia de los datos

- [ ] Distinguir entre una granja activa (`Farm.IsActive`) y un ciclo productivo activo (`ProductionCycle.IsActive`).
- [ ] Revisar cómo se representan los ciclos finalizados mediante `EndDate`.
- [ ] Evitar inferir estados exclusivamente por la existencia o ausencia de registros de cosecha.
- [ ] No marcar automáticamente un ciclo como finalizado por tener cero peces estimados.
- [ ] No modificar el estado del ciclo como efecto secundario de una consulta.
- [ ] No modificar la cantidad inicial de peces para almacenar el resultado del cálculo.
- [ ] No alterar registros de cosecha, mortalidad o biometría durante la lectura.
- [ ] No modificar tablas ni relaciones que pertenezcan a otras funcionalidades.
- [ ] Si se encuentran datos contradictorios, documentar el problema y manejarlo de manera explícita.

## 12. Pruebas y validaciones

Implementar o ampliar las pruebas automatizadas siguiendo las herramientas y convenciones de testing existentes.

### 12.1. Seguridad

- [ ] Una solicitud sin autenticación recibe `401 Unauthorized`.
- [ ] Un token sin un identificador de usuario válido no permite consultar los ciclos.
- [ ] Un usuario solo puede consultar ciclos de sus propias granjas.
- [ ] Un usuario no puede acceder a ciclos de otra granja manipulando el identificador.
- [ ] Un ciclo de un estanque ajeno a la granja autorizada no devuelve información.
- [ ] Una granja inexistente o no autorizada se gestiona correctamente.

### 12.2. Cantidad de peces

Probar, como mínimo:

- [ ] Un ciclo sin cosechas ni mortalidades.
- [ ] Un ciclo con varias cosechas.
- [ ] Un ciclo con varios registros de mortalidad.
- [ ] Un ciclo con cosechas y mortalidades simultáneas.
- [ ] Registros inactivos, cuando aplique su exclusión.
- [ ] Ausencia de registros y valores iniciales nulos.
- [ ] Datos inconsistentes que puedan producir una cantidad negativa.

Ejemplo de cálculo esperado: si un ciclo comienza con 5.000 peces, registra 300 peces cosechados y 120 peces muertos, la cantidad actual estimada debe ser 4.580 peces.

### 12.3. Peso promedio actual

- [ ] Existe un registro biométrico reciente con peso válido.
- [ ] El registro biométrico más reciente tiene el peso nulo, pero existe otro anterior válido.
- [ ] No existen biometrías válidas y hay un registro aplicable en `FeedingByAge`.
- [ ] No hay registros específicos para la especie, pero sí un registro general válido.
- [ ] No existe ningún registro aplicable en `FeedingByAge`.
- [ ] Hay intervalos de edad que no coinciden con la edad actual.

### 12.4. Edad actual

- [ ] Edad inicial de cero días.
- [ ] Ciclo iniciado hace varios días.
- [ ] Ciclo con edad inicial distinta de cero.
- [ ] Fecha de inicio futura o datos inconsistentes.
- [ ] Edad inicial nula.
- [ ] Ciclo finalizado con fecha de finalización registrada.

### 12.5. Integración y rendimiento

- [ ] Los DTOs devuelven todos los campos requeridos.
- [ ] Las propiedades coinciden con los contratos existentes del frontend.
- [ ] No se producen sumas duplicadas por joins.
- [ ] Las consultas son asíncronas y no generan consultas innecesarias por cada ciclo.
- [ ] Las pruebas existentes del proyecto continúan funcionando.
- [ ] El backend compila correctamente.

No considerar la implementación terminada si únicamente compila, pero los cálculos o las restricciones de acceso no han sido probados.

## 13. Restricciones de alcance

Durante esta tarea:

- No implementar funcionalidades nuevas en el frontend.
- No modificar estilos, componentes ni pantallas.
- No implementar otros apartados del sistema, como alimentación, calidad del agua, mortalidad, cosecha o inventario, salvo las consultas necesarias para calcular los datos de los ciclos productivos.
- No realizar refactorizaciones generales que no sean necesarias para esta funcionalidad.
- No cambiar el esquema de la base de datos por conveniencia.
- No ejecutar la sentencia SQL proporcionada como script.
- No inventar propiedades, tablas, relaciones ni endpoints que no estén respaldados por el proyecto.
- No reemplazar datos inexistentes por valores ficticios.
- No eliminar funcionalidades existentes ni modificar sus contratos sin justificación.

Si se descubre que falta una propiedad, relación o información necesaria en el modelo actual, documentar el hallazgo y proponer la modificación mínima necesaria antes de realizar un cambio estructural.

## 14. Criterios de aceptación

La tarea se considerará completada cuando:

- [ ] El apartado pueda obtener los ciclos productivos de la granja seleccionada.
- [ ] Todas las solicitudes estén protegidas por autenticación.
- [ ] Se verifique la propiedad de la granja antes de devolver datos.
- [ ] La información general de la granja, el estanque y la especie se obtenga de las relaciones correctas.
- [ ] La cantidad actual de peces se calcule con las cantidades iniciales, cosechas y mortalidades.
- [ ] El peso promedio actual se obtenga primero de biometrías y, si no existen pesos válidos, se estime mediante `FeedingByAge`.
- [ ] La edad actual se calcule con la edad inicial y los días transcurridos desde `StartDate`.
- [ ] Las fechas, cantidades iniciales, peso inicial, edad inicial y observaciones provengan de `ProductionCycle`.
- [ ] Los DTOs sean coherentes con los campos esperados por el frontend.
- [ ] Las consultas sean eficientes y no filtren información de otras granjas.
- [ ] El backend compile y las pruebas relevantes pasen.
- [ ] No se hayan modificado funcionalidades del frontend ni funcionalidades ajenas al alcance.

## 15. Informe final de implementación

Al finalizar, entregar un resumen técnico que incluya:

1. Archivos creados y modificados, indicando sus rutas.
2. Endpoints creados o modificados, con método HTTP, ruta y propósito.
3. Servicios, repositorios, DTOs y entidades involucrados.
4. Reglas de negocio implementadas para cada cálculo.
5. Medidas de autenticación y autorización aplicadas.
6. Pruebas ejecutadas y resultados obtenidos.
7. Problemas encontrados en el esquema, relaciones o contratos existentes.
8. Cualquier cambio de base de datos que resulte indispensable y su justificación, sin ejecutarlo automáticamente.
9. Confirmación explícita de que el frontend solo fue revisado y no modificado.

**Prioridad principal:** implementar correctamente la lógica de negocio en el backend, respetar la arquitectura existente, garantizar el aislamiento de los datos por usuario y granja, y entregar al frontend información consistente y calculada correctamente.
