package ru.acurresearch.dombyta_new.ui.order.pay.add_good

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition
import ru.acurresearch.dombyta_new.data.common.model.ServiceItemCustom
import java.util.*

interface AddGoodView: BaseView<AddGoodViewAction>

@DiffElement(diffReceiver = AddGoodViewPMRenderer::class)
data class AddGoodViewPM(
    val goods: BaseLCE<List<ServiceItemCustom>>,
    val selectedGood: BaseLCE<ServiceItemCustom>,
    val date: BaseLCE<Date>,
    val price: BaseLCE<String>,
    val listShown: BaseLCE<Boolean>,
    val nameShown: BaseLCE<Boolean>,
    val priceShown: BaseLCE<Boolean>,
    val deadlineShown: BaseLCE<Boolean>
)

interface AddGoodViewPMRenderer {
    fun renderGoods(goods: BaseLCE<List<ServiceItemCustom>>)
    fun renderSelectedGood(selectedGood: BaseLCE<ServiceItemCustom>)
    fun renderListShown(listShown: BaseLCE<Boolean>)
    fun renderNameShown(nameShown: BaseLCE<Boolean>)
    fun renderPriceShown(priceShown: BaseLCE<Boolean>)
    fun renderDeadlineShown(deadlineShown: BaseLCE<Boolean>)
}

sealed class AddGoodViewEvent

class AddGoodViewInitializeEvent: AddGoodViewEvent()
class AddGoodViewAddDeadlineClickedEvent: AddGoodViewEvent()
data class AddGoodViewPriceEditedEvent(val price: String): AddGoodViewEvent()
data class AddGoodViewDateEditedEvent(
    val year: Int,
    val month: Int,
    val day: Int
): AddGoodViewEvent()
data class AddGoodViewTimeEditedEvent(
    val hour: Int,
    val minute: Int
): AddGoodViewEvent()
class AddGoodViewPriceClickedEvent: AddGoodViewEvent()
data class AddGoodViewGoodClickedEvent(val good: ServiceItemCustom): AddGoodViewEvent()

sealed class AddGoodViewAction

data class AddGoodViewUpdatePMAction(val pm: AddGoodViewPM): AddGoodViewAction()

sealed class AddGoodViewSkipAction: AddGoodViewAction()

data class AddGoodViewResultAction(val orderPositionId: Long): AddGoodViewSkipAction()