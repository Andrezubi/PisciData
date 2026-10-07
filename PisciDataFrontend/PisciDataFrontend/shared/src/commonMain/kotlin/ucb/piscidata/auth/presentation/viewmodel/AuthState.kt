package ucb.piscidata.auth.presentation.viewmodel

import ucb.piscidata.auth.domain.model.UserModel

data class AuthState(
    val phone: String = "76424237",
    val password: String = "••••••••••••",
    val isLoading: Boolean = false,
    val user: UserModel? = null,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)
