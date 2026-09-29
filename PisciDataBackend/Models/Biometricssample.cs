using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Biometricssample
{
    public int Id { get; set; }

    public int BiometricsId { get; set; }

    public int SampleNumber { get; set; }

    public decimal? BucketWaterWeightKg { get; set; }

    public decimal? BucketWithFishWeightKg { get; set; }

    public int FishCount { get; set; }

    public decimal? TotalFishWeightKg { get; set; }

    public decimal? AverageWeightGrams { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Biometric Biometrics { get; set; } = null!;

    public virtual User? User { get; set; }
}
