# ADR-005: Desacoplamiento de Asistencia de IA mediante Model Context Protocol (MCP)

* **Estado:** Aceptado e Implementado
* **Fecha:** 06 de octubre de 2026 (Actualizado: 08 de octubre de 2026)
* **Decisores:** Equipo  (implementación MCP)

---

## 1. Contexto

Se identificó la necesidad de integrar capacidades de Inteligencia Artificial para asistir al piscicultor en la toma de decisiones, análisis biométrico y cálculo de alimentación, sin acoplar dependencias pesadas de IA dentro del backend transaccional (`PisciDataBackend`).

Existía el riesgo de permitir que el modelo de IA interactuara directamente con la base de datos MySQL, lo cual podría vulnerar validaciones de negocio, omitir el borrado lógico (`IsActive`) o provocar corrupción de datos ante alucinaciones.

## 2. Decisión

1. Implementar un servidor independiente denominado **`PisciDataMCP`**, desarrollado sobre .NET 10 (`net10.0`) y el SDK oficial `ModelContextProtocol` v2.1.0, comunicándose mediante transporte estándar de entrada/salida (`stdio`).
2. **Patrón Gateway/Sandbox:** El servidor MCP no accede a la base de datos MySQL directamente; consume la API REST de `PisciDataBackend` a través de un cliente HTTP tipado (`PisciDataApiClient`) configurado mediante `appsettings.json`.
3. **Canalización estricta de Streams:** Se redirigen todos los registros de logging a `stderr` (`LogToStandardErrorThreshold = LogLevel.Trace`), preservando `stdout` de forma exclusiva para los mensajes del protocolo JSON-RPC 2.0.
4. **Catálogo de Herramientas de Dominio:** Exponer herramientas atómicas organizadas por dominio acuícola mediante atributos `[McpServerTool]` y `[Description]`:
   * `FarmTools`: Consulta de instalaciones y granjas activas.
   * `PondTools`: Dimensiones geométricas y capacidad física de estanques.
   * `ProductionCycleTools`: Lotes de siembra y parámetros biológicos iniciales.
   * `BiometricTools`: Muestreos de crecimiento real, peso promedio y biomasa.
   * `FeedingTools`: Auditoría de consumo alimenticio y cálculo de FCR.

## 3. Consecuencias

### Positivas
* **Seguridad e Integridad de Datos:** Toda consulta o escritura pasa por las reglas de validación (FluentValidation) y ciclo de vida de la API REST.
* **Interoperabilidad Universal:** Compatible tanto con modelos locales (vía Ollama con Llama 3 / Qwen) como con asistentes en la nube (Claude Desktop, agentes IDE).
* **Mantenibilidad:** Arquitectura modular basada en Inyección de Dependencias con `Microsoft.Extensions.Hosting`. Se eliminaron las herramientas temporales de plantilla (`RandomNumberTools.cs`).

### Negativas / Mitigaciones
* Requiere que `PisciDataBackend` se encuentre en ejecución en la dirección configurada (`http://localhost:5007/api`) para que las herramientas resuelvan datos. El cliente HTTP maneja fallos de conectividad con mensajes explicativos para evitar excepciones no controladas.
