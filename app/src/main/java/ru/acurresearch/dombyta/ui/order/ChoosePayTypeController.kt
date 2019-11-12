package ru.acurresearch.dombyta.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import ga.nk2ishere.dev.base.BaseController
import kotlinx.android.synthetic.main.activity_choose_pay_type.view.*
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.ui.order.pay.OrderPayActivity
import ru.acurresearch.dombyta.ui.order.pay.OrderPayController
import ru.acurresearch.dombyta.ui.order.search.OrderSearchActivity
import ru.acurresearch.dombyta.ui.token.TokenActivity
import ru.evotor.framework.navigation.NavigationApi

class ChoosePayTypeController(args: Bundle): BaseController(args), ChoosePayTypeView {
    companion object {
        fun create() =
            ChoosePayTypeController(Bundle.EMPTY)
    }

    private fun showLoginAction(action: ChoosePayTypeViewShowLoginAction) {
        startActivityForResult(Intent(activity, TokenActivity::class.java), TokenActivity.REQUEST_CODE)
    }

    private fun showPostPayAction(action: ChoosePayTypeViewShowPostPayAction) {
        startActivity(Intent(view?.context, OrderPayActivity::class.java).apply {
            putExtra(OrderPayController.KEY_PAYMENT_TYPE, Constants.BillingType.POSTPAY)
        })
    }

    private fun showPrePayAction(action: ChoosePayTypeViewShowPrePayAction) {
        startActivity(Intent(view?.context, OrderPayActivity::class.java).apply {
            putExtra(OrderPayController.KEY_PAYMENT_TYPE, Constants.BillingType.PREPAY)
        })
    }

    private fun showRegularPayAction(action: ChoosePayTypeViewShowRegularPayAction) {
        startActivity(NavigationApi.createIntentForSellReceiptEdit())
    }

    private fun showGetOrderAction(action: ChoosePayTypeViewShowGetOrderAction) {
        startActivity(Intent(view?.context, OrderSearchActivity::class.java))
    }

    @InjectPresenter lateinit var presenter: ChoosePayTypePresenter
    @ProvidePresenter fun providePresenter(): ChoosePayTypePresenter = ChoosePayTypePresenter()
    override fun getLayoutId(): Int = R.layout.activity_choose_pay_type

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { when(requestCode) {
        TokenActivity.REQUEST_CODE -> if(data != null && resultCode == TokenActivity.RESULT_OK)
            presenter.handleViewEvent(ChoosePayTypeViewTokenUpdatedEvent(data.getStringExtra(TokenActivity.RESULT_KEY_TOKEN)))
    } }

    override fun initView(view: View) {
        view.post_pay_option_btn.setOnClickListener { presenter.handleViewEvent(ChoosePayTypeViewPostPayClickedEvent()) }
        view.pre_pay_option_btn.setOnClickListener { presenter.handleViewEvent(ChoosePayTypeViewPrePayClickedEvent()) }
        view.get_compl_order_btn.setOnClickListener { presenter.handleViewEvent(ChoosePayTypeViewRegularPayClickedEvent()) }
        view.regular_pay_btn.setOnClickListener { presenter.handleViewEvent(ChoosePayTypeViewGetOrderClickedEvent()) }
    }

    override fun applyAction(action: ChoosePayTypeViewAction) { when(action) {

    } }

    override fun applyActionWithSkip(action: ChoosePayTypeViewAction) { when(action) {
        is ChoosePayTypeViewShowLoginAction -> showLoginAction(action)
        is ChoosePayTypeViewShowPostPayAction -> showPostPayAction(action)
        is ChoosePayTypeViewShowPrePayAction -> showPrePayAction(action)
        is ChoosePayTypeViewShowRegularPayAction -> showRegularPayAction(action)
        is ChoosePayTypeViewShowGetOrderAction -> showGetOrderAction(action)
    } }
}