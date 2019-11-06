package ru.acurresearch.dombyta_new.ui.order.pay.add_good

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.utils.ReducedTextWatcher
import kotlinx.android.synthetic.main.activity_select_good.*
import kotlinx.android.synthetic.main.activity_select_good.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.ServiceItemCustom

class AddGoodController(args: Bundle): BaseController(args), AddGoodView, AddGoodViewPMRenderer {
    companion object {
        fun create() =
            AddGoodController(Bundle.EMPTY)
    }
    private val diffElement = AddGoodViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: AddGoodViewPM? = null
    private val goodsAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

    private val onGoodClicked: (ServiceItemCustom) -> Unit = {
        presenter.handleViewEvent(AddGoodViewGoodClickedEvent(it))
    }

    override fun renderGoods(goods: BaseLCE<List<ServiceItemCustom>>) {
        goodsAdapter.update(
            goods.content?.map { GoodItem(it, onGoodClicked) } ?: listOf()
        )
    }

    override fun renderSelectedGood(selectedGood: BaseLCE<ServiceItemCustom>) {
        view?.text_price_popup?.text = selectedGood.content?.name ?: ""
        view?.adding_price_holder?.text = Editable.Factory.getInstance().newEditable(selectedGood.content?.defPrice.toString())
    }

    override fun renderListShown(listShown: BaseLCE<Boolean>) { when(listShown.content) {
        true -> view?.select_good_list?.visibility = View.VISIBLE
        false -> view?.select_good_list?.visibility = View.INVISIBLE
    } }

    override fun renderNameShown(nameShown: BaseLCE<Boolean>) { when(nameShown.content) {
        true -> view?.selected_service_item_card?.visibility = View.VISIBLE
        false -> view?.selected_service_item_card?.visibility = View.INVISIBLE
    } }

    override fun renderPriceShown(priceShown: BaseLCE<Boolean>) { when(priceShown.content) {
        true -> view?.price_add_card_view?.visibility = View.VISIBLE
        false -> view?.price_add_card_view?.visibility = View.INVISIBLE
    } }

    override fun renderDeadlineShown(deadlineShown: BaseLCE<Boolean>) { when(deadlineShown.content) {
        true -> view?.order_deadline_card?.visibility = View.VISIBLE
        false -> view?.order_deadline_card?.visibility = View.INVISIBLE
    } }

    private fun updatePMAction(action: AddGoodViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun resultAction(action: AddGoodViewResultAction) {
        activity?.setResult(AddGoodActivity.ADD_GOOD_RESULT_OK, Intent().apply {
            putExtra(AddGoodActivity.KEY_ORDER_POSITION_ID, action.orderPositionId)
        })
        activity?.finish()
    }

    @InjectPresenter lateinit var presenter: AddGoodPresenter
    @ProvidePresenter fun providePresenter(): AddGoodPresenter = AddGoodPresenter()
    override fun getLayoutId(): Int = R.layout.activity_select_good

    override fun initView(view: View) {
        view.select_good_list.adapter = goodsAdapter
        view.select_good_list.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        view.add_price_pop_btn.setOnClickListener { presenter.handleViewEvent(AddGoodViewPriceClickedEvent()) }
        view.pop_up_deadline_add_btn.setOnClickListener { presenter.handleViewEvent(AddGoodViewAddDeadlineClickedEvent()) }
        view.adding_price_holder.addTextChangedListener(ReducedTextWatcher {
            presenter.handleViewEvent(AddGoodViewPriceEditedEvent(it))
        })
    }

    override fun applyAction(action: AddGoodViewAction) { when(action) {
        is AddGoodViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: AddGoodViewAction) { when(action) {
        is AddGoodViewResultAction -> resultAction(action)
    } }

}