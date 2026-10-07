package ucb.piscidata.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ucb.piscidata.auth.presentation.viewmodel.AuthViewModel
import ucb.piscidata.chat.presentation.viewmodel.ChatViewModel
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel
import ucb.piscidata.tasks.presentation.viewmodel.TaskViewModel

val presentationModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::DatabaseViewModel)
    viewModelOf(::TaskViewModel)
}
