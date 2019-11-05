package ru.acurresearch.dombyta_new.data.common.interactor

import ga.nk2ishere.dev.utils.RxBoxRepository
import io.reactivex.Single
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.network.Api
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData

class MasterInteractor(
    private val box: RxBoxRepository<Master>,
    private val api: Api
) {
    fun updateMasters(
        token: CashBoxServerData
    ) = api.fetchMasters(token.authHeader)
        .flatMap {
            box.createOrUpdate(it)
                .andThen(Single.fromCallable { it })
        }

    fun getMasters() =
        box.read()
}