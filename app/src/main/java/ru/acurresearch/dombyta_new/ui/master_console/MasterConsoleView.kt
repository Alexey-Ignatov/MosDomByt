package ru.acurresearch.dombyta_new.ui.master_console

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ga.nk2ishere.dev.utils.NeverEqualItemContainer
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData

interface MasterConsoleView: BaseView<MasterConsoleViewAction>

@DiffElement(MasterConsoleViewPMRenderer::class)
data class MasterConsoleViewPM(
    val token: BaseLCE<CashBoxServerData>,
    val tasks: BaseLCE<List<Task>>,
    val masters: BaseLCE<List<Master>>,
    val tasksPageNew: BaseLCE<NeverEqualItemContainer<List<Task>>>,
    val tasksPageInWork: BaseLCE<NeverEqualItemContainer<List<Task>>>,
    val tasksPageComplete: BaseLCE<NeverEqualItemContainer<List<Task>>>
)

interface MasterConsoleViewPMRenderer {
    fun renderTasksPageNew(tasksPageNew: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>)
    fun renderTasksPageInWork(tasksPageInWork: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>)
    fun renderTasksPageComplete(tasksPageComplete: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>)
}

sealed class MasterConsoleViewEvent

class MasterConsoleViewInitializeEvent: MasterConsoleViewEvent()
data class MasterConsoleViewTokenUpdatedEvent(val token: String): MasterConsoleViewEvent()
data class MasterConsoleViewTaskInWorkEvent(
    val task: Task,
    val master: Master
): MasterConsoleViewEvent()
data class MasterConsoleViewTaskCompleteEvent(val task: Task): MasterConsoleViewEvent()
data class MasterConsoleViewTaskNewClickedEvent(val task: Task): MasterConsoleViewEvent()
data class MasterConsoleViewTaskInWorkClickedEvent(val task: Task): MasterConsoleViewEvent()
data class MasterConsoleViewPageUpdatedEvent(val pageId: String): MasterConsoleViewEvent()

sealed class MasterConsoleViewAction

data class MasterConsoleViewUpdatePMAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()
data class MasterConsoleViewInitializeTabsAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()
data class MasterConsoleViewUpdateTabsAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()

sealed class MasterConsoleViewSkipAction: MasterConsoleViewAction()

class MasterConsoleViewShowLoginAction: MasterConsoleViewSkipAction()
data class MasterConsoleViewShowTaskNewToInWorkDialogAction(
    val task: Task,
    val pm: MasterConsoleViewPM
): MasterConsoleViewSkipAction()
data class MasterConsoleViewShowTaskInWorkToCompleteDialog(
    val task: Task,
    val pm: MasterConsoleViewPM
): MasterConsoleViewSkipAction()