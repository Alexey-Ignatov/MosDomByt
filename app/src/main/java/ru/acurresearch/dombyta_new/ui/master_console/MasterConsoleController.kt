package ru.acurresearch.dombyta_new.ui.master_console

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.utils.NeverEqualItemContainer
import kotlinx.android.synthetic.main.activity_master_consol.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Master
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.ui.master_console.page.MasterConsolePageController
import ru.acurresearch.dombyta_new.ui.master_console.page.MasterConsolePageController.Companion.TASK_ITEM_VIEW_COMPLETE
import ru.acurresearch.dombyta_new.ui.master_console.page.MasterConsolePageController.Companion.TASK_ITEM_VIEW_IN_WORK
import ru.acurresearch.dombyta_new.ui.master_console.page.MasterConsolePageController.Companion.TASK_ITEM_VIEW_NEW
import ru.acurresearch.dombyta_new.ui.token.TokenActivity

class MasterConsoleController(args: Bundle): BaseController(args), MasterConsoleView, MasterConsoleViewPMRenderer {
    companion object {
        fun create() =
            MasterConsoleController(Bundle.EMPTY)

        const val TAB_TITLE_NEW = "Новые"
        const val TAB_TITLE_IN_WORK = "В работе"
        const val TAB_TITLE_COMPLETE = "Завершенные"
        const val TAB_TITLE_NULL = "NULL"
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

    private val pagerAdapter: RouterPagerAdapter =
        object: RouterPagerAdapter(this) {
            override fun getCount(): Int = 3
            override fun getPageTitle(position: Int): CharSequence? =
                when(position) {
                    0 -> TAB_TITLE_NEW
                    1 -> TAB_TITLE_IN_WORK
                    2 -> TAB_TITLE_COMPLETE
                    else -> TAB_TITLE_NULL
                }

            override fun configureRouter(router: Router, position: Int) { when(position) {
                0 -> router.setRoot(RouterTransaction.with(MasterConsolePageController(
                    args,
                    TASK_ITEM_VIEW_NEW,
                    TASK_ITEM_VIEW_NEW,
                    onTaskItemNewClicked
                )))
                1 -> router.setRoot(RouterTransaction.with(MasterConsolePageController(
                    args,
                    TASK_ITEM_VIEW_IN_WORK,
                    TASK_ITEM_VIEW_IN_WORK,
                    onTaskItemInWorkClicked
                )))
                2 -> router.setRoot(RouterTransaction.with(MasterConsolePageController(
                    args,
                    TASK_ITEM_VIEW_COMPLETE,
                    TASK_ITEM_VIEW_COMPLETE,
                    onTaskItemCompleteClicked
                )))
            } }
        }
    private val pageChangeListener = object: ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

        override fun onPageSelected(position: Int) {
            updateCurrentTab(position)
        }
    }

    private fun updateCurrentTab(position: Int) {
        pagerAdapter.getRouter(position)
            ?.backstack?.mapNotNull { it.controller() as? MasterConsolePageController }
            ?.forEach { presenter.handleViewEvent(MasterConsoleViewPageUpdatedEvent(it.id)) }
    }

    private fun updateTabById(id: String, tasks: BaseLCE<List<Task>>, masters: BaseLCE<List<Master>>) {
        (0 until pagerAdapter.count).mapNotNull { pagerAdapter.getRouter(it) }
            .flatMap { it.backstack }
            .mapNotNull { it.controller() as? MasterConsolePageController }
            .filter { it.id == id }
            .forEach { it.handleUpdateDataEventOutside(tasks, masters) }
    }

    override fun renderTasksPageNew(tasksPageNew: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>) {
        updateTabById(TASK_ITEM_VIEW_NEW, BaseLCE(false, tasksPageNew.content?.item, null), masters)
    }

    override fun renderTasksPageInWork(tasksPageInWork: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>) {
        updateTabById(TASK_ITEM_VIEW_IN_WORK, BaseLCE(false, tasksPageInWork.content?.item, null), masters)
    }

    override fun renderTasksPageComplete(tasksPageComplete: BaseLCE<NeverEqualItemContainer<List<Task>>>, masters: BaseLCE<List<Master>>) {
        updateTabById(TASK_ITEM_VIEW_COMPLETE, BaseLCE(false, tasksPageComplete.content?.item, null), masters)
    }

    private fun updatePMAction(action: MasterConsoleViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun initializeTabsAction(action: MasterConsoleViewInitializeTabsAction) {
        view?.view_pager?.adapter = pagerAdapter
        view?.tabs?.setupWithViewPager(view?.view_pager)
        updateCurrentTab(view?.view_pager?.currentItem ?: 0)
    }

    private fun updateTabsAction(action: MasterConsoleViewUpdateTabsAction) {
        updateCurrentTab(view?.view_pager?.currentItem ?: 0)
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
        view.view_pager.addOnPageChangeListener(pageChangeListener)
    }

    override fun applyAction(action: MasterConsoleViewAction) { when(action) {
        is MasterConsoleViewUpdatePMAction -> updatePMAction(action)
        is MasterConsoleViewInitializeTabsAction -> initializeTabsAction(action)
        is MasterConsoleViewUpdateTabsAction -> updateTabsAction(action)
    } }

    override fun applyActionWithSkip(action: MasterConsoleViewAction) { when(action) {
        is MasterConsoleViewShowLoginAction -> showLoginAction(action)
        is MasterConsoleViewShowTaskNewToInWorkDialogAction -> showTaskNewToInWorkDialogAction(action)
        is MasterConsoleViewShowTaskInWorkToCompleteDialog -> showTaskInWorkToCompleteDialogAction(action)
    } }


}