package ru.acurresearch.dombyta_new.ui.order.search

import com.arellomobile.mvp.InjectViewState
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta_new.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor
import java.lang.Exception

@InjectViewState
class OrderSearchPresenter(): BasePresenter<OrderSearchViewAction, OrderSearchViewEvent, OrderSearchView, OrderSearchViewPM>(), KoinComponent {
    override val TAG: String = "ORDER_SEARCH"

    private val tokenInteractor: TokenInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    private fun handleInitializeEvent(): ObservableTransformer<OrderSearchViewInitializeEvent, OrderSearchViewAction> =
        ObservableTransformer {
            it.switchMapSingle { tokenInteractor.getToken() }
                .map { OrderSearchViewPM(
                    token = BaseLCE(false, it.orNull(), null),
                    foundOrders = BaseLCE(false, listOf(), null),
                    searchString = BaseLCE(false, "", null)
                ) }
                .doOnNext { handleState(it) }
                .map { OrderSearchViewUpdatePMAction(it) }
        }

    private fun handleSearchStringEditedEvent(): ObservableTransformer<OrderSearchViewSearchStringEditedEvent, OrderSearchViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: OrderSearchViewSearchStringEditedEvent, state: OrderSearchViewPM ->
                event to state
            }).map {  (event, state) ->
                state.copy(
                    searchString = BaseLCE(false, event.searchString, null)
                )
            }.doOnNext { handleState(it) }
                .map { OrderSearchViewUpdatePMAction(it) }
        }

    private fun handleSearchButtonClicked(): ObservableTransformer<OrderSearchViewSearchButtonClicked, OrderSearchViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .switchMapSingle { state ->
                    orderInteractor.searchOrder(state.token.content!!, state.searchString.content ?: "")
                        .map { BaseLCE(false, it, null) }
                        .onErrorReturn { BaseLCE(false, listOf(), it as? Exception) }
                        .map { state.copy(
                            foundOrders = it
                        ) }
                }.doOnNext { handleState(it) }
                .map { OrderSearchViewUpdatePMAction(it) }
        }

    private fun handleOrderClickedEvent(): ObservableTransformer<OrderSearchViewOrderClickedEvent, OrderSearchViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: OrderSearchViewOrderClickedEvent, state: OrderSearchViewPM ->
                event to state
            }).switchMapSingle { (event, state) ->
                orderInteractor.saveOrder(event.order)
                    .map { it to state }
            }.doOnNext { (_, state) -> handleState(state) }
                .map { (order, state) ->
                    OrderSearchViewShowOrderCompleteAction(state, order.id)
                }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(OrderSearchViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: OrderSearchViewAction): Boolean = viewAction is OrderSearchViewSkipAction

    override fun createSharedList(shared: Observable<OrderSearchViewEvent>): List<Observable<OrderSearchViewAction>> =
        listOf(
            shared.ofType(OrderSearchViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(OrderSearchViewSearchStringEditedEvent::class.java).compose(handleSearchStringEditedEvent()),
            shared.ofType(OrderSearchViewSearchButtonClicked::class.java).compose(handleSearchButtonClicked()),
            shared.ofType(OrderSearchViewOrderClickedEvent::class.java).compose(handleOrderClickedEvent())
        )
}