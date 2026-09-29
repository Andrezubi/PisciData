using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Pond
{
    public int Id { get; set; }

    public int FarmId { get; set; }

    public string Code { get; set; } = null!;

    public string? Name { get; set; }

    public string? Shape { get; set; }

    public decimal? Width { get; set; }

    public decimal? Length { get; set; }

    public decimal? Diameter { get; set; }

    public decimal? Depth { get; set; }

    public decimal? Area { get; set; }

    public string? Description { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Farm Farm { get; set; } = null!;

    public virtual ICollection<Productioncycle> Productioncycles { get; set; } = new List<Productioncycle>();

    public virtual ICollection<Task> Tasks { get; set; } = new List<Task>();

    public virtual User? User { get; set; }
}
