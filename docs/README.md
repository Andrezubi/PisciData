# Documentación y Especificación del Sistema PisciData

Bienvenido a la suite de documentación técnica y funcional de **PisciData**, desarrollada bajo el enfoque de **Spec-Driven Development (SDD)** asistido por IA.

Esta carpeta es la **fuente autoritativa de verdad** sobre el diseño, arquitectura, contratos de datos, requerimientos y estado real del proyecto.

---

## Índice General de Documentos

| Documento | Ubicación | Propósito y Contenido |
|---|---|---|
| **01. Visión General** | [`01-overview.md`](./01-overview.md) | Propósito del producto, actores verificables (`Admin`, `Technician`, `Worker`, `OwnerUser`), alcance e hipótesis operacionales. |
| **02. Requerimientos** | [`02-requirements/system-requirements.md`](./02-requirements/system-requirements.md) | Catálogo formal de reglas de negocio (`BR-*`), requerimientos funcionales (`REQ-*`) y matriz de trazabilidad hacia el código. |
| **03. Diseño Funcional** | [`03-functional-design/domain-model.md`](./03-functional-design/domain-model.md) | Diagrama ERD en Mermaid y diccionario de datos detallado de las 19 tablas relacionales en MySQL. |
| **04. Arquitectura** | [`04-architecture/system-architecture.md`](./04-architecture/system-architecture.md) | Diagrama C4 de contenedores, arquitectura en capas del backend, análisis de Inversión de Dependencias (DIP) y diagrama de secuencia de peticiones. |
| **05. Diseño Técnico** | [`05-technical-design/api-contracts.md`](./05-technical-design/api-contracts.md) | Especificación de los contratos JSON de los 45 endpoints implementados en el backend, esquemas DTO y códigos de respuesta HTTP. |
| **06. Decisiones Arquitectónicas** | [`06-decisions/`](./06-decisions) | Registro de Decisiones de Arquitectura (ADRs) que documentan el contexto, decisión, consecuencias y estado de cada elección técnica. |
| **07. Verificación y Pruebas** | [`07-verification.md`](./07-verification.md) | Diagnóstico de cobertura de pruebas (actualmente 0%), pirámide de testing y matriz de verificación hacia requerimientos. |
| **08. Estado Actual** | [`08-current-state.md`](./08-current-state.md) | Inventario auditable del código existente, clasificación de estados (`Implementado`, `Parcial`, `En desarrollo`) y registro de deuda técnica crítica. |
| **09. Guía de Ejecución Local** | [`09-setup-guide.md`](./09-setup-guide.md) | Instrucciones paso a paso para configurar MySQL, compilar y ejecutar Backend, Frontend móvil y Servidor MCP. |

---

## Registro de Decisiones Arquitectónicas (ADRs)

* [**ADR-001: Enfoque Database-First y Scaffold mediante Entity Framework Core**](./06-decisions/ADR-001-database-first-scaffold.md)
* [**ADR-002: Estilo Arquitectónico en Capas en Backend ASP.NET Core**](./06-decisions/ADR-002-layered-backend-architecture.md)
* [**ADR-003: Estrategia Heterogénea de Eliminación (Soft Delete vs Hard Delete)**](./06-decisions/ADR-003-selective-soft-delete.md)
* [**ADR-004: Frontend Móvil Multiplataforma con Kotlin Multiplatform y Compose**](./06-decisions/ADR-004-kotlin-multiplatform-mobile.md)
* [**ADR-005: Desacoplamiento de Asistencia de IA mediante Model Context Protocol (MCP)**](./06-decisions/ADR-005-model-context-protocol-service.md)

---

## Flujo de Trabajo con IA (Spec-Driven Development)

1. **Antes de implementar una nueva funcionalidad:** Consulta los requerimientos correspondientes en `02-requirements/` y los contratos en `05-technical-design/`.
2. **Si se requiere cambiar una regla de negocio o contrato de API:** Actualiza primero la especificación en la documentación y luego implementa el código.
3. **Si se toma una decisión técnica estructural:** Redacta un nuevo ADR en `06-decisions/` antes de realizar cambios arquitectónicos en la solución.
