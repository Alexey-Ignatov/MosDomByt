package ru.acurresearch.dombyta_new.ui.order.pay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bluelinelabs.conductor.RouterTransaction
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.utils.NeverEqualItemContainer
import ga.nk2ishere.dev.utils.ReducedTextWatcher
import kotlinx.android.synthetic.main.content_x.*
import kotlinx.android.synthetic.main.content_x.view.*
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Order
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition
import ru.acurresearch.dombyta_new.ui.order.complete.OrderFinalController
import ru.acurresearch.dombyta_new.ui.order.pay.add_good.AddGoodActivity
import timber.log.Timber

class OrderPayController(args: Bundle): BaseController(args), OrderPayView, OrderPayViewPMRenderer {
    companion object {
        fun create() =
            OrderPayController(Bundle.EMPTY)

        const val KEY_PAYMENT_TYPE = "PAYMENT_TYPE"
    }
    private val diffElement = OrderPayViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: OrderPayViewPM? = null
    private lateinit var paymentType: String

    private val goodsAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

    private val deleteGoodClicked: (OrderPosition) -> Unit = {
        presenter.handleViewEvent(OrderPayViewDeleteGoodButtonClickedEvent(it))
    }

    override fun renderOrder(order: BaseLCE<NeverEqualItemContainer<Order>>) {
        order.content?.item?.let {
            view?.total_count_txt?.text = "Услуги (${it.price} руб.)"
            goodsAdapter.update(it.positionsList.map {
                GoodItem(it, deleteGoodClicked)
            })
        }
    }

    private fun updatePMAction(action: OrderPayViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun showAddGoodAction(action: OrderPayViewShowAddGoodAction) {
        startActivityForResult(Intent(view?.context, AddGoodActivity::class.java), AddGoodActivity.ADD_GOOD_REQUEST)
    }

    private fun showCreateOrderAction(action: OrderPayViewShowCreateOrderAction) {
        router.setRoot(RouterTransaction.with(OrderFinalController.create(
            orderId = action.orderId,
            orderAlreadyExists = false
        )))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { when(requestCode) {
        AddGoodActivity.ADD_GOOD_REQUEST -> if(data != null && resultCode == AddGoodActivity.ADD_GOOD_RESULT_OK)
            presenter.handleViewEvent(OrderPayViewGoodAddedEvent(data.getLongExtra(AddGoodActivity.KEY_ORDER_POSITION_ID, 0)))
    } }

    @InjectPresenter lateinit var presenter: OrderPayPresenter
    @ProvidePresenter fun providePresenter(): OrderPayPresenter = OrderPayPresenter(paymentType)
    override fun getLayoutId(): Int = R.layout.content_x

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        paymentType = activity!!.intent.getStringExtra(KEY_PAYMENT_TYPE)
        return super.onCreateView(inflater, container)
    }

    override fun initView(view: View) {
        view.order_list_view.adapter = goodsAdapter
        view.order_list_view.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)

        view.ed_txt_name.addTextChangedListener(
            ReducedTextWatcher { presenter.handleViewEvent(OrderPayViewClientNameEditedEvent(it)) }
        )
        view.ed_txt_phone.addTextChangedListener(
            ReducedTextWatcher { presenter.handleViewEvent(OrderPayViewClientPhoneEditedEvent(it)) }
        )
        with(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", view.ed_txt_phone)) {
            view.ed_txt_phone.addTextChangedListener(this)
            view.ed_txt_phone.onFocusChangeListener = this
        }
        view.create_pre_pay_order.setOnClickListener { presenter.handleViewEvent(OrderPayViewCreateOrderButtonClickedEvent()) }
        view.create_post_pay_order.setOnClickListener { presenter.handleViewEvent(OrderPayViewCreateOrderButtonClickedEvent()) }
        view.add_good.setOnClickListener { presenter.handleViewEvent(OrderPayViewAddGoodButtonClickedEvent()) }
        when(paymentType) {
            Constants.BillingType.PREPAY -> view.create_post_pay_order.visibility = View.INVISIBLE
            Constants.BillingType.POSTPAY -> view.create_pre_pay_order.visibility = View.INVISIBLE
        }
    }

    override fun applyAction(action: OrderPayViewAction) { when(action) {
        is OrderPayViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: OrderPayViewAction) { when(action) {
        is OrderPayViewShowAddGoodAction -> showAddGoodAction(action)
        is OrderPayViewShowCreateOrderAction -> showCreateOrderAction(action)
    } }
}