using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Mortality
{
    public int Id { get; set; }

    public int ProductionCycleId { get; set; }

    public DateOnly MortalityDate { get; set; }

    public int DeadFishCount { get; set; }

    public string? Cause { get; set; }

    public string? ObservedSigns { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Productioncycle ProductionCycle { get; set; } = null!;

    public virtual User? User { get; set; }
}
