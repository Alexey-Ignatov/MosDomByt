package ru.acurresearch.dombyta_new.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.dsl.module
import ru.acurresearch.dombyta_new.data.common.model.*

val serializationModule = module {
    single<Gson>(
        createdAtStart = true
    ) {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .serializeNulls()
            .registerTypeAdapter(Check::class.java, Check.serializer)
            .registerTypeAdapter(Check::class.java, Check.deserializer)
            .registerTypeAdapter(CheckPosition::class.java, CheckPosition.serializer)
            .registerTypeAdapter(CheckPosition::class.java, CheckPosition.deserializer)
            .registerTypeAdapter(Client::class.java, Client.serializer)
            .registerTypeAdapter(Client::class.java, Client.deserializer)
            .registerTypeAdapter(Master::class.java, Master.serializer)
            .registerTypeAdapter(Master::class.java, Master.deserializer)
            .registerTypeAdapter(Order::class.java, Order.serializer)
            .registerTypeAdapter(Order::class.java, Order.deserializer)
            .registerTypeAdapter(OrderPosition::class.java, OrderPosition.serializer)
            .registerTypeAdapter(OrderPosition::class.java, OrderPosition.deserializer)
            .registerTypeAdapter(ServiceItemCustom::class.java, ServiceItemCustom.serializer)
            .registerTypeAdapter(ServiceItemCustom::class.java, ServiceItemCustom.deserializer)
            .registerTypeAdapter(Task::class.java, Task.serializer)
            .registerTypeAdapter(Task::class.java, Task.deserializer)
            .create()
    }
}