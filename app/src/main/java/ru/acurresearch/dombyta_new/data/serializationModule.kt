package ru.acurresearch.dombyta_new.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.dsl.module

val serializationModule = module {
    single<Gson>(
        createdAtStart = true
    ) {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .serializeNulls()
            .create()
    }
}