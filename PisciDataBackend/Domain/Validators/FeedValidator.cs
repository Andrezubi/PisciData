using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class FeedValidator
    {
        public static List<string> Validate(CreateFeedDto dto) => ValidateCommon(dto.FarmId, dto.Brand, dto.ProductName, dto.Phase, dto.ProteinPercentage, dto.PelletSizeMm, dto.StockKg, dto.MinimumStockKg);

        public static List<string> Validate(UpdateFeedDto dto) => ValidateCommon(dto.FarmId, dto.Brand, dto.ProductName, dto.Phase, dto.ProteinPercentage, dto.PelletSizeMm, dto.StockKg, dto.MinimumStockKg);

        private static List<string> ValidateCommon(int farmId, string brand, string? productName, string? phase, decimal? proteinPercentage, decimal? pelletSizeMm, decimal? stockKg, decimal? minimumStockKg)
        {
            var errors = new List<string>();

            if (farmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(brand))
                errors.Add("Brand is required.");
            else if (brand.Length > 100)
                errors.Add("La marca no puede superar los 100 caracteres.");

            if (productName?.Length > 150)
                errors.Add("El nombre del producto no puede superar los 150 caracteres.");

            if (phase?.Length > 10)
                errors.Add("La fase no puede superar los 10 caracteres.");

            if (proteinPercentage.HasValue && (proteinPercentage < 0 || proteinPercentage > 100))
                errors.Add("ProteinPercentage must be between 0 and 100.");

            if (pelletSizeMm.HasValue && pelletSizeMm < 0)
                errors.Add("PelletSizeMm must be greater than or equal to 0.");

            if (stockKg.HasValue && stockKg < 0)
                errors.Add("StockKg must be greater than or equal to 0.");

            if (minimumStockKg.HasValue && minimumStockKg < 0)
                errors.Add("MinimumStockKg must be greater than or equal to 0.");

            return errors;
        }
    }
}
