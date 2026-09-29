using System;
using System.Collections.Generic;

namespace PisciDataBackend.Models;

public partial class Aimessage
{
    public int Id { get; set; }

    public int AiconversationId { get; set; }

    public string Role { get; set; } = null!;

    public string Content { get; set; } = null!;

    public string MessageType { get; set; } = null!;

    public DateTime MessageDate { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual Aiconversation Aiconversation { get; set; } = null!;

    public virtual User? User { get; set; }
}
