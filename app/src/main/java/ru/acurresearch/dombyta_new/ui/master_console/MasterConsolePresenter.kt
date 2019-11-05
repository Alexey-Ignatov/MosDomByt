package ru.acurresearch.dombyta_new.ui.master_console

import com.arellomobile.mvp.InjectViewState
import com.google.gson.Gson
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.App.fromJson
import ru.acurresearch.dombyta_new.data.common.interactor.MasterInteractor
import ru.acurresearch.dombyta_new.data.common.interactor.TaskInteractor
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor
import java.lang.Exception

@InjectViewState
class MasterConsolePresenter: BasePresenter<MasterConsoleViewAction, MasterConsoleViewEvent, MasterConsoleView, MasterConsoleViewPM>(), KoinComponent {
    override val TAG: String = "MASTER_CONSOLE"

    private val tokenInteractor: TokenInteractor by inject()
    private val taskInteractor: TaskInteractor by inject()
    private val masterInteractor: MasterInteractor by inject()
    private val gson: Gson by inject()

    private fun handleInitializedEvent(): ObservableTransformer<MasterConsoleViewInitializeEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            it.switchMapSingle { tokenInteractor.getToken() }
                .map { BaseLCE(false, it.orNull(), null) }
                .onErrorReturn { BaseLCE(false, null, it as? Exception) }
                .switchMapSingle { token ->
                    when {
                        token.content != null -> taskInteractor.updateTasks(token.content!!)
                        else -> taskInteractor.getTasks()
                    }.map { token to BaseLCE(false, it, null) }
                        .onErrorReturn { token to BaseLCE(false, listOf(), it as? Exception) }
                }.switchMapSingle { (token, tasks) ->
                    when {
                        token.content != null -> masterInteractor.updateMasters(token.content!!)
                        else -> masterInteractor.getMasters()
                    }.map { Triple(token, tasks, BaseLCE(false, it, null)) }
                        .onErrorReturn { Triple(token, tasks, BaseLCE(false, listOf(), it as? Exception)) }
                }.map { (token, tasks, masters) -> MasterConsoleViewPM(
                    token = token,
                    tasks = tasks,
                    masters = masters
                ) }
                .doOnNext { handleState(it) }
                .map { when {
                    it.token.content == null -> MasterConsoleViewShowLoginAction()
                    else -> MasterConsoleViewInitializeTabsAction(it)
                } }
        }

    private fun handleTokenUpdatedEvent(): ObservableTransformer<MasterConsoleViewTokenUpdatedEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsoleViewTokenUpdatedEvent, state: MasterConsoleViewPM -> state.copy(
                token = BaseLCE(false, gson.fromJson(event.token), null)
            ) }).switchMapSingle { state ->
                taskInteractor.updateTasks(state.token.content!!).map { BaseLCE(false, it, null) }
                    .onErrorReturn { BaseLCE(false, listOf(), it as? Exception) }
                    .map { state.copy(
                        tasks = it
                    ) }
            }.switchMapSingle { state ->
                masterInteractor.updateMasters(state.token.content!!).map { BaseLCE(false, it, null) }
                    .onErrorReturn { BaseLCE(false, listOf(), it as? Exception) }
                    .map { state.copy(
                        masters = it
                    ) }
            }.doOnNext { handleState(it) }
                .map { MasterConsoleViewUpdateTabsAction(it) }
        }

    private fun handleTaskInWorkEvent(): ObservableTransformer<MasterConsoleViewTaskInWorkEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsoleViewTaskInWorkEvent, state: MasterConsoleViewPM ->
                event to state
            }).switchMapSingle { (event, state) ->
                taskInteractor.takeInWork(state.token.content!!, event.task, event.master)
                    .onErrorReturn { null }
                    .flatMap { taskInteractor.getTasks() }
                    .map { BaseLCE(false, it, null) }
                    .map { state.copy(
                        tasks = it
                    ) }
            }.doOnNext { handleState(it) }
                .map { MasterConsoleViewUpdateTabsAction(it) }
        }

    private fun handleTaskCompleteEvent(): ObservableTransformer<MasterConsoleViewTaskCompleteEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsoleViewTaskCompleteEvent, state: MasterConsoleViewPM ->
                    event to state
            }).switchMapSingle { (event, state) ->
                taskInteractor.finish(state.token.content!!, event.task)
                    .onErrorReturn { null }
                    .flatMap { taskInteractor.getTasks() }
                    .map { BaseLCE(false, it, null) }
                    .map { state.copy(
                        tasks = it
                    ) }
            }.doOnNext { handleState(it) }
                .map { MasterConsoleViewUpdateTabsAction(it) }
        }

    private fun handleTaskNewClickedEvent(): ObservableTransformer<MasterConsoleTaskNewClickedEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsoleTaskNewClickedEvent, state: MasterConsoleViewPM ->
                event to state
            }).doOnNext { (event, state) -> handleState(state) }
                .map { (event, state) -> MasterConsoleViewShowTaskNewToInWorkDialogAction(
                    task = event.task,
                    pm = state
                ) }
        }

    private fun handleTaskInWorkClickedEvent(): ObservableTransformer<MasterConsoleTaskInWorkClickedEvent, MasterConsoleViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: MasterConsoleTaskInWorkClickedEvent, state: MasterConsoleViewPM ->
                event to state
            }).doOnNext { (event, state) -> handleState(state) }
                .map { (event, state) -> MasterConsoleViewShowTaskInWorkToCompleteDialog(
                    task = event.task,
                    pm = state
                ) }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(MasterConsoleViewInitializeEvent())
    }

    override fun isSkipViewAction(viewAction: MasterConsoleViewAction): Boolean = viewAction is MasterConsoleViewSkipAction

    override fun createSharedList(shared: Observable<MasterConsoleViewEvent>): List<Observable<MasterConsoleViewAction>> =
        listOf(
            shared.ofType(MasterConsoleViewInitializeEvent::class.java).compose(handleInitializedEvent()),
            shared.ofType(MasterConsoleViewTokenUpdatedEvent::class.java).compose(handleTokenUpdatedEvent()),
            shared.ofType(MasterConsoleViewTaskInWorkEvent::class.java).compose(handleTaskInWorkEvent()),
            shared.ofType(MasterConsoleViewTaskCompleteEvent::class.java).compose(handleTaskCompleteEvent()),
            shared.ofType(MasterConsoleTaskNewClickedEvent::class.java).compose(handleTaskNewClickedEvent()),
            shared.ofType(MasterConsoleTaskInWorkClickedEvent::class.java).compose(handleTaskInWorkClickedEvent())
        )
}