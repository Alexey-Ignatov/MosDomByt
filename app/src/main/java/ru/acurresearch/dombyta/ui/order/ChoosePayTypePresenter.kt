package ru.acurresearch.dombyta.ui.order

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.data.network.interactor.TokenInteractor

@InjectViewState
class ChoosePayTypePresenter: BasePresenter<ChoosePayTypeViewAction, ChoosePayTypeViewEvent, ChoosePayTypeView, ChoosePayTypeViewPM>(), KoinComponent {
    override val TAG: String = "ru.acurresearch.dombyta.ui.CHOOSE_PAY_TYPE"

    private val tokenInteractor: TokenInteractor by inject()
    private val gson: Gson by inject()

    private fun handleInitializeEvent(): ObservableTransformer<ChoosePayTypeViewInitializeEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            it.switchMapSingle { tokenInteractor.getToken() }
                .map { ChoosePayTypeViewPM(
                    token = BaseLCE(false, it.orNull(), null)
                ) }
                .doOnNext { handleState(it) }
                .map { when {
                    it.token.content != null -> ChoosePayTypeViewUpdatePMAction(it)
                    else -> ChoosePayTypeViewShowLoginAction()
                } }
        }

    private fun handleTokenUpdatedEvent(): ObservableTransformer<ChoosePayTypeViewTokenUpdatedEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: ChoosePayTypeViewTokenUpdatedEvent, state: ChoosePayTypeViewPM ->
                state.copy(
                    token = BaseLCE(false, gson.fromJson(event.token), null)
                )
            }).doOnNext { handleState(it) }
                .map { ChoosePayTypeViewUpdatePMAction(it) }
        }

    private fun handlePostPayClickedEvent(): ObservableTransformer<ChoosePayTypeViewPostPayClickedEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter { it.token.content != null }
                .map { ChoosePayTypeViewShowPostPayAction() }
        }

    private fun handlePrePayClickedEvent(): ObservableTransformer<ChoosePayTypeViewPrePayClickedEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter { it.token.content != null }
                .map { ChoosePayTypeViewShowPrePayAction() }
        }

    private fun handleRegularPayClickedEvent(): ObservableTransformer<ChoosePayTypeViewRegularPayClickedEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter { it.token.content != null }
                .map { ChoosePayTypeViewShowRegularPayAction() }
        }

    private fun handleGetOrderClickedEvent(): ObservableTransformer<ChoosePayTypeViewGetOrderClickedEvent, ChoosePayTypeViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter { it.token.content != null }
                .map { ChoosePayTypeViewShowGetOrderAction() }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(ChoosePayTypeViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: ChoosePayTypeViewAction): Boolean = viewAction is ChoosePayTypeViewSkipAction
    override fun createSharedList(shared: Observable<ChoosePayTypeViewEvent>): List<Observable<ChoosePayTypeViewAction>> =
        listOf(
            shared.ofType(ChoosePayTypeViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(ChoosePayTypeViewTokenUpdatedEvent::class.java).compose(handleTokenUpdatedEvent()),
            shared.ofType(ChoosePayTypeViewPostPayClickedEvent::class.java).compose(handlePostPayClickedEvent()),
            shared.ofType(ChoosePayTypeViewPrePayClickedEvent::class.java).compose(handlePrePayClickedEvent()),
            shared.ofType(ChoosePayTypeViewRegularPayClickedEvent::class.java).compose(handleRegularPayClickedEvent()),
            shared.ofType(ChoosePayTypeViewGetOrderClickedEvent::class.java).compose(handleGetOrderClickedEvent())
        )
}