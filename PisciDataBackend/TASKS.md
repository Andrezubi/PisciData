# TASKS.md — Implementación del módulo de Calidad del Agua (Backend) — PisciData

## Objetivo

Implementar completamente el módulo de **Calidad del Agua** en el backend de PisciData para que funcione como una nueva sección de la pantalla Base de Datos.

El sistema debe permitir consultar, registrar, gestionar y analizar las mediciones reales de calidad del agua de los estanques de las granjas del usuario, utilizando exclusivamente la base de datos y las reglas de negocio correspondientes.

La implementación debe incluir toda la lógica necesaria, persistencia, endpoints, validaciones, relaciones, seguridad, consultas históricas y evaluación de parámetros.

**No debe existir ningún dato hardcodeado. Toda la información debe obtenerse de las tablas existentes en la base de datos o calcularse mediante reglas de negocio justificadas y correctamente implementadas.**

---

## 1. Análisis previo de arquitectura y base de datos

Antes de implementar:

- Analiza la arquitectura completa del backend.
- Revisa controladores, servicios, repositorios, DTOs, entidades y patrones utilizados.
- Inspecciona la estructura real de la base de datos.
- Identifica todas las tablas relacionadas directa o indirectamente con la calidad del agua.
- Analiza sus relaciones, restricciones, claves foráneas y campos.
- Revisa la implementación actual de la pantalla Base de Datos.
- Revisa las funcionalidades existentes de estanques, granjas y ciclos productivos.
- Identifica si ya existe una implementación parcial de calidad del agua.
- Revisa si existen tablas de referencias, rangos recomendados, parámetros de calidad, alertas o cálculos relacionados.
- Analiza cómo se identifica al usuario autenticado y cómo se valida su acceso a una granja.

No inventes estructuras ni dupliques funcionalidades existentes. Aprovecha y completa las implementaciones actuales.

---

## 2. Revisión de las tablas relacionadas

Identifica todas las tablas que contengan información relacionada con calidad del agua.

Analiza, entre otras, las tablas o entidades que puedan corresponder a:

- Mediciones de calidad del agua.
- Parámetros fisicoquímicos.
- Referencias o rangos recomendados.
- Estanques.
- Granjas.
- Ciclos productivos.
- Especies.
- Usuarios.
- Alertas o registros relacionados.

En caso de que el proyecto cuente con entidades como `WaterQuality` y `WaterQualityReference`, analiza su estructura real y utiliza sus campos, relaciones y reglas.

Verifica qué parámetros se almacenan realmente, por ejemplo:

- Temperatura.
- pH.
- Oxígeno disuelto.
- Transparencia.
- Otros parámetros que existan en el modelo real.

No agregues parámetros que no estén contemplados en el modelo o que no sean necesarios para los requerimientos del sistema.

---

## 3. Consultas de calidad del agua

Implementa la lógica para consultar los registros reales de calidad del agua.

Debe permitir:

- Obtener mediciones de las granjas del usuario.
- Obtener registros de una granja específica.
- Obtener mediciones de un estanque específico.
- Consultar el historial de mediciones.
- Obtener el último registro de calidad del agua de cada estanque.
- Consultar mediciones por fechas, si corresponde.
- Consultar registros relacionados con un ciclo productivo, si existe dicha relación.
- Ordenar los registros cronológicamente.
- Obtener información del estanque asociado a cada medición.
- Obtener la información de la granja correspondiente.

Las consultas deben ser eficientes y evitar cargar datos innecesarios.

---

## 4. Registro de nuevas mediciones

Implementa la lógica completa para registrar datos de calidad del agua.

El registro debe incluir los parámetros que realmente estén contemplados en la base de datos, por ejemplo:

- Estanque.
- Fecha y hora de medición.
- Temperatura.
- pH.
- Oxígeno disuelto.
- Transparencia.
- Otros parámetros existentes.
- Observaciones, si están contempladas.

La selección del estanque debe validarse desde backend.

El sistema debe verificar:

- Que el estanque exista.
- Que pertenezca a una granja válida.
- Que la granja pertenezca al usuario autenticado o que el usuario tenga acceso autorizado.
- Que los valores registrados sean válidos.
- Que los campos obligatorios estén presentes.
- Que los valores respeten los tipos de datos y restricciones de la base de datos.
- Que la fecha de medición sea válida.
- Que las relaciones se registren correctamente.

No permitas que el frontend pueda registrar mediciones en estanques pertenecientes a otros usuarios.

---

## 5. Rangos de referencia y evaluación de parámetros

Analiza las tablas de referencia existentes para determinar cómo evaluar los parámetros de calidad del agua.

Si existe una tabla de referencias, como `WaterQualityReference`, utiliza sus datos reales para establecer los rangos de evaluación.

Implementa una lógica que permita identificar, según los datos y reglas reales:

- Parámetros dentro del rango recomendado.
- Parámetros por debajo del rango.
- Parámetros por encima del rango.
- Parámetros que requieren atención.
- Parámetros para los cuales no existe referencia disponible.

La evaluación debe realizarse en backend cuando corresponda a la lógica de negocio.

No hardcodees rangos que ya existan en la base de datos.

Si las referencias dependen de especies, ciclos, condiciones específicas u otras relaciones, analiza e implementa correctamente esas dependencias.

No inventes recomendaciones técnicas ni rangos biológicos. Utiliza los valores de referencia que existan en la base de datos y las reglas documentadas del proyecto.

Si no existe una referencia para un parámetro, no inventes una. Devuelve la información como no evaluada o sin referencia disponible.

---

## 6. Resumen y estado de calidad del agua

Implementa los datos necesarios para que el frontend pueda mostrar resúmenes útiles de calidad del agua.

Evalúa la posibilidad de proporcionar:

- Última medición de cada estanque.
- Fecha de la última medición.
- Valores actuales de cada parámetro.
- Estado de los parámetros evaluados.
- Cantidad de parámetros dentro del rango.
- Cantidad de parámetros fuera del rango.
- Mediciones que requieren atención.
- Historial de variaciones.
- Estado general de calidad del agua, cuando las reglas de negocio permitan calcularlo.

El estado general debe basarse en información real y reglas coherentes.

No devuelvas estadísticas ficticias ni indicadores estáticos.

Si el frontend necesita gráficos históricos, proporciona los datos necesarios mediante consultas apropiadas.

---

## 7. CRUD y gestión de registros

Implementa las operaciones necesarias según las reglas actuales del proyecto:

- Crear mediciones.
- Consultar mediciones.
- Consultar detalle.
- Editar mediciones, si está permitido.
- Eliminar mediciones, si está permitido.

Si los registros son históricos y no deben modificarse o eliminarse libremente, respeta las reglas de negocio correspondientes.

Implementa correctamente las fechas de creación, actualización y medición, si existen.

No permitas modificaciones que generen inconsistencias en los datos históricos.

---

## 8. Endpoints

Implementa o completa los endpoints REST necesarios para integrar el módulo con frontend.

Contempla operaciones como:

- Obtener registros de calidad del agua.
- Obtener mediciones por granja.
- Obtener mediciones por estanque.
- Obtener historial de mediciones.
- Obtener última medición de un estanque.
- Obtener resumen de calidad del agua.
- Obtener referencias y rangos de parámetros.
- Registrar una medición.
- Obtener detalle de una medición.
- Actualizar una medición, si corresponde.
- Eliminar una medición, si corresponde.

Utiliza las convenciones de rutas y estructura existentes en PisciData.

Los endpoints deben:

- Utilizar DTOs apropiados.
- No exponer entidades directamente.
- Validar las solicitudes.
- Manejar correctamente errores.
- Retornar códigos HTTP apropiados.
- Mantener consistencia con el resto de la API.
- Evitar duplicar endpoints existentes.

---

## 9. Seguridad y autorización

Todas las operaciones deben respetar la autenticación y autorización del backend.

- Obtener el usuario autenticado mediante el mecanismo existente.
- Validar el acceso a la granja.
- Validar que el estanque pertenezca a la granja correspondiente.
- Evitar acceso a registros de otros usuarios.
- Evitar que se manipulen IDs para acceder a mediciones ajenas.
- Validar la propiedad de los registros al editarlos o eliminarlos.
- No confiar en identificadores enviados por el frontend sin verificarlos.

---

## 10. Integración con el módulo de estanques

La sección de calidad del agua debe integrarse coherentemente con la información de estanques existente.

Analiza si los registros de calidad del agua deben relacionarse con:

- El estanque.
- La granja.
- El ciclo productivo activo.
- La especie.
- Otras entidades existentes.

Implementa las relaciones necesarias utilizando la estructura actual de la base de datos.

No dupliques información de estanques o granjas en los registros si ya existe una relación adecuada.

Cuando se consulte calidad del agua, debe ser posible identificar claramente a qué estanque pertenece cada medición.

---

## 11. Validaciones y manejo de errores

Implementa validaciones para:

- Valores numéricos.
- Campos obligatorios.
- Rangos permitidos por el tipo de dato.
- Fechas.
- Existencia de estanques.
- Existencia de granjas.
- Autorización del usuario.
- Consistencia de relaciones.
- Valores inválidos.
- Registros inexistentes.
- Parámetros no contemplados.
- Referencias inexistentes.

Los mensajes de error deben ser claros y adecuados para que frontend pueda mostrarlos al usuario.

---

## 12. Pruebas

Implementa o actualiza pruebas para verificar:

- Consultar registros.
- Consultar por granja.
- Consultar por estanque.
- Consultar historial.
- Consultar última medición.
- Registrar mediciones.
- Validar valores.
- Evaluar parámetros con referencias reales.
- Manejar parámetros sin referencias.
- Obtener resúmenes.
- Editar registros, si corresponde.
- Eliminar registros, si corresponde.
- Autenticación.
- Autorización.
- Protección de datos entre usuarios.
- Relaciones correctas con estanques y granjas.
- Persistencia real.
- Ausencia de datos hardcodeados.

---

## 13. Resultado esperado

El backend debe contar con un módulo de Calidad del Agua completamente funcional y preparado para integrarse en la pantalla Base de Datos de PisciData.

Debe permitir que frontend consulte, registre y gestione información real de calidad del agua, utilizando los datos de la base de datos y las reglas de negocio correspondientes.

Implementa directamente los cambios en el proyecto.

Al finalizar, documenta:

- Entidades y tablas utilizadas.
- Cambios realizados en base de datos.
- Migraciones creadas.
- Endpoints implementados.
- DTOs creados o modificados.
- Reglas de negocio implementadas.
- Validaciones.
- Pruebas realizadas.
- Funcionalidades pendientes, si las hubiera.

**Prioridad: implementación real, persistencia correcta, seguridad por granja, referencias dinámicas y ausencia absoluta de hardcoding.**