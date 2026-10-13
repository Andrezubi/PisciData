package ucb.piscidata.auth.domain.model

data class UserModel(
    val name: String,
    val phone: String,
    val role: String,
    val farmName: String
)
