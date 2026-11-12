using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.OpenApi;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi;
using PisciDataBackend.AI;
using PisciDataBackend.Application.Services;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Clients;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddCors(options =>
{
    options.AddPolicy("DevelopmentCors", policy =>
    {
        policy
            .AllowAnyOrigin()
            .AllowAnyHeader()
            .AllowAnyMethod();
    });
});
// Ollama and MCP configuration

builder.Services.Configure<OllamaOptions>(
    builder.Configuration.GetSection("AI:Ollama"));

builder.Services.Configure<McpOptions>(
    builder.Configuration.GetSection("AI:MCP"));

builder.Services.Configure<WhisperOptions>(
    builder.Configuration.GetSection("AI:Whisper"));

// MCP client: one shared connection to the stdio server.
builder.Services.AddSingleton<McpToolService>();

// Whisper STT service: singleton to share loaded model instance.
builder.Services.AddSingleton<IWhisperService, WhisperService>();

// Ollama client: HTTP client with MCP integration.
builder.Services.AddHttpClient<OllamaApiClient>((serviceProvider, client) =>
{
    var options = serviceProvider
        .GetRequiredService<IOptions<OllamaOptions>>()
        .Value;

    client.BaseAddress = new Uri(options.BaseUrl);
    client.Timeout = TimeSpan.FromMinutes(5);
});




// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

// Swagger / OpenAPI (Swashbuckle)
builder.Services.AddEndpointsApiExplorer();

builder.Services.AddSwaggerGen(options =>
{
    options.AddSecurityDefinition("Bearer", new Microsoft.OpenApi.OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = Microsoft.OpenApi.SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        Description = "Enter your JWT token."
    });

    options.AddSecurityRequirement(document =>
        new Microsoft.OpenApi.OpenApiSecurityRequirement
        {
            [new Microsoft.OpenApi.OpenApiSecuritySchemeReference("Bearer", document)] = new List<string>()
        });
});


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
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
if (string.IsNullOrWhiteSpace(connectionString))
{
    throw new Exception("Connection string is not configured. Set ConnectionStrings:DefaultConnection in User Secrets or an environment variable.");
}

builder.Services.AddDbContext<PiscidatadbContext>(options =>
    options.UseMySql(
        connectionString,
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
builder.Services.AddScoped<AiconversationRepository>();
builder.Services.AddScoped<AimessageRepository>();

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
builder.Services.AddScoped<AiChatService>();



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
// Solo redirigir a HTTPS si no estamos en entorno de desarrollo
if (!app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}

app.UseCors("DevelopmentCors");

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
