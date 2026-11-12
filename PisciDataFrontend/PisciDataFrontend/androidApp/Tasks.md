# Tasks.md — Implementación Frontend de Formularios de Base de Datos (PisciData)

## Objetivo general

Modificar y completar la pantalla **Base de Datos** de PisciData para que las secciones de ciclos activos, estanques e inventario funcionen completamente con información real del backend.

Actualmente se necesita implementar correctamente los formularios de registro y las operaciones de cada sección.

Cada botón de registro debe abrir el formulario correspondiente a la sección desde la que se presiona.

Ejemplos:

- Si el usuario está en Ciclos activos y presiona Nuevo, debe abrir el formulario de registro de ciclos.
- Si está en Estanques y presiona Nuevo, debe abrir el formulario de registro de estanques.
- Si está en Inventario y presiona Nuevo, debe permitir registrar el tipo de elemento correspondiente, respetando la separación entre alimentos y suministros.

No debe existir un formulario genérico que mezcle campos de distintas entidades.

**El backend es la única fuente de verdad. No debe existir información hardcodeada en la pantalla ni en los formularios.**

---

## 1. Inspección inicial

Antes de modificar el frontend:

- Revisar la arquitectura completa del frontend.
- Identificar la pantalla Base de Datos.
- Revisar sus componentes y secciones.
- Identificar todos los botones de registro.
- Revisar los formularios existentes.
- Revisar los modelos y DTOs.
- Revisar los servicios de comunicación con el backend.
- Revisar el ApiClient.
- Revisar la configuración de autenticación.
- Revisar el manejo del token.
- Revisar la navegación y los estados de pantalla.
- Identificar toda la información hardcodeada.
- Revisar el contrato real de los endpoints del backend.

No modificar pantallas ajenas a la funcionalidad solicitada.

## 2. Conexión completa con backend

Revisar los endpoints que el backend haya implementado para las tres secciones.

Conectar la pantalla utilizando los servicios y mecanismos existentes.

Eliminar:

- Listas inventadas.
- Ciclos de ejemplo.
- Estanques de ejemplo.
- Especies escritas directamente en el frontend.
- Alimentos inventados.
- Suministros inventados.
- Cantidades de stock simuladas.
- Estados calculados con valores ficticios.
- Datos estáticos utilizados para completar tarjetas.
- Respuestas simuladas que sustituyan las solicitudes reales.

Todos los datos deben obtenerse del backend.

No inventar endpoints ni propiedades.

Si falta una operación necesaria en el backend, documentar el endpoint requerido en lugar de simular su funcionamiento.

## 3. Organización de la pantalla Base de Datos

Mantener las tres secciones principales:

1. Ciclos activos.
2. Estanques.
3. Inventario.

Cada sección debe mantener su propia información, formularios y operaciones.

Los botones y acciones deben responder al contexto de la sección actual.

### Comportamiento de los botones Nuevo

**Ciclos activos:**

Al presionar Nuevo, abrir exclusivamente el formulario para registrar un ciclo productivo.

**Estanques:**

Al presionar Nuevo, abrir exclusivamente el formulario para registrar un estanque.

**Inventario:**

Al presionar Nuevo, permitir seleccionar o abrir el formulario correspondiente al tipo de registro:

- Alimento para peces (`Feed`).
- Suministro (`Supply`).

No mezclar los campos de alimentos y suministros.

Cada formulario debe enviar información al endpoint correspondiente.

## 4. Formulario de ciclos productivos

Modificar completamente el formulario de registro de ciclos para que corresponda a la estructura y reglas del backend.

### Campos

Mostrar los campos que el backend permita registrar, según el contrato real.

Como mínimo, revisar los siguientes:

- Estanque.
- Especie.
- Fecha de inicio.
- Edad inicial en días.
- Cantidad inicial de peces.
- Peso promedio inicial.
- Observaciones.

No mostrar un campo de fecha de finalización durante el registro.

### Selector de estanques

El campo de estanque debe ser un dropdown o selector.

Debe obtener los estanques disponibles mediante el backend.

Requisitos:

- Mostrar los estanques pertenecientes a las granjas del usuario autenticado.
- Mostrar información útil para distinguirlos, como código y nombre.
- Guardar el identificador real del estanque seleccionado.
- No permitir escribir identificadores manualmente.
- No mostrar estanques inventados.
- No mostrar estanques pertenecientes a otros usuarios.
- Manejar correctamente la ausencia de estanques disponibles.

### Selector de especies

La especie también debe seleccionarse mediante un dropdown.

Requisitos:

- Obtener las especies desde el backend.
- Mostrar los nombres reales.
- Enviar el identificador correspondiente.
- No utilizar listas estáticas.
- Mostrar un estado apropiado mientras se cargan.
- Manejar el caso en que no existan especies disponibles.

### Fecha de finalización

Eliminar completamente el campo de fecha de finalización del formulario de registro.

Al guardar:

- Enviar únicamente los campos necesarios.
- No enviar una fecha de finalización inventada.
- Mostrar el ciclo como activo.
- Reflejar el estado que devuelva el backend.

### Guardado

Al presionar Guardar:

1. Validar los campos.
2. Construir el DTO esperado por el backend.
3. Enviar la solicitud autenticada.
4. Esperar la respuesta.
5. Mostrar confirmación únicamente si se guardó correctamente.
6. Cerrar el formulario cuando corresponda.
7. Actualizar la lista de ciclos.
8. Mostrar el ciclo recién registrado con sus datos reales.

No agregar el ciclo únicamente a una lista local.

## 5. Finalización de ciclos desde las tarjetas

Modificar las tarjetas donde se muestran los ciclos productivos.

Cada ciclo activo debe tener una acción para finalizarlo.

Puede implementarse como un botón o una opción de menú, manteniendo coherencia con el diseño.

Al seleccionar Finalizar ciclo:

- Mostrar una confirmación para evitar finalizaciones accidentales.
- Ejecutar el endpoint correspondiente del backend.
- No cambiar el estado antes de recibir una respuesta exitosa.
- Actualizar la tarjeta después de finalizar.
- Reflejar el estado real devuelto por el backend.
- Actualizar la lista de ciclos activos.
- Mostrar un mensaje de confirmación.

Si el backend devuelve ciclos finalizados en otra consulta o con otro estado, respetar ese comportamiento.

No permitir que el usuario cambie manualmente la fecha de finalización.

No simular la finalización modificando únicamente una variable local.

Manejar errores si el ciclo ya está finalizado o no se puede modificar.

## 6. Formulario de estanques

Implementar el formulario específico de registro de estanques.

Obtener los campos y tipos de datos del contrato real del backend.

Revisar especialmente:

- Código.
- Nombre.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Granja relacionada.
- Cualquier otro campo existente y permitido.

### Selección de granja

Si el backend requiere seleccionar una granja:

- Obtener las granjas disponibles del usuario autenticado.
- Mostrar un selector.
- Enviar el identificador real.
- No mostrar granjas de otros usuarios.
- No utilizar datos hardcodeados.

Si el backend determina automáticamente la granja, no solicitarla innecesariamente en el formulario.

### Validaciones

- Campos obligatorios.
- Valores numéricos.
- Valores positivos cuando corresponda.
- Longitudes máximas.
- Código duplicado, si corresponde.
- Tipos de datos.

### Guardado

Al guardar:

- Enviar el DTO correcto.
- Mostrar errores del backend.
- Esperar confirmación.
- Actualizar la lista de estanques desde el backend.
- Mostrar el registro nuevo.

No simular el registro localmente.

## 7. Calidad del agua

Revisar cómo el backend expone los datos de calidad del agua.

Si corresponde mostrar o registrar mediciones desde esta pantalla, utilizar los endpoints y campos reales.

No inventar valores para:

- Transparencia.
- pH.
- Temperatura.
- Oxígeno disuelto.

Cuando no existan mediciones:

- Mostrar un estado vacío.
- No colocar valores ficticios.
- No interpretar ausencia de datos como una medición normal.

Si el backend proporciona estados o referencias de calidad del agua, utilizar sus resultados.

No duplicar cálculos que correspondan al backend.

## 8. Formulario de inventario

La sección Inventario debe distinguir claramente los dos tipos de registros existentes.

### 8.1. Alimentos para peces — Feed

Crear un formulario para registrar alimentos.

Utilizar los campos reales del backend, como:

- Marca.
- Nombre del producto.
- Fase.
- Porcentaje de proteína.
- Tamaño del pellet.
- Stock en kilogramos.
- Stock mínimo en kilogramos.
- Cualquier otro campo permitido.

Diferenciar visualmente proteínas y pellets según la información real del registro.

No inventar categorías ni valores.

No utilizar los campos de `Supply` para este formulario.

### 8.2. Suministros — Supply

Crear un formulario independiente para registrar suministros.

Utilizar los campos reales que correspondan, como:

- Nombre.
- Categoría.
- Cantidad.
- Unidad.
- Stock mínimo.
- Cualquier otro campo permitido.

Las categorías deben provenir del backend si existe un catálogo o una operación para obtenerlas.

Si la base de datos utiliza un campo de texto y no existe un catálogo, respetar el contrato y las validaciones reales del backend.

No inventar categorías en el frontend.

### 8.3. Guardado de inventario

Cada formulario debe utilizar su propia operación de registro.

Al guardar:

1. Validar los datos.
2. Construir el DTO correcto.
3. Enviar la solicitud al backend.
4. Esperar la respuesta.
5. Mostrar confirmación.
6. Actualizar la lista de inventario.
7. Reflejar los datos reales.

No registrar un alimento en el endpoint de suministros ni un suministro en el endpoint de alimentos.

## 9. Tarjetas y visualización de registros

Revisar y adaptar las tarjetas de las tres secciones.

Mostrar información relevante obtenida del backend.

### Ciclos
- Estanque.
- Especie.
- Fecha de inicio.
- Edad.
- Cantidad actual de peces.
- Peso promedio.
- Estado.
- Acción para finalizar si está activo.

### Estanques
- Código.
- Nombre.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Calidad del agua disponible.
- Estado.

### Inventario
- Nombre.
- Marca o categoría según corresponda.
- Tipo de registro.
- Stock actual.
- Stock mínimo.
- Unidad.
- Estado del stock.

No mostrar campos que no existan en la respuesta del backend.

No reemplazar valores nulos por datos inventados.

## 10. Estados de carga, errores y validaciones

Implementar correctamente:

### Carga
Mostrar indicadores mientras se consultan los registros o se cargan los selectores.

### Sin registros
Mostrar mensajes claros cuando una sección no tenga datos.

### Error
Mostrar mensajes comprensibles cuando falle una consulta o actualización.

### Guardado
Deshabilitar temporalmente el botón mientras se procesa la solicitud.

### Éxito
Mostrar confirmación cuando el backend responda correctamente.

### Validaciones
Mostrar los errores de los campos correspondientes.

### Sesión
Manejar tokens inválidos y sesiones expiradas utilizando el mecanismo existente.

Evitar que una solicitud fallida deje la pantalla en un estado inconsistente.

## 11. Actualización y sincronización

Después de registrar, modificar o finalizar un elemento:

- Actualizar los datos de la sección correspondiente.
- Consultar nuevamente al backend cuando sea necesario.
- Mantener sincronizadas las tarjetas.
- Evitar duplicados visuales.
- No conservar registros que ya no existen.
- No mostrar cambios que no se hayan guardado.
- Respetar los estados devueltos por el backend.

Los datos deben seguir siendo correctos al salir y volver a entrar a la pantalla.

## 12. Pruebas

Verificar los siguientes escenarios:

### Ciclos
- Abrir el formulario desde Ciclos activos.
- Cargar estanques reales del usuario.
- Cargar especies reales.
- Registrar un ciclo correctamente.
- Comprobar que no se solicita fecha final.
- Verificar que el ciclo aparezca activo.
- Finalizar un ciclo desde su tarjeta.
- Comprobar que desaparezca de la lista de activos o cambie de sección según la respuesta del backend.
- Verificar persistencia después de volver a consultar.

### Estanques
- Abrir el formulario desde Estanques.
- Registrar un estanque.
- Validar los campos.
- Verificar que aparezca en la lista.
- Comprobar que los datos provengan del backend.
- Manejar ausencia de calidad del agua.

### Inventario
- Abrir el formulario desde Inventario.
- Registrar un alimento.
- Registrar un suministro.
- Verificar que cada uno utilice su endpoint correspondiente.
- Comprobar stock y stock mínimo.
- Verificar el estado de disponibilidad.
- Comprobar persistencia.

### Integración
- Probar errores de conexión.
- Probar token inválido.
- Probar respuestas de validación.
- Comprobar que no se muestren datos de otro usuario.
- Verificar que no existan datos hardcodeados.

---

## 13. Restricciones

- No modificar el backend.
- No modificar la base de datos.
- No modificar pantallas ajenas a Base de Datos.
- No inventar endpoints.
- No inventar campos.
- No utilizar datos hardcodeados.
- No utilizar listas estáticas para estanques, especies, granjas o categorías cuando el backend deba proporcionarlas.
- No simular registros ni actualizaciones.
- No simular la finalización de ciclos.
- No duplicar modelos o servicios existentes.
- No cambiar reglas de negocio del backend desde el frontend.

## 14. Resultado esperado

La pantalla Base de Datos debe funcionar completamente conectada al backend.

Cada sección debe tener sus propios formularios, consultas y operaciones.

Los registros deben guardarse correctamente en la base de datos y mostrarse con información real del usuario autenticado.

Los ciclos deben crearse activos y poder finalizarse desde sus tarjetas, sin solicitar una fecha final durante el registro.

Al finalizar, entregar:

1. Archivos revisados.
2. Archivos modificados o creados.
3. Formularios implementados.
4. Endpoints consumidos.
5. DTOs utilizados.
6. Campos reales mostrados y enviados.
7. Validaciones implementadas.
8. Acciones de finalización de ciclos.
9. Pruebas de integración realizadas.
10. Confirmación de eliminación de datos hardcodeados.
11. Problemas o endpoints pendientes del backend.
