# Implementar correctamente el inicio de sesión

Quiero que revises y hagas funcionar correctamente la pantalla de **Inicio de Sesión** de todo el sistema.

La implementación debe utilizar una autenticación real y no debe existir ningún usuario, contraseña, token, rol ni dato de autenticación colocado por defecto.

## 1. Analizar antes de modificar

Primero revisa toda la implementación actual del inicio de sesión, tanto en frontend como en backend.

Debes revisar:

- Pantalla de Login.
- Componentes utilizados por el Login.
- Navegación.
- Servicios del frontend.
- Modelos.
- DTOs.
- Repositories.
- Services del backend.
- Controllers.
- Entidades de usuario.
- Entidades de roles.
- Base de datos.
- `BaseDatos.md`.
- Configuración de autenticación.
- JWT, si existe.
- Claims, si existen.
- Middleware de autenticación.
- Autorización.
- Manejo de sesión.
- Redirección después del Login.

No asumas cómo funciona el sistema. Lee primero el código existente.

## 2. Revisar BaseDatos.md

Lee `BaseDatos.md` y determina exactamente cómo se almacenan:

- Usuarios.
- Contraseñas o credenciales.
- Roles.
- Relaciones entre usuarios y roles.
- Estados del usuario.
- Cualquier otro dato necesario para autenticación.

No inventes campos ni relaciones.

La autenticación debe utilizar la estructura real de la base de datos.

## 3. Eliminar datos por defecto

El Login NO debe tener datos prellenados.

No debe existir:

- Usuario por defecto.
- Correo por defecto.
- Contraseña por defecto.
- Token por defecto.
- Rol seleccionado por defecto.
- Usuario hardcodeado.
- Contraseña hardcodeada.
- Credenciales dentro del código.
- Login automático.
- Sesión simulada.
- Usuario ficticio para pruebas dentro de la aplicación.

Por ejemplo, no quiero cosas como:

```kotlin
username = "admin"
password = "admin"