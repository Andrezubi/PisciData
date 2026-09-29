using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Species
{
    public int Id { get; set; }

    public string CommonName { get; set; } = null!;

    public string? ScientificName { get; set; }

    public string? Description { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual ICollection<Feedingbyage> Feedingbyages { get; set; } = new List<Feedingbyage>();

    public virtual ICollection<Feedingbyweight> Feedingbyweights { get; set; } = new List<Feedingbyweight>();

    public virtual ICollection<Productioncycle> Productioncycles { get; set; } = new List<Productioncycle>();

    public virtual User? User { get; set; }
}
