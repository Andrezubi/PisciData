# 01. Visión General del Producto (Product Overview)

**Última actualización:** 06 de octubre de 2026  
**Propósito:** Definir el propósito, alcance, contexto operacional y actores del sistema PisciData.

> **Definición de Baseline SDD:**
> «"docs/" representa la especificación y el estado conocido del proyecto. La especificación define la intención del sistema; el código representa el estado actual de implementación. Las divergencias entre ambos son defectos de implementación o elementos pendientes, no cambios implícitos de la especificación.»
> «La documentación no declara que una funcionalidad esté verificada únicamente porque exista código relacionado con ella.»

---

## 1. Declaración de Propósito

**PisciData** es un sistema integral de información y gestión para unidades productivas acuícolas (piscigranjas de trucha, tilapia u otras especies afines).

El objetivo del sistema es centralizar el control operativo de las granjas, cubriendo desde la infraestructura física (estanques) y lotes biológicos (ciclos de producción), hasta el monitoreo del crecimiento (biometrías), suministro de alimento balanceado, inventario de insumos y parámetros ambientales.

---

## 2. Actores del Sistema (Roles Verificables)

De acuerdo con las reglas de validación en [`UserValidator.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Domain/Validators/UserValidator.cs) y las relaciones del modelo relacional en [`PiscidatadbContext.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/PiscidatadbContext.cs), el sistema reconoce los siguientes actores:

| Actor / Rol | Evidencia en Repositorio | Responsabilidades y Alcance en el Sistema |
|---|---|---|
| **Admin** (Administrador) | Valor permitido en `UserValidator.AllowedRoles` | Gestión global del sistema: alta y baja de usuarios, configuración general de granjas y supervisión de todas las entidades operativas. |
| **OwnerUser** (Propietario de Granja) | Relación `Farm.OwnerUserId` y `Aiconversation.OwnerUserId` | Dueño o concesionario del predio acuícola. Posee granjas asociadas y es el titular de las sesiones de consulta con el asistente de IA. |
| **Technician** (Técnico Acuícola / Biólogo) | Valor permitido en `UserValidator.AllowedRoles` | Personal calificado responsable del registro técnico: muestreos biométricos de peces, control de calidad fisicoquímica del agua y diagnóstico de mortalidad. |
| **Worker** (Operario de Granja) | Valor permitido en `UserValidator.AllowedRoles` | Personal de campo responsable de labores directas: alimentación diaria por estanque, reporte de tareas rutinarias y recepción/despacho de insumos de almacén. |
| **AI Assistant (Whisper/Ollama/MCP)** | (Diseño aprobado / `PisciDataMCP` inicial) | Sistema asistencial basado en IA local (Whisper para STT, Ollama para LLM) que consumirá herramientas a través de Model Context Protocol (MCP) para interactuar de forma multimodal (Audio/Texto) con el piscicultor. |

---

## 3. Alcance del Sistema (In-Scope vs Out-of-Scope)

### 3.1. Dentro del Alcance (Verificado en Código y Modelo de Datos)
* **Gestión de Granjas y Estanques:** Catálogo de predios, geolocalización (latitud/longitud), dimensionamiento físico (largo, ancho, profundidad, diámetro, área superficial) y codificación de estanques.
* **Ciclos de Producción:** Apertura y cierre de lotes productivos por estanque y especie, registrando siembra inicial (cantidad de alevines, peso y edad inicial).
* **Control de Nutrición:** Catálogo de marcas de alimento balanceado con especificaciones (% proteína, tamaño de pellet), control de stock mínimo y registro detallado de raciones servidas por toma/hora.
* **Muestreos Biométricos:** Registro de pesajes volumétricos por cubetas (tara, peso con agua y peces, conteo), con cálculo de peso unitario promedio y biomasa estimada.
* **Gestión de Insumos de Granja:** Control de existencias físicas y stock de seguridad de materiales operativos.
* **Módulos Modelados Pendientes de Exposición:** Monitoreo fisicoquímico de agua (oxígeno disuelto, pH, temperatura, transparencia), registro de mortalidad, cosechas, tareas y kardex de insumos.

### 3.2. Fuera del Alcance Actual / No Evidenciado (TBD)
* Facturación electrónica, contabilidad financiera o emisión fiscal.
* Pasarelas de pago o comercio electrónico directo.
* Integración telemática directa con sensores IoT de hardware en tiempo real (las mediciones de agua se modelan actualmente como registros manuales con fecha y hora).
* Módulos de logística de transporte o cadena de frío externa.

---

## 4. Supuestos y Restricciones Operacionales

1. **Conectividad en Campo:**  
   Las piscigranjas suelen operar en áreas rurales o valles montañosos con conectividad intermitente a internet. El frontend está estructurado en Kotlin Multiplatform para aplicaciones móviles nativas (Android e iOS), lo que facilitará en etapas futuras el soporte offline (requiere confirmación y diseño de sincronización).
2. **Identificación de Personal:**  
   El sistema no asume que los operarios de campo utilicen correo electrónico como mecanismo cotidiano; el identificador primario del usuario es su número de teléfono celular ([`UserDto.Phone`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Application/DTOs/UserDto.cs#L8)).

---

## 5. Diseño de Interfaz de Usuario (UI/UX Oficial)

El frontend de la aplicación (Kotlin Multiplatform) se estructura en torno a **4 pantallas principales especificadas y aprobadas en diseño** que guiarán la experiencia de usuario (actualmente en estado **MISSING** en el código):

1. **Login/Bienvenida:** Punto de acceso inicial y control de identidad.
2. **Base de Datos (Gestión):** Interfaz central para la administración y registro operativo manual (granjas, estanques, ciclos productivos, biometrías, alimento).
3. **Reportes (KPIs):** Panel de visualización analítica de indicadores clave de rendimiento productivo y ambiental.
4. **Asistente IA (Chat Multimodal):** Interfaz conversacional que permite la interacción por voz (captura de audio para STT) y texto con el asistente inteligente del sistema.
