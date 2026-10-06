using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Feed
{
    public int Id { get; set; }

    public int FarmId { get; set; }

    public string Brand { get; set; } = null!;

    public string? ProductName { get; set; }

    public string? Phase { get; set; }

    public decimal? ProteinPercentage { get; set; }

    public decimal? PelletSizeMm { get; set; }

    public decimal? StockKg { get; set; }

    public decimal? MinimumStockKg { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Farm Farm { get; set; } = null!;

    public virtual ICollection<Feeding> Feedings { get; set; } = new List<Feeding>();

    public virtual User? User { get; set; }
}
