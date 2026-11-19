package ucb.piscidata.database.domain.model

data class FeedingRecord(
    val id: Int,
    val productionCycleId: Int,
    val feedId: Int,
    val feedName: String,
    val pelletSizeMm: Double?,
    val proteinPercentage: Double?,
    val feedPhase: String?,
    val feedingDate: String,
    val feedingTime: String,
    val quantityKg: Double,
    val mealNumber: Int?,
    val behavior: String?,
    val observations: String?
)

data class FeedingRecommendation(
    val method: String,
    val hasBiometrics: Boolean,
    val currentFishCount: Int,
    val averageWeightGrams: Double?,
    val currentBiomassKg: Double?,
    val currentAgeDays: Int,
    val feedingRatePercentage: Double?,
    val dailyTotalKg: Double,
    val dailyMeals: Int,
    val portionPerMealKg: Double,
    val idealFeedPhase: String?,
    val idealPelletSizeMinMm: Double?,
    val idealPelletSizeMaxMm: Double?,
    val isPowder: Boolean,
    val idealProteinMinPercentage: Double?,
    val idealProteinMaxPercentage: Double?,
    val source: String?,
    val alternativeAgeRecommendation: FeedingRecommendation? = null
)
