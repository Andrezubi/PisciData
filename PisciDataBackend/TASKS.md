# Tasks.md — Implementación Backend de Estanques e Inventario (PisciData)

## 1. Objetivo

Implementar y completar la lógica de backend correspondiente exclusivamente a los apartados **Estanques (Ponds)** e **Inventario (Inventory)** de PisciData.

El agente debe analizar detalladamente la arquitectura existente, revisar las entidades y relaciones de la base de datos, identificar los servicios y controladores correspondientes y realizar los cambios necesarios para que ambos apartados obtengan información real y coherente.

La implementación debe respetar la estructura actual del proyecto y las reglas de negocio existentes.

### Alcance

Este documento contempla únicamente:

1. Estanques.
2. Calidad del agua asociada a los estanques.
3. Referencias de calidad del agua.
4. Inventario de alimentos (Feed).
5. Inventario de suministros (Supply).

No implementar otros módulos ni modificar funcionalidades ajenas a estos apartados.

---

## 2. Análisis obligatorio de la arquitectura

Antes de implementar, revisar detalladamente la estructura del backend.

### 2.1. Revisar la arquitectura existente

Identificar:

- Controllers.
- Services.
- Repositories.
- Entities y modelos.
- DTO de solicitud y respuesta.
- Configuración de Entity Framework Core.
- Relaciones entre entidades.
- Configuración de autenticación y autorización.
- Manejo de errores.
- Validaciones.
- Configuración de inyección de dependencias.
- Convenciones de nombres y estructura utilizadas por el proyecto.

Reutilizar los patrones existentes.

No crear una arquitectura paralela ni duplicar servicios que ya resuelvan funcionalidades equivalentes.

### 2.2. Revisar la base de datos real

Inspeccionar detalladamente el esquema SQL existente y los modelos correspondientes.

Verificar los nombres exactos de:

- Tablas.
- Columnas.
- Identificadores.
- Claves primarias.
- Claves foráneas.
- Tipos de datos.
- Valores nulos.
- Valores predeterminados.
- Relaciones.
- Campos de auditoría.
- Campos de estado.

Prestar especial atención a las tablas:

- `Farm`.
- `Pond`.
- `WaterQuality`.
- `WaterQualityReference`.
- `Feed`.
- `Supply`.
- `User`.

También revisar otras tablas relacionadas cuando sean necesarias para comprender las relaciones o las reglas de negocio.

**No inventar columnas ni asumir que los nombres utilizados en el frontend coinciden con los nombres de la base de datos.**

No ejecutar el SQL de referencia como un script de modificación de la base de datos.

No modificar el esquema ni crear migraciones innecesarias.

---

# 3. Estanques (Ponds)

## 3.1. Objetivo del apartado

Implementar la consulta completa de estanques para que el frontend pueda mostrar la información real de cada estanque, sus características físicas y sus últimos registros de calidad del agua.

La información debe obtenerse de las tablas correspondientes y respetar las relaciones existentes.

## 3.2. Revisar la entidad Pond

Analizar la entidad `Pond` y determinar los campos reales disponibles.

Prestar especial atención a los campos relacionados con:

- Identificador del estanque.
- Granja a la que pertenece.
- Código.
- Nombre.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Estado.
- Auditoría.

Los nombres anteriores son referencias funcionales. Confirmar los nombres exactos en el esquema y en los modelos.

### Información física

Los datos de forma, área, profundidad y transparencia deben obtenerse de la tabla `Pond`.

No obtener estos datos de otras tablas si ya se encuentran almacenados correctamente en `Pond`.

No sustituir los valores por constantes ni por información de prueba.

Si los campos permiten valores nulos, respetar esa condición y devolverlos como información no disponible cuando corresponda.

---

## 3.3. Forma, área y profundidad

Obtener directamente los valores correspondientes de `Pond`.

### Forma

- Consultar el campo real de forma del estanque.
- Mostrar o devolver el valor registrado en la base de datos.
- No inferir la forma a partir del área o de otros campos.
- No asignar una forma predeterminada si no existe información.

### Área

- Obtener el área registrada en `Pond`.
- Identificar su tipo de dato y unidad real.
- Mantener la unidad coherente con el esquema y el contrato del backend.
- No convertir el área a otra unidad sin una necesidad real.
- Si se realiza una conversión, documentar la fórmula y garantizar que no se pierda precisión.

### Profundidad

- Obtener la profundidad registrada en `Pond`.
- Verificar la unidad real.
- No utilizar valores de profundidad ficticios.
- No calcular la profundidad promedio si la base de datos contiene un valor directo que representa la profundidad del estanque.

### Transparencia

La transparencia solicitada para este apartado debe obtenerse de `Pond`, de acuerdo con el campo real existente.

- Consultar el valor almacenado en el estanque.
- No confundirlo con los valores de transparencia de otros registros de calidad del agua, si existen.
- No reemplazarlo automáticamente por un valor de referencia.
- Respetar los valores nulos.

Si la base de datos contiene más de un campo relacionado con transparencia, analizar su propósito y determinar cuál corresponde al dato físico solicitado por el frontend.

Documentar cualquier diferencia entre el dato almacenado en `Pond` y el dato de una medición de `WaterQuality`.

---

## 3.4. Calidad del agua (WaterQuality)

Implementar la consulta de calidad del agua asociada a cada estanque.

La información debe obtenerse de la tabla `WaterQuality` y de las relaciones reales definidas en la base de datos.

Revisar todos los campos disponibles y determinar cuáles corresponden a las mediciones solicitadas.

### Datos que deben mostrarse

Obtener los últimos registros disponibles de:

- Transparencia.
- pH.
- Temperatura.
- Oxígeno disuelto.

Confirmar los nombres exactos de las columnas y sus unidades en el esquema.

No inventar nombres como `PH`, `Temperature` o `DissolvedOxygen` si el modelo utiliza otros nombres.

### Último registro válido

Para cada estanque:

- Consultar los registros asociados al estanque.
- Identificar el campo real de fecha de medición.
- Ordenar correctamente los registros por fecha.
- Obtener el registro más reciente que cumpla las condiciones de validez establecidas por el modelo.
- Respetar los filtros de registros activos, si existen y corresponden a la lógica del proyecto.
- No utilizar registros de otro estanque.
- No mezclar mediciones de diferentes fechas para construir un único registro inexistente.

Si existe un registro reciente que contiene todos los parámetros solicitados, utilizarlo como fuente para esos parámetros.

Si el modelo permite que los parámetros sean nulos, conservar los valores disponibles y devolver como no disponibles los que no tengan medición.

Si no existe ningún registro válido de calidad del agua para el estanque, devolver los campos correspondientes como nulos o mediante el formato de ausencia de datos establecido por el contrato.

**No devolver valores ficticios de pH, temperatura, transparencia u oxígeno disuelto cuando no existan mediciones.**

### Precisión temporal

- Verificar el tipo de fecha utilizado en `WaterQuality`.
- Mantener una ordenación determinista.
- Si varios registros tienen la misma fecha, utilizar un criterio secundario basado en el identificador u otro campo real que permita establecer un orden consistente.
- No utilizar la fecha actual como fecha de una medición que no existe.
- No alterar las fechas almacenadas.

Si el modelo tiene registros de calidad del agua con distintos estados, revisar cómo se representan y qué registros deben considerarse válidos.

---

## 3.5. WaterQualityReference

Revisar detalladamente la tabla `WaterQualityReference`.

Identificar los campos reales relacionados con:

- Parámetro.
- Valor mínimo.
- Valor máximo.
- Unidad.
- Estado.
- Tipo de referencia.
- Especie o condición, si existen.
- Cualquier otra propiedad utilizada por las reglas de negocio.

Los nombres anteriores son orientativos. Utilizar los nombres reales del esquema.

### Objetivo

Utilizar las referencias de calidad del agua para interpretar correctamente las mediciones de cada estanque.

No limitarse a consultar las mediciones sin revisar las referencias.

### Comparación de valores

Para cada parámetro que tenga una referencia aplicable:

1. Obtener la medición real del estanque.
2. Obtener la referencia correspondiente.
3. Comparar la medición con los límites definidos en la base de datos.
4. Determinar su estado conforme a las reglas reales del proyecto.
5. Devolver la información necesaria para que el frontend pueda mostrar correctamente el estado.

La comparación debe considerar:

- Parámetro.
- Unidad.
- Límites mínimos.
- Límites máximos.
- Valores nulos.
- Referencias activas.
- Criterios de inclusión y exclusión establecidos por el modelo.

No utilizar límites inventados ni constantes hardcodeadas.

No asumir que todos los parámetros tienen el mismo rango.

No comparar valores expresados en unidades diferentes.

### Estados de calidad del agua

Si el frontend necesita mostrar estados como normal, bajo, alto o fuera de rango:

- Implementar la lógica en el backend.
- Utilizar los límites reales de `WaterQualityReference`.
- Mantener los estados coherentes con las reglas existentes.
- Documentar cómo se manejan los valores exactamente iguales a los límites.
- No clasificar una medición inexistente como normal.
- No asignar automáticamente un estado de alerta cuando no hay datos suficientes.

Si el esquema permite referencias múltiples para un mismo parámetro, determinar cuál corresponde según las relaciones y condiciones reales.

No elegir arbitrariamente una referencia cuando existen varias aplicables.

Si falta una referencia válida, devolver el estado como no disponible o utilizar el comportamiento establecido por el proyecto.

### Resultado esperado

El frontend debe poder obtener, cuando corresponda:

- Valor medido.
- Unidad.
- Fecha de medición.
- Valor mínimo de referencia.
- Valor máximo de referencia.
- Estado de la medición.

Todos los campos deben estar respaldados por datos reales.

---

# 4. Inventario (Inventory)

## 4.1. Objetivo

Implementar la lógica de consulta de inventario utilizando las tablas reales `Feed` y `Supply`.

El frontend debe poder mostrar correctamente los alimentos y suministros registrados en la base de datos.

La implementación debe contemplar:

- Nombre del producto.
- Categoría.
- Inventario actual.
- Stock mínimo.
- Tipo de producto.
- Estado.
- Unidades.
- Otros campos que realmente existan y sean necesarios para la pantalla.

No inventar propiedades ni utilizar datos de ejemplo.

---

## 4.2. Revisar las tablas Feed y Supply

Analizar detalladamente ambas tablas.

Identificar:

- Identificadores.
- Nombre.
- Categoría.
- Tipo de alimento o suministro.
- Stock actual.
- Stock mínimo.
- Unidad de medida.
- Estado.
- Campos de auditoría.
- Relaciones.
- Restricciones.
- Campos adicionales utilizados por el proyecto.

Confirmar los nombres exactos y los tipos de datos.

No asumir que `Feed` y `Supply` poseen exactamente las mismas columnas.

No asumir que el inventario actual se almacena con el mismo nombre en ambas tablas.

No combinar los modelos directamente sin revisar sus diferencias.

---

## 4.3. Feed — Inventario de alimentos

Consultar los registros de la tabla `Feed`.

Los alimentos deben mostrarse correctamente según su categoría y sus datos reales.

### Tipos de alimentos

Prestar especial atención a los alimentos relacionados con:

- Proteínas.
- Pellets.

Revisar cómo están representados estos tipos en la base de datos.

Determinar si corresponden a:

- Una categoría.
- Un tipo de alimento.
- Una propiedad específica.
- Un campo relacionado.
- Un registro de catálogo.

No asumir que proteína y pellets son columnas independientes.

No crear categorías nuevas ni modificar las existentes sin justificación.

### Información de Feed

Obtener y devolver, cuando exista:

- Identificador.
- Nombre del alimento.
- Categoría.
- Tipo.
- Inventario actual.
- Stock mínimo.
- Unidad de medida.
- Estado.
- Otros campos relevantes para la pantalla actual.

Los nombres anteriores son funcionales. Utilizar el contrato real de la base de datos.

### Categoría y clasificación

- Identificar cómo se almacena realmente la categoría.
- Revisar si existe una tabla de categorías relacionada.
- Si la categoría es una propiedad de `Feed`, utilizarla correctamente.
- Si se almacena mediante una relación, resolverla utilizando la navegación o consulta correspondiente.
- No mostrar identificadores numéricos como categorías si existe un nombre relacionado que debe mostrarse.
- No inferir que un alimento es proteína o pellet por su nombre comercial.
- Utilizar la clasificación registrada en la base de datos.

### Inventario actual

Obtener el stock real almacenado.

- Confirmar el campo exacto.
- Confirmar su unidad.
- Respetar la precisión del valor.
- No sumar entradas o salidas nuevamente si el campo ya representa el stock actual.
- No inventar una cantidad inicial.
- No sustituir un valor nulo por cero sin revisar el significado de ese campo.

### Stock mínimo

Obtener el stock mínimo real.

- No utilizar valores hardcodeados.
- No establecer un stock mínimo general para todos los alimentos.
- Respetar la unidad de medida.
- Utilizarlo para determinar si el inventario requiere reposición.

### Estado del inventario

Si el proyecto requiere indicar disponibilidad o alerta:

- Comparar el inventario actual con el stock mínimo.
- Utilizar una regla coherente con el dominio.
- Documentar cómo se manejan valores iguales al stock mínimo.
- No mostrar alertas de reposición cuando no existe información suficiente para realizar la comparación.
- No modificar automáticamente el inventario.

El backend debe proporcionar la información necesaria para que el frontend presente correctamente el estado del alimento.

---

## 4.4. Supply — Inventario de suministros

Consultar los registros de la tabla `Supply`.

Revisar su estructura completa y determinar los campos que corresponden al inventario de suministros.

### Información que debe exponerse

Cuando los campos existan:

- Identificador.
- Nombre del suministro.
- Categoría.
- Inventario actual.
- Stock mínimo.
- Unidad de medida.
- Estado.
- Otros datos relevantes para la pantalla.

No utilizar automáticamente el DTO de `Feed` para representar `Supply` si sus campos o reglas son diferentes.

### Categoría

- Obtener la categoría real del suministro.
- Revisar si existe una tabla relacionada.
- Resolver correctamente las relaciones.
- Mostrar el nombre de la categoría cuando corresponda.
- No inventar categorías.
- No clasificar suministros como alimentos.

### Inventario actual y stock mínimo

- Obtener el inventario actual del suministro.
- Obtener el stock mínimo.
- Confirmar unidades y precisión.
- Comparar los valores únicamente cuando sean compatibles.
- Aplicar las reglas de alerta que correspondan.
- No utilizar valores ficticios para completar campos ausentes.

---

## 4.5. Cálculos de inventario

Revisar la implementación existente antes de decidir si se necesitan cálculos adicionales.

Si el stock actual está almacenado directamente en `Feed` o `Supply`, utilizar ese valor como fuente de verdad.

No recalcularlo mediante operaciones de entrada o salida sin revisar primero cómo funciona el sistema.

Si el proyecto utiliza movimientos de inventario, revisar las tablas y servicios correspondientes para determinar si el stock debe obtenerse mediante un cálculo.

No asumir que existen movimientos si no están definidos en la arquitectura real.

### Reglas

- No duplicar movimientos.
- No sumar entradas ya contabilizadas.
- No descontar salidas que ya hayan sido aplicadas al stock.
- No mezclar unidades.
- No redondear innecesariamente.
- No permitir resultados negativos por errores de consulta o duplicación.
- No modificar el inventario durante una consulta de lectura.

Si se detecta una inconsistencia en los datos almacenados, documentarla y no corregirla automáticamente mediante una operación de lectura.

---

# 5. Servicios

Implementar o adaptar los servicios correspondientes siguiendo la arquitectura existente.

## 5.1. Servicio de estanques

El servicio debe:

- Consultar los estanques existentes.
- Obtener sus datos físicos desde `Pond`.
- Obtener la información de calidad del agua relacionada.
- Obtener las referencias aplicables.
- Identificar las mediciones más recientes.
- Calcular los estados de calidad cuando corresponda.
- Preparar una respuesta coherente para el controller.
- Respetar las relaciones entre granja, estanque y registros.

Evitar incluir cálculos de negocio en el controller.

## 5.2. Servicio de inventario

El servicio debe:

- Consultar los alimentos.
- Consultar los suministros.
- Resolver las categorías correspondientes.
- Obtener el stock actual.
- Obtener el stock mínimo.
- Mantener las unidades.
- Preparar los datos necesarios para el frontend.
- Aplicar las reglas de inventario cuando correspondan.
- Evitar duplicaciones y cálculos incorrectos.

Si la arquitectura existente utiliza servicios separados para Feed y Supply, mantener esa separación.

Si existe un servicio común de inventario, reutilizarlo cuando sea coherente con los modelos.

No crear abstracciones innecesarias.

---

# 6. Repositories y consultas

Revisar los repositorios existentes y adaptar las consultas necesarias.

### Estanques

- Consultar los estanques según las relaciones existentes.
- Incluir la información necesaria de la granja.
- Obtener la información de calidad del agua sin mezclar registros de otros estanques.
- Obtener las referencias correspondientes.
- Evitar consultas que produzcan duplicados.
- Utilizar consultas eficientes.

### Inventario

- Consultar `Feed` y `Supply` de acuerdo con sus modelos.
- Respetar los filtros de estado existentes.
- Obtener categorías y relaciones necesarias.
- Evitar duplicar productos.
- Evitar consultas N+1.
- No devolver registros de otros usuarios o granjas cuando exista una relación de propiedad o alcance.

Utilizar operaciones asíncronas y las convenciones de rendimiento existentes.

Si se utilizan filtros de registros activos, verificar que correspondan a la lógica real del proyecto y no excluir registros legítimos por una suposición.

---

# 7. Controllers y endpoints

Revisar los controllers existentes de estanques e inventario.

Adaptar o implementar los endpoints necesarios para que el frontend pueda obtener los datos reales.

No inventar rutas antes de revisar las convenciones existentes.

### Estanques

El endpoint debe proporcionar la información necesaria para mostrar:

- Datos del estanque.
- Forma.
- Área.
- Profundidad.
- Transparencia almacenada en `Pond`.
- Últimos registros de calidad del agua.
- Referencias.
- Estado de los parámetros, cuando corresponda.

### Inventario

Los endpoints deben permitir obtener:

- Alimentos.
- Suministros.
- Categorías.
- Stock actual.
- Stock mínimo.
- Unidades.
- Estados correspondientes.

Si el proyecto utiliza endpoints separados, mantenerlos.

Si existe un endpoint de resumen que resulte coherente con la arquitectura, adaptar su respuesta sin duplicar lógica.

### Responsabilidad del controller

- Recibir y validar los parámetros.
- Delegar la lógica al servicio.
- Devolver el DTO correspondiente.
- Aplicar los mecanismos de autorización.
- Manejar errores siguiendo las convenciones del proyecto.

No realizar cálculos de calidad del agua o inventario directamente dentro del controller.

---

# 8. DTO y contrato de respuesta

Crear o adaptar los DTO de respuesta necesarios.

Los contratos deben reflejar los campos reales de la base de datos y las reglas de negocio implementadas.

### DTO de estanques

Debe proporcionar los campos necesarios para:

- Identificación del estanque.
- Nombre y código.
- Granja relacionada, cuando corresponda.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Estado.
- Últimas mediciones.
- Fecha de medición.
- Referencias.
- Estado de calidad de los parámetros.

No es obligatorio que todos estos campos se encuentren en un único DTO. Respetar la arquitectura existente y evitar respuestas innecesariamente grandes.

### DTO de inventario

Debe proporcionar la información necesaria para mostrar:

- Identificación del producto.
- Nombre.
- Categoría.
- Tipo.
- Stock actual.
- Stock mínimo.
- Unidad.
- Estado.

Distinguir correctamente entre Feed y Supply.

No devolver datos de una tabla bajo el nombre de otra entidad.

### Valores nulos

- Respetar los campos opcionales.
- No reemplazar datos ausentes por valores inventados.
- Utilizar el formato de respuesta establecido por el proyecto.
- Mantener compatibilidad con el frontend.

---

# 9. Seguridad y autorización

Revisar la autenticación y autorización existente.

- Aplicar los mecanismos de autenticación utilizados por el proyecto.
- Identificar al usuario autenticado mediante el patrón existente.
- Respetar la propiedad y las relaciones de los datos.
- Si los estanques pertenecen a una granja, verificar que la granja corresponda al usuario autenticado.
- Si los alimentos o suministros tienen relaciones de propiedad, granja o usuario, respetar esas relaciones.
- No permitir acceso a información de otros usuarios.
- No confiar únicamente en identificadores enviados por el cliente.
- No devolver datos de otra granja por una consulta sin filtro.
- Mantener las respuestas de autorización coherentes con el resto del backend.

No modificar el mecanismo general de autenticación para resolver problemas específicos de estos apartados.

---

# 10. Manejo de errores y datos incompletos

Implementar un comportamiento consistente cuando:

- No existan estanques.
- Un estanque no tenga registros de calidad del agua.
- Exista una medición sin todos los parámetros.
- No exista una referencia aplicable.
- Existan categorías nulas.
- Un alimento no tenga stock disponible.
- Un suministro tenga campos opcionales vacíos.
- El identificador solicitado no exista.
- El usuario no tenga autorización.
- Ocurra un error de base de datos.

No convertir la ausencia de información en un valor normal o ficticio.

Diferenciar correctamente entre:

- Cero.
- Nulo.
- Sin registros.
- Registro no disponible.
- Error de consulta.

---

# 11. Pruebas

Implementar o actualizar las pruebas siguiendo las herramientas existentes en el backend.

## 11.1. Estanques

Probar:

1. Estanque con todos los datos físicos.
2. Estanque con campos físicos nulos.
3. Estanque con un registro de calidad del agua.
4. Estanque con varios registros.
5. Estanque sin registros de calidad del agua.
6. Medición con parámetros nulos.
7. Referencia válida.
8. Referencia inexistente.
9. Medición por debajo del límite.
10. Medición por encima del límite.
11. Medición exactamente en el límite.
12. Registros con fechas iguales.
13. Usuario sin autorización.
14. Consulta de un estanque que pertenece a otra granja.

## 11.2. Inventario

Probar:

1. Feed con categoría registrada.
2. Feed de tipo proteína.
3. Feed de tipo pellet.
4. Feed sin categoría.
5. Feed con stock actual.
6. Feed con stock mínimo.
7. Feed con stock igual al mínimo.
8. Feed por debajo del stock mínimo.
9. Supply con categoría registrada.
10. Supply con stock actual.
11. Supply con stock mínimo.
12. Supply con valores opcionales nulos.
13. Inventario sin registros.
14. Consulta de datos no autorizados.

## 11.3. Validaciones técnicas

- Ejecutar las pruebas existentes.
- Ejecutar la compilación del backend.
- Verificar las consultas.
- Verificar la serialización de los DTO.
- Confirmar que no se producen duplicados.
- Confirmar que las consultas respetan las relaciones.
- Documentar cualquier prueba que no pueda ejecutarse.

No afirmar que una prueba fue ejecutada si no se realizó realmente.

---

# 12. Restricciones

- No modificar otros módulos de PisciData.
- No implementar Production Cycle dentro de esta tarea.
- No implementar frontend.
- No ejecutar scripts SQL de referencia.
- No modificar el esquema de la base de datos sin una necesidad demostrada.
- No inventar columnas.
- No inventar categorías.
- No inventar unidades.
- No inventar referencias de calidad del agua.
- No utilizar valores hardcodeados como reemplazo de información faltante.
- No realizar modificaciones de inventario durante consultas.
- No duplicar cálculos que ya realiza el sistema.
- No realizar refactorizaciones generales ajenas a esta implementación.

Si se detecta que falta una columna, relación, endpoint o dato indispensable, documentar el problema y justificar cualquier cambio requerido.

---

# 13. Criterios de aceptación

La implementación estará completa cuando:

- [ ] Se revisó la arquitectura del backend.
- [ ] Se verificaron los nombres reales de las tablas y columnas.
- [ ] Se implementó la consulta de estanques.
- [ ] Forma, área, profundidad y transparencia se obtienen correctamente de `Pond`.
- [ ] La calidad del agua se obtiene de `WaterQuality`.
- [ ] Se consultan correctamente los registros más recientes.
- [ ] Se revisa y utiliza `WaterQualityReference`.
- [ ] Los estados de calidad se calculan con referencias reales.
- [ ] Los datos ausentes se representan correctamente.
- [ ] Feed se consulta desde su tabla real.
- [ ] Supply se consulta desde su tabla real.
- [ ] Proteínas y pellets se clasifican correctamente.
- [ ] Categorías, stock actual y stock mínimo se obtienen de los campos reales.
- [ ] No existen cálculos duplicados de inventario.
- [ ] Los DTO coinciden con los datos que necesita el frontend.
- [ ] Se respetan las relaciones y autorizaciones.
- [ ] Se ejecutaron las verificaciones técnicas aplicables.
- [ ] No se modificaron módulos ajenos al alcance.

---

# 14. Informe final

Al finalizar, presentar:

1. Estructura del backend revisada.
2. Tablas y columnas utilizadas.
3. Controllers modificados.
4. Services modificados.
5. Repositories modificados.
6. DTO creados o actualizados.
7. Endpoints implementados.
8. Cálculos realizados y sus fórmulas.
9. Reglas utilizadas para determinar el último registro de calidad del agua.
10. Referencias de calidad del agua utilizadas.
11. Clasificación de Feed y Supply.
12. Pruebas ejecutadas y resultados.
13. Problemas encontrados en el esquema o los datos.
14. Cambios que fueron necesarios fuera de los archivos inicialmente identificados y su justificación.

**Este documento implementa exclusivamente el backend de Estanques e Inventario. No ampliar el alcance a otros módulos.**