package ucb.piscidata.profile.domain.model

data class UserProfileModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String,
    val createdAt: String? = null
)
