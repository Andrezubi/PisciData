# Tasks.md — Implementación Backend del Perfil de Usuario (PisciData)

## Objetivo

Revisar e implementar el funcionamiento completo del perfil de usuario en el backend de PisciData.

Actualmente, la pantalla de perfil del frontend utiliza información hardcodeada. Se necesita que el perfil funcione con los datos reales del usuario autenticado, permitiendo consultar, editar y guardar correctamente la información en la base de datos.

El perfil debe tener un funcionamiento profesional, seguro y coherente con la arquitectura existente del proyecto.

## 1. Revisión inicial del proyecto

Antes de realizar modificaciones:

- Revisar la arquitectura completa del backend.
- Identificar la estructura de controladores, servicios, repositorios, DTOs, entidades y configuraciones.
- Revisar cómo se implementan actualmente el registro, inicio de sesión y autenticación.
- Identificar cómo se obtiene el usuario autenticado en los endpoints existentes.
- Revisar la configuración de Entity Framework Core y la conexión con la base de datos.
- Identificar la entidad y tabla correspondiente a los usuarios.
- Revisar todas las columnas existentes de la tabla de usuarios.
- Identificar qué campos se almacenan durante el registro y cuáles todavía no tienen implementación.
- Revisar si ya existe un servicio, controlador o endpoint relacionado con el perfil.
- Revisar si existen funcionalidades de actualización de usuarios y determinar si pueden reutilizarse.
- Revisar las validaciones existentes y mantener los estándares del proyecto.

No implementar funcionalidades duplicadas si ya existe una solución parcial que pueda completarse.

## 2. Revisión de la base de datos

Inspeccionar la estructura real de la tabla de usuarios.

Identificar:

- Nombre real de la tabla.
- Clave primaria.
- Nombre y apellido.
- Correo electrónico.
- Nombre de usuario, si existe.
- Número de teléfono.
- Dirección.
- Fecha de nacimiento.
- Imagen o fotografía de perfil, si existe.
- Estado activo o inactivo.
- Fecha de creación.
- Fecha de actualización.
- Identificador del usuario.
- Rol o relación con roles.
- Cualquier otro campo existente en la tabla.

Utilizar los nombres y tipos de datos reales de la base de datos.

No inventar columnas, propiedades, relaciones ni valores.

Si existen campos adicionales en la tabla de usuarios, revisar si corresponden al perfil y deben exponerse mediante la API.

No incluir contraseñas, hashes, tokens, claves de autenticación ni información sensible en las respuestas del perfil.

## 3. Consulta del perfil del usuario autenticado

Implementar o completar un endpoint que permita obtener el perfil del usuario actualmente autenticado.

Requisitos:

- El endpoint debe requerir autenticación.
- Debe identificar al usuario mediante los claims del token.
- Revisar el mecanismo de identificación utilizado actualmente en el proyecto.
- Reutilizar el mecanismo de autenticación existente.
- Consultar únicamente al usuario autenticado.
- No permitir que un usuario consulte el perfil privado de otro usuario modificando un identificador.
- Obtener los datos directamente desde la base de datos.
- No utilizar datos hardcodeados.
- Retornar los campos reales del perfil que correspondan.
- Mantener los nombres de propiedades coherentes con los DTOs existentes.

Si no se puede identificar al usuario autenticado, devolver una respuesta de autorización adecuada.

Si el usuario no existe en la base de datos, devolver una respuesta apropiada sin generar errores inesperados.

## 4. Actualización del perfil

Implementar o completar la funcionalidad para modificar los datos del perfil.

El usuario autenticado debe poder editar los campos personales permitidos que existan en la base de datos.

Requisitos:

- Utilizar un DTO específico para actualizar el perfil.
- No permitir que el frontend modifique directamente el identificador del usuario.
- No permitir cambiar el propietario de la cuenta.
- No permitir modificar roles desde el perfil.
- No permitir modificar el estado de la cuenta desde el perfil.
- No permitir modificar contraseñas mediante el endpoint general del perfil.
- No permitir que el usuario actualice información perteneciente a otra cuenta.
- Validar los datos recibidos.
- Respetar los tipos de datos y restricciones de la base de datos.
- Actualizar únicamente los campos permitidos.
- Guardar los cambios mediante el servicio y repositorio correspondientes.
- Persistir los cambios correctamente en la base de datos.
- Retornar el perfil actualizado después de guardar.

Si un campo no debe modificarse desde el perfil, mantenerlo fuera del DTO de actualización.

No modificar campos que el usuario no haya enviado o que no tenga permiso de editar.

## 5. Validaciones

Implementar las validaciones necesarias para los campos reales del perfil.

Revisar:

- Nombre y apellido.
- Correo electrónico.
- Número de teléfono.
- Dirección.
- Fecha de nacimiento.
- Nombre de usuario, si existe.
- Longitudes máximas.
- Campos obligatorios.
- Formatos de datos.
- Restricciones de unicidad.
- Valores permitidos según las entidades existentes.

Evitar aceptar valores inválidos que posteriormente provoquen errores de base de datos.

Si el correo electrónico o nombre de usuario deben ser únicos, comprobar que no pertenezcan a otro usuario.

No cambiar las reglas de autenticación existentes sin justificación.

## 6. Seguridad

Verificar que:

- Todos los endpoints privados del perfil requieren autenticación.
- El usuario solo pueda consultar y modificar su propia información.
- No se confíe en el identificador enviado por el frontend para determinar el usuario.
- No se expongan contraseñas ni información sensible.
- No se permita modificar roles desde el perfil.
- No se permita modificar el estado de la cuenta desde el perfil.
- No se permita modificar relaciones de propiedad o permisos.
- Los errores no expongan información interna de la base de datos.

Mantener el mecanismo de autorización utilizado por el proyecto.

## 7. Servicio y repositorio

Revisar la implementación actual.

Si ya existe un servicio de usuarios:

- Completarlo.
- Reutilizar sus métodos.
- Evitar duplicar consultas.
- Mantener la lógica de negocio fuera del controlador.

Si no existe una implementación adecuada:

- Crear los métodos necesarios en el servicio.
- Crear o completar los métodos del repositorio.
- Utilizar las consultas de Entity Framework Core siguiendo los patrones existentes.
- Mantener las responsabilidades separadas.
- Evitar acceder directamente a la base de datos desde el controlador.

## 8. DTOs y respuestas

Crear o modificar los DTOs necesarios para:

- Consultar el perfil.
- Actualizar el perfil.
- Retornar el perfil actualizado.

Los DTOs deben:

- Contener únicamente los campos correspondientes al perfil.
- Utilizar los nombres reales de las propiedades.
- Respetar los tipos de datos existentes.
- Evitar exponer información sensible.
- Mantener compatibilidad con la respuesta que utilizará el frontend.

Revisar el contrato de respuesta antes de finalizar.

## 9. Persistencia

Verificar que las modificaciones:

- Se guarden correctamente en la base de datos.
- Utilicen el contexto de Entity Framework Core existente.
- Actualicen la fecha de modificación si el proyecto utiliza ese campo.
- No sobrescriban información que no se haya enviado.
- No generen registros duplicados.
- No modifiquen usuarios diferentes al autenticado.

No crear migraciones ni modificar el esquema de la base de datos si no es estrictamente necesario.

Si se detecta que falta una columna indispensable, documentar el problema y explicar la modificación necesaria antes de alterar el esquema.

## 10. Pruebas

Realizar pruebas para verificar:

- Consulta del perfil con autenticación válida.
- Consulta sin autenticación.
- Usuario autenticado inexistente.
- Actualización correcta de datos.
- Persistencia de los cambios.
- Actualización con campos inválidos.
- Correo duplicado, si corresponde.
- Intento de modificar el perfil de otro usuario.
- Intento de modificar roles.
- Intento de modificar el identificador.
- Respuesta del perfil después de guardar.

Verificar que los cambios permanezcan en la base de datos después de volver a consultar el perfil.

## 11. Documentación

Documentar:

- Endpoint para consultar el perfil.
- Endpoint para actualizar el perfil.
- Método HTTP.
- Requisitos de autenticación.
- Campos de consulta.
- Campos modificables.
- Validaciones.
- Ejemplo de respuesta.
- Ejemplo de actualización.
- Códigos de respuesta.
- Cualquier campo de la base de datos que no pueda exponerse y el motivo.

## 12. Restricciones

- No modificar funcionalidades ajenas al perfil.
- No modificar el frontend.
- No implementar información hardcodeada.
- No crear usuarios de prueba innecesarios.
- No cambiar el sistema de autenticación existente sin justificación.
- No modificar roles desde el perfil.
- No exponer contraseñas ni información sensible.
- No inventar campos que no existan en la base de datos.
- No duplicar servicios o endpoints existentes.

## Resultado esperado

El backend debe permitir que el usuario autenticado consulte su perfil, modifique los campos personales permitidos y guarde los cambios correctamente en la base de datos.

La implementación debe estar integrada con la arquitectura existente y preparada para que el frontend consuma los datos reales.

Al finalizar, entregar:

1. Archivos revisados.
2. Archivos modificados o creados.
3. Campos reales del perfil identificados en la base de datos.
4. Endpoints implementados o corregidos.
5. DTOs utilizados.
6. Validaciones implementadas.
7. Pruebas realizadas.
8. Resultado de las pruebas de persistencia.
9. Instrucciones para consumir los endpoints desde el frontend.
10. Problemas encontrados y cualquier limitación pendiente.
