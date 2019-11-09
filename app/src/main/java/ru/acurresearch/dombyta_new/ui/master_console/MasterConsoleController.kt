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
import kotlinx.android.synthetic.main.activity_master_consol.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Task
import ru.acurresearch.dombyta_new.ui.master_console.page.MasterConsolePageController
import ru.acurresearch.dombyta_new.ui.token.TokenActivity

class MasterConsoleController(args: Bundle): BaseController(args), MasterConsoleView {

    companion object {
        fun create() =
            MasterConsoleController(Bundle.EMPTY)

        const val TAB_TITLE_NEW = "Новые"
        const val TAB_TITLE_IN_WORK = "В работе"
        const val TAB_TITLE_COMPLETE = "Завершенные"
        const val TAB_TITLE_NULL = "NULL"
    }

    private lateinit var state: MasterConsoleViewPM

    private val onTaskItemNewClicked: (Task) -> Unit = {
        presenter.handleViewEvent(MasterConsoleTaskNewClickedEvent(it))
    }
    private val onTaskItemInWorkClicked: (Task) -> Unit = {
        presenter.handleViewEvent(MasterConsoleTaskInWorkClickedEvent(it))
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
                    MasterConsolePageController.TASK_ITEM_VIEW_NEW,
                    MasterConsolePageController.TASK_ITEM_VIEW_NEW,
                    onTaskItemNewClicked
                )))
                1 -> router.setRoot(RouterTransaction.with(MasterConsolePageController(
                    args,
                    MasterConsolePageController.TASK_ITEM_VIEW_IN_WORK,
                    MasterConsolePageController.TASK_ITEM_VIEW_IN_WORK,
                    onTaskItemInWorkClicked
                )))
                2 -> router.setRoot(RouterTransaction.with(MasterConsolePageController(
                    args,
                    MasterConsolePageController.TASK_ITEM_VIEW_COMPLETE,
                    MasterConsolePageController.TASK_ITEM_VIEW_COMPLETE,
                    onTaskItemCompleteClicked
                )))
            } }
        }
    private val pageChangeListener = object: ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

        override fun onPageSelected(position: Int) {
            updateCurrentPage()
        }
    }
    private fun updateCurrentPage() {
        pagerAdapter.getRouter(view?.view_pager?.currentItem ?: 0)
            ?.backstack?.mapNotNull { it.controller() as? MasterConsolePageController }
            ?.forEach { it.handleUpdateDataEventOutside(
                masters = state.masters,
                tasks = when(it.id) {
                    MasterConsolePageController.TASK_ITEM_VIEW_NEW -> state.tasks.copy(
                        content = state.tasks.content?.filter { it.status == Task.TaskStatus.NEW }
                    )
                    MasterConsolePageController.TASK_ITEM_VIEW_IN_WORK -> state.tasks.copy(
                        content = state.tasks.content?.filter { it.status == Task.TaskStatus.IN_WORK }
                    )
                    MasterConsolePageController.TASK_ITEM_VIEW_COMPLETE -> state.tasks.copy(
                        content = state.tasks.content?.filter { it.status == Task.TaskStatus.COMPLETE }
                    )
                    else -> state.tasks
                }
            ) }
    }

    private fun initializeTabsAction(action: MasterConsoleViewInitializeTabsAction) {
        view?.view_pager?.adapter = pagerAdapter
        view?.tabs?.setupWithViewPager(view?.view_pager)

        state = action.pm
        updateCurrentPage()
    }

    private fun updateTabsAction(action: MasterConsoleViewUpdateTabsAction) {
        state = action.pm
        updateCurrentPage()
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
        is MasterConsoleViewInitializeTabsAction -> initializeTabsAction(action)
        is MasterConsoleViewUpdateTabsAction -> updateTabsAction(action)
    } }

    override fun applyActionWithSkip(action: MasterConsoleViewAction) { when(action) {
        is MasterConsoleViewShowLoginAction -> showLoginAction(action)
        is MasterConsoleViewShowTaskNewToInWorkDialogAction -> showTaskNewToInWorkDialogAction(action)
        is MasterConsoleViewShowTaskInWorkToCompleteDialog -> showTaskInWorkToCompleteDialogAction(action)
    } }


}