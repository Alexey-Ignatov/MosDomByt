package ru.acurresearch.dombyta_new.ui.master_console.page

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import kotlinx.android.synthetic.main.complete_task_fragment_master_consol.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.ui.master_console.item.TaskItemComplete
import ru.acurresearch.dombyta_new.ui.master_console.item.TaskItemInWork
import ru.acurresearch.dombyta_new.ui.master_console.item.TaskItemNew
import timber.log.Timber

class MasterConsolePageController(
    args: Bundle,
    val id: String,
    private val taskItemViewId: String,
    private val onTaskItemClicked: (Task) -> Unit
): BaseController(args), MasterConsolePageView, MasterConsolePageViewRenderer {

    constructor(args: Bundle): this(args, "NULL", "NULL", {})

    companion object {
        fun create(id: String, taskItemView: String, onTaskItemClicked: (Task) -> Unit) =
            MasterConsolePageController(Bundle.EMPTY, id, taskItemView, onTaskItemClicked)

        const val TASK_ITEM_VIEW_NEW = "NEW"
        const val TASK_ITEM_VIEW_IN_WORK = "IN_WORK"
        const val TASK_ITEM_VIEW_COMPLETE = "COMPLETE"
    }

    private val diffElement = MasterConsolePageViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: MasterConsolePageViewPM? = null

    private val taskListAdapter = GroupAdapter<GroupieViewHolder>()
    private fun getTaskItemView(task: Task) = when(taskItemViewId) {
        TASK_ITEM_VIEW_NEW -> TaskItemNew(task, onTaskItemClicked)
        TASK_ITEM_VIEW_IN_WORK -> TaskItemInWork(task, onTaskItemClicked)
        TASK_ITEM_VIEW_COMPLETE -> TaskItemComplete(task, onTaskItemClicked)
        else -> null
    }!!

    override fun renderTasks(tasks: BaseLCE<List<Task>>) {
        tasks.content?.let {
            taskListAdapter.update(it.map { getTaskItemView(it) })
        }
    }

    private fun updatePMAction(action: MasterConsolePageViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    @InjectPresenter lateinit var presenter: MasterConsolePagePresenter
    @ProvidePresenter fun providePresenter(): MasterConsolePagePresenter = MasterConsolePagePresenter(id)
    override fun getLayoutId(): Int = R.layout.complete_task_fragment_master_consol

    fun handleUpdateDataEventOutside(
        tasks: BaseLCE<List<Task>>,
        masters: BaseLCE<List<Master>>
    ) {
        presenter.handleViewEvent(MasterConsolePageViewUpdateDataEvent(tasks, masters))
    }

    override fun initView(view: View) {
        view.task_list.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        view.task_list.adapter = taskListAdapter
    }

    override fun applyAction(action: MasterConsolePageViewAction) { when(action) {
        is MasterConsolePageViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: MasterConsolePageViewAction) { when(action) {

    } }

}