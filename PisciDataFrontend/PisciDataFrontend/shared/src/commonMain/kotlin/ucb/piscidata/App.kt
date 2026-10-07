package ucb.piscidata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import ucb.piscidata.di.sharedModule
import ucb.piscidata.navigation.AppNavHost

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(sharedModule())
        }
    ) {
        MaterialTheme {
            AppNavHost()
        }
    }
}
