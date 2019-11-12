package ru.acurresearch.dombyta.data.common.interactor

import ga.nk2ishere.dev.utils.RxBoxRepository
import io.reactivex.Single
import ru.acurresearch.dombyta.data.common.model.Master
import ru.acurresearch.dombyta.data.network.Api
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData

class MasterInteractor(
    private val box: RxBoxRepository<Master>,
    private val api: Api
) {
    fun updateMasters(
        token: CashBoxServerData
    ) = api.fetchMasters(token.authHeader)
        .flatMap {
            box.createOrUpdate(it)
                .andThen(Single.just(it))
        }

    fun getMasters() =
        box.read()
}