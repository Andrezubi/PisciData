using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Application.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

// Swagger / OpenAPI (Swashbuckle)
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// JWT Authentication
var jwtKey = builder.Configuration["Jwt:Key"];
if (string.IsNullOrEmpty(jwtKey))
{
    throw new Exception("JWT key is not configured. Set Jwt:Key in appsettings.");
}
var keyBytes = Encoding.ASCII.GetBytes(jwtKey);
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
    .AddJwtBearer(options =>
    {
        options.RequireHttpsMetadata = false;
        options.SaveToken = true;
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(keyBytes),
            ValidateIssuer = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidateAudience = true,
            ValidAudience = builder.Configuration["Jwt:Audience"],
            ValidateLifetime = true,
            ClockSkew = TimeSpan.Zero
        };
    });

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
    // Enable Swagger UI in Development for testing
    app.UseSwagger();
    app.UseSwaggerUI(options =>
    {
        options.RoutePrefix = "swagger"; // serve at /swagger
        options.SwaggerEndpoint("/swagger/v1/swagger.json", "PisciData API V1");
    });
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
