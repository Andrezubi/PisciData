using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Biometric
{
    public int Id { get; set; }

    public int ProductionCycleId { get; set; }

    public DateOnly MeasurementDate { get; set; }

    public decimal? AverageWeightGrams { get; set; }

    public decimal? BiomassKg { get; set; }

    public decimal? CalculatedFeedKg { get; set; }

    public string? HealthStatus { get; set; }

    public string? Observations { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual ICollection<Biometricssample> Biometricssamples { get; set; } = new List<Biometricssample>();

    public virtual Productioncycle ProductionCycle { get; set; } = null!;

    public virtual User? User { get; set; }
}
