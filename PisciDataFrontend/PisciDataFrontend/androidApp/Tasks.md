# TASKS.md — Implementación de la sección Calidad del Agua (Frontend) — PisciData

## Objetivo

Agregar una nueva sección de **Calidad del Agua** dentro de la pantalla Base de Datos de PisciData, manteniendo la estructura visual, identidad y navegación existente.

Esta sección debe permitir consultar, visualizar, registrar y gestionar las mediciones de calidad del agua de los estanques de las granjas del usuario, conectándose completamente con el backend.

La implementación debe incluir las nuevas vistas, listados, tarjetas, filtros, formularios, historial, detalles y visualización de parámetros, según las funcionalidades reales disponibles en backend.

**No debe existir absolutamente ningún dato hardcodeado. Todo lo mostrado debe obtenerse de los endpoints reales y de la base de datos.**

---

## 1. Análisis previo

Antes de implementar:

- Analiza la estructura actual del frontend.
- Revisa la pantalla Base de Datos.
- Analiza cómo están implementadas las secciones de ciclos productivos, estanques e inventario.
- Revisa sus componentes visuales, navegación, formularios y manejo de estado.
- Inspecciona la arquitectura de conexión con backend.
- Revisa los servicios, repositorios, modelos y DTOs actuales.
- Analiza los endpoints reales del módulo Calidad del Agua implementados en backend.
- Identifica las funcionalidades existentes que pueden reutilizarse.
- Revisa cómo se selecciona la granja y cómo se gestiona la autenticación.

La nueva sección debe integrarse siguiendo la arquitectura existente, sin crear estructuras innecesarias ni duplicar componentes.

---

## 2. Nueva sección dentro de Base de Datos

Agrega **Calidad del Agua** como una sección independiente dentro de Base de Datos, al mismo nivel que:

- Ciclos productivos.
- Estanques.
- Inventario.
- Calidad del Agua.

La nueva sección debe:

- Integrarse correctamente en la navegación existente.
- Mantener el diseño original de PisciData.
- Utilizar los mismos patrones visuales de las otras secciones.
- Adaptarse correctamente a los tamaños de pantalla.
- Permitir acceder a sus registros y formularios.
- Mostrar información real de la granja seleccionada.

No reemplaces las secciones existentes ni alteres sus funcionalidades.

---

## 3. Pantalla principal de Calidad del Agua

Implementa una pantalla clara, moderna y profesional que permita visualizar los registros de calidad del agua.

Incluye, según la información disponible en backend:

- Título y descripción de la sección.
- Selector de granja, si corresponde a la navegación actual.
- Resumen general.
- Listado de mediciones.
- Identificación del estanque asociado.
- Fecha y hora de medición.
- Parámetros registrados.
- Estado de los parámetros.
- Indicadores de valores fuera de rango.
- Botón para registrar una nueva medición.
- Acceso a detalles e historial.

Los datos deben organizarse de manera que sea sencillo comparar mediciones y detectar problemas.

---

## 4. Tarjetas de mediciones

Implementa tarjetas o un formato de listado apropiado para cada registro.

Cada tarjeta debe mostrar, según los datos reales disponibles:

- Estanque.
- Código o nombre del estanque.
- Fecha y hora de medición.
- Temperatura.
- pH.
- Oxígeno disuelto.
- Transparencia.
- Otros parámetros disponibles.
- Estado de los parámetros.
- Observaciones, si existen.

Las tarjetas deben permitir identificar rápidamente si una medición presenta valores que requieren atención.

Utiliza indicadores visuales consistentes para los estados:

- Dentro del rango.
- Por debajo del rango.
- Por encima del rango.
- Sin referencia disponible.

Los estados deben provenir de la lógica de backend. No implementes rangos inventados ni evaluaciones basadas en valores estáticos.

---

## 5. Formulario de registro de calidad del agua

Implementa un formulario específico para registrar mediciones de calidad del agua.

Debe abrirse al presionar el botón **Nueva medición** o **Registrar calidad del agua** dentro de esta sección.

El formulario debe contener exclusivamente los campos correspondientes al modelo y endpoints reales del backend.

Contempla, según corresponda:

- Selección del estanque.
- Fecha y hora de medición.
- Temperatura.
- pH.
- Oxígeno disuelto.
- Transparencia.
- Otros parámetros contemplados en la base de datos.
- Observaciones.

### Selección del estanque

- Cargar los estanques disponibles desde backend.
- Mostrar el nombre y código del estanque, si existen.
- Permitir seleccionar únicamente estanques accesibles para el usuario.
- No utilizar opciones hardcodeadas.
- Validar que se haya seleccionado un estanque cuando sea obligatorio.

### Campos de medición

- Utilizar campos adecuados para valores numéricos.
- Mostrar las unidades correspondientes cuando estén disponibles.
- Evitar permitir caracteres inválidos.
- Validar los campos obligatorios.
- Mostrar mensajes de error comprensibles.
- Mantener los valores ingresados si ocurre un error de registro.

### Registro

Al guardar:

1. Validar el formulario.
2. Enviar la información al endpoint real.
3. Mostrar indicador de carga.
4. Evitar envíos duplicados.
5. Procesar correctamente la respuesta del backend.
6. Mostrar mensaje de éxito si se guardó correctamente.
7. Cerrar el formulario cuando corresponda.
8. Actualizar la lista de mediciones.
9. Actualizar los resúmenes y estados asociados.

Si el backend devuelve un error, mostrarlo de manera comprensible y no simular que la medición fue registrada.

---

## 6. Detalles e historial de mediciones

Implementa una vista de detalle o historial para consultar información completa de calidad del agua.

Permite visualizar:

- Información del estanque.
- Información de la granja.
- Fecha de medición.
- Todos los parámetros registrados.
- Valores de referencia disponibles.
- Estado de cada parámetro.
- Observaciones.
- Mediciones anteriores, cuando corresponda.

Si backend proporciona datos históricos para gráficos, implementa visualizaciones apropiadas para analizar la evolución de los parámetros a lo largo del tiempo.

Los gráficos deben construirse utilizando registros reales y fechas reales.

No generes series de datos ficticias ni inventes tendencias.

---

## 7. Filtros y navegación

Implementa filtros que funcionen con datos reales, según las capacidades de backend:

- Por granja.
- Por estanque.
- Por fecha o rango de fechas.
- Por parámetros que requieran atención.
- Por estado de medición, si corresponde.

Permite que los filtros puedan combinarse coherentemente.

Al cambiar la granja seleccionada, actualiza correctamente los registros y evita mostrar información de la granja anterior.

---

## 8. Referencias y rangos de parámetros

Conecta la interfaz con los datos de referencia proporcionados por backend.

Cuando existan rangos recomendados:

- Mostrar el rango correspondiente.
- Mostrar el valor medido.
- Mostrar el estado de evaluación.
- Permitir identificar si el parámetro se encuentra por debajo, dentro o por encima del rango.

Cuando no exista referencia:

- No inventar valores.
- Mostrar claramente que no existe referencia disponible.
- Evitar clasificar arbitrariamente el parámetro.

Toda la evaluación debe ser coherente con las reglas implementadas en backend.

---

## 9. Edición y eliminación

Si backend permite editar o eliminar mediciones, implementa las acciones correspondientes.

- Mostrar botones o menús de acción apropiados.
- Utilizar formularios de edición.
- Cargar los valores actuales desde backend.
- Validar los datos.
- Enviar cambios mediante los endpoints correspondientes.
- Solicitar confirmación cuando sea necesario.
- Actualizar las listas y detalles después de una operación exitosa.

Si backend no permite editar o eliminar determinados registros, no habilites acciones que no estén soportadas.

---

## 10. Estados de la interfaz

Implementa todos los estados necesarios.

### Cargando

Mostrar indicadores visuales mientras se obtienen las mediciones.

### Sin registros

Mostrar un estado vacío profesional que indique que aún no existen mediciones de calidad del agua.

Incluir un botón para registrar la primera medición, si corresponde.

### Error

Mostrar mensajes comprensibles cuando falle la comunicación con backend.

Permitir reintentar.

### Registro y edición

Mostrar indicadores de carga y evitar operaciones duplicadas.

### Datos actualizados

Reflejar inmediatamente las operaciones exitosas utilizando los datos reales del backend.

---

## 11. Diseño visual y consistencia

Mantén el estilo original de PisciData y la consistencia con las secciones existentes de Base de Datos.

Mejora:

- Distribución de tarjetas.
- Espaciado.
- Tipografía.
- Iconos.
- Botones.
- Formularios.
- Filtros.
- Indicadores de estado.
- Modales.
- Gráficos, si existen.
- Jerarquía visual.

La pantalla debe ser profesional, limpia, intuitiva y fácil de utilizar.

Evita saturarla con gráficos, colores o elementos decorativos innecesarios.

---

## 12. Integración con backend

Utiliza exclusivamente los endpoints, DTOs y modelos reales implementados en backend.

- Respeta los nombres de campos.
- Respeta los tipos de datos.
- Respeta los formatos de fechas.
- Utiliza correctamente los tokens de autenticación.
- Maneja errores HTTP.
- No inventes endpoints.
- No inventes parámetros.
- No dupliques lógica de negocio.
- No calcules estados de calidad del agua mediante rangos hardcodeados.
- No utilices datos de demostración.
- No sustituyas respuestas reales por valores ficticios.

Toda la información debe provenir de backend.

---

## 13. Pruebas y verificación

Verifica:

- Acceso a la nueva sección.
- Navegación dentro de Base de Datos.
- Consulta de mediciones.
- Selección de granja.
- Selección de estanque.
- Registro de mediciones.
- Validación de campos.
- Visualización de valores y referencias.
- Estados de parámetros.
- Historial.
- Filtros.
- Edición y eliminación, si corresponden.
- Estados vacíos.
- Estados de carga.
- Errores de API.
- Actualización de datos después de operaciones.
- Autenticación.
- Ausencia de hardcoding.
- Correcta integración con todos los endpoints disponibles.

---

## 14. Resultado esperado

La pantalla Base de Datos debe incluir una nueva sección funcional de Calidad del Agua, integrada con el resto de los módulos.

El usuario debe poder consultar las mediciones de sus estanques, registrar nuevos datos, revisar su historial y reconocer los parámetros que requieren atención.

Implementa directamente todos los cambios en el proyecto.

Al finalizar, informa:

- Nuevas pantallas y componentes creados.
- Modificaciones realizadas en Base de Datos.
- Formularios implementados.
- Endpoints conectados.
- Funcionalidades implementadas.
- Pruebas realizadas.
- Funcionalidades pendientes, si las hubiera.

**Prioridad: integración real con backend, visualización correcta de mediciones, registro funcional, referencias dinámicas y ausencia absoluta de datos hardcodeados.**