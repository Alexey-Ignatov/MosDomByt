package ru.acurresearch.dombyta_new

import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs
import ga.nk2ishere.dev.base.BaseApplication
import ga.nk2ishere.dev.base.BaseGlobalEventRelay
import ga.nk2ishere.dev.utils.FileLoggingTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import ru.acurresearch.dombyta_new.data.apiModule
import ru.acurresearch.dombyta_new.data.networkModule
import ru.acurresearch.dombyta_new.data.serializationModule
import timber.log.Timber

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
}