# ADR-002: Estilo Arquitectónico en Capas en Backend ASP.NET Core

* **Estado:** Aceptado / Implementado (con deuda técnica en inversión de dependencias)
* **Fecha:** 06 de octubre de 2026
* **Decisores:** Equipo PisciData (inferido de commit `bdccf27`)

---

## 1. Contexto

Se requería estructurar el backend en ASP.NET Core (.NET 10) para dar soporte a las operaciones de la piscigranja, separando el protocolo HTTP, las reglas de validación sintáctica, la manipulación de entidades y el acceso a la base de datos.

## 2. Decisión

Implementar una **Arquitectura en Capas (Layered Architecture)** organizada en cuatro carpetas principales:
1. `Controllers/`: Controladores REST que exponen 5 rutas estándar por entidad.
2. `Application/`: Servicios (`*Service.cs`) que orquestan el flujo y DTOs (`*Dto.cs`) que definen contratos de entrada y salida.
3. `Domain/`: Validadores estáticos (`*Validator.cs`), entidades de negocio (`Domain/Models/`) e interfaz genérica (`IBaseRepository<T>`).
4. `Infraestructure/`: Repositorios concretos (`*Repository.cs`) que heredan de `BaseRepository<T>` y encapsulan Entity Framework Core.

## 3. Consecuencias

### Positivas
* Estructura predecible y uniforme: el patrón para añadir nuevas entidades es idéntico y fácil de replicar.
* Aislamiento de validaciones sintácticas en clases estáticas dedicadas en la capa de dominio.
* Los controladores se mantienen delgados, delegando la orquestación a los servicios.

### Negativas y Deuda Técnica (TECH-ARC-01)
* **Inversión de Dependencias (DIP) Rota:** Aunque existe `IBaseRepository<T>`, los servicios inyectan directamente repositorios concretos (`FarmRepository`, `UserRepository`, etc.) y `Program.cs` los registra como tipos concretos.
* Esto dificulta la creación de pruebas unitarias aisladas con mocks de repositorio sin levantar una base de datos real o in-memory.

## 4. Alternativas Consideradas
* **Clean Architecture estricta / Hexagonal:** Se definieron interfaces de repositorio pero no se completó la inversión de dependencias en el contenedor de IoC.
