package ru.acurresearch.dombyta_new.ui.order.pay

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.common.model.Order
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition

interface OrderPayView: BaseView<OrderPayViewAction>

@DiffElement(OrderPayViewPMRenderer::class)
data class OrderPayViewPM(
    val order: BaseLCE<Order>,
    val clientName: BaseLCE<String>,
    val clientPhone: BaseLCE<String>
)

interface OrderPayViewPMRenderer {
    fun renderOrder(order: BaseLCE<Order>)
}

sealed class OrderPayViewEvent

class OrderPayViewInitializeEvent: OrderPayViewEvent()
data class OrderPayViewClientNameEditedEvent(val clientName: String): OrderPayViewEvent()
data class OrderPayViewClientPhoneEditedEvent(val clientPhone: String): OrderPayViewEvent()
class OrderPayViewAddGoodButtonClickedEvent: OrderPayViewEvent()
data class OrderPayViewDeleteGoodButtonClickedEvent(val orderPosition: OrderPosition): OrderPayViewEvent()
class OrderPayViewCreateOrderButtonClickedEvent: OrderPayViewEvent()
data class OrderPayViewGoodAddedEvent(val orderPositionId: Long): OrderPayViewEvent()

sealed class OrderPayViewAction

data class OrderPayViewUpdatePMAction(val pm: OrderPayViewPM): OrderPayViewAction()

sealed class OrderPayViewSkipAction: OrderPayViewAction()

data class OrderPayViewShowAddGoodAction(val order: Order): OrderPayViewSkipAction()
data class OrderPayViewShowCreateOrderAction(val orderId: Long): OrderPayViewSkipAction()