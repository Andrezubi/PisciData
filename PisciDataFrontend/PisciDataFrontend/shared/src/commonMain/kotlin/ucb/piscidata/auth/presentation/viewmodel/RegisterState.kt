package ucb.piscidata.auth.presentation.viewmodel

import ucb.piscidata.auth.domain.model.UserModel

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val role: String = "Owner",
    val isLoading: Boolean = false,
    val registeredUser: UserModel? = null,
    val isRegistered: Boolean = false,
    val error: String? = null
)
