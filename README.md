# PisciData

Sistema de gestión y monitoreo integral para producción acuícola (piscigranjas).

El proyecto integra un backend central en ASP.NET Core, una aplicación cliente móvil desarrollada en Kotlin Multiplatform (KMP) y un servidor de herramientas de asistencia basado en Model Context Protocol (MCP).

---

## 1. Topología del Proyecto

El repositorio está estructurado en tres componentes principales:

```text
PisciData/
├── PisciDataBackend/        # API REST central (ASP.NET Core .NET 10 + MySQL)
├── PisciDataFrontend/       # Aplicación móvil Android / iOS (Kotlin Multiplatform + Compose)
├── PisciDataMCP/            # Servidor Model Context Protocol (.NET 10)
├── docs/                    # Documentación formal y especificaciones (Spec-Driven Development)
└── PisciData.slnx           # Solución de desarrollo para backend y servidor MCP
```

### Componentes y Tecnologías

| Componente | Directorio | Tecnología | Estado de Implementación |
|---|---|---|---|
| **Backend API** | [`PisciDataBackend/`](./PisciDataBackend) | .NET 10 (`net10.0`), ASP.NET Core, Entity Framework Core (`Microting.EntityFrameworkCore.MySql`) | **Parcialmente implementado** (9 entidades con CRUD completo, 10 entidades en modelo de datos pendientes) |
| **Frontend Móvil** | [`PisciDataFrontend/`](./PisciDataFrontend/PisciDataFrontend) | Kotlin 2.4.20, Compose Multiplatform 1.12.1, Android SDK 37 | **Plantilla inicial** (Esqueleto generado por wizard, sin consumo de API ni pantallas de negocio) |
| **Servidor MCP** | [`PisciDataMCP/`](./PisciDataMCP) | .NET 10 (`net10.0`), `ModelContextProtocol 2.1.0` | **Implementado** (10 herramientas de dominio, cliente API REST tipado, transporte stdio) |

---

## 2. Estado Real del Sistema (Resumen)

El desarrollo del sistema se encuentra en fase activa de construcción del backend:

* **Base de datos:** Modelo relacional completo de 19 entidades en MySQL configurado mediante Entity Framework Core.
* **API REST:** 9 controladores REST funcionales con validaciones de datos (Granjas, Usuarios, Estanques, Ciclos, Alimentos, Alimentación, Suministros, Biometrías y Muestras).
* **Módulos pendientes en API:** Calidad del agua, mortalidad, cosechas, especies, tareas, tablas de alimentación y movimientos de inventario existen en el modelo de datos pero aún no están expuestos en la API.
* **Seguridad y Autenticación:** Actualmente no hay sistema de autenticación activo (JWT/cookies) y las contraseñas se almacenan en texto plano en el campo `PasswordHash` (registrado como deuda técnica crítica).

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
3. Ejecutar el proyecto:
   ```bash
   dotnet run --project PisciDataBackend/PisciDataBackend.csproj
   ```
4. Documento OpenAPI generado disponible en: `http://localhost:5007/openapi/v1.json` (o puerto configurado por Kestrel).

Para la guía detallada de configuración paso a paso y resolución de problemas, consulta [`docs/09-setup-guide.md`](./docs/09-setup-guide.md).

---

## 4. Índice de Documentación y Especificación

El proyecto sigue una metodología de documentación orientada a especificación (*Spec-Driven Development*):

* [**08. Estado Actual del Sistema** (`docs/08-current-state.md`)](./docs/08-current-state.md): Matriz de trazabilidad de componentes, entidades implementadas vs huérfanas, controladores y deuda técnica.
* [**09. Guía de Configuración Local** (`docs/09-setup-guide.md`)](./docs/09-setup-guide.md): Instrucciones completas para levantar base de datos, backend, frontend y servidor MCP.
* *(En construcción)* Próximos documentos de arquitectura, contratos de API, especificación de requerimientos y decisiones arquitectónicas (ADRs).
