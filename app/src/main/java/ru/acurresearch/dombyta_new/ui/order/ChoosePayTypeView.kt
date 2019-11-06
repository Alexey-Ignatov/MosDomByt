package ru.acurresearch.dombyta_new.ui.order

import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData

interface ChoosePayTypeView: BaseView<ChoosePayTypeViewAction>

data class ChoosePayTypeViewPM(
    val token: BaseLCE<CashBoxServerData>
)

sealed class ChoosePayTypeViewEvent

class ChoosePayTypeViewInitializeEvent: ChoosePayTypeViewEvent()
data class ChoosePayTypeViewTokenUpdatedEvent(val token: String): ChoosePayTypeViewEvent()
class ChoosePayTypeViewPostPayClickedEvent: ChoosePayTypeViewEvent()
class ChoosePayTypeViewPrePayClickedEvent: ChoosePayTypeViewEvent()
class ChoosePayTypeViewRegularPayClickedEvent: ChoosePayTypeViewEvent()
class ChoosePayTypeViewGetOrderClickedEvent: ChoosePayTypeViewEvent()

sealed class ChoosePayTypeViewAction

data class ChoosePayTypeViewUpdatePMAction(val pm: ChoosePayTypeViewPM): ChoosePayTypeViewAction()

sealed class ChoosePayTypeViewSkipAction: ChoosePayTypeViewAction()

class ChoosePayTypeViewShowLoginAction: ChoosePayTypeViewSkipAction()
class ChoosePayTypeViewShowPostPayAction: ChoosePayTypeViewSkipAction()
class ChoosePayTypeViewShowPrePayAction: ChoosePayTypeViewSkipAction()
class ChoosePayTypeViewShowRegularPayAction: ChoosePayTypeViewSkipAction()
class ChoosePayTypeViewShowGetOrderAction: ChoosePayTypeViewSkipAction()