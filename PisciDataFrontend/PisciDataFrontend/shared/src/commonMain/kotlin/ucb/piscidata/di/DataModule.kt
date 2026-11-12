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
import ucb.piscidata.database.data.repository.FarmLookupRepositoryImpl
import ucb.piscidata.database.data.repository.InventoryRepositoryImpl
import ucb.piscidata.database.data.repository.PondRepositoryImpl
import ucb.piscidata.database.data.repository.ProductionCycleRepositoryImpl
import ucb.piscidata.database.data.service.FarmLookupService
import ucb.piscidata.database.data.service.InventoryService
import ucb.piscidata.database.data.service.PondService
import ucb.piscidata.database.data.service.ProductionCycleService
import ucb.piscidata.database.domain.repository.FarmLookupRepository
import ucb.piscidata.database.domain.repository.InventoryRepository
import ucb.piscidata.database.domain.repository.PondRepository
import ucb.piscidata.database.domain.repository.ProductionCycleRepository
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.data.repository.TaskRepositoryImpl
import ucb.piscidata.tasks.data.service.TaskService
import ucb.piscidata.tasks.domain.repository.TaskRepository
import ucb.piscidata.farm.data.datasource.FarmRemoteDataSource
import ucb.piscidata.farm.data.repository.FarmRepositoryImpl
import ucb.piscidata.farm.data.service.FarmService
import ucb.piscidata.farm.domain.repository.FarmRepository
import ucb.piscidata.profile.data.datasource.ProfileRemoteDataSource
import ucb.piscidata.profile.data.repository.ProfileRepositoryImpl
import ucb.piscidata.profile.data.service.ProfileService
import ucb.piscidata.profile.domain.repository.ProfileRepository

val dataModule = module {
    single { SessionManager() }
    single<AuthRemoteDataSource> { AuthService() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<ChatRemoteDataSource> { ChatService() }
    single<ChatRepository> { ChatRepositoryImpl(get()) }

    // Database services and repositories
    single { ProductionCycleService() }
    single<ProductionCycleRepository> { ProductionCycleRepositoryImpl(get()) }

    single { PondService(get()) }
    single<PondRepository> { PondRepositoryImpl(get()) }

    single { InventoryService() }
    single<InventoryRepository> { InventoryRepositoryImpl(get()) }

    single { FarmLookupService() }
    single<FarmLookupRepository> { FarmLookupRepositoryImpl(get()) }

    single { TaskService() }
    single<TaskRepository> { TaskRepositoryImpl(get()) }

    single<FarmRemoteDataSource> { FarmService() }
    single<FarmRepository> { FarmRepositoryImpl(get()) }

    single<ProfileRemoteDataSource> { ProfileService() }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}
