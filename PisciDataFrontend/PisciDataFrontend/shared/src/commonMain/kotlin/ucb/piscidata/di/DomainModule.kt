package ucb.piscidata.di

import org.koin.dsl.module
import ucb.piscidata.auth.domain.usecase.LoginUseCase
import ucb.piscidata.auth.domain.usecase.RegisterUseCase
import ucb.piscidata.chat.domain.usecase.SendMessageUseCase
import ucb.piscidata.database.domain.usecase.*
import ucb.piscidata.tasks.domain.usecase.*

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { SendMessageUseCase(get()) }

    factory { GetFarmsUseCase(get()) }
    factory { GetCiclosUseCase(get()) }
    factory { SaveCicloUseCase(get()) }
    factory { DeleteCicloUseCase(get()) }
    factory { ToggleCicloExpandUseCase(get()) }
    factory { GetPondsUseCase(get()) }
    factory { GetInventoryUseCase(get()) }
    factory { UpdateInventoryQtyUseCase(get()) }

    factory { GetTasksUseCase(get()) }
    factory { ToggleTaskUseCase(get()) }
    factory { AddTaskUseCase(get()) }
}
