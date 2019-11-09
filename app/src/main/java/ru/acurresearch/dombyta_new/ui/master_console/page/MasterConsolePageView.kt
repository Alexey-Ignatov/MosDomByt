package ru.acurresearch.dombyta_new.ui.master_console.page

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.Task

interface MasterConsolePageView: BaseView<MasterConsolePageViewAction>

@DiffElement(diffReceiver = MasterConsolePageViewRenderer::class)
data class MasterConsolePageViewPM(
    val tasks: BaseLCE<List<Task>>,
    val masters: BaseLCE<List<Master>>
)

interface MasterConsolePageViewRenderer {
    fun renderTasks(tasks: BaseLCE<List<Task>>)
}

sealed class MasterConsolePageViewEvent

class MasterConsolePageViewInitializeEvent: MasterConsolePageViewEvent()
data class MasterConsolePageViewUpdateDataEvent(
    val tasks: BaseLCE<List<Task>>,
    val masters: BaseLCE<List<Master>>
): MasterConsolePageViewEvent()

sealed class MasterConsolePageViewAction

data class MasterConsolePageViewUpdatePMAction(
    val pm: MasterConsolePageViewPM
): MasterConsolePageViewAction()

sealed class MasterConsolePageViewSkipAction: MasterConsolePageViewAction()