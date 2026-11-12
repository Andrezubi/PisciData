# Tasks.md — Implementación Frontend del Perfil de Usuario (PisciData)

## Objetivo

Revisar y modificar completamente la pantalla de perfil de usuario de PisciData para que deje de utilizar información hardcodeada y funcione con los datos reales del usuario autenticado.

El perfil debe permitir consultar, visualizar, editar y guardar los datos personales del usuario utilizando los endpoints reales del backend.

La pantalla debe tener un diseño profesional, moderno, organizado y coherente con el sistema actual.

## 1. Revisión inicial

Antes de modificar el frontend:

- Revisar la arquitectura completa del frontend.
- Identificar la pantalla actual del perfil.
- Revisar los componentes utilizados.
- Revisar los servicios de autenticación.
- Revisar el manejo del token.
- Revisar cómo se obtiene el usuario autenticado.
- Revisar el ApiClient y la configuración de las solicitudes HTTP.
- Revisar los DTOs y modelos existentes.
- Revisar cómo se manejan los estados de carga y errores.
- Revisar el sistema de navegación.
- Revisar el diseño actual de la aplicación.
- Identificar todos los datos hardcodeados de la pantalla.

No modificar funcionalidades ajenas al perfil.

## 2. Eliminar información hardcodeada

Eliminar todos los datos de ejemplo utilizados actualmente en la pantalla.

No utilizar:

- Nombres inventados.
- Correos de ejemplo.
- Teléfonos ficticios.
- Direcciones inventadas.
- Imágenes de perfil predeterminadas que representen información personal falsa.
- Roles hardcodeados.
- Fechas de nacimiento ficticias.
- Datos de usuario almacenados únicamente en variables locales para simular el perfil.

Todos los datos deben provenir del usuario autenticado y del backend.

Si un campo no tiene información, mostrar un estado vacío apropiado.

No reemplazar valores inexistentes por datos inventados.

## 3. Conexión con el backend

Revisar los endpoints de perfil implementados en el backend.

Utilizar los endpoints reales para:

- Obtener el perfil del usuario autenticado.
- Actualizar los datos personales.
- Obtener la respuesta actualizada después de guardar.

Requisitos:

- Utilizar el ApiClient existente.
- Utilizar el mecanismo de autenticación existente.
- Enviar el token cuando corresponda.
- No enviar un identificador de usuario para determinar qué perfil modificar, salvo que el contrato real del backend lo requiera por otro motivo.
- No crear URLs duplicadas o hardcodeadas si existe una configuración centralizada.
- Mantener los nombres de propiedades compatibles con los DTOs del backend.
- Revisar los tipos de datos de cada campo.
- Manejar correctamente las respuestas HTTP.

No inventar endpoints.

Si el backend todavía no implementa alguna funcionalidad necesaria, documentar el endpoint faltante y coordinar el contrato con backend.

## 4. Rediseño de la pantalla de perfil

Modificar toda la pantalla de perfil para que tenga una presentación profesional.

Organizar la información en secciones claras.

### Información personal

Mostrar los campos reales disponibles en la base de datos, como:

- Nombre.
- Apellido.
- Nombre de usuario, si existe.
- Correo electrónico.
- Número de teléfono.
- Dirección.
- Fecha de nacimiento.
- Fotografía de perfil, si existe.
- Fecha de registro.
- Fecha de actualización.

Revisar la tabla real de usuarios y mostrar todos los campos de perfil que correspondan.

No mostrar información sensible.

### Información de cuenta

Mostrar únicamente los campos de cuenta que sean apropiados para el usuario.

Por ejemplo:

- Estado de la cuenta, si corresponde.
- Fecha de creación.
- Información de identificación del usuario.
- Rol, únicamente como información de lectura si el backend lo permite.

No permitir modificar roles, permisos, identificadores ni estado de la cuenta desde el perfil.

### Diseño

La pantalla debe:

- Tener una estructura visual clara.
- Utilizar componentes reutilizables.
- Mantener coherencia con el diseño de PisciData.
- Utilizar correctamente los colores y estilos existentes.
- Adaptarse a diferentes tamaños de pantalla.
- Evitar formularios desordenados.
- Mostrar claramente qué campos pueden editarse.
- Diferenciar los datos de lectura de los campos editables.
- Mantener una navegación intuitiva.

No realizar un rediseño general de toda la aplicación.

## 5. Edición del perfil

Implementar un modo de edición del perfil.

El usuario debe poder:

- Consultar sus datos actuales.
- Activar la edición.
- Modificar los campos permitidos.
- Cancelar los cambios.
- Guardar las modificaciones.
- Visualizar los datos actualizados después de guardar.

Los campos editables deben corresponder exactamente a los campos permitidos por el backend.

No permitir editar:

- Identificador del usuario.
- Rol.
- Permisos.
- Estado de la cuenta.
- Contraseña desde el formulario general del perfil.
- Relaciones de propiedad.
- Campos internos de autenticación.

Si el backend permite modificar el correo o nombre de usuario, respetar las validaciones correspondientes.

## 6. Formularios y validaciones

Implementar formularios adecuados para los campos reales.

Validar:

- Campos obligatorios.
- Nombre y apellido.
- Correo electrónico.
- Número de teléfono.
- Dirección.
- Fecha de nacimiento.
- Longitudes máximas.
- Formatos de datos.

Utilizar los tipos de entrada adecuados.

Mostrar mensajes claros cuando:

- Un campo sea obligatorio.
- El correo tenga un formato incorrecto.
- El teléfono no sea válido.
- La fecha no sea válida.
- El backend rechace una actualización.

No permitir enviar formularios inválidos.

Mantener los valores actuales cuando el usuario cancela la edición.

## 7. Guardado de cambios

Cuando el usuario guarde el perfil:

1. Validar los campos.
2. Construir el DTO esperado por el backend.
3. Enviar la solicitud autenticada.
4. Esperar la respuesta.
5. Actualizar la información mostrada.
6. Mostrar una confirmación de éxito.
7. Mantener los datos sincronizados con el backend.

No mostrar un mensaje de éxito antes de confirmar que la operación se completó correctamente.

Si la solicitud falla:

- Mantener los datos anteriores.
- Mostrar un mensaje claro.
- Permitir volver a intentar.
- No modificar visualmente el perfil como si se hubiera guardado.

No utilizar únicamente estado local para simular la persistencia.

## 8. Estados de la pantalla

Implementar correctamente:

### Cargando

Mostrar un indicador mientras se consulta el perfil.

### Perfil cargado

Mostrar los datos reales obtenidos del backend.

### Sin información

Mostrar un estado vacío para los campos que no tengan valores.

### Error

Mostrar un mensaje cuando no se pueda obtener el perfil.

### Guardando

Deshabilitar temporalmente el botón de guardar mientras se procesa la solicitud.

### Guardado correctamente

Mostrar los datos actualizados y una confirmación.

### Sesión inválida

Utilizar el mecanismo de autenticación existente para manejar token inválido o sesión expirada.

## 9. Sincronización con backend

Revisar el contrato real del backend.

Asegurar que:

- Los nombres de los campos coincidan.
- Los tipos de datos coincidan.
- Los campos opcionales se manejen correctamente.
- Los campos no editables no se envíen.
- La actualización no sobrescriba información que no corresponda.
- El perfil se consulte nuevamente cuando sea necesario.
- Los datos actualizados permanezcan después de cerrar y volver a abrir la pantalla.

No crear modelos duplicados si ya existen DTOs adecuados.

## 10. Pruebas

Verificar:

- Consulta del perfil del usuario autenticado.
- Visualización de datos reales.
- Perfil con campos vacíos.
- Activación del modo edición.
- Modificación de datos.
- Cancelación de cambios.
- Guardado correcto.
- Actualización visual después de guardar.
- Error de conexión.
- Token inválido.
- Respuesta de validación del backend.
- Persistencia después de volver a consultar el perfil.

Confirmar que el perfil no muestre información perteneciente a otro usuario.

## 11. Restricciones

- No modificar el backend.
- No modificar la base de datos.
- No modificar el sistema de autenticación.
- No modificar pantallas ajenas al perfil.
- No utilizar información hardcodeada.
- No inventar endpoints.
- No inventar campos.
- No permitir cambiar roles o permisos.
- No guardar cambios únicamente en memoria.
- No mostrar información sensible.

## Resultado esperado

La pantalla de perfil debe funcionar completamente con los datos reales del usuario autenticado.

El usuario debe poder consultar su información, editar los campos permitidos y guardar los cambios correctamente en la base de datos mediante el backend.

Al finalizar, entregar:

1. Archivos revisados.
2. Archivos modificados o creados.
3. Campos reales utilizados.
4. Endpoints consumidos.
5. DTOs utilizados.
6. Cambios realizados en el diseño.
7. Validaciones implementadas.
8. Pruebas realizadas.
9. Resultado de las pruebas de actualización y persistencia.
10. Problemas encontrados o endpoints pendientes del backend.
