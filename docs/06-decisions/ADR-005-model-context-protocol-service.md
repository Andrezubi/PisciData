# ADR-005: Desacoplamiento de Asistencia de IA mediante Model Context Protocol (MCP)

* **Estado:** Aceptado / En fase de plantilla inicial
* **Fecha:** 06 de octubre de 2026
* **Decisores:** Equipo PisciData (inferido de `f677bfe`)

---

## 1. Contexto

Se identificó la necesidad de integrar capacidades de Inteligencia Artificial para asistir al piscicultor (como se evidencia en las tablas de base de datos `aiconversation` y `aimessage`). 

Acoplar librerías o llamadas propietarias de APIs de LLM directamente dentro del backend de la API REST principal añade dependencias innecesarias y limita la interoperabilidad con diferentes agentes y clientes de IA.

## 2. Decisión

Implementar un componente desacoplado denominado **`PisciDataMCP`**, estructurado como un servidor independiente bajo el estándar abierto **Model Context Protocol (MCP)** en C# (.NET 10) utilizando el paquete oficial `ModelContextProtocol` versión `2.1.0`.

El servidor se comunica mediante transporte estándar de entrada/salida (`stdio`).

## 3. Consecuencias

### Positivas
* **Interoperabilidad:** El servidor MCP puede ser conectado de inmediato a herramientas de desarrollo y asistentes compatibles (Claude Desktop, GitHub Copilot en VS Code, Visual Studio, etc.).
* **Desacoplamiento:** La lógica y herramientas de IA quedan aisladas de la API REST transaccional principal.
* **Seguridad de datos:** El servidor MCP expone herramientas controladas mediante atributos `[McpServerTool]`.

### Negativas y Estado Actual
* Actualmente el proyecto contiene únicamente la herramienta de demostración [`RandomNumberTools.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataMCP/Tools/RandomNumberTools.cs) generada por la plantilla.
* Falta implementar herramientas de dominio acuícola reales (ej. consulta de biomasa, cálculo de raciones recomendadas, alertas de calidad de agua) y conectarlas a `PisciDataBackend` o `PiscidatadbContext`.
