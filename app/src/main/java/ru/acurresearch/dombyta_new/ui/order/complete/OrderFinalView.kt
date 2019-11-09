package ru.acurresearch.dombyta_new.ui.order.complete

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.common.model.Order
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd

interface OrderFinalView: BaseView<OrderFinalViewAction>

@DiffElement(diffReceiver = OrderFinalViewPMRenderer::class)
data class OrderFinalViewPM(
    val token: BaseLCE<CashBoxServerData>,
    val currentOrder: BaseLCE<Order>,
    val suggestedAction: BaseLCE<String>,
    val printerInitialized: BaseLCE<Boolean>
)

interface OrderFinalViewPMRenderer {
    fun renderCurrentOrder(currentOrder: BaseLCE<Order>)
    fun renderSuggestedAction(suggestedAction: BaseLCE<String>)
}

sealed class OrderFinalViewEvent

class OrderFinalViewInitializeEvent: OrderFinalViewEvent()
class OrderFinalViewPayClickedEvent: OrderFinalViewEvent()
class OrderFinalViewCloseClickedEvent: OrderFinalViewEvent()
class OrderFinalViewPrintClickedEvent: OrderFinalViewEvent()
class OrderFinalViewPrinterInitializedEvent: OrderFinalViewEvent()
class OrderFinalViewLabelPrintConfirmedEvent: OrderFinalViewEvent()

sealed class OrderFinalViewAction

data class OrderFinalViewUpdatePMAction(val pm: OrderFinalViewPM): OrderFinalViewAction()

sealed class OrderFinalViewSkipAction: OrderFinalViewAction()

data class OrderFinalViewPrintLabelAction(val text: String): OrderFinalViewSkipAction()
class OrderFinalViewShowPrintLabelDialog: OrderFinalViewSkipAction()
class OrderFinalViewCloseAction: OrderFinalViewSkipAction()
data class OrderFinalViewOpenSellCloseAction(val positions: List<PositionAdd>): OrderFinalViewSkipAction()
