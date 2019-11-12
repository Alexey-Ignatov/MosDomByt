package ru.acurresearch.dombyta.ui.order.pay.add_good

import com.arellomobile.mvp.InjectViewState
import ga.nk2ishere.dev.base.BaseLCE
import ga.nk2ishere.dev.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta.data.common.model.OrderPosition
import ru.acurresearch.dombyta.data.network.interactor.TokenInteractor
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

@InjectViewState
class AddGoodPresenter: BasePresenter<AddGoodViewAction, AddGoodViewEvent, AddGoodView, AddGoodViewPM>(), KoinComponent {
    override val TAG: String = "ru.acurresearch.dombyta.ui.ADD_GOOD"

    private val tokenInteractor: TokenInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    private fun handleInitializeEvent(): ObservableTransformer<AddGoodViewInitializeEvent, AddGoodViewAction> =
        ObservableTransformer {
            it.switchMapSingle { tokenInteractor.getToken() }
                .switchMapSingle { orderInteractor.updateServiceItems(it.orThrow(IllegalStateException("No token is present"))) }
                .map { BaseLCE(false, it, null) }
                .onErrorReturn { it.printStackTrace();BaseLCE(false, listOf(), it as? Exception) }
                .map { AddGoodViewPM(
                    goods = it,
                    selectedGood = BaseLCE(false, null, null),
                    date = BaseLCE(false, null, null),
                    price = BaseLCE(false, "", null),
                    listShown = BaseLCE(false, true, null),
                    nameShown = BaseLCE(false, false, null),
                    priceShown = BaseLCE(false, false, null),
                    deadlineShown = BaseLCE(false, false, null)
                ) }
                .doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    private fun handleAddDeadlineClickedEvent(): ObservableTransformer<AddGoodViewAddDeadlineClickedEvent, AddGoodViewAction> =
        ObservableTransformer {
            it.flatMap { state }
                .filter {
                    it.selectedGood.content != null
                            && it.date.content != null
                            && it.price.content.isNullOrBlank().not()
                            && (it.price.content ?: "").toDoubleOrNull() != null
                }.doOnNext { handleState(it) }
                .switchMapSingle { orderInteractor.saveOrderPosition(
                    OrderPosition(
                        0,
                        UUID.randomUUID().toString(),
                        1.0,
                        it.price.content!!.toDouble(),
                        it.selectedGood.content!!.name,
                        it.date.content
                    ).apply {
                        it.selectedGood.content!!.orderPosition.target = this
                        serviceItem.target = it.selectedGood.content!!
                    }
                ) }
                .map { AddGoodViewResultAction(it.id) }
        }

    private fun handlePriceEditedEvent(): ObservableTransformer<AddGoodViewPriceEditedEvent, AddGoodViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: AddGoodViewPriceEditedEvent, state: AddGoodViewPM ->
                event to state
            }).map { (event, state) -> state.copy(
                price = BaseLCE(false, event.price, null)
            ) }.doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    private fun handleGoodClickedEvent(): ObservableTransformer<AddGoodViewGoodClickedEvent, AddGoodViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: AddGoodViewGoodClickedEvent, state: AddGoodViewPM ->
                event to state
            }).map { (event, state) -> state.copy(
                selectedGood = BaseLCE(false, event.good, null),
                listShown = BaseLCE(false, false, null),
                nameShown = BaseLCE(false, true, null),
                priceShown = BaseLCE(false, true, null),
                deadlineShown = BaseLCE(false, false, null)
            ) }.doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    private fun handlePriceClickedEvent(): ObservableTransformer<AddGoodViewPriceClickedEvent, AddGoodViewAction> =
        ObservableTransformer {
            Observable.zip(
                it,
                it.flatMap { state }
                    .filter { it.price.content.isNullOrBlank().not()
                            && (it.price.content ?: "").toDoubleOrNull() != null },
                BiFunction { event: AddGoodViewPriceClickedEvent, state: AddGoodViewPM ->
                    event to state
                }
            ).map { (event, state) -> state.copy(
                priceShown = BaseLCE(false, false, null),
                deadlineShown = BaseLCE(false, true, null)
            ) }.doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    private fun handleDateEditedEvent(): ObservableTransformer<AddGoodViewDateEditedEvent, AddGoodViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: AddGoodViewDateEditedEvent, state: AddGoodViewPM ->
                event to state
            }).map { (event, state) -> state.copy(
                date = BaseLCE(false,
                    DateTime(state.date.content?.time ?: System.currentTimeMillis())
                        .year().setCopy(event.year)
                        .monthOfYear().setCopy(event.month + 1)
                        .dayOfMonth().setCopy(event.day)
                        .toDate(),
                    null
                )
            ) }.doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    private fun handleTimeEditedEvent(): ObservableTransformer<AddGoodViewTimeEditedEvent, AddGoodViewAction> =
        ObservableTransformer {
            Observable.zip(it, it.flatMap { state }, BiFunction { event: AddGoodViewTimeEditedEvent, state: AddGoodViewPM ->
                event to state
            }).map { (event, state) -> state.copy(
                date = BaseLCE(false,
                    DateTime(state.date.content?.time ?: System.currentTimeMillis())
                        .hourOfDay().setCopy(event.hour)
                        .minuteOfHour().setCopy(event.minute)
                        .secondOfMinute().setCopy(0)
                        .toDate(),
                    null)
            ) }.doOnNext { handleState(it) }
                .map { AddGoodViewUpdatePMAction(it) }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        handleViewEvent(AddGoodViewInitializeEvent())
        with(DateTime()) {
            handleViewEvent(AddGoodViewDateEditedEvent(year, monthOfYear, dayOfMonth))
            handleViewEvent(AddGoodViewTimeEditedEvent(hourOfDay, minuteOfHour))
        }
    }

    override fun isSkipViewAction(viewAction: AddGoodViewAction): Boolean = viewAction is AddGoodViewSkipAction

    override fun createSharedList(shared: Observable<AddGoodViewEvent>): List<Observable<AddGoodViewAction>> =
        listOf(
            shared.ofType(AddGoodViewInitializeEvent::class.java).compose(handleInitializeEvent()),
            shared.ofType(AddGoodViewAddDeadlineClickedEvent::class.java).compose(handleAddDeadlineClickedEvent()),
            shared.ofType(AddGoodViewPriceEditedEvent::class.java).compose(handlePriceEditedEvent()),
            shared.ofType(AddGoodViewGoodClickedEvent::class.java).compose(handleGoodClickedEvent()),
            shared.ofType(AddGoodViewPriceClickedEvent::class.java).compose(handlePriceClickedEvent()),
            shared.ofType(AddGoodViewDateEditedEvent::class.java).compose(handleDateEditedEvent()),
            shared.ofType(AddGoodViewTimeEditedEvent::class.java).compose(handleTimeEditedEvent())
        )
}