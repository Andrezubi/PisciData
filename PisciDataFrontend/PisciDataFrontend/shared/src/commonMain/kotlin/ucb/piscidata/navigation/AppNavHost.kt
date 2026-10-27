package ucb.piscidata.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import ucb.piscidata.auth.presentation.screen.LoginScreen
import ucb.piscidata.auth.presentation.screen.RegisterScreen
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.farm.domain.usecase.GetMyFarmsUseCase
import ucb.piscidata.session.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val sessionManager: SessionManager = koinInject()
    val getMyFarmsUseCase: GetMyFarmsUseCase = koinInject()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Login
    ) {
        composable<NavRoute.Login> {
            LoginScreen(
                onLoginSuccess = {
                    ApiClient.sessionManager = sessionManager
                    scope.launch {
                        getMyFarmsUseCase().onSuccess { farms ->
                            val farmModels = farms.map { ucb.piscidata.database.domain.model.FarmModel(it.id, it.name) }
                            sessionManager.setFarms(farmModels)
                        }
                    }
                    navController.navigate(NavRoute.Main) {
                        popUpTo(NavRoute.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoute.Register)
                }
            )
        }
        composable<NavRoute.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    ApiClient.sessionManager = sessionManager
                    scope.launch {
                        getMyFarmsUseCase().onSuccess { farms ->
                            val farmModels = farms.map { ucb.piscidata.database.domain.model.FarmModel(it.id, it.name) }
                            sessionManager.setFarms(farmModels)
                        }
                    }
                    navController.navigate(NavRoute.Main) {
                        popUpTo(NavRoute.Login) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<NavRoute.Main> {
            MainTabScreen(
                onLogout = {
                    sessionManager.clear()
                    ApiClient.sessionManager = sessionManager
                    navController.navigate(NavRoute.Login) {
                        popUpTo(NavRoute.Main) { inclusive = true }
                    }
                }
            )
        }
    }
}
