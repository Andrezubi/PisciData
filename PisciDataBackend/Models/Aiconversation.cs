using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Aiconversation
{
    public int Id { get; set; }

    public int OwnerUserId { get; set; }

    public DateTime StartDate { get; set; }

    public DateTime? LastMessageDate { get; set; }

    public string? Title { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual ICollection<Aimessage> Aimessages { get; set; } = new List<Aimessage>();

    public virtual User OwnerUser { get; set; } = null!;

    public virtual User? User { get; set; }
}
