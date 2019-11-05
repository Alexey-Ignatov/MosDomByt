package ru.acurresearch.dombyta_new.data

import android.content.Context
import ga.nk2ishere.dev.utils.RxBoxRepository
import io.objectbox.Box
import io.objectbox.BoxStore
import org.koin.dsl.module
import ru.acurresearch.dombyta_new.data.common.interactor.MasterInteractor
import ru.acurresearch.dombyta_new.data.common.interactor.TaskInteractor
import ru.acurresearch.dombyta_new.data.common.model.Check
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.MyObjectBox
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor

val apiModule = module {
    single { MyObjectBox.builder().androidContext(get<Context>()).build() }
    single { TokenInteractor(get(), get(), get()) }
    single { TaskInteractor(RxBoxRepository(get<BoxStore>().boxFor(Task::class.java)), get()) }
    single { MasterInteractor(RxBoxRepository(get<BoxStore>().boxFor(Master::class.java)), get()) }
}