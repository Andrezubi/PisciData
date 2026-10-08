package ucb.piscidata.di

import org.koin.dsl.module
import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.data.repository.AuthRepositoryImpl
import ucb.piscidata.auth.data.service.AuthService
import ucb.piscidata.auth.domain.repository.AuthRepository
import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.data.repository.ChatRepositoryImpl
import ucb.piscidata.chat.data.service.ChatService
import ucb.piscidata.chat.domain.repository.ChatRepository
import ucb.piscidata.database.data.repository.DatabaseRepositoryImpl
import ucb.piscidata.database.data.service.DatabaseService
import ucb.piscidata.database.domain.repository.DatabaseRepository
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.data.repository.TaskRepositoryImpl
import ucb.piscidata.tasks.data.service.TaskService
import ucb.piscidata.tasks.domain.repository.TaskRepository

val dataModule = module {
    single<AuthRemoteDataSource> { AuthService() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<ChatRemoteDataSource> { ChatService() }
    single<ChatRepository> { ChatRepositoryImpl(get()) }

    single { DatabaseService() }
    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }

    single { TaskService() }
    single<TaskRepository> { TaskRepositoryImpl(get()) }

    single { SessionManager() }
}
