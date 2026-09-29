using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Supplymovement
{
    public int Id { get; set; }

    public int SupplyId { get; set; }

    public DateTime MovementDate { get; set; }

    public string MovementType { get; set; } = null!;

    public decimal Quantity { get; set; }

    public string? Reason { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Supply Supply { get; set; } = null!;

    public virtual User? User { get; set; }
}
