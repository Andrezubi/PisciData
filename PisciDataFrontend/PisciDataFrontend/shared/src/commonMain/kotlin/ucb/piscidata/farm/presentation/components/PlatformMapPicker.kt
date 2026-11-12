package ucb.piscidata.farm.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCurrentLocationRequester(
    onLocationObtained: (latitude: Double, longitude: Double) -> Unit,
    onError: (message: String) -> Unit
): () -> Unit

@Composable
expect fun PlatformMapPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismissRequest: () -> Unit,
    onLocationConfirmed: (latitude: Double, longitude: Double) -> Unit
)
