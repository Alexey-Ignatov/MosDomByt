package ru.acurresearch.dombyta.ui.master_console

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import kotlinx.android.synthetic.main.activity_master_consol.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.common.model.Master
import ru.acurresearch.dombyta.data.common.model.Task
import ru.acurresearch.dombyta.ui.master_console.item.ExpandableHeaderItem
import ru.acurresearch.dombyta.ui.master_console.item.TaskItemComplete
import ru.acurresearch.dombyta.ui.master_console.item.TaskItemInWork
import ru.acurresearch.dombyta.ui.master_console.item.TaskItemNew
import ru.acurresearch.dombyta.ui.token.TokenActivity

class MasterConsoleController(args: Bundle): BaseController(args), MasterConsoleView, MasterConsoleViewPMRenderer {
    companion object {
        fun create() =
            MasterConsoleController(Bundle.EMPTY)

        const val TAB_TITLE_NEW = "Новые"
        const val TAB_TITLE_IN_WORK = "В работе"
        const val TAB_TITLE_COMPLETE = "Завершенные"
    }

    private val diffElement = MasterConsoleViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: MasterConsoleViewPM? = null

    private val onTaskItemNewClicked: (Task) -> Unit = {
        presenter.handleViewEvent(MasterConsoleViewTaskNewClickedEvent(it))
    }
    private val onTaskItemInWorkClicked: (Task) -> Unit = {
        presenter.handleViewEvent(MasterConsoleViewTaskInWorkClickedEvent(it))
    }
    private val onTaskItemCompleteClicked: (Task) -> Unit = onTaskItemNewClicked

    private val tasksAdapter = GroupAdapter<GroupieViewHolder>()
    private val newTasksExpandableGroup = ExpandableGroup(ExpandableHeaderItem(TAB_TITLE_NEW), true)
    private val inWorkTasksExpandableGroup = ExpandableGroup(ExpandableHeaderItem(TAB_TITLE_IN_WORK), true)
    private val completeTasksExpandableGroup = ExpandableGroup(ExpandableHeaderItem(TAB_TITLE_COMPLETE), true)
    private val newTasksSection = Section()
    private val inWorkTasksSection = Section()
    private val completeTasksSection = Section()

    override fun renderTasksPageNew(tasksPageNew: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>) {
        newTasksSection.update(
            tasksPageNew.content?.map { TaskItemNew(it, onTaskItemNewClicked) } ?: listOf()
        )
    }

    override fun renderTasksPageInWork(tasksPageInWork: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>) {
        inWorkTasksSection.update(
            tasksPageInWork.content?.map { TaskItemInWork(it, onTaskItemInWorkClicked) } ?: listOf()
        )
    }

    override fun renderTasksPageComplete(tasksPageComplete: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>) {
        completeTasksSection.update(
            tasksPageComplete.content?.map { TaskItemComplete(it, onTaskItemCompleteClicked) } ?: listOf()
        )
    }

    private fun updatePMAction(action: MasterConsoleViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun showLoginAction(action: MasterConsoleViewShowLoginAction) {
        startActivityForResult(Intent(activity, TokenActivity::class.java), TokenActivity.REQUEST_CODE)
    }

    private fun showTaskNewToInWorkDialogAction(action: MasterConsoleViewShowTaskNewToInWorkDialogAction) {
        val alertDialog = AlertDialog.Builder(view?.context!!)
        alertDialog.setTitle("Чтобы взять заказ в работу, выберете мастера:")

        val masters = action.pm.masters.content?.map { it.name }?.toTypedArray() ?: arrayOf()
        alertDialog.setItems(masters) { dialog, which ->
            presenter.handleViewEvent(MasterConsoleViewTaskInWorkEvent(action.task,
                action.pm.masters.content?.find { it.name == masters[which] }!!
            ))
        }
        alertDialog.setNegativeButton("Отмена") { dialog, id ->
            dialog.cancel()
        }
        alertDialog.create().show()
    }

    private fun showTaskInWorkToCompleteDialogAction(action: MasterConsoleViewShowTaskInWorkToCompleteDialog) {
        val alertDialog = AlertDialog.Builder(view?.context!!)
        alertDialog.setTitle("Завершить выполнение заказа?")
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton("Да") { dialog, id ->
            presenter.handleViewEvent(MasterConsoleViewTaskCompleteEvent(action.task))
        }
        alertDialog.setNegativeButton("Нет") { dialog, id ->
            dialog.cancel()
        }
        alertDialog.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { when(requestCode) {
        TokenActivity.REQUEST_CODE -> if(data != null && resultCode == TokenActivity.RESULT_OK)
            presenter.handleViewEvent(MasterConsoleViewTokenUpdatedEvent(data.getStringExtra(TokenActivity.RESULT_KEY_TOKEN)))
    } }


    @InjectPresenter lateinit var presenter: MasterConsolePresenter
    @ProvidePresenter fun providePresenter(): MasterConsolePresenter = MasterConsolePresenter()
    override fun getLayoutId(): Int = R.layout.activity_master_consol

    override fun initView(view: View) {
        tasksAdapter.add(newTasksExpandableGroup)
        tasksAdapter.add(inWorkTasksExpandableGroup)
        tasksAdapter.add(completeTasksExpandableGroup)
        newTasksExpandableGroup.add(newTasksSection)
        inWorkTasksExpandableGroup.add(inWorkTasksSection)
        completeTasksExpandableGroup.add(completeTasksSection)
        view.task_list.adapter = tasksAdapter
        view.task_list.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
    }

    override fun applyAction(action: MasterConsoleViewAction) { when(action) {
        is MasterConsoleViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: MasterConsoleViewAction) { when(action) {
        is MasterConsoleViewShowLoginAction -> showLoginAction(action)
        is MasterConsoleViewShowTaskNewToInWorkDialogAction -> showTaskNewToInWorkDialogAction(action)
        is MasterConsoleViewShowTaskInWorkToCompleteDialog -> showTaskInWorkToCompleteDialogAction(action)
    } }


}