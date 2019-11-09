package ru.acurresearch.dombyta_new.ui.master_console.page

import com.arellomobile.mvp.InjectViewState
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

@InjectViewState
class MasterConsolePagePresenter(
    private val id: String
): BasePresenter<MasterConsolePageViewAction, MasterConsolePageViewEvent, MasterConsolePageView, MasterConsolePageViewPM>() {

    override val TAG: String = "MASTER_CONSOLE_PAGE:$id"

    private fun handleInitialize(): ObservableTransformer<MasterConsolePageViewInitializeEvent, MasterConsolePageViewAction> =
        ObservableTransformer {
            it.map { MasterConsolePageViewPM(
                tasks = BaseLCE(false, listOf(), null),
                masters = BaseLCE(false, listOf(), null)
            ) }.doOnNext { handleState(it) }
                .map { MasterConsolePageViewUpdatePMAction(it) }
        }

    private fun handleUpdateData(): ObservableTransformer<MasterConsolePageViewUpdateDataEvent, MasterConsolePageViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsolePageViewUpdateDataEvent, state: MasterConsolePageViewPM ->
                state.copy(
                    tasks = event.tasks,
                    masters = event.masters
                )
            }).doOnNext { handleState(it) }
                .map { MasterConsolePageViewUpdatePMAction(it) }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(MasterConsolePageViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: MasterConsolePageViewAction): Boolean = viewAction is MasterConsolePageViewSkipAction

    override fun createSharedList(shared: Observable<MasterConsolePageViewEvent>): List<Observable<MasterConsolePageViewAction>> =
        listOf(
            shared.ofType(MasterConsolePageViewInitializeEvent::class.java).compose(handleInitialize()),
            shared.ofType(MasterConsolePageViewUpdateDataEvent::class.java).compose(handleUpdateData())
        )
}