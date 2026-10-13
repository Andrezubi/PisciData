package ucb.piscidata.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

class ApiService {

    private val client = ApiClient.client

    suspend fun getWeather(): List<WeatherForecast> {

        return client
            .get("http://10.0.2.2:5007/weatherforecast")
            .body()
    }
}

@Serializable
data class WeatherForecast(
    val date: String,
    val temperatureC: Int,
    val summary: String?
)