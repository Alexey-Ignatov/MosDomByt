package ru.acurresearch.dombyta.data

import android.content.Context
import ga.nk2ishere.dev.utils.RxBoxRepository
import io.objectbox.BoxStore
import org.koin.dsl.module
import ru.acurresearch.dombyta.data.common.interactor.MasterInteractor
import ru.acurresearch.dombyta.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta.data.common.interactor.TaskInteractor
import ru.acurresearch.dombyta.data.common.model.*
import ru.acurresearch.dombyta.data.network.interactor.TokenInteractor

val apiModule = module {
    single { MyObjectBox.builder().androidContext(get<Context>()).build() }
    single { TokenInteractor(get(), get(), get()) }
    single { TaskInteractor(RxBoxRepository(get<BoxStore>().boxFor(Task::class.java)), get()) }
    single { MasterInteractor(RxBoxRepository(get<BoxStore>().boxFor(Master::class.java)), get()) }
    single { OrderInteractor(
        RxBoxRepository(get<BoxStore>().boxFor(Order::class.java)),
        RxBoxRepository(get<BoxStore>().boxFor(OrderPosition::class.java)),
        RxBoxRepository(get<BoxStore>().boxFor(ServiceItemCustom::class.java)),
        get()
    ) }
}