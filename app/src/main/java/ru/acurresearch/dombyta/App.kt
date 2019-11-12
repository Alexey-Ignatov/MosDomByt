package ru.acurresearch.dombyta

import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs
import ga.nk2ishere.dev.base.BaseApplication
import ga.nk2ishere.dev.base.BaseGlobalEventRelay
import ga.nk2ishere.dev.utils.FileLoggingTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import ru.acurresearch.dombyta.data.apiModule
import ru.acurresearch.dombyta.data.networkModule
import ru.acurresearch.dombyta.data.serializationModule
import timber.log.Timber
import java.lang.Exception

class App: BaseApplication() {
    override fun provideGlobalEventRelay(): BaseGlobalEventRelay = BaseGlobalEventRelay()

    override fun onApplicationInitialize() {
        Timber.plant(Timber.DebugTree(), FileLoggingTree(this))
        startKoin {
            androidContext(this@App)
            logger(EmptyLogger())
            modules(listOf(
                apiModule, networkModule, serializationModule
            ))
        }
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

    }

    companion object {
        fun log(message: String, vararg args: Any) {
            Timber.tag("ru.acurresearch.dombyta")
                .i(message, *args)
        }

        fun log(message: Any?) {
            Timber.tag("ru.acurresearch.dombyta")
                .i(message.toString())
        }

        fun log(message: Throwable) {
            Timber.tag("ru.acurresearch.dombyta")
                .i(message)
        }
    }
}