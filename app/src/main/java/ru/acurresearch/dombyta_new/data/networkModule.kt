package ru.acurresearch.dombyta_new.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.acurresearch.dombyta_new.data.network.Api

val networkModule = module {
    single(
        qualifier = StringQualifier("BASE_URL"),
        createdAtStart = true
    ) { "https://acur-research24.ru" }

    single<Api> {
        Retrofit.Builder()
            .baseUrl(get<String>(StringQualifier("BASE_URL")))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(ChuckerInterceptor(get()))
                    .build()
            )
            .build()
            .create(Api::class.java)
    }
}