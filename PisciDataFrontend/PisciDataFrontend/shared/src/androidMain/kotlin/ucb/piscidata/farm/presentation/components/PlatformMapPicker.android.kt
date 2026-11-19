package ucb.piscidata.farm.presentation.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat

@SuppressLint("MissingPermission")
private fun retrieveLocation(
    context: Context,
    onLocationObtained: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
        onError("Servicio de ubicación no disponible en el dispositivo.")
        return
    }

    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsEnabled && !isNetworkEnabled) {
        onError("El GPS o la ubicación están apagados. Por favor actívalos.")
        return
    }

    val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    val lastNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    val bestLocation = when {
        lastGps != null && lastNet != null -> if (lastGps.time >= lastNet.time) lastGps else lastNet
        lastGps != null -> lastGps
        else -> lastNet
    }

    if (bestLocation != null) {
        onLocationObtained(bestLocation.latitude, bestLocation.longitude)
        return
    }

    val provider = if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER
    try {
        locationManager.requestSingleUpdate(
            provider,
            object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    onLocationObtained(loc.latitude, loc.longitude)
                }
                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            },
            Looper.getMainLooper()
        )
    } catch (e: Exception) {
        onError("No se pudo obtener la ubicación actual: ${e.message}")
    }
}

@Composable
actual fun rememberCurrentLocationRequester(
    onLocationObtained: (latitude: Double, longitude: Double) -> Unit,
    onError: (message: String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            retrieveLocation(context, onLocationObtained, onError)
        } else {
            onError("Se necesitan permisos de ubicación para obtener tu posición actual.")
        }
    }

    return {
        val finePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePermission == PackageManager.PERMISSION_GRANTED || coarsePermission == PackageManager.PERMISSION_GRANTED) {
            retrieveLocation(context, onLocationObtained, onError)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

private class MapBridge(private val onCoordSelected: (Double, Double) -> Unit) {
    @JavascriptInterface
    fun onLocationSelected(lat: Double, lng: Double) {
        onCoordSelected(lat, lng)
    }
}

@Composable
actual fun PlatformMapPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismissRequest: () -> Unit,
    onLocationConfirmed: (latitude: Double, longitude: Double) -> Unit
) {
    val defaultLat = initialLatitude ?: -16.5000
    val defaultLng = initialLongitude ?: -68.1500

    var selectedLat by remember { mutableStateOf(defaultLat) }
    var selectedLng by remember { mutableStateOf(defaultLng) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B2B3B))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Seleccionar Ubicación",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Toca el mapa o arrastra el marcador",
                            color = Color(0xFFBAC7CE),
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text("Cerrar", color = Color.White)
                    }
                }

                // Map View
                Box(modifier = Modifier.weight(1f)) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                addJavascriptInterface(
                                    MapBridge { lat, lng ->
                                        selectedLat = lat
                                        selectedLng = lng
                                    },
                                    "AndroidBridge"
                                )

                                val html = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                      <meta charset="utf-8" />
                                      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                                      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                                      <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                                      <style>
                                        html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; }
                                      </style>
                                    </head>
                                    <body>
                                      <div id="map"></div>
                                      <script>
                                        var map = L.map('map').setView([$defaultLat, $defaultLng], 14);
                                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                          maxZoom: 19,
                                          attribution: '© OpenStreetMap'
                                        }).addTo(map);

                                        var marker = L.marker([$defaultLat, $defaultLng], {draggable: true}).addTo(map);

                                        function notifyPos(lat, lng) {
                                          marker.setLatLng([lat, lng]);
                                          if (window.AndroidBridge) {
                                            window.AndroidBridge.onLocationSelected(lat, lng);
                                          }
                                        }

                                        map.on('click', function(e) {
                                          notifyPos(e.latlng.lat, e.latlng.lng);
                                        });

                                        marker.on('dragend', function(e) {
                                          var p = marker.getLatLng();
                                          notifyPos(p.lat, p.lng);
                                        });
                                      </script>
                                    </body>
                                    </html>
                                """.trimIndent()

                                loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null)
                            }
                        }
                    )
                }

                // Bottom Panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Coordenadas:",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B818B),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${selectedLat.toString().take(9)}, ${selectedLng.toString().take(9)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B)
                                )
                            }
                        }

                        Button(
                            onClick = { onLocationConfirmed(selectedLat, selectedLng) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                        ) {
                            Text("Confirmar esta ubicación", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
