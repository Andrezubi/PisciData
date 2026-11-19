package ucb.piscidata.database.data.mapper

import ucb.piscidata.database.data.dto.FeedingDetailResponseDto
import ucb.piscidata.database.data.dto.FeedingRecommendationResponseDto
import ucb.piscidata.database.domain.model.FeedingRecord
import ucb.piscidata.database.domain.model.FeedingRecommendation

fun FeedingDetailResponseDto.toRecord(): FeedingRecord {
    val name = buildString {
        append(feedBrand)
        if (!feedProductName.isNullOrBlank()) {
            append(" - ")
            append(feedProductName)
        }
    }
    return FeedingRecord(
        id = id,
        productionCycleId = productionCycleId,
        feedId = feedId,
        feedName = name,
        pelletSizeMm = feedPelletSizeMm,
        proteinPercentage = feedProteinPercentage,
        feedPhase = feedPhase,
        feedingDate = feedingDate,
        feedingTime = feedingTime,
        quantityKg = quantityKg,
        mealNumber = mealNumber,
        behavior = behavior,
        observations = observations
    )
}

fun FeedingRecommendationResponseDto.toModel(): FeedingRecommendation {
    return FeedingRecommendation(
        method = method,
        hasBiometrics = hasBiometrics,
        currentFishCount = currentFishCount,
        averageWeightGrams = averageWeightGrams,
        currentBiomassKg = currentBiomassKg,
        currentAgeDays = currentAgeDays,
        feedingRatePercentage = feedingRatePercentage,
        dailyTotalKg = dailyTotalKg,
        dailyMeals = dailyMeals,
        portionPerMealKg = portionPerMealKg,
        idealFeedPhase = idealFeedPhase,
        idealPelletSizeMinMm = idealPelletSizeMinMm,
        idealPelletSizeMaxMm = idealPelletSizeMaxMm,
        isPowder = isPowder,
        idealProteinMinPercentage = idealProteinMinPercentage,
        idealProteinMaxPercentage = idealProteinMaxPercentage,
        source = source,
        alternativeAgeRecommendation = alternativeAgeRecommendation?.toModel()
    )
}
