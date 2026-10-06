using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class Task
{
    public int Id { get; set; }

    public int FarmId { get; set; }

    public int? PondId { get; set; }

    public string Title { get; set; } = null!;

    public string? Description { get; set; }

    public DateTime? ScheduledDate { get; set; }

    public DateTime? CompletedDate { get; set; }

    public string Priority { get; set; } = null!;

    public bool Completed { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Farm Farm { get; set; } = null!;

    public virtual Pond? Pond { get; set; }

    public virtual User? User { get; set; }
}
