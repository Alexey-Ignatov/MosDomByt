package ru.acurresearch.dombyta_new.data

import android.content.Context
import ga.nk2ishere.dev.utils.RxBoxRepository
import org.koin.dsl.module
import ru.acurresearch.dombyta_new.data.common.model.Check
import ru.acurresearch.dombyta_new.data.common.model.MyObjectBox

val apiModule = module {
    single { MyObjectBox.builder().androidContext(get<Context>()).build() }
}