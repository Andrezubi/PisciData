using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Productioncycle
{
    public int Id { get; set; }

    public int PondId { get; set; }

    public int SpeciesId { get; set; }

    public DateOnly StartDate { get; set; }

    public DateOnly? EndDate { get; set; }

    public int? InitialFishCount { get; set; }

    public decimal? InitialAverageWeightGrams { get; set; }

    public int? InitialAgeDays { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual ICollection<Biometric> Biometrics { get; set; } = new List<Biometric>();

    public virtual ICollection<Feeding> Feedings { get; set; } = new List<Feeding>();

    public virtual ICollection<Harvest> Harvests { get; set; } = new List<Harvest>();

    public virtual ICollection<Mortality> Mortalities { get; set; } = new List<Mortality>();

    public virtual Pond Pond { get; set; } = null!;

    public virtual Species Species { get; set; } = null!;

    public virtual User? User { get; set; }

    public virtual ICollection<Waterquality> Waterqualities { get; set; } = new List<Waterquality>();
}
