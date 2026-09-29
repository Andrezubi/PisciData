using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microting.EntityFrameworkCore.MySql.Scaffolding.Internal;

namespace PisciDataBackend.Models;

public partial class PiscidatadbContext : DbContext
{
    public PiscidatadbContext()
    {
    }

    public PiscidatadbContext(DbContextOptions<PiscidatadbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Aiconversation> Aiconversations { get; set; }

    public virtual DbSet<Aimessage> Aimessages { get; set; }

    public virtual DbSet<Biometric> Biometrics { get; set; }

    public virtual DbSet<Biometricssample> Biometricssamples { get; set; }

    public virtual DbSet<Farm> Farms { get; set; }

    public virtual DbSet<Feed> Feeds { get; set; }

    public virtual DbSet<Feeding> Feedings { get; set; }

    public virtual DbSet<Feedingbyage> Feedingbyages { get; set; }

    public virtual DbSet<Feedingbyweight> Feedingbyweights { get; set; }

    public virtual DbSet<Harvest> Harvests { get; set; }

    public virtual DbSet<Mortality> Mortalities { get; set; }

    public virtual DbSet<Pond> Ponds { get; set; }

    public virtual DbSet<Productioncycle> Productioncycles { get; set; }

    public virtual DbSet<Species> Species { get; set; }

    public virtual DbSet<Supply> Supplies { get; set; }

    public virtual DbSet<Supplymovement> Supplymovements { get; set; }

    public virtual DbSet<Task> Tasks { get; set; }

    public virtual DbSet<User> Users { get; set; }

    public virtual DbSet<Waterquality> Waterqualities { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. You can avoid scaffolding the connection string by using the Name= syntax to read it from configuration - see https://go.microsoft.com/fwlink/?linkid=2131148. For more guidance on storing connection strings, see https://go.microsoft.com/fwlink/?LinkId=723263.
        => optionsBuilder.UseMySql("server=localhost;port=3306;database=piscidatadb;user=root;password=1234", Microsoft.EntityFrameworkCore.ServerVersion.Parse("8.0.46-mysql"));

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .UseCollation("utf8mb4_unicode_ci")
            .HasCharSet("utf8mb4");

        modelBuilder.Entity<Aiconversation>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("aiconversation");

            entity.HasIndex(e => e.UserId, "FK_AIConversation_User");

            entity.HasIndex(e => e.OwnerUserId, "IX_AIConversation_OwnerUserId");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.LastMessageDate).HasColumnType("datetime");
            entity.Property(e => e.StartDate)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.Title).HasMaxLength(255);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.OwnerUser).WithMany(p => p.AiconversationOwnerUsers)
                .HasForeignKey(d => d.OwnerUserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_AIConversation_OwnerUser");

            entity.HasOne(d => d.User).WithMany(p => p.AiconversationUsers)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_AIConversation_User");
        });

        modelBuilder.Entity<Aimessage>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("aimessage");

            entity.HasIndex(e => e.UserId, "FK_AIMessage_User");

            entity.HasIndex(e => e.AiconversationId, "IX_AIMessage_AIConversationId");

            entity.Property(e => e.AiconversationId).HasColumnName("AIConversationId");
            entity.Property(e => e.Content).HasColumnType("text");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MessageDate)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.MessageType)
                .HasMaxLength(30)
                .HasDefaultValueSql("'TEXT'");
            entity.Property(e => e.Role).HasMaxLength(30);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Aiconversation).WithMany(p => p.Aimessages)
                .HasForeignKey(d => d.AiconversationId)
                .HasConstraintName("FK_AIMessage_Conversation");

            entity.HasOne(d => d.User).WithMany(p => p.Aimessages)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_AIMessage_User");
        });

        modelBuilder.Entity<Biometric>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("biometrics");

            entity.HasIndex(e => e.UserId, "FK_Biometrics_User");

            entity.HasIndex(e => e.ProductionCycleId, "IX_Biometrics_ProductionCycleId");

            entity.Property(e => e.AverageWeightGrams).HasPrecision(10, 2);
            entity.Property(e => e.BiomassKg).HasPrecision(12, 3);
            entity.Property(e => e.CalculatedFeedKg).HasPrecision(12, 3);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.HealthStatus).HasMaxLength(100);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.ProductionCycle).WithMany(p => p.Biometrics)
                .HasForeignKey(d => d.ProductionCycleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Biometrics_ProductionCycle");

            entity.HasOne(d => d.User).WithMany(p => p.Biometrics)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Biometrics_User");
        });

        modelBuilder.Entity<Biometricssample>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("biometricssample");

            entity.HasIndex(e => e.UserId, "FK_BiometricsSample_User");

            entity.HasIndex(e => e.BiometricsId, "IX_BiometricsSample_BiometricsId");

            entity.Property(e => e.AverageWeightGrams).HasPrecision(10, 2);
            entity.Property(e => e.BucketWaterWeightKg).HasPrecision(10, 3);
            entity.Property(e => e.BucketWithFishWeightKg).HasPrecision(10, 3);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.TotalFishWeightKg).HasPrecision(10, 3);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Biometrics).WithMany(p => p.Biometricssamples)
                .HasForeignKey(d => d.BiometricsId)
                .HasConstraintName("FK_BiometricsSample_Biometrics");

            entity.HasOne(d => d.User).WithMany(p => p.Biometricssamples)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_BiometricsSample_User");
        });

        modelBuilder.Entity<Farm>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("farm");

            entity.HasIndex(e => e.OwnerUserId, "IX_Farm_OwnerUserId");

            entity.HasIndex(e => e.UserId, "IX_Farm_UserId");

            entity.Property(e => e.Address).HasMaxLength(255);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.Description).HasMaxLength(500);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Latitude).HasPrecision(10, 7);
            entity.Property(e => e.Longitude).HasPrecision(10, 7);
            entity.Property(e => e.Name).HasMaxLength(150);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.OwnerUser).WithMany(p => p.FarmOwnerUsers)
                .HasForeignKey(d => d.OwnerUserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Farm_OwnerUser");

            entity.HasOne(d => d.User).WithMany(p => p.FarmUsers)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Farm_User");
        });

        modelBuilder.Entity<Feed>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("feed");

            entity.HasIndex(e => e.UserId, "FK_Feed_User");

            entity.HasIndex(e => e.FarmId, "IX_Feed_FarmId");

            entity.Property(e => e.Brand).HasMaxLength(100);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MinimumStockKg).HasPrecision(12, 3);
            entity.Property(e => e.PelletSizeMm).HasPrecision(6, 2);
            entity.Property(e => e.Phase).HasMaxLength(10);
            entity.Property(e => e.ProductName).HasMaxLength(150);
            entity.Property(e => e.ProteinPercentage).HasPrecision(5, 2);
            entity.Property(e => e.StockKg).HasPrecision(12, 3);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Farm).WithMany(p => p.Feeds)
                .HasForeignKey(d => d.FarmId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Feed_Farm");

            entity.HasOne(d => d.User).WithMany(p => p.Feeds)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Feed_User");
        });

        modelBuilder.Entity<Feeding>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("feeding");

            entity.HasIndex(e => e.UserId, "FK_Feeding_User");

            entity.HasIndex(e => e.FeedId, "IX_Feeding_FeedId");

            entity.HasIndex(e => e.ProductionCycleId, "IX_Feeding_ProductionCycleId");

            entity.Property(e => e.Behavior).HasMaxLength(255);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.FeedingTime).HasColumnType("time");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.QuantityKg).HasPrecision(12, 3);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Feed).WithMany(p => p.Feedings)
                .HasForeignKey(d => d.FeedId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Feeding_Feed");

            entity.HasOne(d => d.ProductionCycle).WithMany(p => p.Feedings)
                .HasForeignKey(d => d.ProductionCycleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Feeding_ProductionCycle");

            entity.HasOne(d => d.User).WithMany(p => p.Feedings)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Feeding_User");
        });

        modelBuilder.Entity<Feedingbyage>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("feedingbyage");

            entity.HasIndex(e => e.SpeciesId, "FK_FeedingByAge_Species");

            entity.HasIndex(e => e.UserId, "FK_FeedingByAge_User");

            entity.Property(e => e.ApproximatedFishWeightGrams).HasPrecision(7, 2);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.DailyAmountKilo).HasPrecision(5, 2);
            entity.Property(e => e.FeedingRatePercentage).HasPrecision(6, 3);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.PelletSizeMm).HasPrecision(6, 2);
            entity.Property(e => e.ProteinPercentage).HasPrecision(5, 2);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Species).WithMany(p => p.Feedingbyages)
                .HasForeignKey(d => d.SpeciesId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_FeedingByAge_Species");

            entity.HasOne(d => d.User).WithMany(p => p.Feedingbyages)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_FeedingByAge_User");
        });

        modelBuilder.Entity<Feedingbyweight>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("feedingbyweight");

            entity.HasIndex(e => e.SpeciesId, "FK_FeedingByWeight_Species");

            entity.HasIndex(e => e.UserId, "FK_FeedingByWeight_User");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.FeedPhase).HasMaxLength(10);
            entity.Property(e => e.FeedingRatePercentage).HasPrecision(6, 3);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MaximumWeightGrams).HasPrecision(10, 2);
            entity.Property(e => e.MinimumWeightGrams).HasPrecision(10, 2);
            entity.Property(e => e.PelletSizeMm).HasPrecision(6, 2);
            entity.Property(e => e.ProteinPercentage).HasPrecision(5, 2);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Species).WithMany(p => p.Feedingbyweights)
                .HasForeignKey(d => d.SpeciesId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_FeedingByWeight_Species");

            entity.HasOne(d => d.User).WithMany(p => p.Feedingbyweights)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_FeedingByWeight_User");
        });

        modelBuilder.Entity<Harvest>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("harvest");

            entity.HasIndex(e => e.UserId, "FK_Harvest_User");

            entity.HasIndex(e => e.ProductionCycleId, "IX_Harvest_ProductionCycleId");

            entity.Property(e => e.AverageWeightGrams).HasPrecision(12, 3);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.HarvestType).HasMaxLength(100);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.TotalWeightKg).HasPrecision(12, 3);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.ProductionCycle).WithMany(p => p.Harvests)
                .HasForeignKey(d => d.ProductionCycleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Harvest_ProductionCycle");

            entity.HasOne(d => d.User).WithMany(p => p.Harvests)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Harvest_User");
        });

        modelBuilder.Entity<Mortality>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("mortality");

            entity.HasIndex(e => e.UserId, "FK_Mortality_User");

            entity.HasIndex(e => e.ProductionCycleId, "IX_Mortality_ProductionCycleId");

            entity.Property(e => e.Cause).HasMaxLength(255);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.ObservedSigns).HasMaxLength(1000);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.ProductionCycle).WithMany(p => p.Mortalities)
                .HasForeignKey(d => d.ProductionCycleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Mortality_ProductionCycle");

            entity.HasOne(d => d.User).WithMany(p => p.Mortalities)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Mortality_User");
        });

        modelBuilder.Entity<Pond>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("pond");

            entity.HasIndex(e => e.FarmId, "IX_Pond_FarmId");

            entity.HasIndex(e => e.UserId, "IX_Pond_UserId");

            entity.HasIndex(e => new { e.FarmId, e.Code }, "UQ_Pond_Farm_Code").IsUnique();

            entity.Property(e => e.Area).HasPrecision(12, 2);
            entity.Property(e => e.Code).HasMaxLength(50);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.Depth).HasPrecision(10, 2);
            entity.Property(e => e.Description).HasMaxLength(500);
            entity.Property(e => e.Diameter).HasPrecision(10, 2);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Length).HasPrecision(10, 2);
            entity.Property(e => e.Name).HasMaxLength(100);
            entity.Property(e => e.Shape).HasMaxLength(50);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");
            entity.Property(e => e.Width).HasPrecision(10, 2);

            entity.HasOne(d => d.Farm).WithMany(p => p.Ponds)
                .HasForeignKey(d => d.FarmId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Pond_Farm");

            entity.HasOne(d => d.User).WithMany(p => p.Ponds)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Pond_User");
        });

        modelBuilder.Entity<Productioncycle>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("productioncycle");

            entity.HasIndex(e => e.PondId, "IX_ProductionCycle_PondId");

            entity.HasIndex(e => e.SpeciesId, "IX_ProductionCycle_SpeciesId");

            entity.HasIndex(e => e.UserId, "IX_ProductionCycle_UserId");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.InitialAverageWeightGrams).HasPrecision(7, 2);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Pond).WithMany(p => p.Productioncycles)
                .HasForeignKey(d => d.PondId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ProductionCycle_Pond");

            entity.HasOne(d => d.Species).WithMany(p => p.Productioncycles)
                .HasForeignKey(d => d.SpeciesId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_ProductionCycle_Species");

            entity.HasOne(d => d.User).WithMany(p => p.Productioncycles)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_ProductionCycle_User");
        });

        modelBuilder.Entity<Species>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("species");

            entity.HasIndex(e => e.UserId, "FK_Species_User");

            entity.HasIndex(e => e.CommonName, "UQ_Species_CommonName").IsUnique();

            entity.Property(e => e.CommonName).HasMaxLength(100);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.Description).HasMaxLength(500);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.ScientificName).HasMaxLength(150);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.User).WithMany(p => p.Species)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Species_User");
        });

        modelBuilder.Entity<Supply>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("supply");

            entity.HasIndex(e => e.UserId, "FK_Supply_User");

            entity.HasIndex(e => e.FarmId, "IX_Supply_FarmId");

            entity.Property(e => e.Category).HasMaxLength(100);
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MinimumStock).HasPrecision(12, 3);
            entity.Property(e => e.Name).HasMaxLength(150);
            entity.Property(e => e.Quantity).HasPrecision(12, 3);
            entity.Property(e => e.Unit).HasMaxLength(30);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Farm).WithMany(p => p.Supplies)
                .HasForeignKey(d => d.FarmId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Supply_Farm");

            entity.HasOne(d => d.User).WithMany(p => p.Supplies)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Supply_User");
        });

        modelBuilder.Entity<Supplymovement>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("supplymovement");

            entity.HasIndex(e => e.UserId, "FK_SupplyMovement_User");

            entity.HasIndex(e => e.SupplyId, "IX_SupplyMovement_SupplyId");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MovementDate)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.MovementType).HasMaxLength(30);
            entity.Property(e => e.Quantity).HasPrecision(12, 3);
            entity.Property(e => e.Reason).HasMaxLength(255);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Supply).WithMany(p => p.Supplymovements)
                .HasForeignKey(d => d.SupplyId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_SupplyMovement_Supply");

            entity.HasOne(d => d.User).WithMany(p => p.Supplymovements)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_SupplyMovement_User");
        });

        modelBuilder.Entity<Task>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("task");

            entity.HasIndex(e => e.UserId, "FK_Task_User");

            entity.HasIndex(e => e.FarmId, "IX_Task_FarmId");

            entity.HasIndex(e => e.PondId, "IX_Task_PondId");

            entity.Property(e => e.CompletedDate).HasColumnType("datetime");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.Description).HasMaxLength(1000);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.Priority)
                .HasMaxLength(30)
                .HasDefaultValueSql("'Normal'");
            entity.Property(e => e.ScheduledDate).HasColumnType("datetime");
            entity.Property(e => e.Title).HasMaxLength(200);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.Farm).WithMany(p => p.Tasks)
                .HasForeignKey(d => d.FarmId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Task_Farm");

            entity.HasOne(d => d.Pond).WithMany(p => p.Tasks)
                .HasForeignKey(d => d.PondId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Task_Pond");

            entity.HasOne(d => d.User).WithMany(p => p.Tasks)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_Task_User");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("user");

            entity.HasIndex(e => e.UserId, "FK_User_User");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.FirstName).HasMaxLength(100);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.LastLoginAt).HasColumnType("datetime");
            entity.Property(e => e.LastName).HasMaxLength(100);
            entity.Property(e => e.PasswordHash).HasMaxLength(255);
            entity.Property(e => e.Phone).HasMaxLength(30);
            entity.Property(e => e.Role)
                .HasMaxLength(30)
                .HasDefaultValueSql("'Owner'");
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.UserNavigation).WithMany(p => p.InverseUserNavigation)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_User_User");
        });

        modelBuilder.Entity<Waterquality>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("waterquality");

            entity.HasIndex(e => e.UserId, "FK_WaterQuality_User");

            entity.HasIndex(e => e.ProductionCycleId, "IX_WaterQuality_ProductionCycleId");

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime");
            entity.Property(e => e.DissolvedOxygen).HasPrecision(8, 3);
            entity.Property(e => e.IsActive)
                .IsRequired()
                .HasDefaultValueSql("'1'");
            entity.Property(e => e.MeasurementTime).HasColumnType("time");
            entity.Property(e => e.Observations).HasMaxLength(1000);
            entity.Property(e => e.Ph)
                .HasPrecision(5, 2)
                .HasColumnName("PH");
            entity.Property(e => e.TemperatureC).HasPrecision(6, 2);
            entity.Property(e => e.TransparencyCm).HasPrecision(8, 2);
            entity.Property(e => e.UpdatedAt)
                .ValueGeneratedOnAddOrUpdate()
                .HasColumnType("datetime");

            entity.HasOne(d => d.ProductionCycle).WithMany(p => p.Waterqualities)
                .HasForeignKey(d => d.ProductionCycleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_WaterQuality_ProductionCycle");

            entity.HasOne(d => d.User).WithMany(p => p.Waterqualities)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.SetNull)
                .HasConstraintName("FK_WaterQuality_User");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
