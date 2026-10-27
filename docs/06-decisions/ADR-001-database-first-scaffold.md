# ADR-001: Enfoque Database-First y Scaffold mediante Entity Framework Core

* **Estado:** Aceptado / Implementado
* **Fecha:** 06 de octubre de 2026
* **Decisores:** Equipo PisciData (inferido de commits `b9849cd` y `f677bfe`)

---

## 1. Contexto

Al iniciar el proyecto se requería modelar un dominio acuícola complejo con 19 tablas interrelacionadas (granjas, estanques, ciclos de siembra, biometrías, insumos, calidad de agua y chats de IA). 

La base de datos relacional `piscidatadb` fue creada inicialmente en un servidor MySQL local y posteriormente importada al backend de C# mediante ingeniería inversa (*Database-First Scaffold*) utilizando el proveedor `Microting.EntityFrameworkCore.MySql`.

## 2. Decisión

Utilizar el enfoque **Database-First** generando el contexto [`PiscidatadbContext.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/PiscidatadbContext.cs) y las 19 clases de entidad en `Domain/Models/` a través de herramientas de scaffold de EF Core, sin inicializar el sistema de migraciones de código (`Migrations/`).

## 3. Consecuencias

### Positivas
* Disponibilidad inmediata de las 19 entidades fuertemente tipadas en C# con llaves foráneas, índices y tipos de datos configurados en `OnModelCreating`.
* Agilidad inicial para comenzar a implementar controladores y servicios CRUD sin escribir configuraciones manuales de EF Core.

### Negativas y Riesgos Identificados
* **Ausencia de control de versiones de base de datos:** El repositorio no contiene migraciones ni scripts SQL DDL, dificultando que nuevos desarrolladores levanten la base de datos en entornos limpios.
* **Exposición de credenciales:** El scaffold generó una advertencia (`#warning`) con una cadena de conexión local fija (`root:1234`) en `OnConfiguring`.
* **Desalineación de Namespace:** El archivo se guardó físicamente en `Infraestructure/Persistence/` pero conserva el namespace `PisciDataBackend.Domain.Models`.

## 4. Alternativas Consideradas
* **Code-First con Migraciones:** *Requiere confirmación* si el equipo migrará a Code-First en una fase posterior o si mantendrá scripts SQL versionados.
