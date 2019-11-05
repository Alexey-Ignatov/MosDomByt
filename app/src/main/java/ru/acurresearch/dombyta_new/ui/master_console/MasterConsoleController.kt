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

    private val onTaskItemNewClicked: (Task) -> Unit = {
        val alertDialog = AlertDialog.Builder(view?.context!!)
        alertDialog.setTitle("Чтобы взять заказ в работу, выберете мастера:")

        val masters = currentPM.masters.content?.map { it.name }?.toTypedArray() ?: arrayOf()
        alertDialog.setItems(masters) { dialog, which ->
            presenter.handleViewEvent(MasterConsoleViewTaskInWorkEvent(it,
                currentPM.masters.content?.find { it.name == masters[which] }!!
            ))
        }
        alertDialog.create().show()
    }
    private val onTaskItemInWorkClicked: (Task) -> Unit = {
        val alertDialog = AlertDialog.Builder(view?.context!!)
        alertDialog.setTitle("Завершить выполнение заказа?")
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton("Да") { dialog, id ->
            presenter.handleViewEvent(MasterConsoleViewTaskCompleteEvent(it))
        }
        alertDialog.setNegativeButton("Нет") { dialog, id ->
            dialog.cancel()
        }
        alertDialog.create()
            .show()
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
                masters = currentPM.masters,
                tasks = when(it.id) {
                    MasterConsolePageController.TASK_ITEM_VIEW_NEW -> currentPM.tasks.copy(
                        content = currentPM.tasks.content?.filter { it.status == Task.TaskStatus.NEW }
                    )
                    MasterConsolePageController.TASK_ITEM_VIEW_IN_WORK -> currentPM.tasks.copy(
                        content = currentPM.tasks.content?.filter { it.status == Task.TaskStatus.IN_WORK }
                    )
                    MasterConsolePageController.TASK_ITEM_VIEW_COMPLETE -> currentPM.tasks.copy(
                        content = currentPM.tasks.content?.filter { it.status == Task.TaskStatus.COMPLETE }
                    )
                    else -> currentPM.tasks
                }
            ) }
    }
    //TODO[Major design flaw] base1.1r1 make pageadapter tolerant to controller safe reuse
    private lateinit var currentPM: MasterConsoleViewPM

    private fun initializeTabsAction(action: MasterConsoleViewInitializeTabsAction) {
        view?.view_pager?.adapter = pagerAdapter
        view?.tabs?.setupWithViewPager(view?.view_pager)

        currentPM = action.pm
        updateCurrentPage()
    }

    private fun updateTabsAction(action: MasterConsoleViewUpdateTabsAction) {
        currentPM = action.pm
        updateCurrentPage()
    }

    private fun showLoginAction(action: MasterConsoleViewShowLoginAction) {
        startActivityForResult(Intent(activity, TokenActivity::class.java), TokenActivity.REQUEST_CODE)
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
    } }


}