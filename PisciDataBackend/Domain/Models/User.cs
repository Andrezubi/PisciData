using System;
using System.Collections.Generic;

namespace PisciDataBackend.Domain.Models;

public partial class User
{
    public int Id { get; set; }

    public string FirstName { get; set; } = null!;

    public string LastName { get; set; } = null!;

    public string Phone { get; set; } = null!;

    public string PasswordHash { get; set; } = null!;

    public string Role { get; set; } = null!;

    public DateTime? LastLoginAt { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public bool? IsActive { get; set; }

    public int? UserId { get; set; }

    public virtual ICollection<Aiconversation> AiconversationOwnerUsers { get; set; } = new List<Aiconversation>();

    public virtual ICollection<Aiconversation> AiconversationUsers { get; set; } = new List<Aiconversation>();

    public virtual ICollection<Aimessage> Aimessages { get; set; } = new List<Aimessage>();

    public virtual ICollection<Biometric> Biometrics { get; set; } = new List<Biometric>();

    public virtual ICollection<Biometricssample> Biometricssamples { get; set; } = new List<Biometricssample>();

    public virtual ICollection<Farm> FarmOwnerUsers { get; set; } = new List<Farm>();

    public virtual ICollection<Farm> FarmUsers { get; set; } = new List<Farm>();

    public virtual ICollection<Feedingbyage> Feedingbyages { get; set; } = new List<Feedingbyage>();

    public virtual ICollection<Feedingbyweight> Feedingbyweights { get; set; } = new List<Feedingbyweight>();

    public virtual ICollection<Feeding> Feedings { get; set; } = new List<Feeding>();

    public virtual ICollection<Feed> Feeds { get; set; } = new List<Feed>();

    public virtual ICollection<Harvest> Harvests { get; set; } = new List<Harvest>();

    public virtual ICollection<User> InverseUserNavigation { get; set; } = new List<User>();

    public virtual ICollection<Mortality> Mortalities { get; set; } = new List<Mortality>();

    public virtual ICollection<Pond> Ponds { get; set; } = new List<Pond>();

    public virtual ICollection<Productioncycle> Productioncycles { get; set; } = new List<Productioncycle>();

    public virtual ICollection<Species> Species { get; set; } = new List<Species>();

    public virtual ICollection<Supply> Supplies { get; set; } = new List<Supply>();

    public virtual ICollection<Supplymovement> Supplymovements { get; set; } = new List<Supplymovement>();

    public virtual ICollection<Task> Tasks { get; set; } = new List<Task>();

    public virtual User? UserNavigation { get; set; }

    public virtual ICollection<Waterquality> Waterqualities { get; set; } = new List<Waterquality>();
}
