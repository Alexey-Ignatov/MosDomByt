package ru.acurresearch.dombyta.ui.order.search

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta.data.common.model.Order
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData

interface OrderSearchView: BaseView<OrderSearchViewAction>

@DiffElement(OrderSearchViewPMRenderer::class)
data class OrderSearchViewPM(
    val token: BaseLCE<CashBoxServerData>,
    val foundOrders: BaseLCE<List<Order>>,
    val searchString: BaseLCE<String>
)

interface OrderSearchViewPMRenderer {
    fun renderFoundOrders(foundOrders: BaseLCE<List<Order>>)
}

sealed class OrderSearchViewEvent

class OrderSearchViewInitializeEvent: OrderSearchViewEvent()
data class OrderSearchViewSearchStringEditedEvent(val searchString: String): OrderSearchViewEvent()
class OrderSearchViewSearchButtonClicked: OrderSearchViewEvent()
data class OrderSearchViewOrderClickedEvent(val order: Order): OrderSearchViewEvent()

sealed class OrderSearchViewAction

data class OrderSearchViewUpdatePMAction(val pm: OrderSearchViewPM): OrderSearchViewAction()

sealed class OrderSearchViewSkipAction: OrderSearchViewAction()

data class OrderSearchViewShowOrderCompleteAction(
    val pm: OrderSearchViewPM,
    val orderId: Long
): OrderSearchViewSkipAction()