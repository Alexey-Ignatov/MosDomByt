package ru.acurresearch.dombyta_new.ui.token

import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData
import ru.acurresearch.dombyta_new.data.network.model.SiteToken
import java.lang.Exception

class TokenPresenter: BasePresenter<TokenViewAction, TokenViewEvent, TokenView, TokenViewPM>(), KoinComponent {
    override val TAG: String = "TOKEN_VIEW"
    private val tokenInteractor: TokenInteractor by inject()

    private fun handleInitializeEvent() =
        ObservableTransformer<TokenViewInitializeEvent, TokenViewAction> {
            it.map { TokenViewPM(
                token = BaseLCE(false, null, null)
            ) }
            .doOnNext { handleState(it) }
            .map { TokenViewUpdatePMAction(it) }
        }

    private fun handleLoginClickedEvent() =
        ObservableTransformer<TokenViewLoginClickedEvent, TokenViewAction> {
            Observable.zip(it.switchMapSingle { tokenInteractor.updateToken(SiteToken("a")) }, it.flatMap { state }, BiFunction { token: CashBoxServerData, pm: TokenViewPM ->
                pm.copy(token = BaseLCE(false, token, null))
            } ).onErrorReturn { TokenViewPM(
                    token = BaseLCE(false, null, it as? Exception)
            ) }.map { when {
                it.token.error != null -> TokenViewUpdatePMAction(it)
                else -> TokenViewResultAction(it.token.content?.token ?: "")
            } }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(TokenViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: TokenViewAction): Boolean = viewAction is TokenViewSkipAction

    override fun createSharedList(shared: Observable<TokenViewEvent>): List<Observable<TokenViewAction>> =
        listOf(
            shared.ofType(TokenViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(TokenViewLoginClickedEvent::class.java).compose(handleLoginClickedEvent())
        )
}