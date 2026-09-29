using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Feedingbyage
{
    public int Id { get; set; }

    public int? SpeciesId { get; set; }

    public int MinimumAgeDays { get; set; }

    public int MaximumAgeDays { get; set; }

    public decimal? ApproximatedFishWeightGrams { get; set; }

    public decimal? FeedingRatePercentage { get; set; }

    public decimal? ProteinPercentage { get; set; }

    public decimal? PelletSizeMm { get; set; }

    public decimal? DailyAmountKilo { get; set; }

    public int? DailyMeals { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Species? Species { get; set; }

    public virtual User? User { get; set; }
}
