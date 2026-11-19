using MySqlConnector;

namespace PisciDataBackend.Infraestructure.Persistence
{
    public static class DatabaseInitializer
    {
        public static async Task EnsureSeededAsync(string connectionString, string contentRootPath, ILogger logger)
        {
            var seedPath = Path.Combine(contentRootPath, "SqlInit", "piscidatadb_seed.sql");
            if (!File.Exists(seedPath))
            {
                logger.LogInformation("Seed SQL no encontrado en {Path}; se omite inicialización.", seedPath);
                return;
            }

            try
            {
                var builder = new MySqlConnectionStringBuilder(connectionString)
                {
                    AllowUserVariables = true
                };
                await using var conn = new MySqlConnection(builder.ConnectionString);
                await conn.OpenAsync();

                await using (var checkCmd = new MySqlCommand("SHOW TABLES LIKE 'user';", conn))
                {
                    var exists = await checkCmd.ExecuteScalarAsync();
                    if (exists != null)
                    {
                        logger.LogInformation("La base de datos ya está inicializada (tabla 'user' existe).");
                        return;
                    }
                }

                logger.LogInformation("Inicializando esquema y datos de PisciData desde {Path}...", seedPath);
                var sql = await File.ReadAllTextAsync(seedPath);
                await using var seedCmd = new MySqlCommand(sql, conn) { CommandTimeout = 120 };
                await seedCmd.ExecuteNonQueryAsync();
                logger.LogInformation("Base de datos PisciData inicializada correctamente.");
            }
            catch (Exception ex)
            {
                logger.LogError(ex, "Error al inicializar automáticamente la base de datos.");
            }
        }
    }
}
