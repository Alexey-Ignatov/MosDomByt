package ru.acurresearch.dombyta.ui.master_console

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta.data.common.model.Master
import ru.acurresearch.dombyta.data.common.model.Task
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData

interface MasterConsoleView: BaseView<MasterConsoleViewAction>

@DiffElement(MasterConsoleViewPMRenderer::class)
data class MasterConsoleViewPM(
    val token: BaseLCE<CashBoxServerData>,
    val tasks: BaseLCE<List<Task>>,
    val masters: BaseLCE<List<Master>>,
    val tasksPageNew: BaseLCE<List<Task>>,
    val tasksPageInWork: BaseLCE<List<Task>>,
    val tasksPageComplete: BaseLCE<List<Task>>
)

interface MasterConsoleViewPMRenderer {
    fun renderTasksPageNew(tasksPageNew: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>)
    fun renderTasksPageInWork(tasksPageInWork: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>)
    fun renderTasksPageComplete(tasksPageComplete: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>)
    fun renderTasks(tasks: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>)
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

sealed class MasterConsoleViewAction

data class MasterConsoleViewUpdatePMAction(val pm: MasterConsoleViewPM): MasterConsoleViewAction()

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