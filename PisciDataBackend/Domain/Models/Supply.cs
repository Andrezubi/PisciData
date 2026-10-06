using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Supply
{
    public int Id { get; set; }

    public int FarmId { get; set; }

    public string Name { get; set; } = null!;

    public string? Category { get; set; }

    public decimal Quantity { get; set; }

    public string Unit { get; set; } = null!;

    public decimal? MinimumStock { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Farm Farm { get; set; } = null!;

    public virtual ICollection<Supplymovement> Supplymovements { get; set; } = new List<Supplymovement>();

    public virtual User? User { get; set; }
}
