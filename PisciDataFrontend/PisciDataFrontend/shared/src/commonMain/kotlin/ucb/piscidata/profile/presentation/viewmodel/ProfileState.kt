package ucb.piscidata.profile.presentation.viewmodel

import ucb.piscidata.profile.domain.model.UserProfileModel

data class ProfileState(
    val profile: UserProfileModel? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val role: String = "Admin",
    val createdAt: String? = null,
    val editMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
