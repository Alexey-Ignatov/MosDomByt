package ru.acurresearch.dombyta_new.ui.order.complete

import com.arellomobile.mvp.InjectViewState
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta_new.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta_new.data.common.model.Order
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor
import timber.log.Timber
import java.lang.Exception
import java.util.*

@InjectViewState
class OrderFinalPresenter(
    private val orderId: Long,
    private val orderAlreadyExists: Boolean
): BasePresenter<OrderFinalViewAction, OrderFinalViewEvent, OrderFinalView, OrderFinalViewPM>(), KoinComponent {
    override val TAG: String = "ORDER_FINAL"

    private val tokenInteractor: TokenInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    private fun handleInitializeEvent(): ObservableTransformer<OrderFinalViewInitializeEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.switchMapSingle { tokenInteractor.getToken() }
                .switchMapSingle { token ->
                    orderInteractor.getOrderById(orderId)
                        .map { it to token }
                }.map { (order, token) -> OrderFinalViewPM(
                    token = BaseLCE(false, token.orNull(), null),
                    currentOrder = BaseLCE(false, order, null),
                    suggestedAction = BaseLCE(false, order.suggestAction(), null),
                    printerInitialized = BaseLCE(false, false, null)
                ) }
                .doOnNext { handleState(it) }
                .map { OrderFinalViewUpdatePMAction(it) }
        }

    private fun handlePayClickedEvent(): ObservableTransformer<OrderFinalViewPayClickedEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .doOnNext { handleState(it) }
                .map { OrderFinalViewOpenSellCloseAction(
                    it.currentOrder.content!!.positionsList
                        .map { it.toEvotorPositionAdd() }
                ) }
        }

    private fun handleCloseClickedEvent(): ObservableTransformer<OrderFinalViewCloseClickedEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .switchMapSingle { state ->
                    orderInteractor.updateOrder(
                        state.token.content!!,
                        state.currentOrder.content!!.apply {
                            this.status = Order.OrderStatus.CLOSED
                        },
                        orderAlreadyExists
                    ).map { BaseLCE(false, it, null) }
                        .onErrorReturn { BaseLCE(false, state.currentOrder.content, it as? Exception) }
                        .map { state.copy(
                            currentOrder = it
                        ) }
                }.doOnNext { handleState(it) }
                .map { OrderFinalViewCloseAction() }
        }

    private fun handlePrintClickedEvent(): ObservableTransformer<OrderFinalViewPrintClickedEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                    //TODO CHECK ON REAL DEV
//                .filter { it.printerInitialized.content == true }
                .switchMapSingle { state ->
                    orderInteractor.updateOrder(
                        state.token.content!!,
                        state.currentOrder.content!!.apply {
                            dateCreated = Date()
                            status = Order.OrderStatus.CREATED
                        },
                        orderAlreadyExists
                    ).map { BaseLCE(false, it, null) }
                        .onErrorReturn { it.printStackTrace();BaseLCE(false, state.currentOrder.content, it as? Exception) }
                        .map { state.copy(
                            currentOrder = it
                        ) }
                }.filter { it.currentOrder.error == null }
                .doOnNext { handleState(it) }
                .map {
                    handleViewAction(OrderFinalViewShowPrintLabelDialog())
                    OrderFinalViewPrintLabelAction(it.currentOrder.content!!.printKvitok ?: "")
                }
        }

    private fun handlePrinterInitializedEvent(): ObservableTransformer<OrderFinalViewPrinterInitializedEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .map { it.copy(
                    printerInitialized = BaseLCE(false, true, null)
                ) }
                .doOnNext { handleState(it) }
                .map { OrderFinalViewUpdatePMAction(it) }
        }

    private fun handleLabelPrintConfirmedEvent(): ObservableTransformer<OrderFinalViewLabelPrintConfirmedEvent, OrderFinalViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter {
                    it.currentOrder.content?.printLabel.isNullOrBlank().not()
                            && it.currentOrder.content?.printKvitok.isNullOrBlank().not()
//                            && it.printerInitialized.content == true
                }.switchMapSingle {
                    orderInteractor.clearCurrentOrder()
                        .andThen(orderInteractor.saveCurrentOrder(it.currentOrder.content!!, orderAlreadyExists))
                        .andThen(Single.just(it))
                }.doOnNext { handleState(it) }
                .map {
                    handleViewAction(OrderFinalViewPrintLabelAction(it.currentOrder.content!!.printLabel ?: ""))
                    OrderFinalViewCloseAction()
                }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(OrderFinalViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: OrderFinalViewAction): Boolean = viewAction is OrderFinalViewSkipAction

    override fun createSharedList(shared: Observable<OrderFinalViewEvent>): List<Observable<OrderFinalViewAction>> =
        listOf(
            shared.ofType(OrderFinalViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(OrderFinalViewPayClickedEvent::class.java).compose(handlePayClickedEvent()),
            shared.ofType(OrderFinalViewCloseClickedEvent::class.java).compose(handleCloseClickedEvent()),
            shared.ofType(OrderFinalViewPrintClickedEvent::class.java).compose(handlePrintClickedEvent()),
            shared.ofType(OrderFinalViewPrinterInitializedEvent::class.java).compose(handlePrinterInitializedEvent()),
            shared.ofType(OrderFinalViewLabelPrintConfirmedEvent::class.java).compose(handleLabelPrintConfirmedEvent())
        )
}