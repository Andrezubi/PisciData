package ucb.piscidata.farm.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberCurrentLocationRequester(
    onLocationObtained: (latitude: Double, longitude: Double) -> Unit,
    onError: (message: String) -> Unit
): () -> Unit {
    return {
        onError("La obtención de ubicación GPS directa está disponible en Android.")
    }
}

@Composable
actual fun PlatformMapPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismissRequest: () -> Unit,
    onLocationConfirmed: (latitude: Double, longitude: Double) -> Unit
) {
    var lat by remember { mutableStateOf(initialLatitude ?: -16.5) }
    var lng by remember { mutableStateOf(initialLongitude ?: -68.15) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selector de Mapa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Coordenadas seleccionadas:")
                Text("Latitud: $lat\nLongitud: $lng")
            }
        },
        confirmButton = {
            Button(onClick = { onLocationConfirmed(lat, lng) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
