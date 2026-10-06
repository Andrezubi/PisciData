using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Feeding
{
    public int Id { get; set; }

    public int ProductionCycleId { get; set; }

    public int FeedId { get; set; }

    public DateOnly FeedingDate { get; set; }

    public TimeOnly FeedingTime { get; set; }

    public decimal QuantityKg { get; set; }

    public int? MealNumber { get; set; }

    public string? Behavior { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Feed Feed { get; set; } = null!;

    public virtual Productioncycle ProductionCycle { get; set; } = null!;

    public virtual User? User { get; set; }
}
