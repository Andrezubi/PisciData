using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Waterqualityreference
{
    public int Id { get; set; }

    public int? SpeciesId { get; set; }

    public string Parameter { get; set; } = null!;

    public string Unit { get; set; } = null!;

    public decimal? OptimalMin { get; set; }

    public decimal? OptimalMax { get; set; }

    public decimal? WarningMin { get; set; }

    public decimal? WarningMax { get; set; }

    public decimal? CriticalMin { get; set; }

    public decimal? CriticalMax { get; set; }

    public string? RecommendedAction { get; set; }

    public string? Source { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Species? Species { get; set; }

    public virtual User? User { get; set; }
}
