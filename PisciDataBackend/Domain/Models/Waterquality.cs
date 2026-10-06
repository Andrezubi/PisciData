using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Waterquality
{
    public int Id { get; set; }

    public int ProductionCycleId { get; set; }

    public DateOnly MeasurementDate { get; set; }

    public TimeOnly MeasurementTime { get; set; }

    public decimal? TransparencyCm { get; set; }

    public decimal? TemperatureC { get; set; }

    public decimal? Ph { get; set; }

    public decimal? DissolvedOxygen { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Productioncycle ProductionCycle { get; set; } = null!;

    public virtual User? User { get; set; }
}
