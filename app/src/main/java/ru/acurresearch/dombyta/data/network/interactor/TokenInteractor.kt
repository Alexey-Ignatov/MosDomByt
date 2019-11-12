package ru.acurresearch.dombyta.data.network.interactor

import android.content.Context
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.hadisatrio.optional.Optional
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.Single
import ru.acurresearch.dombyta.data.network.Api
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData
import ru.acurresearch.dombyta.data.network.model.SiteToken

class TokenInteractor(
    private val context: Context,
    private val gson: Gson,
    private val api: Api
) {
    companion object {
        const val KEY_TOKEN = "TOKEN"
        const val KEY_DEV_TOKEN = "DEV"
    }

    fun updateToken(
        siteToken: SiteToken
    ): Single<CashBoxServerData> =
        when(siteToken.token) {
            KEY_DEV_TOKEN ->  Single.fromCallable { CashBoxServerData("214940b6555e74eeeb800b169fe80673b941b267", "aa") }
            else -> api.getToken(siteToken)
        }.doOnSuccess { Prefs.putString(KEY_TOKEN, gson.toJson(it)) }

    fun getToken(
    ): Single<Optional<CashBoxServerData>> =
        Single.fromCallable { Prefs.getString(KEY_TOKEN, "") }
            .map {
                if(it != "") Optional.of(gson.fromJson<CashBoxServerData>(it))
                else Optional.absent()
            }
}