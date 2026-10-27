# Tasks.md — Implementación Frontend de Ciclos Productivos (PisciData)

## 1. Objetivo

Implementar y adaptar completamente el apartado **Ciclos activos (Production Cycle)** del frontend de PisciData para que funcione con la implementación real del backend.

Si corresponde con la funcionalidad existente, cambiar el nombre visible de **Ciclos activos** a **Ciclos productivos**, manteniendo la coherencia con los nombres utilizados en el backend y el resto de la aplicación.

Actualmente, la información mostrada en pantalla podría contener datos estáticos, valores de prueba o información hardcodeada. Se debe revisar detalladamente el frontend y reemplazar dichos datos por información real obtenida desde los endpoints del backend.

El objetivo es que el usuario visualice correctamente los ciclos productivos de sus propias granjas, con sus datos actualizados y calculados de acuerdo con la lógica implementada en el backend.

**Alcance exclusivo:** frontend del apartado Production Cycle. No implementar ni modificar otros apartados de PisciData.

---

## 2. Análisis obligatorio antes de implementar

Antes de realizar cambios, el agente debe revisar y comprender la implementación existente.

### 2.1. Revisar el frontend

- Identificar la página, vista o componente correspondiente a Production Cycle.
- Identificar los componentes reutilizados por esta sección.
- Revisar los servicios HTTP, clientes API, modelos, interfaces, DTO y tipos relacionados con los ciclos productivos.
- Identificar los endpoints que utiliza actualmente la sección.
- Detectar datos hardcodeados, arreglos de ejemplo, valores estáticos, cálculos realizados en el cliente y respuestas simuladas.
- Revisar los mecanismos existentes para gestionar la sesión, el token de autenticación y los errores de las peticiones.
- Identificar cómo se selecciona una granja y cómo se conserva el `FarmId` seleccionado.
- Revisar el diseño actual para conservar la identidad visual y los componentes reutilizables del proyecto.

### 2.2. Revisar la implementación real del backend

Leer y comprender el código implementado para Production Cycle, incluyendo los elementos que correspondan según la arquitectura del proyecto:

- Controladores y endpoints.
- DTO de respuesta y solicitud.
- Entidades y relaciones.
- Servicios y reglas de negocio.
- Repositorios y consultas a la base de datos.
- Autenticación y autorización.
- Validaciones y manejo de errores.
- Cálculos de cantidad actual de peces, peso promedio y edad actual.
- Filtros por granja y por usuario autenticado.

No asumir que los nombres de los endpoints, los campos de respuesta o los formatos de los datos son los esperados. Obtenerlos directamente del código real del backend.

Si existen diferencias entre los contratos del frontend y del backend, adaptar el frontend al contrato real implementado. No modificar el backend como solución automática a un problema del frontend.

---

## 3. Conexión con el backend

### 3.1. Eliminar los datos estáticos

- Reemplazar los datos hardcodeados de Production Cycle por datos obtenidos mediante peticiones reales al backend.
- Eliminar los arreglos de prueba y objetos ficticios que se utilicen para mostrar ciclos productivos.
- Evitar que se muestren datos de ejemplo cuando una petición falla, salvo que exista un modo de demostración explícitamente establecido por el proyecto.
- No generar ciclos, granjas, especies, cantidades ni estadísticas ficticias.
- No mantener valores estáticos que aparenten ser información real de la base de datos.

Los textos informativos, etiquetas, iconos y elementos puramente visuales pueden permanecer estáticos cuando no representen datos del negocio.

### 3.2. Utilizar los servicios existentes

- Reutilizar el cliente HTTP y la configuración de conexión al backend que ya utilice el proyecto.
- Reutilizar los servicios de autenticación y autorización existentes.
- Crear o adaptar un servicio específico de Production Cycle únicamente si es necesario según la arquitectura actual.
- Mantener separados los componentes visuales, los modelos y la comunicación con la API siguiendo las convenciones del proyecto.
- No duplicar servicios ni crear clientes HTTP innecesarios.
- No escribir consultas directas a MySQL desde el frontend.
- No introducir una segunda implementación de las reglas de negocio que ya resuelve el backend.

### 3.3. Contrato de datos

Verificar que el frontend interprete correctamente los campos devueltos por el backend, incluidos los tipos, los valores nulos y los formatos de fecha.

Crear o actualizar los modelos e interfaces necesarios para representar la respuesta real de la API.

No inventar propiedades para completar la interfaz. Si algún dato no forma parte de la respuesta real, comprobar si existe otro endpoint legítimo que permita obtenerlo antes de decidir cómo mostrarlo.

---

## 4. Autenticación y selección de granja

### 4.1. Usuario autenticado

- Las peticiones de Production Cycle deben utilizar el mecanismo de autenticación establecido por el proyecto.
- Enviar el token o las credenciales requeridas por el backend mediante el mecanismo existente.
- No guardar tokens ni credenciales en lugares inseguros ni duplicar la lógica de autenticación.
- Gestionar adecuadamente las respuestas `401 Unauthorized` y `403 Forbidden`, según el comportamiento real de la API.
- Si la sesión ha expirado, utilizar el flujo de sesión expirada o redirección al inicio de sesión ya establecido por la aplicación.

### 4.2. Selección de granja

La información de ciclos productivos debe corresponder exclusivamente a la granja seleccionada por el usuario.

- Obtener las granjas mediante el endpoint real que utiliza el backend para devolver las granjas del usuario autenticado.
- Mostrar la información de las granjas que realmente devuelve la API.
- Permitir seleccionar una granja y consultar los ciclos correspondientes a ella.
- Utilizar el `FarmId` real de la granja seleccionada.
- Actualizar la información cuando el usuario cambie de granja.
- Evitar que permanezcan visibles los ciclos de la granja anterior mientras se cargan los datos de la nueva selección.
- Mostrar un estado vacío cuando la granja seleccionada no tenga ciclos.
- No asumir que un usuario tiene una sola granja.
- No utilizar un `FarmId` fijo ni seleccionar una granja de prueba automáticamente.

El backend es responsable de comprobar que la granja pertenece al usuario autenticado. El frontend debe enviar la selección correcta y respetar las respuestas de autorización, sin sustituir las validaciones de seguridad del servidor.

---

## 5. Información que debe mostrar Production Cycle

La interfaz debe reflejar los datos reales que entrega el backend y mantener coherencia con las reglas de negocio implementadas.

### 5.1. Información de la granja

Cuando el backend proporcione estos datos, mostrar:

- Nombre de la granja.
- Código de la granja, únicamente si existe realmente en el modelo o contrato implementado.
- Estado de la granja: activa o inactiva.

No utilizar el código del estanque como si fuera el código de la granja. Si el esquema o el contrato actual no incluyen un código de granja, no inventarlo ni mostrar un valor ficticio.

### 5.2. Información de cada ciclo productivo

Mostrar los siguientes datos cuando estén disponibles en la respuesta del backend:

- Identificador del ciclo, si forma parte del contrato.
- Especie cultivada.
- Estanque relacionado, si el backend proporciona esa información.
- Fecha de inicio.
- Fecha de finalización, cuando corresponda.
- Cantidad inicial de peces.
- Cantidad actual de peces.
- Peso promedio inicial en gramos.
- Peso promedio actual en gramos.
- Edad inicial en días.
- Edad actual en días.
- Observaciones.
- Estado del ciclo, según las reglas reales del backend.

Organizar estos datos en las tarjetas, tablas, paneles o componentes que mejor encajen con el diseño actual de la sección.

No es necesario mostrar todos los campos con la misma jerarquía visual, pero sí deben poder consultarse los datos relevantes que el backend expone para este apartado.

### 5.3. Información de ciclos activos e históricos

- Revisar cómo determina el backend si un ciclo está activo, finalizado o en otro estado.
- Utilizar la definición real del backend para clasificar y presentar los ciclos.
- No asumir que `IsActive` y `EndDate` nulo son equivalentes sin comprobar la lógica implementada.
- Si la API permite consultar ciclos activos e históricos, respetar sus filtros y contratos.
- Si el backend solo expone una colección, clasificar los resultados únicamente cuando las reglas del servidor permitan hacerlo sin contradecir su lógica.
- No cambiar el estado de un ciclo desde el frontend mediante cálculos o modificaciones locales.

El nombre visible de la sección debe ser coherente con el comportamiento real. Si se muestran tanto ciclos activos como finalizados, utilizar una denominación que represente correctamente el conjunto de información.

---

## 6. Mostrar correctamente los cálculos del backend

Los valores calculados deben proceder del backend. El frontend no debe reemplazar esos cálculos por estimaciones propias.

### 6.1. Cantidad actual de peces

Mostrar la cantidad actual devuelta por el backend.

El backend determina esta cantidad considerando:

- Cantidad inicial de peces del ciclo.
- Peces cosechados registrados en `Harvest`.
- Peces muertos registrados en `Mortality`.

La lógica de referencia es:

**Cantidad actual = cantidad inicial − peces cosechados − peces muertos.**

El frontend no debe volver a sumar cosechas y mortalidades para calcular el resultado, ni restarlas nuevamente al valor recibido.

Mostrar las cantidades con un formato legible, sin alterar su valor real. Si el backend devuelve un valor nulo o un resultado no disponible, manejarlo según el contrato y no sustituirlo por una cantidad ficticia.

### 6.2. Peso promedio actual

Mostrar el peso promedio actual proporcionado por el backend.

La lógica implementada en el servidor prioriza la última medición válida de `Biometrics.AverageWeightGrams`. Si no existen mediciones válidas, utiliza la estimación correspondiente de `FeedingByAge.ApproximatedFishWeightGrams`, teniendo en cuenta la especie y la edad del ciclo.

En el frontend:

- No calcular el peso promedio a partir de la cantidad de alimento.
- No utilizar `DailyAmountKilo` como si fuera el peso promedio de un pez.
- No reemplazar el valor del backend por un promedio calculado con mediciones incompletas.
- Diferenciar claramente el peso inicial y el peso promedio actual.
- Mostrar la unidad en gramos.
- Si no hay un peso disponible, indicar que el dato no está disponible en lugar de inventarlo.

### 6.3. Edad actual del ciclo

Mostrar la edad actual calculada por el backend.

La edad considera la edad inicial del ciclo y los días naturales transcurridos desde su fecha de inicio, con el tratamiento de fechas que establezca el servidor para ciclos finalizados o fechas inconsistentes.

- No sumar días en el frontend.
- No utilizar cálculos basados en horas o zonas horarias que contradigan el resultado del backend.
- Mostrar la unidad en días.
- Respetar los valores nulos o no disponibles de acuerdo con el contrato.

### 6.4. Fechas

- Mostrar las fechas en un formato legible y consistente con el idioma de la aplicación.
- Interpretar correctamente el formato que devuelve la API.
- Evitar desplazamientos de fecha ocasionados por conversiones de zona horaria innecesarias.
- No mostrar una fecha de finalización ficticia cuando el ciclo no tenga una.
- Diferenciar visualmente las fechas de inicio y finalización.

---

## 7. Estados de carga, errores y datos vacíos

La sección debe manejar correctamente los distintos estados de la comunicación con el backend.

### 7.1. Cargando

- Mostrar un indicador de carga mientras se obtienen las granjas o los ciclos.
- Evitar que la interfaz parezca vacía o rota durante la petición.
- Evitar peticiones duplicadas innecesarias.
- Evitar mostrar temporalmente datos de otra granja como si pertenecieran a la selección actual.

### 7.2. Sin resultados

Cuando la API responda correctamente, pero no existan ciclos para la granja seleccionada:

- Mostrar un mensaje claro indicando que no hay ciclos productivos registrados.
- No mostrar tarjetas, cantidades ni especies ficticias.
- Mantener la interfaz visualmente consistente con el resto del proyecto.
- Si existe una funcionalidad de registro de ciclos en el frontend, mostrar su acceso únicamente si ya está implementado y permitido por el alcance actual.

### 7.3. Errores

Gestionar, como mínimo, los siguientes casos:

- Error de conexión con el backend.
- Respuesta `400 Bad Request`.
- Respuesta `401 Unauthorized`.
- Respuesta `403 Forbidden`.
- Respuesta `404 Not Found`, cuando corresponda.
- Error interno del servidor.
- Respuesta vacía, incompleta o incompatible con el contrato esperado.

Mostrar mensajes comprensibles para el usuario y registrar detalles técnicos mediante los mecanismos de diagnóstico ya existentes.

No mostrar errores técnicos sensibles, tokens, credenciales ni detalles internos del servidor en la interfaz.

Permitir reintentar la consulta cuando sea apropiado, sin crear ciclos duplicados ni modificar datos del servidor.

---

## 8. Diseño y experiencia de usuario

- Mantener el diseño visual existente de PisciData.
- Reutilizar los componentes y estilos que ya utiliza la aplicación.
- Mantener la coherencia de colores, tipografía, espaciados, iconografía y jerarquía visual.
- Presentar las cantidades con separadores y unidades legibles.
- Diferenciar los estados de los ciclos mediante etiquetas visuales coherentes con el diseño existente.
- Evitar que los valores nulos, las fechas extensas o los nombres largos rompan la distribución.
- Asegurar que la información se vea correctamente en las resoluciones y dispositivos que soporte actualmente el proyecto.
- Mantener accesibles los textos, indicadores de carga y mensajes de error.
- No rediseñar las demás secciones de la aplicación.
- No introducir dependencias de interfaz nuevas si los componentes existentes son suficientes.

El cambio de nombre de la sección debe aplicarse donde corresponda dentro de este apartado, incluidos su título, encabezados y textos relacionados. No renombrar globalmente otros módulos que no formen parte de esta tarea.

---

## 9. Actualización y consistencia de los datos

- Cargar la información real cuando se abra el apartado.
- Actualizar los ciclos cuando cambie la granja seleccionada.
- Respetar los mecanismos de actualización, invalidación de caché o recarga de datos que ya existan en el proyecto.
- Si se vuelve a consultar el backend, mostrar los datos recibidos más recientes.
- No modificar localmente cantidades de peces, pesos, edades o estados para simular una actualización.
- No guardar en caché datos de una granja de forma que puedan mostrarse a otro usuario.
- Si una petición falla, no presentar información antigua como si fuera actual sin indicarlo claramente.

La lectura de Production Cycle debe ser de solo consulta dentro de este alcance. No implementar operaciones de creación, edición, eliminación, cosecha, mortalidad ni biometría salvo que ya sean necesarias para integrar la lectura existente; esas funcionalidades quedan fuera de esta tarea.

---

## 10. Pruebas y validación

Realizar las pruebas que correspondan a la tecnología y herramientas existentes en el frontend.

### 10.1. Integración con la API

- Verificar que se consultan los endpoints reales.
- Verificar que los modelos del frontend coinciden con las respuestas del backend.
- Confirmar que se envía la autenticación requerida.
- Confirmar que el `FarmId` seleccionado se utiliza correctamente.
- Comprobar que no quedan datos de prueba en la visualización.

### 10.2. Casos funcionales

Probar al menos los siguientes escenarios:

1. Usuario autenticado con una granja y ciclos registrados.
2. Usuario autenticado con varias granjas.
3. Cambio entre dos granjas con ciclos diferentes.
4. Granja sin ciclos productivos.
5. Ciclo con mediciones biométricas válidas.
6. Ciclo sin mediciones biométricas, pero con una estimación disponible por edad.
7. Ciclo con cantidad actual de peces reducida por cosechas y mortalidad.
8. Ciclo con fecha de finalización.
9. Ciclo con datos opcionales nulos.
10. Usuario sin sesión válida.
11. Error de conexión con el backend.
12. Respuesta de autorización denegada.
13. Recarga de la sección después de que cambien los datos del backend.

### 10.3. Calidad técnica

- Ejecutar las pruebas existentes relacionadas con la sección.
- Ejecutar el proceso de compilación o verificación del frontend.
- Corregir los errores introducidos por los cambios.
- Evitar modificar funcionalidades de otros apartados para solucionar problemas ajenos a Production Cycle.

No afirmar que las pruebas fueron exitosas si no se ejecutaron realmente.

---

## 11. Restricciones de alcance

Durante esta implementación:

- Modificar exclusivamente el frontend de Production Cycle y los archivos compartidos que sean estrictamente necesarios para su integración.
- No implementar nuevas reglas de negocio que correspondan al backend.
- No modificar controladores, servicios ni repositorios del backend.
- No ejecutar ni modificar scripts SQL.
- No cambiar el esquema de la base de datos.
- No inventar endpoints, propiedades, datos, estados ni identificadores.
- No eliminar validaciones o mecanismos de autenticación existentes.
- No alterar otras secciones de PisciData.
- No realizar refactorizaciones generales que no sean necesarias para esta funcionalidad.
- No sustituir la arquitectura existente por otra.
- No introducir dependencias innecesarias.

Si se descubre que falta un dato o endpoint imprescindible en el backend, documentar exactamente qué falta y comunicarlo antes de realizar cambios fuera del alcance del frontend.

---

## 12. Criterios de aceptación

La implementación se considerará completa cuando:

- [ ] La sección de Production Cycle utiliza información real del backend.
- [ ] Se eliminaron los datos hardcodeados que representaban información del negocio.
- [ ] Los servicios y modelos del frontend coinciden con el contrato real de la API.
- [ ] La información corresponde a la granja seleccionada.
- [ ] Se respeta la autenticación del usuario.
- [ ] Se muestran correctamente los datos disponibles de la granja y del ciclo.
- [ ] La cantidad actual de peces coincide con el valor calculado por el backend.
- [ ] El peso promedio actual coincide con el valor calculado por el backend.
- [ ] La edad actual coincide con el valor calculado por el backend.
- [ ] Las fechas, unidades, estados y valores nulos se presentan correctamente.
- [ ] Existen estados visuales de carga, error y ausencia de resultados.
- [ ] El cambio de granja actualiza correctamente los datos.
- [ ] La interfaz conserva la identidad visual de PisciData.
- [ ] No se modificaron otros módulos fuera del alcance.
- [ ] Las verificaciones técnicas aplicables se ejecutaron y sus resultados fueron documentados.

---

## 13. Informe final del agente

Al finalizar, presentar un resumen que incluya:

1. Archivos del frontend revisados.
2. Archivos creados o modificados.
3. Endpoints reales utilizados y su propósito.
4. Modelos y campos conectados con el backend.
5. Datos estáticos eliminados o sustituidos.
6. Forma en que se gestiona la autenticación y la selección de granja.
7. Confirmación de cómo se presentan la cantidad actual de peces, el peso promedio y la edad.
8. Pruebas y verificaciones ejecutadas, con sus resultados reales.
9. Problemas encontrados en el contrato del backend, si los hubiera.
10. Funcionalidades pendientes que estén fuera del alcance de esta tarea.

**Importante:** esta tarea implementa únicamente la integración del frontend para Production Cycle. Las siguientes funcionalidades se agregarán cuando se indiquen nuevos requisitos, sin ampliar automáticamente el alcance a otros módulos.
