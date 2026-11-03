# Tasks.md — Implementación Frontend de Estanques e Inventario (PisciData)

## 1. Objetivo

Implementar y adaptar completamente las pantallas de **Estanques (Ponds)** e **Inventario (Inventory)** del frontend de PisciData para que utilicen la información real proporcionada por el backend.

El agente debe revisar detalladamente la implementación de backend correspondiente a ambos apartados, comprender los endpoints, DTO, campos, cálculos y reglas de negocio, y adaptar las pantallas existentes para mostrar correctamente los datos.

Actualmente, las pantallas podrían contener información hardcodeada, datos estáticos, valores de prueba o cálculos locales que no coinciden con la implementación real.

El objetivo es eliminar esos datos ficticios y conectar las pantallas con la API real.

**Alcance exclusivo:** frontend de Estanques e Inventario.

No implementar otros módulos de PisciData.

---

# 2. Análisis obligatorio antes de implementar

## 2.1. Revisar el frontend

Identificar:

- Pantalla de Estanques.
- Pantalla de Inventario.
- Componentes relacionados.
- Servicios HTTP.
- Cliente API.
- Modelos.
- DTO e interfaces.
- Manejo de autenticación.
- Manejo de estados.
- Componentes de selección.
- Filtros.
- Categorías.
- Tarjetas, listas, tablas o indicadores de inventario.
- Componentes de calidad del agua.
- Datos hardcodeados.
- Cálculos realizados en el cliente.

Revisar detalladamente cómo están representados actualmente los datos de estanques, alimentos y suministros.

Identificar qué campos se muestran, cuáles faltan y cuáles utilizan valores estáticos.

## 2.2. Revisar detalladamente el backend

Antes de modificar las pantallas, revisar la implementación real del backend de Estanques e Inventario.

Analizar:

- Controllers.
- Endpoints.
- DTO de respuesta.
- Services.
- Repositories.
- Entidades.
- Relaciones.
- Campos.
- Tipos de datos.
- Valores nulos.
- Estados.
- Cálculos.
- Referencias.
- Manejo de errores.

El frontend debe adaptarse al contrato real del backend.

No inventar endpoints, campos, nombres de propiedades ni estructuras de respuesta.

Si existe una diferencia entre la información que espera la pantalla y la información que realmente devuelve el backend, identificarla y adaptar el frontend.

No modificar el backend como solución automática a problemas de integración del frontend.

---

# 3. Conexión con la API

## 3.1. Eliminar datos hardcodeados

Reemplazar los datos ficticios utilizados en Estanques e Inventario por datos reales.

Eliminar:

- Arreglos de prueba.
- Objetos ficticios.
- Cantidades estáticas de inventario.
- Valores ficticios de calidad del agua.
- Categorías inventadas.
- Estados de estanques inventados.
- Referencias de calidad hardcodeadas.
- Valores de stock mínimo de prueba.

Mantener únicamente los elementos estáticos que sean puramente visuales, como etiquetas, iconos y textos generales.

No mostrar datos de prueba cuando una petición falle.

No generar automáticamente valores ficticios para completar las tarjetas.

## 3.2. Reutilizar la arquitectura

- Utilizar el cliente HTTP existente.
- Reutilizar la configuración de conexión.
- Reutilizar la autenticación.
- Reutilizar los servicios compartidos.
- Crear servicios específicos únicamente cuando sea necesario.
- Mantener separados los modelos, servicios y componentes visuales.
- No realizar consultas directas a MySQL desde el frontend.
- No duplicar las reglas de negocio del backend.

## 3.3. Modelos y DTO

Actualizar los modelos e interfaces del frontend para que coincidan con los DTO reales.

Verificar:

- Nombres exactos.
- Tipos.
- Identificadores.
- Campos opcionales.
- Valores nulos.
- Unidades.
- Fechas.
- Categorías.
- Estados.

No crear propiedades ficticias para completar la interfaz.

Si el backend devuelve una estructura diferente a la esperada, adaptar el modelo y la pantalla de acuerdo con la respuesta real.

---

# 4. Estanques (Ponds)

## 4.1. Información general

Conectar la pantalla de Estanques con el endpoint real del backend.

Mostrar los datos correspondientes a cada estanque utilizando los campos reales.

Cuando estén disponibles, presentar:

- Nombre del estanque.
- Código.
- Granja relacionada.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Estado.

Confirmar que cada campo corresponde a la información real que devuelve el backend.

No inventar valores para campos ausentes.

No utilizar información de otros estanques para completar la pantalla actual.

---

## 4.2. Forma, área y profundidad

Obtener estos datos desde la respuesta del backend.

### Forma

- Mostrar la forma registrada en la base de datos.
- Utilizar la propiedad real del DTO.
- No inferir la forma por el nombre del estanque.
- No mostrar una forma predeterminada cuando no existe información.

### Área

- Mostrar el área real del estanque.
- Respetar la unidad proporcionada por el backend.
- Presentar el valor con un formato legible.
- No realizar conversiones innecesarias.
- No reemplazar valores nulos por cantidades ficticias.

### Profundidad

- Mostrar la profundidad real.
- Utilizar la unidad correspondiente.
- No calcular una profundidad inventada.
- No utilizar valores estáticos.

### Transparencia física

- Mostrar el valor de transparencia obtenido desde `Pond`, cuando esté disponible.
- No confundirlo con una medición de calidad del agua de otra fecha.
- No sustituirlo automáticamente por la referencia mínima o máxima.
- Respetar los valores nulos.

Si la respuesta del backend contiene información adicional para identificar el origen de la transparencia, utilizarla para mantener la coherencia de la pantalla.

---

# 5. Calidad del agua

## 5.1. Datos reales

Conectar la información de calidad del agua con los registros proporcionados por el backend.

Mostrar correctamente:

- Transparencia.
- pH.
- Temperatura.
- Oxígeno disuelto.
- Fecha de medición.
- Referencias.
- Estado de los parámetros, cuando exista.

Los nombres anteriores son funcionales. Utilizar los campos reales del DTO.

No utilizar valores de calidad del agua hardcodeados.

No generar valores aleatorios.

No mostrar valores de referencia como si fueran mediciones reales.

## 5.2. Última medición

La última medición válida debe ser determinada por el backend.

El frontend debe mostrar el registro recibido, sin volver a seleccionar una fecha ni combinar mediciones de distintos registros.

- Mostrar la fecha real.
- Mostrar los parámetros disponibles.
- Respetar valores nulos.
- No reemplazar parámetros faltantes por valores ficticios.
- No utilizar una medición de otro estanque.
- No mostrar registros antiguos como si fueran los últimos cuando el backend ya proporciona la medición actual.

Si el backend no devuelve ninguna medición, mostrar un estado claro de ausencia de datos.

## 5.3. WaterQualityReference

Utilizar las referencias que proporciona el backend.

Mostrar correctamente, cuando estén disponibles:

- Valor medido.
- Valor mínimo.
- Valor máximo.
- Unidad.
- Estado.

No hardcodear límites de pH, temperatura, transparencia u oxígeno disuelto.

No establecer estados de alerta mediante reglas locales diferentes a las del backend.

## 5.4. Estado de calidad

Si el backend devuelve el estado de cada parámetro:

- Mostrarlo directamente.
- Mantener coherencia con las reglas de calidad del agua.
- Utilizar etiquetas, colores o indicadores visuales que correspondan al estado recibido.
- No volver a calcular el estado en el frontend.

Si el backend devuelve los valores y las referencias, pero no el estado:

- Revisar el contrato.
- Confirmar si el estado debe ser calculado por el frontend según la arquitectura existente.
- Evitar duplicar una regla que ya esté implementada en el servidor.
- Si es necesario implementar una presentación basada en las referencias, documentar la regla utilizada y mantenerla alineada con el backend.

No mostrar una medición inexistente como normal.

No mostrar alertas cuando faltan los datos necesarios para evaluar un parámetro.

---

# 6. Inventario

## 6.1. Objetivo

Conectar la pantalla de Inventario con la implementación real del backend.

Eliminar todos los datos ficticios que representen productos, categorías o cantidades.

La interfaz debe mostrar correctamente:

- Alimentos.
- Suministros.
- Nombre.
- Categoría.
- Tipo.
- Inventario actual.
- Stock mínimo.
- Unidad.
- Estado.

Los campos anteriores deben ajustarse a las propiedades reales del backend.

No asumir que Feed y Supply tienen exactamente la misma estructura.

---

# 7. Feed — Inventario de alimentos

## 7.1. Obtener los alimentos reales

Consultar el endpoint real de Feed.

Mostrar los alimentos registrados en la base de datos.

Revisar detalladamente el modelo y el DTO para identificar las propiedades correctas.

No utilizar una lista de alimentos ficticios.

No inventar categorías ni cantidades.

## 7.2. Proteínas y pellets

Prestar especial atención a los alimentos de tipo:

- Proteínas.
- Pellets.

Revisar cómo los representa realmente el backend.

Determinar si se identifican mediante una categoría, tipo, propiedad o relación.

No asumir que son columnas independientes.

No clasificar alimentos por su nombre comercial.

No crear categorías nuevas desde el frontend.

Mostrar correctamente la clasificación real recibida.

Si la API proporciona el nombre de la categoría mediante una relación, utilizarlo en la pantalla en lugar de mostrar únicamente un identificador.

## 7.3. Información del alimento

Mostrar los campos reales disponibles:

- Nombre.
- Categoría.
- Tipo.
- Inventario actual.
- Stock mínimo.
- Unidad.
- Estado.

Si existen otros campos relevantes para la pantalla, revisar su utilidad y mostrarlos únicamente cuando correspondan al alcance de esta tarea.

No mostrar identificadores internos como nombres de categorías cuando exista un nombre legible disponible.

## 7.4. Inventario actual

Mostrar la cantidad real que devuelve el backend.

- Utilizar el campo correcto.
- Respetar su unidad.
- Mostrar la precisión adecuada.
- No recalcular el stock.
- No sumar entradas o salidas nuevamente.
- No sustituir cantidades nulas por valores inventados.

Si el backend proporciona el stock actual calculado, mostrar ese valor directamente.

## 7.5. Stock mínimo

Mostrar el stock mínimo registrado.

- Utilizar el campo real.
- Respetar la unidad.
- No utilizar un valor fijo para todos los alimentos.
- No reemplazar un valor nulo por una cantidad ficticia.

Si el backend proporciona un estado de inventario, utilizarlo.

Si no existe un estado y la interfaz necesita mostrar una alerta:

- Revisar la regla real del backend.
- Mantener la comparación coherente.
- No duplicar cálculos existentes.
- No mostrar alertas incorrectas cuando falten datos.

---

# 8. Supply — Inventario de suministros

## 8.1. Obtener suministros reales

Consultar el endpoint real de Supply.

Mostrar correctamente los suministros registrados.

No utilizar datos ficticios.

No asumir que los suministros son alimentos.

## 8.2. Información del suministro

Mostrar los campos disponibles en el backend:

- Nombre.
- Categoría.
- Inventario actual.
- Stock mínimo.
- Unidad.
- Estado.

Revisar si existen otros campos relevantes para la pantalla.

No utilizar automáticamente el modelo de Feed si el DTO de Supply tiene propiedades diferentes.

## 8.3. Categorías

- Utilizar las categorías reales.
- Resolver correctamente las relaciones.
- Mostrar nombres legibles.
- No inventar categorías.
- No mostrar identificadores numéricos como categorías si el backend proporciona el nombre correspondiente.

## 8.4. Inventario y stock mínimo

- Mostrar el stock actual real.
- Mostrar el stock mínimo real.
- Respetar las unidades.
- No recalcular el inventario.
- No realizar movimientos de stock desde esta pantalla.
- No utilizar cantidades ficticias.
- No comparar cantidades que tengan unidades incompatibles.

Si el backend proporciona alertas de reposición, mostrarlas de acuerdo con el contrato real.

---

# 9. Organización de la pantalla de inventario

Revisar la pantalla existente y adaptar su estructura para mostrar claramente los alimentos y los suministros.

Si el diseño actual contiene pestañas, filtros o categorías:

- Mantenerlos cuando sean coherentes con el modelo real.
- Adaptarlos a los datos reales de Feed y Supply.
- No inventar categorías para llenar filtros vacíos.
- No mostrar productos de prueba.

Si existen filtros por categoría:

- Utilizar las categorías reales recibidas.
- Evitar filtros hardcodeados que no coincidan con el backend.
- Mantener la selección y actualización de filtros coherente con la respuesta.

Si existen tarjetas de inventario:

- Mostrar el nombre.
- Mostrar la categoría.
- Mostrar el stock actual.
- Mostrar el stock mínimo.
- Mostrar la unidad.
- Mostrar el estado, cuando exista.

No es necesario mostrar todos los campos con la misma jerarquía visual, pero los datos relevantes deben poder consultarse correctamente.

---

# 10. Estados de carga y errores

Implementar estados de interfaz para ambos apartados.

## 10.1. Cargando

- Mostrar indicadores mientras se consultan los datos.
- Evitar que la pantalla parezca rota.
- Evitar peticiones duplicadas.
- Evitar mostrar temporalmente información de otro estanque o inventario.

## 10.2. Sin resultados

Cuando el backend responda correctamente sin registros:

### Estanques

Mostrar un mensaje claro indicando que no existen estanques registrados o disponibles.

No mostrar estanques ficticios.

### Inventario

Mostrar un mensaje claro cuando no existan alimentos o suministros.

No mostrar cantidades de prueba.

## 10.3. Errores

Gestionar:

- Error de conexión.
- `400 Bad Request`.
- `401 Unauthorized`.
- `403 Forbidden`.
- `404 Not Found`, cuando corresponda.
- Error interno del servidor.
- Respuestas incompatibles con el modelo.
- Campos opcionales nulos.

Mostrar mensajes comprensibles.

No mostrar detalles internos del servidor, credenciales o tokens.

Utilizar el mecanismo de manejo de errores existente.

Permitir reintentar las consultas cuando corresponda.

---

# 11. Actualización y sincronización

La información visualizada debe mantenerse sincronizada con la respuesta real del backend.

- Cargar los datos al abrir las pantallas.
- Actualizar los estanques cuando se realice una nueva consulta.
- Actualizar el inventario cuando se vuelva a consultar.
- Respetar los mecanismos de actualización y caché existentes.
- No conservar datos antiguos como si fueran actuales después de una consulta fallida.
- No utilizar valores locales para simular cambios de stock.
- No modificar los datos del backend desde operaciones de lectura.

Si el proyecto ya dispone de mecanismos de actualización automática, utilizarlos cuando sean compatibles.

No implementar sincronización en tiempo real adicional si no forma parte de la arquitectura existente.

---

# 12. Diseño y experiencia de usuario

- Mantener la identidad visual de PisciData.
- Reutilizar componentes existentes.
- Mantener coherencia entre Estanques e Inventario.
- Presentar unidades de forma clara.
- Utilizar formatos numéricos legibles.
- Mostrar correctamente los valores nulos.
- Diferenciar estados de calidad del agua.
- Diferenciar alertas de stock.
- Evitar que los nombres extensos o las cantidades rompan el diseño.
- Mantener la accesibilidad.
- Respetar los tamaños de pantalla soportados.
- No rediseñar otras secciones.
- No introducir dependencias innecesarias.

No cambiar la arquitectura visual de la aplicación sin una necesidad concreta.

---

# 13. Pruebas

Realizar las pruebas existentes y las que sean necesarias para verificar la integración.

## 13.1. Estanques

Probar:

1. Estanque con todos los datos físicos.
2. Estanque con valores nulos.
3. Estanque con última medición de calidad del agua.
4. Estanque con parámetros incompletos.
5. Estanque sin mediciones.
6. Medición con referencias.
7. Medición con estado normal.
8. Medición con alerta.
9. Cambio de estanque.
10. Error de conexión.

## 13.2. Inventario

Probar:

1. Feed con categoría.
2. Feed de proteínas.
3. Feed de pellets.
4. Feed con stock actual.
5. Feed con stock mínimo.
6. Feed con stock bajo.
7. Feed con datos opcionales nulos.
8. Supply con categoría.
9. Supply con stock actual.
10. Supply con stock mínimo.
11. Inventario vacío.
12. Respuesta del backend con error.
13. Actualización de inventario después de una nueva consulta.

## 13.3. Validación técnica

- Ejecutar las pruebas existentes.
- Ejecutar la compilación del frontend.
- Verificar la integración con la API.
- Confirmar que los modelos coinciden con los DTO.
- Confirmar que no existen datos ficticios.
- Verificar que los campos se presentan correctamente.
- Documentar los resultados reales.

No afirmar que las pruebas fueron ejecutadas si no se realizaron.

---

# 14. Restricciones de alcance

- Modificar exclusivamente el frontend de Estanques e Inventario.
- No modificar el backend.
- No ejecutar SQL.
- No cambiar el esquema de la base de datos.
- No implementar Production Cycle.
- No implementar módulos adicionales.
- No inventar endpoints.
- No inventar campos.
- No inventar categorías.
- No inventar valores de calidad del agua.
- No inventar cantidades de inventario.
- No recalcular valores que ya proporciona el backend.
- No duplicar reglas de negocio.
- No introducir dependencias innecesarias.
- No realizar refactorizaciones generales.

Si falta un campo o endpoint necesario, documentar exactamente qué información falta y comunicarlo antes de realizar cambios fuera del alcance.

---

# 15. Criterios de aceptación

La implementación se considerará completa cuando:

- [ ] Se revisó detalladamente la implementación real del backend.
- [ ] Se revisaron los DTO y endpoints de Estanques e Inventario.
- [ ] Los datos hardcodeados fueron reemplazados por datos reales.
- [ ] Forma, área, profundidad y transparencia se muestran correctamente.
- [ ] pH, temperatura, oxígeno disuelto y transparencia se muestran desde las mediciones reales.
- [ ] Se muestra correctamente la fecha de la última medición.
- [ ] Se muestran las referencias de calidad del agua disponibles.
- [ ] Los estados de calidad coinciden con la lógica del backend.
- [ ] Los datos ausentes se presentan correctamente.
- [ ] Feed se conecta con el endpoint real.
- [ ] Proteínas y pellets se clasifican correctamente.
- [ ] Supply se conecta con el endpoint real.
- [ ] Los nombres y categorías son correctos.
- [ ] El inventario actual coincide con el backend.
- [ ] El stock mínimo coincide con el backend.
- [ ] Las unidades se muestran correctamente.
- [ ] No existen cantidades ficticias.
- [ ] Se muestran estados de carga, error y ausencia de resultados.
- [ ] Las pantallas se actualizan correctamente.
- [ ] Se conserva el diseño existente.
- [ ] No se modificaron otros módulos.
- [ ] Las verificaciones técnicas aplicables fueron ejecutadas.

---

# 16. Informe final

Al finalizar, presentar:

1. Archivos del frontend revisados.
2. Archivos creados o modificados.
3. Endpoints utilizados.
4. Modelos e interfaces actualizados.
5. Datos hardcodeados eliminados.
6. Campos de estanques conectados.
7. Campos de calidad del agua conectados.
8. Referencias utilizadas.
9. Campos de Feed conectados.
10. Campos de Supply conectados.
11. Forma en que se muestran proteínas y pellets.
12. Forma en que se muestran stock actual y stock mínimo.
13. Manejo de estados de carga, error y datos vacíos.
14. Pruebas ejecutadas y resultados reales.
15. Problemas encontrados en el contrato del backend.
16. Funcionalidades pendientes que estén fuera del alcance.

**Importante:** esta tarea implementa exclusivamente el frontend de Estanques e Inventario. No ampliar el alcance a otros módulos de PisciData hasta recibir nuevas instrucciones.