package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeedingDetailResponseDto(
    val id: Int,
    val productionCycleId: Int,
    val feedId: Int,
    val feedBrand: String = "",
    val feedProductName: String? = null,
    val feedPelletSizeMm: Double? = null,
    val feedProteinPercentage: Double? = null,
    val feedPhase: String? = null,
    val feedingDate: String,
    val feedingTime: String,
    val quantityKg: Double,
    val mealNumber: Int? = null,
    val behavior: String? = null,
    val observations: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isActive: Boolean? = null
)

@Serializable
data class FeedingRecommendationResponseDto(
    val method: String = "BY_WEIGHT",
    val hasBiometrics: Boolean = false,
    val productionCycleId: Int,
    val currentFishCount: Int = 0,
    val averageWeightGrams: Double? = null,
    val currentBiomassKg: Double? = null,
    val currentAgeDays: Int = 0,
    val feedingRatePercentage: Double? = null,
    val dailyTotalKg: Double = 0.0,
    val dailyMeals: Int = 1,
    val portionPerMealKg: Double = 0.0,
    val idealFeedPhase: String? = null,
    val idealPelletSizeMinMm: Double? = null,
    val idealPelletSizeMaxMm: Double? = null,
    val isPowder: Boolean = false,
    val idealProteinMinPercentage: Double? = null,
    val idealProteinMaxPercentage: Double? = null,
    val source: String? = null,
    val alternativeAgeRecommendation: FeedingRecommendationResponseDto? = null
)

@Serializable
data class CreateFeedingRequestDto(
    val productionCycleId: Int,
    val feedId: Int,
    val feedingDate: String,
    val feedingTime: String,
    val quantityKg: Double,
    val mealNumber: Int? = null,
    val behavior: String? = null,
    val observations: String? = null
)
