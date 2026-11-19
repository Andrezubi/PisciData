using System;

namespace PisciDataBackend.Application.DTOs
{
    public class TaskDto
    {
        public int Id { get; set; }
        public int FarmId { get; set; }
        public int? PondId { get; set; }
        public string? PondName { get; set; }
        public string Title { get; set; } = string.Empty;
        public string? Description { get; set; }
        public DateTime? ScheduledDate { get; set; }
        public DateTime? CompletedDate { get; set; }
        public string Priority { get; set; } = "Normal";
        public bool Completed { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class CreateTaskDto
    {
        public int FarmId { get; set; }
        public int? PondId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string? Description { get; set; }
        public DateTime? ScheduledDate { get; set; }
        public string Priority { get; set; } = "Normal";
    }

    public class UpdateTaskDto
    {
        public int? PondId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string? Description { get; set; }
        public DateTime? ScheduledDate { get; set; }
        public string Priority { get; set; } = "Normal";
        public bool Completed { get; set; }
    }
}

