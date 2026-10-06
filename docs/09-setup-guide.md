# 09. Guía de Configuración y Ejecución Local (Setup Guide)

**Última actualización:** 06 de octubre de 2026  
**Propósito:** Proveer instrucciones paso a paso, reproducibles y verificables para clonar, configurar y ejecutar los proyectos de PisciData en un entorno de desarrollo local.

---

## 1. Requisitos Previos del Sistema

Asegúrate de contar con las siguientes herramientas instaladas antes de comenzar:

| Herramienta | Versión Mínima Requerida | Uso en el Proyecto | Verificación en Terminal |
|---|---|---|---|
| **.NET SDK** | `10.0.100` o superior | Compilación y ejecución de Backend y MCP | `dotnet --version` |
| **MySQL Server** | `8.0` o superior | Base de datos relacional del backend | `mysql --version` |
| **JDK (Java)** | OpenJDK `17` o superior | Compilación de módulos Kotlin / Gradle | `java -version` |
| **Android Studio** | Ladybug / Koala o superior | Emulador y SDK de Android para KMP | `adb version` |
| **Git** | `2.40` o superior | Control de versiones | `git --version` |

---

## 2. Configuración de la Base de Datos (MySQL)

### 2.1. Crear la Base de Datos

Inicia sesión en tu servidor MySQL local y ejecuta el **Script DDL Oficial** proporcionado con el proyecto. Este script contiene la creación de la base de datos `PisciDataDB` y las 19 tablas oficiales con sus respectivas restricciones de integridad (`CHECK`, `UNIQUE`, `CASCADE`/`RESTRICT`).

> [!IMPORTANT]
> **Modelo Baseline y Migraciones:**  
> Actualmente, la fuente de verdad de la base de datos es el **DDL Oficial** (que configura la estructura, charset `utf8mb4_unicode_ci` y restricciones).
> 
> NO debes usar la generación del esquema mediante Entity Framework Core (`dotnet ef dbcontext script`), ya que el scaffold original generado carece de las configuraciones de cascada y restricciones avanzadas (checks/uniques) incluidas en el DDL oficial.
> 
> Ejecuta el archivo DDL directamente en tu cliente MySQL o por línea de comandos:
> ```bash
> mysql -u root -p < database_schema_oficial.sql
> ```

### 2.2. Configuración de Cadena de Conexión

Por defecto, [`appsettings.json`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/appsettings.json) apunta a:

```json
"ConnectionStrings": {
  "DefaultConnection": "server=localhost;port=3306;database=piscidatadb;user=root;password=1234"
}
```

Para evitar modificar archivos bajo control de versiones con tus credenciales personales, se recomienda utilizar **.NET User Secrets** en tu entorno local:

```bash
cd PisciDataBackend
dotnet user-secrets init
dotnet user-secrets set "ConnectionStrings:DefaultConnection" "server=localhost;port=3306;database=piscidatadb;user=TU_USUARIO;password=TU_PASSWORD"
cd ..
```

---

## 3. Ejecución del Backend (`PisciDataBackend`)

### 3.1. Restauración y Compilación

Desde la raíz del repositorio:

```bash
# Restaurar dependencias NuGet
dotnet restore PisciDataBackend/PisciDataBackend.csproj

# Compilar proyecto
dotnet build PisciDataBackend/PisciDataBackend.csproj
```

### 3.2. Iniciar el Servidor de Desarrollo

```bash
dotnet run --project PisciDataBackend/PisciDataBackend.csproj
```

La consola mostrará los puertos asignados por Kestrel (por defecto HTTP `5007` y HTTPS `7071` o dinámicos según `launchSettings.json`).

### 3.3. Verificación de Funcionamiento

* **Documento OpenAPI:** Abre en tu navegador o cliente HTTP:  
  `http://localhost:5007/openapi/v1.json`
* **Prueba de Endpoint de Granjas:**
  ```bash
  curl -i http://localhost:5007/api/Farms
  ```
  Debería responder `HTTP/1.1 200 OK` con un arreglo JSON `[]` si la base de datos está vacía.

---

## 4. Ejecución del Frontend Móvil (`PisciDataFrontend`)

> [!NOTE]
> Nota de estructura: El código fuente del frontend se encuentra en la ruta anidada `PisciDataFrontend/PisciDataFrontend/`.

### 4.1. Permisos del Wrapper de Gradle (Linux / macOS)

Si trabajas en Linux o macOS, asegúrate de que el wrapper tenga permisos de ejecución:

```bash
chmod +x PisciDataFrontend/PisciDataFrontend/gradlew
```

### 4.2. Compilación del Módulo Android

Para compilar el APK de depuración:

```bash
cd PisciDataFrontend/PisciDataFrontend
./gradlew assembleDebug
```

Para instalarlo en un emulador o dispositivo físico conectado por ADB:

```bash
./gradlew :androidApp:installDebug
```

---

## 5. Ejecución del Servidor MCP (`PisciDataMCP`)

El proyecto `PisciDataMCP` es una aplicación de consola en .NET 10 que implementa el protocolo Model Context Protocol sobre transporte `stdio`.

### 5.1. Ejecución Manual

```bash
dotnet run --project PisciDataMCP/PisciDataMCP.csproj
```

*(El proceso quedará a la espera de mensajes JSON-RPC por entrada estándar `stdin`).*

### 5.2. Configuración en un Cliente MCP (ej. VS Code Copilot o Claude Desktop)

Agrega la siguiente configuración en tu archivo `mcp.json` del cliente:

```json
{
  "servers": {
    "PisciDataMCP": {
      "type": "stdio",
      "command": "dotnet",
      "args": [
        "run",
        "--project",
        "/RUTA/ABSOLUTA/A/PisciData/PisciDataMCP/PisciDataMCP.csproj"
      ]
    }
  }
}
```

---

## 6. Resolución de Problemas Frecuentes (Troubleshooting)

### Error: `Unable to connect to any of the specified MySQL hosts`
* **Causa:** El servicio de MySQL no está corriendo o las credenciales en `appsettings.json` son incorrectas.
* **Solución:** Verifica que el demonio de MySQL esté activo (`sudo systemctl status mysql` o servicios de Windows) y que puedas ingresar con `mysql -u root -p`.

### Error: `The target framework net10.0 is not recognized`
* **Causa:** Tienes instalado .NET 8 o .NET 9 pero el proyecto requiere .NET 10.
* **Solución:** Instala el SDK de .NET 10 desde el portal oficial de Microsoft o verifica tus SDKs instalados con `dotnet --list-sdks`.

### Advertencia en `PiscidatadbContext.cs`: `#warning To protect potentially sensitive information...`
* **Causa:** El método `OnConfiguring` incluye un fallback con credenciales fijas generado por el scaffold de EF Core.
* **Solución:** No afecta la ejecución si la cadena está configurada en `appsettings.json` o User Secrets, pero está registrado como deuda técnica a resolver.
