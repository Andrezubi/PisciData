package ucb.piscidata.tasks.presentation.viewmodel

sealed interface TaskEffect {
    data class ShowToast(val message: String) : TaskEffect
}
