package ucb.piscidata.di

import org.koin.core.module.Module

fun sharedModule(): List<Module> = listOf(
    dataModule,
    presentationModule,
    domainModule
)
