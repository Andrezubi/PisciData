# ADR-003: Estrategia Heterogénea de Eliminación (Soft Delete vs Hard Delete)

* **Estado:** Parcialmente implementado / Inconsistencia registrada (Requiere confirmación humana)
* **Fecha:** 06 de octubre de 2026
* **Decisores:** Equipo PisciData (inferido de `FarmRepository.cs` vs `BaseRepository.cs`)

---

## 1. Contexto

En sistemas de gestión acuícola, eliminar registros maestros como Granjas o Usuarios de forma física (`DELETE FROM ...`) puede romper la integridad referencial y destruir el rastro de auditoría histórica de pesajes, siembras y cosechas pasadas.

En el esquema de base de datos MySQL, **todas las 19 tablas** poseen una columna `IsActive` (`TINYINT(1)` con valor por defecto `1`).

## 2. Decisión (Estado Real de la Implementación)

Se implementó una estrategia **híbrida no uniforme**:
1. Para `Farm` y `User`: Se sobreescribió el método `DeleteAsync` en sus repositorios respectivos para realizar **Soft Delete**, cambiando `IsActive = false` y actualizando `UpdatedAt = DateTime.UtcNow`. En `GetAllAsync` y `GetByIdAsync` se filtra activamente por `IsActive != false`.
2. Para las restantes 7 entidades implementadas (`Pond`, `Productioncycle`, `Feed`, `Feeding`, `Supply`, `Biometric`, `Biometricssample`): Heredan directamente de [`BaseRepository.cs`](file:///home/javi/Documentos/uni/taller/PisciData/PisciDataBackend/Infraestructure/Persistence/BaseRepository.cs), el cual ejecuta **Hard Delete** (`_dbSet.Remove(entity)`).

## 3. Consecuencias

### Positivas
* Se protege la integridad histórica de Granjas y Usuarios principales.

### Negativas y Riesgos (`TECH-DAT-01`)
* Discrepancia en el comportamiento del sistema ante peticiones `DELETE`: algunas entidades se desactivan lógicamente mientras que otras se destruyen físicamente.
* Si se elimina físicamente un estanque (`Pond`) que tiene ciclos de producción asociados, MySQL arrojará un error de restricción de llave foránea (`FK constraint violation`) si la relación no es en cascada.

## 4. Acción Requerida (TBD)
* Se requiere confirmación del equipo para estandarizar el comportamiento en `BaseRepository<T>` de modo que todas las entidades con `IsActive` utilicen soft delete por defecto o filtros globales de consulta en EF Core (`HasQueryFilter`).
