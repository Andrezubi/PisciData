using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class FeedValidator
    {
        public static List<string> Validate(CreateFeedDto dto) => ValidateCommon(dto.FarmId, dto.Brand, dto.ProteinPercentage, dto.PelletSizeMm, dto.StockKg, dto.MinimumStockKg);

        public static List<string> Validate(UpdateFeedDto dto) => ValidateCommon(dto.FarmId, dto.Brand, dto.ProteinPercentage, dto.PelletSizeMm, dto.StockKg, dto.MinimumStockKg);

        private static List<string> ValidateCommon(int farmId, string brand, decimal? proteinPercentage, decimal? pelletSizeMm, decimal? stockKg, decimal? minimumStockKg)
        {
            var errors = new List<string>();

            if (farmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(brand))
                errors.Add("Brand is required.");

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
