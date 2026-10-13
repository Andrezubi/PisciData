package ucb.piscidata.database.presentation.viewmodel

sealed interface DatabaseEffect {
    data class ShowToast(val message: String) : DatabaseEffect
}
