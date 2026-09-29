using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Feedingbyweight
{
    public int Id { get; set; }

    public int? SpeciesId { get; set; }

    public decimal MinimumWeightGrams { get; set; }

    public decimal MaximumWeightGrams { get; set; }

    public decimal? FeedingRatePercentage { get; set; }

    public string? FeedPhase { get; set; }

    public decimal? ProteinPercentage { get; set; }

    public decimal? PelletSizeMm { get; set; }

    public int? DailyMeals { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Species? Species { get; set; }

    public virtual User? User { get; set; }
}
