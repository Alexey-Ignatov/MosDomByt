package ru.acurresearch.dombyta_new.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.RemoteException
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.acurresearch.dombyta.Activities.OrderFinalActivity
import ru.acurresearch.dombyta.Check
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.Services.WorkService
import ru.acurresearch.dombyta.Utils.syncOrder
import ru.acurresearch.dombyta_new.data.common.interactor.OrderInteractor
import ru.acurresearch.dombyta_new.data.network.interactor.TokenInteractor
import ru.acurresearch.dombyta_new.ui.order.complete.OrderFinalActivityWrapper
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionAddedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClearedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent
import ru.evotor.framework.receipt.ReceiptApi
import timber.log.Timber

/**
 * Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека
 * Смарт терминал не ждёт ответ от приложения на широковещательные сообщения.
 */
class GlobalService : BroadcastReceiver(), KoinComponent {

    private val tokenInteractor: TokenInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val bundle = intent.extras
        Timber.i(action)
        when (action) {
            "evotor.intent.action.receipt.sell.OPENED" -> Timber.i("Data:%s", ReceiptOpenedEvent.create(bundle)!!.receiptUuid)
            "evotor.intent.action.receipt.sell.POSITION_ADDED" -> Timber.i("Data:%s %s", PositionAddedEvent.create(bundle)!!.receiptUuid, PositionAddedEvent.create(bundle)!!.position.name)
            "evotor.intent.action.receipt.sell.POSITION_EDITED" -> Timber.i("Data:%s %s", PositionAddedEvent.create(bundle)!!.receiptUuid, PositionAddedEvent.create(bundle)!!.position.name)
            "evotor.intent.action.receipt.sell.POSITION_REMOVED" -> Timber.i("Data:%s %s", PositionAddedEvent.create(bundle)!!.receiptUuid, PositionAddedEvent.create(bundle)!!.position.name)
            "evotor.intent.action.inventory.PRODUCTS_UPDATED" -> Timber.i("Data: PRODUCTS_UPDATED")
            "evotor.intent.action.receipt.sell.CLEARED" -> Timber.i("Data:%s", ReceiptClearedEvent.create(bundle)!!.receiptUuid)
            "evotor.intent.action.receipt.sell.RECEIPT_CLOSED" -> {
                Timber.i("Data: Check пробит")
                val evoReceipt = ReceiptApi.getReceipt(context, ReceiptClosedEvent.create(bundle)!!.receiptUuid)
                val checkToCheck = Check.fromEvoReceipt(evoReceipt!!)

                orderInteractor.loadCurrentOrder()
                    .filter { (order, _) -> order.isPresent }
                    .map { (order, orderAlreadyExists) -> order.get() to orderAlreadyExists }
                    .filter { (order, _) -> checkToCheck.position.size == order.positionsList.size }
                    .filter { (order, _) ->
                        checkToCheck.position.map { it.uuid }
                            .containsAll(order.positionsList.map { it.uuid })
                    }.toSingle()
                    .flatMap { (order, orderAlreadyExists) ->
                        orderInteractor.clearCurrentOrder()
                            .andThen(
                                tokenInteractor.getToken()
                                    .filter { it.isPresent }
                                    .map { it.get() }
                                    .toSingle()
                                    .flatMap { orderInteractor.updateOrder(
                                        it,
                                        order.apply {
                                            evoResUuid = evoReceipt.header.uuid
                                            isPaid = true
                                        },
                                        orderAlreadyExists
                                    ) }
                                    .map { it to orderAlreadyExists }
                            )
                    }.doOnSuccess { (order, orderAlreadyExists) ->
                        val intent = Intent(context, OrderFinalActivityWrapper::class.java)
//                        intent.putExtra(WorkService.EXTRA_NAME_OPERATION, "sell")
                        intent.putExtra(OrderFinalActivityWrapper.KEY_ORDER_ID, order.id)
                        intent.putExtra(OrderFinalActivityWrapper.KEY_ORDER_ALREADY_EXISTS, orderAlreadyExists)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }.subscribe()
            }
        }
    }
}
