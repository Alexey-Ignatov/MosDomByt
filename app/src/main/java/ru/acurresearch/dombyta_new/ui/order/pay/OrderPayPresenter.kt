package ru.acurresearch.dombyta_new.ui.order.pay

import com.arellomobile.mvp.InjectViewState
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta_new.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta_new.data.common.model.Client
import ru.acurresearch.dombyta_new.data.common.model.Order

//TODO name phone price checks
@InjectViewState
class OrderPayPresenter(
    private val paymentType: String
): BasePresenter<OrderPayViewAction, OrderPayViewEvent, OrderPayView, OrderPayViewPM>(), KoinComponent {
    override val TAG: String = "ORDER_PAY"

    private val orderInteractor: OrderInteractor by inject()

    private fun handleInitializeEvent(): ObservableTransformer<OrderPayViewInitializeEvent, OrderPayViewAction> =
        ObservableTransformer {
            it.map { OrderPayViewPM(
                order = when(paymentType) {
                    Constants.BillingType.POSTPAY -> BaseLCE(false, Order.newPostPaid(), null)
                    Constants.BillingType.PREPAY -> BaseLCE(false, Order.newPrePaid(), null)
                    else -> BaseLCE<Order>(false, null, null)
                },
                clientName = BaseLCE(false, "", null),
                clientPhone = BaseLCE(false, "", null)
            ) }.doOnNext { handleState(it) }
                .map { OrderPayViewUpdatePMAction(it) }
        }

    private fun handleClientNameEditedEvent(): ObservableTransformer<OrderPayViewClientNameEditedEvent, OrderPayViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: OrderPayViewClientNameEditedEvent, state: OrderPayViewPM ->
                event to state
            }).map { (event, state) ->
                state.copy(
                    clientName = BaseLCE(false, event.clientName, null)
                )
            }.doOnNext { handleState(it) }
                .map { OrderPayViewUpdatePMAction(it) }
        }

    private fun handleClientPhoneEditedEvent(): ObservableTransformer<OrderPayViewClientPhoneEditedEvent, OrderPayViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: OrderPayViewClientPhoneEditedEvent, state: OrderPayViewPM ->
                event to state
            }).map { (event, state) ->
                state.copy(
                    clientPhone = BaseLCE(false, event.clientPhone, null)
                )
            }.doOnNext { handleState(it) }
                .map { OrderPayViewUpdatePMAction(it) }
        }

    private fun handleAddGoodButtonClickedEvent(): ObservableTransformer<OrderPayViewAddGoodButtonClickedEvent, OrderPayViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .doOnNext { handleState(it) }
                .map { OrderPayViewShowAddGoodAction(it.order.content!!.apply {
                    client.target = Client(0, it.clientName.content ?: "", it.clientPhone.content ?: "")
                }) }
        }

    private fun handleCreateOrderButtonClickedEvent(): ObservableTransformer<OrderPayViewCreateOrderButtonClickedEvent, OrderPayViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter {
                    it.order.content != null
                            && it.order.content?.positionsList?.isNullOrEmpty() == false
                            && it.clientName.content.isNullOrBlank().not()
                            && it.clientPhone.content.isNullOrBlank().not()
                }.switchMapSingle { state ->
                    orderInteractor.saveOrder(state.order.content!!)
                        .map { state.copy(
                            order = BaseLCE(false, it, null)
                        ) }
                }.doOnNext { handleState(it) }
                .map { OrderPayViewShowCreateOrderAction(it.order.content!!.id) }
        }

    private fun handleGoodAddedEvent(): ObservableTransformer<OrderPayViewGoodAddedEvent, OrderPayViewAction> =
        ObservableTransformer {
            Observable.zip(
                it,
                it.flatMap { state }
                    .filter { it.order.content != null },
                BiFunction { event: OrderPayViewGoodAddedEvent, state: OrderPayViewPM ->
                    event to state
                }
            ).switchMapSingle { (event, state) ->
                orderInteractor.getOrderPositionById(event.orderPositionId)
                    .map { it to state }
            }.switchMapSingle { (orderPosition, state) ->
                orderInteractor.addOrderPositionToOrder(state.order.content!!, orderPosition)
                    .map { state.copy(
                        order = BaseLCE(false, it, null)
                    ) }
            }.doOnNext { handleState(it) }
                .map { OrderPayViewUpdatePMAction(it) }
        }

    private fun handleDeleteGoodButtonClickedEvent(): ObservableTransformer<OrderPayViewDeleteGoodButtonClickedEvent, OrderPayViewAction> =
        ObservableTransformer {
            Observable.zip(
                it,
                it.flatMap { state }
                    .filter { it.order.content != null },
                BiFunction { event: OrderPayViewDeleteGoodButtonClickedEvent, state: OrderPayViewPM ->
                    event to state
                }
            ).switchMapSingle { (event, state) ->
                orderInteractor.deleteOrderPositionFromOrder(state.order.content!!, event.orderPosition)
                    .map { state.copy(
                        order = BaseLCE(false, it, null)
                    ) }
            }.doOnNext { handleState(it) }
                .map { OrderPayViewUpdatePMAction(it) }
        }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(OrderPayViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: OrderPayViewAction): Boolean = viewAction is OrderPayViewSkipAction

    override fun createSharedList(shared: Observable<OrderPayViewEvent>): List<Observable<OrderPayViewAction>> =
        listOf(
            shared.ofType(OrderPayViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(OrderPayViewClientNameEditedEvent::class.java).compose(handleClientNameEditedEvent()),
            shared.ofType(OrderPayViewClientPhoneEditedEvent::class.java).compose(handleClientPhoneEditedEvent()),
            shared.ofType(OrderPayViewAddGoodButtonClickedEvent::class.java).compose(handleAddGoodButtonClickedEvent()),
            shared.ofType(OrderPayViewCreateOrderButtonClickedEvent::class.java).compose(handleCreateOrderButtonClickedEvent()),
            shared.ofType(OrderPayViewGoodAddedEvent::class.java).compose(handleGoodAddedEvent()),
            shared.ofType(OrderPayViewDeleteGoodButtonClickedEvent::class.java).compose(handleDeleteGoodButtonClickedEvent())
        )
}