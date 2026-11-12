package ucb.piscidata.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    object Login : NavRoute()

    @Serializable
    object Register : NavRoute()

    @Serializable
    data class RegisterFarm(
        val userId: Int,
        val phone: String,
        val pass: String
    ) : NavRoute()

    @Serializable
    object Main : NavRoute()
}
