package ru.acurresearch.dombyta_new.ui.token

import com.github.dimsuz.diffdispatcher.annotations.DiffElement
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BaseView
import ru.acurresearch.dombyta.SiteToken
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData

interface TokenView: BaseView<TokenViewAction>

@DiffElement(diffReceiver = TokenViewPMRenderer::class)
data class TokenViewPM(
    val token: BaseLCE<CashBoxServerData>
)

interface TokenViewPMRenderer {
    fun renderToken(token: BaseLCE<CashBoxServerData>)
}

sealed class TokenViewEvent

class TokenViewInitializeEvent: TokenViewEvent()
class TokenViewLoginClickedEvent: TokenViewEvent()

sealed class TokenViewAction

data class TokenViewUpdatePMAction(val pm: TokenViewPM): TokenViewAction()

sealed class TokenViewSkipAction: TokenViewAction()

data class TokenViewResultAction(val token: String): TokenViewSkipAction()