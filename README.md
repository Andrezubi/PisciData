# PisciData

Sistema de gestión y monitoreo integral para producción acuícola (piscigranjas).

El proyecto integra una API ASP.NET Core, una aplicación móvil Kotlin Multiplatform (KMP), un servidor de herramientas MCP y un orquestador web de prueba para el asistente.

---

## 1. Topología del Proyecto

El repositorio está estructurado en tres componentes principales:

```text
PisciData/
├── PisciDataBackend/        # API REST central (ASP.NET Core .NET 10 + MySQL)
├── PisciDataFrontend/       # Aplicación móvil Android / iOS (Kotlin Multiplatform + Compose)
├── PisciDataMCP/            # Servidor Model Context Protocol (.NET 10)
├── PisciDataChat/            # Orquestador web de prueba (Ollama + MCP + API)
├── docs/                    # Documentación formal y especificaciones (Spec-Driven Development)
└── PisciData.slnx           # Solución de desarrollo para backend y servidor MCP
```

### Componentes y Tecnologías

| Componente | Directorio | Tecnología | Estado de Implementación |
|---|---|---|---|
| **Backend API** | [`PisciDataBackend/`](./PisciDataBackend) | .NET 10 (`net10.0`), ASP.NET Core, Entity Framework Core (`Microting.EntityFrameworkCore.MySql`) | **Parcialmente implementado** (9 entidades con CRUD completo, 10 entidades en modelo de datos pendientes) |
| **Frontend Móvil** | [`PisciDataFrontend/`](./PisciDataFrontend/PisciDataFrontend) | Kotlin 2.4.20, Compose Multiplatform 1.12.1, Android SDK 37 | **Parcial** (registro/login, consultas de granjas y chat conectados al backend) |
| **Servidor MCP** | [`PisciDataMCP/`](./PisciDataMCP) | .NET 10 (`net10.0`), `ModelContextProtocol 2.1.0` | **Implementado** (10 herramientas de dominio, cliente API REST tipado, transporte stdio) |
| **Chat web de prueba** | [`PisciDataChat/`](./PisciDataChat) | ASP.NET Core .NET 10, Ollama API, MCP stdio | **Implementado** como ruta independiente para probar consultas con herramientas |

---

## 2. Estado Real del Sistema (Resumen)

El desarrollo del sistema se encuentra en fase activa de construcción del backend:

* **Base de datos:** Modelo relacional completo de 19 entidades en MySQL configurado mediante Entity Framework Core.
* **API REST:** 9 controladores REST funcionales con validaciones de datos (Granjas, Usuarios, Estanques, Ciclos, Alimentos, Alimentación, Suministros, Biometrías y Muestras).
* **Módulos pendientes en API:** Calidad del agua, mortalidad, cosechas, especies, tareas, tablas de alimentación y movimientos de inventario existen en el modelo de datos pero aún no están expuestos en la API.
* **Seguridad y Autenticación:** El backend emite JWT y guarda contraseñas con BCrypt. La clave JWT y la cadena de conexión de `appsettings.json` son valores de desarrollo y deben sustituirse antes de desplegar.
* **Chat móvil:** verificado manualmente en un teléfono Android el 03 de noviembre de 2026. La app envía mensajes al backend, que consulta Ollama local y devuelve la respuesta. Este flujo todavía no usa MCP ni consulta datos de granjas con herramientas.
* **Chat web + MCP:** el flujo opcional de `PisciDataChat` conecta Ollama con las herramientas MCP y el backend; no es el flujo usado por la aplicación móvil.

Para el detalle exhaustivo del inventario de código, consulta [`docs/08-current-state.md`](./docs/08-current-state.md).

---

## 3. Inicio Rápido (Local Development)

### Requisitos Previos

* [.NET 10 SDK](https://dotnet.microsoft.com/)
* [MySQL Server 8.0+](https://dev.mysql.com/)
* [JDK 17+](https://adoptium.net/) y Android Studio (para el módulo frontend)

### Ejecución del Backend

1. Crear la base de datos `piscidatadb` en MySQL.
2. Configurar la cadena de conexión en [`PisciDataBackend/appsettings.json`](./PisciDataBackend/appsettings.json):
   ```json
   "ConnectionStrings": {
     "DefaultConnection": "server=localhost;port=3306;database=piscidatadb;user=TU_USUARIO;password=TU_PASSWORD"
   }
   ```
3. Iniciar Ollama y crear el modelo local usado por el backend (omite `ollama serve` si ya corre como servicio):
   ```bash
   ollama serve
   # En otra terminal, si todavía no existe el modelo base:
   ollama pull qwen3.5:4b
   ollama create piscidata-ai -f Modelfile
   ```
   El backend usa `http://localhost:11434` y el modelo `piscidata-ai`, configurados en `PisciDataBackend/appsettings.json`.
4. Ejecutar el proyecto:
   ```bash
   dotnet run --project PisciDataBackend/PisciDataBackend.csproj
   ```
5. Documento OpenAPI generado disponible en: `http://localhost:5007/openapi/v1.json` (o puerto configurado por Kestrel).

Para la guía detallada de configuración paso a paso y resolución de problemas, consulta [`docs/09-setup-guide.md`](./docs/09-setup-guide.md).

---

## 4. Índice de Documentación y Especificación

El proyecto sigue una metodología de documentación orientada a especificación (*Spec-Driven Development*):

* [**08. Estado Actual del Sistema** (`docs/08-current-state.md`)](./docs/08-current-state.md): Matriz de trazabilidad de componentes, entidades implementadas vs huérfanas, controladores y deuda técnica.
* [**09. Guía de Configuración Local** (`docs/09-setup-guide.md`)](./docs/09-setup-guide.md): Instrucciones completas para levantar base de datos, backend, frontend y servidor MCP.

> **Nota sobre MCP:** El chat de la app móvil se conecta directamente al backend y Ollama. El orquestador `PisciDataChat` es una ruta de prueba web separada que conecta Ollama, MCP y el backend; todavía no está integrado en el chat móvil.
* *(En construcción)* Próximos documentos de arquitectura, contratos de API, especificación de requerimientos y decisiones arquitectónicas (ADRs).
