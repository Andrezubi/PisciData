package ucb.piscidata.farm.presentation.viewmodel

data class RegisterFarmState(
    val userId: Int = 0,
    val phone: String = "",
    val pass: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isMapPickerOpen: Boolean = false,
    val error: String? = null
)
