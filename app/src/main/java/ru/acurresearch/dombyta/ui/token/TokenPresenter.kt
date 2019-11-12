package ru.acurresearch.dombyta.ui.token

import com.arellomobile.mvp.InjectViewState
import com.google.gson.Gson
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.data.network.interactor.TokenInteractor
import ru.acurresearch.dombyta.data.network.interactor.TokenInteractor.Companion.KEY_DEV_TOKEN
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData
import ru.acurresearch.dombyta.data.network.model.SiteToken
import java.lang.Exception

@InjectViewState
class TokenPresenter: BasePresenter<TokenViewAction, TokenViewEvent, TokenView, TokenViewPM>(), KoinComponent {
    override val TAG: String = "TOKEN_VIEW"
    private val tokenInteractor: TokenInteractor by inject()
    private val gson: Gson by inject()

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
            Observable.zip(
                it.switchMapSingle { tokenInteractor.updateToken(SiteToken(KEY_DEV_TOKEN)) }
                    .map { BaseLCE(false, it, null) }
                    .onErrorReturn { BaseLCE(false, null, it as? Exception) },
                it.flatMap { state },
                BiFunction { token: BaseLCE<CashBoxServerData>, pm: TokenViewPM ->
                    pm.copy(token = token)
                }
            ).doOnNext { handleState(it) }
                .map { when {
                    it.token.error != null -> TokenViewUpdatePMAction(it)
                    else -> TokenViewResultAction(gson.toJson(it.token.content))
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