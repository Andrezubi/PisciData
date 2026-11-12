package ucb.piscidata.farm.presentation.viewmodel

sealed class RegisterFarmEvent {
    data class Init(val userId: Int, val phone: String, val pass: String) : RegisterFarmEvent()
    data class OnNameChanged(val value: String) : RegisterFarmEvent()
    data class OnDescriptionChanged(val value: String) : RegisterFarmEvent()
    data class OnAddressChanged(val value: String) : RegisterFarmEvent()
    data class OnLocationSelected(val latitude: Double, val longitude: Double) : RegisterFarmEvent()
    data class SetMapPickerOpen(val open: Boolean) : RegisterFarmEvent()
    data class SetError(val error: String?) : RegisterFarmEvent()
    object OnSubmitClicked : RegisterFarmEvent()
}
