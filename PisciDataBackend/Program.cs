using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Application.Services;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

// Database context
builder.Services.AddDbContext<PiscidatadbContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        Microsoft.EntityFrameworkCore.ServerVersion.Parse("8.0.46-mysql")));

// Repositories
builder.Services.AddScoped<FarmRepository>();
builder.Services.AddScoped<UserRepository>();
builder.Services.AddScoped<PondRepository>();
builder.Services.AddScoped<ProductioncycleRepository>();
builder.Services.AddScoped<FeedingRepository>();
builder.Services.AddScoped<FeedRepository>();
builder.Services.AddScoped<SupplyRepository>();
builder.Services.AddScoped<BiometricRepository>();
builder.Services.AddScoped<BiometricssampleRepository>();

// Services
builder.Services.AddScoped<FarmService>();
builder.Services.AddScoped<UserService>();
builder.Services.AddScoped<PondService>();
builder.Services.AddScoped<ProductioncycleService>();
builder.Services.AddScoped<FeedingService>();
builder.Services.AddScoped<FeedService>();
builder.Services.AddScoped<SupplyService>();
builder.Services.AddScoped<BiometricService>();
builder.Services.AddScoped<BiometricssampleService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
