# 09. Guía de Configuración y Ejecución Local (Setup Guide)

**Última actualización:** 03 de noviembre de 2026
**Propósito:** Proveer instrucciones paso a paso, reproducibles y verificables para clonar, configurar y ejecutar los proyectos de PisciData en un entorno de desarrollo local.

---

## 1. Requisitos Previos del Sistema

Asegúrate de contar con las siguientes herramientas instaladas antes de comenzar:

| Herramienta | Versión Mínima Requerida | Uso en el Proyecto | Verificación en Terminal |
|---|---|---|---|
| **.NET SDK** | `10.0.100` o superior | Compilación y ejecución de Backend y MCP | `dotnet --version` |
| **MySQL Server** | `8.0` o superior | Base de datos relacional del backend | `mysql --version` |
| **Ollama** | Versión actual | Inferencia local del chat móvil | `ollama --version` |
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

### 3.1. Iniciar Ollama

El chat móvil usa Ollama a través del backend. El backend espera Ollama en `http://localhost:11434` y el modelo `piscidata-ai` (ver `PisciDataBackend/appsettings.json`). Si Ollama ya está activo como servicio, no inicies una segunda instancia.

```bash
# Si no está activo como servicio, mantener esta terminal abierta
ollama serve
```

En otra terminal, crea el modelo local a partir de `Modelfile`:

```bash
ollama pull qwen3.5:4b
ollama create piscidata-ai -f Modelfile
ollama list
```

### 3.2. Restauración y compilación

Desde la raíz del repositorio:

```bash
# Restaurar dependencias NuGet
dotnet restore PisciDataBackend/PisciDataBackend.csproj

# Compilar proyecto
dotnet build PisciDataBackend/PisciDataBackend.csproj
```

### 3.3. Iniciar el Servidor de Desarrollo

```bash
dotnet run --project PisciDataBackend/PisciDataBackend.csproj
```

La consola mostrará los puertos asignados por Kestrel (por defecto HTTP `5007` y HTTPS `7071` o dinámicos según `launchSettings.json`).

### 3.4. Verificación de Funcionamiento

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

La URL base del cliente está centralizada en `shared/src/commonMain/kotlin/ucb/piscidata/data/remote/ApiEndpoints.kt`. Para un teléfono conectado por USB, conservar `127.0.0.1:5007` y crear el túnel ADB:

```bash
adb reverse tcp:5007 tcp:5007
adb reverse --list
```

El backend debe estar ejecutándose en la laptop. Al desconectar el USB se pierde el túnel; para usar el teléfono por Wi-Fi hay que emparejar ADB inalámbrico o configurar una dirección LAN accesible para el teléfono. El emulador Android usa `10.0.2.2` para llegar al host.

Para probar el chat móvil, registra una cuenta con contraseña de al menos 6 caracteres y un rol aceptado (`Admin`, `Technician` o `Worker`), luego inicia sesión y envía un mensaje. El registro devuelve al login para obtener el JWT requerido por el chat. La inferencia local puede tardar; el backend y el chat permiten hasta 6 minutos. El chat responde con Ollama y todavía no usa el MCP para consultar granjas.

---

## 5. Ejecución del Servidor MCP (`PisciDataMCP`)

El proyecto `PisciDataMCP` es una aplicación de consola en .NET 10 que implementa el protocolo Model Context Protocol sobre transporte `stdio`.

### 5.1. Ejecución Manual

```bash
dotnet run --project PisciDataMCP/PisciDataMCP.csproj
```

*(El proceso quedará a la espera de mensajes JSON-RPC por entrada estándar `stdin`).*

### 5.2. Chat web de prueba con MCP

`PisciDataChat` es un orquestador separado del chat móvil. Conecta un servidor compatible con API OpenAI, el servidor MCP por stdio y el backend REST. Para probarlo con el modelo GGUF local y un backend simulado, configura la ruta local del modelo (no se guarda en el repositorio):

```bash
export QWEN_GGUF="/ruta/local/al/modelo.gguf"
scripts/start-all.sh --mock
```

La interfaz se sirve en `http://localhost:5050`. El script requiere `llama-server`, el archivo configurado en `QWEN_GGUF`, .NET 10 y dependencias NuGet disponibles. `--mock` usa datos simulados de solo lectura; este flujo no es el chat móvil.

Para detener los servicios iniciados por el script:

```bash
scripts/start-all.sh --stop
```

### 5.3. Configuración en un Cliente MCP (ej. VS Code Copilot o Claude Desktop)

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
