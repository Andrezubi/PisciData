using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Harvest
{
    public int Id { get; set; }

    public int ProductionCycleId { get; set; }

    public DateOnly HarvestDate { get; set; }

    public int FishCount { get; set; }

    public decimal? TotalWeightKg { get; set; }

    public decimal? AverageWeightGrams { get; set; }

    public string? HarvestType { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Productioncycle ProductionCycle { get; set; } = null!;

    public virtual User? User { get; set; }
}
