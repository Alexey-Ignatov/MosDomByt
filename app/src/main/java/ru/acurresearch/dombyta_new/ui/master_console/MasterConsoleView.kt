package ru.acurresearch.dombyta_new.ui.master_console

import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData

interface MasterConsoleView: BaseView<MasterConsoleViewAction>

data class MasterConsoleViewPM(
    val token: BaseLCE<CashBoxServerData>,
    val tasks: BaseLCE<List<Task>>,
    val masters: BaseLCE<List<Master>>
)

sealed class MasterConsoleViewEvent

class MasterConsoleViewInitializeEvent: MasterConsoleViewEvent()
data class MasterConsoleViewTokenUpdatedEvent(val token: String): MasterConsoleViewEvent()
data class MasterConsoleViewTaskInWorkEvent(
    val task: Task,
    val master: Master
): MasterConsoleViewEvent()
data class MasterConsoleViewTaskCompleteEvent(val task: Task): MasterConsoleViewEvent()

sealed class MasterConsoleViewAction

data class MasterConsoleViewInitializeTabsAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()
data class MasterConsoleViewUpdateTabsAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()

sealed class MasterConsoleViewSkipAction: MasterConsoleViewAction()

class MasterConsoleViewShowLoginAction: MasterConsoleViewSkipAction()