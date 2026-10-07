package ucb.piscidata.di

import org.koin.dsl.module
import ucb.piscidata.auth.domain.usecase.LoginUseCase
import ucb.piscidata.chat.domain.usecase.SendMessageUseCase
import ucb.piscidata.database.domain.usecase.*
import ucb.piscidata.tasks.domain.usecase.*

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { SendMessageUseCase(get()) }

    factory { GetCiclosUseCase(get()) }
    factory { SaveCicloUseCase(get()) }
    factory { DeleteCicloUseCase(get()) }
    factory { GetPondsUseCase(get()) }
    factory { GetInventoryUseCase(get()) }
    factory { UpdateInventoryQtyUseCase(get()) }

    factory { GetTasksUseCase(get()) }
    factory { ToggleTaskUseCase(get()) }
    factory { AddTaskUseCase(get()) }
}
