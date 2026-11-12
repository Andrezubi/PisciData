# Tasks.md — Implementación Backend de Formularios de Base de Datos (PisciData)

## Objetivo general

Revisar y completar la implementación del backend correspondiente a la pantalla **Base de Datos** de PisciData, enfocándose exclusivamente en las siguientes secciones:

1. Ciclos activos (ciclos productivos).
2. Estanques.
3. Inventario.

Se necesita que los formularios de registro de cada sección permitan guardar información correctamente en la base de datos, respetando las relaciones, restricciones, campos, tipos de datos y reglas de negocio existentes.

También se deben implementar o corregir las operaciones necesarias para consultar, registrar, actualizar y finalizar los registros.

El backend será la fuente de verdad para toda la información que consuma el frontend.

**No asumir que las funcionalidades ya están implementadas. Primero inspeccionar el proyecto y después completar lo que realmente haga falta.**

---

## 1. Inspección inicial y arquitectura

Antes de modificar código:

- Revisar la arquitectura completa del backend.
- Identificar controladores, servicios, repositorios, entidades, DTOs y configuraciones.
- Revisar la conexión con la base de datos y el uso de Entity Framework Core.
- Inspeccionar los controladores y servicios relacionados con ciclos productivos, estanques e inventario.
- Revisar las consultas utilizadas para obtener los registros.
- Revisar los endpoints de creación, actualización y eliminación existentes.
- Revisar las relaciones entre las entidades.
- Identificar qué funcionalidades están incompletas o utilizan información incorrecta.
- Revisar las validaciones actuales.
- Comprobar que las operaciones respeten al usuario autenticado.

No duplicar servicios, controladores ni endpoints que ya estén correctamente implementados.

No modificar módulos ajenos a las tres secciones indicadas.

## 2. Inspección de la base de datos

Revisar el esquema real de `PisciDataDB`.

Inspeccionar como mínimo las tablas y relaciones correspondientes a:

- `Farm`
- `Pond`
- `Species`
- `ProductionCycle`
- `Biometrics`
- `Harvest`
- `Mortality`
- `Feed`
- `Supply`
- `WaterQuality`
- `WaterQualityReference`
- Cualquier otra tabla que intervenga directamente en estas funcionalidades.

Identificar:

- Columnas reales.
- Claves primarias y foráneas.
- Tipos de datos.
- Campos obligatorios y opcionales.
- Valores predeterminados.
- Restricciones.
- Relaciones.
- Estados.
- Campos de auditoría.
- Relaciones con usuarios y granjas.

Utilizar los nombres y tipos de datos reales del esquema.

No inventar columnas ni relaciones.

No modificar el esquema de la base de datos sin justificar previamente por qué resulta indispensable.

---

## 3. Ciclos productivos

Revisar y completar la lógica de registro y administración de ciclos productivos.

### 3.1. Consulta de ciclos

Implementar o corregir la consulta para obtener los ciclos correspondientes al usuario autenticado.

Cada ciclo debe incluir los datos reales que correspondan, por ejemplo:

- Identificador del ciclo.
- Granja.
- Estanque.
- Código y nombre del estanque.
- Especie.
- Fecha de inicio.
- Edad inicial.
- Cantidad inicial de peces.
- Peso promedio inicial.
- Peso promedio actual, si existe información.
- Cantidad actual de peces, según los registros de cosecha y mortalidad.
- Edad actual calculada.
- Observaciones.
- Estado del ciclo.

No devolver ciclos pertenecientes a granjas o usuarios diferentes.

### 3.2. Registro de ciclos

Implementar o corregir el formulario de creación de ciclos desde el backend.

El registro debe respetar los campos reales de `ProductionCycle`.

Requisitos:

- El ciclo debe pertenecer a un estanque existente.
- El estanque debe pertenecer a una granja del usuario autenticado.
- La especie debe seleccionarse de los registros existentes de `Species`.
- No aceptar identificadores de estanques o especies inexistentes.
- Validar que los registros relacionados sean válidos y estén activos cuando corresponda.
- Validar cantidad inicial de peces.
- Validar peso promedio inicial.
- Validar edad inicial.
- Validar fecha de inicio.
- Validar observaciones según las restricciones reales.
- Establecer el estado inicial del ciclo como activo, utilizando el campo real de la entidad.
- No solicitar una fecha de finalización al registrar un ciclo.
- No permitir que el frontend determine el usuario propietario del ciclo.

El usuario y la granja deben determinarse mediante las relaciones y el mecanismo de autenticación existente.

### 3.3. Estanques y especies disponibles

Implementar o completar los endpoints necesarios para alimentar los selectores del formulario.

El formulario de ciclos necesita obtener:

**Estanques disponibles:**
- Consultar los estanques que pertenecen a las granjas del usuario autenticado.
- Devolver únicamente los datos necesarios para seleccionar un estanque.
- Respetar las reglas de disponibilidad existentes.
- No mostrar estanques de otros usuarios.
- No permitir seleccionar un estanque que no corresponda al usuario.

**Especies disponibles:**
- Consultar las especies existentes en la base de datos.
- Mostrar las especies que se encuentren habilitadas para su selección.
- No utilizar listas de especies escritas directamente en el código.

Los selectores deben consumir información real del backend.

Revisar si ya existen endpoints adecuados y reutilizarlos cuando sea posible.

### 3.4. Finalización de ciclos

Modificar la lógica para que el ciclo se registre inicialmente sin fecha de finalización.

La finalización debe realizarse posteriormente desde la tarjeta del ciclo.

Implementar o completar un endpoint específico para finalizar un ciclo.

Requisitos:

- Identificar el ciclo que se desea finalizar.
- Verificar que pertenezca al usuario autenticado.
- Verificar que exista.
- Verificar que todavía esté activo.
- Cambiar su estado a finalizado utilizando el campo real de la base de datos.
- Registrar la fecha de finalización en `EndDate`, si corresponde según el esquema.
- Evitar finalizar dos veces el mismo ciclo.
- Guardar los cambios correctamente.
- Retornar el estado actualizado del ciclo.

La fecha de finalización debe generarse desde el backend y no depender de una fecha arbitraria enviada por el frontend.

No permitir que el frontend cambie directamente el estado mediante una actualización general del ciclo.

### 3.5. Actualización de ciclos

Revisar si existe una operación de actualización.

Permitir modificar únicamente los campos que correspondan y respetar las reglas de negocio.

No permitir que una actualización general reactive accidentalmente un ciclo finalizado ni altere su fecha de finalización.

No permitir cambiar la propiedad del ciclo.

---

## 4. Estanques

Revisar y completar el funcionamiento del módulo de estanques.

### 4.1. Consulta

Obtener los estanques correspondientes al usuario autenticado.

Mostrar los campos reales disponibles en la base de datos, incluyendo cuando existan:

- Código.
- Nombre.
- Forma.
- Área.
- Profundidad.
- Transparencia.
- Estado.
- Granja a la que pertenece.
- Información de calidad del agua relacionada.

Para los datos de calidad del agua, revisar la relación con `WaterQuality`.

Cuando corresponda, obtener el registro más reciente de cada estanque.

No utilizar valores inventados si no existen registros de calidad del agua.

### 4.2. Registro

Implementar o completar la creación de estanques.

El formulario debe permitir registrar los campos que realmente correspondan a la entidad `Pond`.

Requisitos:

- Asociar el estanque a una granja del usuario autenticado.
- No permitir asociarlo a una granja ajena.
- Validar los campos obligatorios.
- Respetar tipos de datos y restricciones.
- Validar código único según las restricciones reales de la base de datos.
- Validar dimensiones y valores numéricos.
- Establecer el estado inicial según las reglas del proyecto.
- No aceptar identificadores de usuario o propietario enviados libremente por el frontend.

Si el registro de estanques requiere seleccionar una granja, proporcionar un endpoint para obtener las granjas disponibles del usuario.

### 4.3. Actualización

Permitir modificar los campos editables del estanque.

Respetar sus relaciones y restricciones.

No modificar automáticamente los ciclos productivos relacionados.

### 4.4. Calidad del agua

Revisar cómo se registran los datos de `WaterQuality`.

Si la pantalla permite registrar mediciones de calidad del agua, verificar que exista una operación adecuada para guardar:

- Transparencia.
- pH.
- Temperatura.
- Oxígeno disuelto.
- Fecha de medición.
- Estanque relacionado.
- Cualquier otro campo real existente.

No inventar columnas.

Si el registro de calidad del agua pertenece a otra funcionalidad o pantalla, no duplicar su implementación; documentar cómo debe integrarse.

Revisar `WaterQualityReference` para que los estados o alertas se calculen utilizando los rangos reales configurados en la base de datos.

---

## 5. Inventario

Revisar las entidades `Feed` y `Supply` y determinar las operaciones correspondientes a cada una.

No asumir que ambas tablas representan el mismo tipo de inventario.

### 5.1. Alimentación — Feed

Implementar o completar el registro de alimentos para peces.

Respetar los campos reales de la entidad, incluyendo cuando existan:

- Marca.
- Nombre del producto.
- Fase.
- Porcentaje de proteína.
- Tamaño del pellet.
- Stock en kilogramos.
- Stock mínimo en kilogramos.
- Estado.
- Granja relacionada.
- Usuario relacionado.

Los alimentos deben poder registrarse con los datos reales que espera la base de datos.

Diferenciar correctamente proteínas y pellets utilizando los campos existentes.

No inventar categorías ni clasificaciones que no estén respaldadas por el esquema.

### 5.2. Suministros — Supply

Implementar o completar el registro de suministros.

Respetar los campos existentes, como:

- Nombre.
- Categoría.
- Cantidad.
- Unidad.
- Stock mínimo.
- Estado.
- Granja relacionada.

Revisar los tipos de datos y las restricciones reales.

No utilizar los campos de `Feed` para registrar suministros ni viceversa.

### 5.3. Consulta y actualización

Implementar o completar:

- Consulta de alimentos.
- Consulta de suministros.
- Registro de alimentos.
- Registro de suministros.
- Actualización de registros cuando corresponda.
- Validación de stock.
- Consulta de disponibilidad.

Los registros deben estar asociados a las granjas del usuario autenticado.

### 5.4. Estado del inventario

Revisar la lógica existente para determinar cuándo un producto tiene stock disponible o bajo.

Utilizar las cantidades y los mínimos reales.

No calcular estados utilizando valores hardcodeados.

Respetar las reglas que ya utilice el backend y mantenerlas coherentes en todos los endpoints.

---

## 6. Formularios y endpoints

Revisar los endpoints existentes y garantizar que cada operación corresponda a su entidad.

El frontend necesita operaciones independientes para:

- Consultar ciclos.
- Registrar ciclos.
- Obtener estanques disponibles.
- Obtener especies disponibles.
- Finalizar ciclos.
- Consultar estanques.
- Registrar estanques.
- Obtener granjas disponibles.
- Consultar alimentos.
- Registrar alimentos.
- Consultar suministros.
- Registrar suministros.
- Actualizar los registros permitidos.

No es obligatorio crear un endpoint nuevo para cada operación si ya existe uno que cumple correctamente su función.

Cada endpoint debe:

- Tener el método HTTP apropiado.
- Utilizar DTOs adecuados.
- Validar los datos.
- Aplicar autenticación y autorización.
- Retornar respuestas coherentes.
- Manejar errores.
- Mantener los contratos consistentes.

---

## 7. Seguridad y propiedad de los datos

Toda operación debe respetar al usuario autenticado.

Verificar que:

- Un usuario solo consulte sus propios registros.
- No pueda registrar ciclos en estanques ajenos.
- No pueda registrar estanques en granjas ajenas.
- No pueda registrar alimentos o suministros en granjas ajenas.
- No pueda finalizar ciclos de otros usuarios.
- No pueda modificar identificadores para acceder a información ajena.

No confiar en los identificadores enviados desde el frontend sin verificar sus relaciones.

Mantener el sistema de autenticación existente.

---

## 8. Pruebas

Realizar pruebas para verificar:

### Ciclos
- Crear un ciclo con estanque y especie existentes.
- Rechazar un estanque ajeno.
- Rechazar una especie inexistente.
- Crear un ciclo activo sin fecha de finalización.
- Consultar ciclos del usuario.
- Finalizar un ciclo.
- Rechazar la finalización de un ciclo ya finalizado.
- Verificar la persistencia de los cambios.

### Estanques
- Registrar un estanque.
- Consultar estanques del usuario.
- Actualizar un estanque.
- Rechazar una granja ajena.
- Validar campos obligatorios.
- Consultar calidad del agua cuando exista información.
- Manejar ausencia de mediciones.

### Inventario
- Registrar un alimento.
- Registrar un suministro.
- Consultar ambos tipos de inventario.
- Verificar proteínas y pellets.
- Verificar cantidades y mínimos.
- Comprobar los estados de stock.
- Rechazar relaciones con granjas ajenas.
- Comprobar persistencia.

---

## 9. Restricciones

- No modificar el frontend.
- No modificar módulos ajenos a ciclos, estanques e inventario.
- No inventar campos de base de datos.
- No utilizar datos hardcodeados.
- No duplicar endpoints.
- No crear relaciones que no existan.
- No eliminar información existente.
- No modificar el sistema de autenticación sin justificación.
- No cambiar reglas de negocio sin revisar su impacto.

## 10. Resultado esperado

El backend debe quedar preparado para que cada formulario del frontend pueda registrar y consultar información real.

Cada sección debe tener operaciones coherentes con su entidad y con las relaciones de la base de datos.

Al finalizar, entregar:

1. Arquitectura y archivos revisados.
2. Entidades y tablas identificadas.
3. Endpoints existentes reutilizados.
4. Endpoints nuevos o corregidos.
5. DTOs implementados.
6. Reglas de validación.
7. Lógica de finalización de ciclos.
8. Relaciones y filtros por usuario.
9. Pruebas realizadas.
10. Contrato exacto que deberá consumir el frontend.
11. Limitaciones o problemas pendientes.
