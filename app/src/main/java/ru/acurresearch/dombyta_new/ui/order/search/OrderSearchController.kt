package ru.acurresearch.dombyta_new.ui.order.search

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.utils.ReducedTextWatcher
import kotlinx.android.synthetic.main.activity_order_search.*
import kotlinx.android.synthetic.main.activity_order_search.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Order

class OrderSearchController(args: Bundle): BaseController(args), OrderSearchView, OrderSearchViewPMRenderer {

    companion object {
        fun create() =
            OrderSearchController(Bundle.EMPTY)
    }
    private val diffElement = OrderSearchViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: OrderSearchViewPM? = null
    private val foundOrdersAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

    private val onOrderClicked: (Order) -> Unit = {
        presenter.handleViewEvent(OrderSearchViewOrderClickedEvent(it))
    }

    override fun renderFoundOrders(foundOrders: BaseLCE<List<Order>>) {
        foundOrdersAdapter.update(
            foundOrders.content?.map { OrderSearchItem(it, onOrderClicked) } ?: listOf()
        )
    }

    private fun updatePMAction(action: OrderSearchViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun showOrderCompleteAction(action: OrderSearchViewShowOrderCompleteAction) {

    }

    @InjectPresenter lateinit var presenter: OrderSearchPresenter
    @ProvidePresenter fun providePresenter(): OrderSearchPresenter = OrderSearchPresenter()
    override fun getLayoutId(): Int = R.layout.activity_order_search

    override fun initView(view: View) {
        view.search_orders_list.adapter = foundOrdersAdapter
        view.search_orders_list.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        view.search_act_search_btn.setOnClickListener { presenter.handleViewEvent(OrderSearchViewSearchButtonClicked()) }
        view.search_act_search_input.addTextChangedListener(ReducedTextWatcher {
            presenter.handleViewEvent(OrderSearchViewSearchStringEditedEvent(it))
        })

        with(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", view.search_act_search_input)) {
            view.search_act_search_input.addTextChangedListener(this)
            view.search_act_search_input.onFocusChangeListener = this
        }
    }

    override fun applyAction(action: OrderSearchViewAction) { when(action) {
        is OrderSearchViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: OrderSearchViewAction) { when(action) {
        is OrderSearchViewShowOrderCompleteAction -> showOrderCompleteAction(action)
    } }

}