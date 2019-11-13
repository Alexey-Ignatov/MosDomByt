package ru.acurresearch.dombyta.ui.token

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import kotlinx.android.synthetic.main.activity_token.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.network.model.CashBoxServerData
import ru.acurresearch.dombyta.ui.token.TokenActivity.Companion.RESULT_KEY_TOKEN
import ru.acurresearch.dombyta.ui.token.TokenActivity.Companion.RESULT_OK

class TokenController(args: Bundle): BaseController(args), TokenView, TokenViewPMRenderer {

    companion object {
        fun create() =
            TokenController(Bundle.EMPTY)
    }

    private val diffElement = TokenViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: TokenViewPM? = null

    override fun renderToken(token: BaseLCE<CashBoxServerData>) {
        if(token.error != null) Toast.makeText(view?.context, "Ошибка: токен не передан, попробуйте еще раз.", Toast.LENGTH_LONG).show()
    }

    private fun updatePMAction(action: TokenViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun resultAction(action: TokenViewResultAction) {
        activity?.setResult(RESULT_OK, Intent().apply {
            this.putExtra(RESULT_KEY_TOKEN, action.token)
        })
        activity?.finish()
    }

    @InjectPresenter lateinit var presenter: TokenPresenter
    @ProvidePresenter fun providePresenter(): TokenPresenter = TokenPresenter()
    override fun getLayoutId(): Int = R.layout.activity_token

    override fun initView(view: View) {
        view.send_token_btn.setOnClickListener { presenter.handleViewEvent(TokenViewLoginClickedEvent()) }
    }

    override fun applyAction(action: TokenViewAction) { when(action) {
      is TokenViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: TokenViewAction) { when(action) {
      is TokenViewResultAction -> resultAction(action)
    } }

}