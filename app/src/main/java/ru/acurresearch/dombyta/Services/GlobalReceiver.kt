package ru.acurresearch.dombyta.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.OrderFinalActivity
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.Check
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.Order
import ru.acurresearch.dombyta.Utils.syncOrder

import ru.evotor.framework.core.action.event.receipt.position_edited.PositionAddedEvent
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionEditedEvent
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionRemovedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClearedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent
import ru.evotor.framework.receipt.ReceiptApi

/**
 * Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека
 * Смарт терминал не ждёт ответ от приложения на широковещательные сообщения.
 */
class GlobalReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val bundle = intent.extras
        Log.e(javaClass.simpleName, action)

        if (action != null) {
            when (action) {
                //Чек продажи был успешно открыт
                "evotor.intent.action.receipt.sell.OPENED" -> {
                    Log.e(javaClass.simpleName, "Data:" + ReceiptOpenedEvent.create(bundle)!!.receiptUuid)

                }

                //Позиция была добавлена в чек продажи
                "evotor.intent.action.receipt.sell.POSITION_ADDED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionAddedEvent.create(bundle)!!.receiptUuid + " " + PositionAddedEvent.create(
                            bundle
                        )!!.position.name
                    )

                }

                //Позиция была отредактирована в чеке продажи
                "evotor.intent.action.receipt.sell.POSITION_EDITED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionEditedEvent.create(bundle)!!.receiptUuid + " " + PositionEditedEvent.create(
                            bundle
                        )!!.position.name
                    )

                }

                //Позиция была удалена из чека продажи
                "evotor.intent.action.receipt.sell.POSITION_REMOVED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionRemovedEvent.create(bundle)!!.receiptUuid + " " + PositionRemovedEvent.create(
                            bundle
                        )!!.position.name
                    )

                }

                //Обновление базы товаров
                "evotor.intent.action.inventory.PRODUCTS_UPDATED" -> {
                    Log.e(javaClass.simpleName, "Data: PRODUCTS_UPDATED")
                    //Toast.makeText(context, "Data: PRODUCTS_UPDATED", Toast.LENGTH_SHORT).show()
                }
                //Чек продажи был очищен
                "evotor.intent.action.receipt.sell.CLEARED" -> {
                    Log.e(javaClass.simpleName, "Data:" + ReceiptClearedEvent.create(bundle)!!.receiptUuid)

                }

                //Чек продажи был успешно закрыт
                "evotor.intent.action.receipt.sell.RECEIPT_CLOSED" -> {
                    Log.e(javaClass.simpleName, "Data:" + ReceiptClosedEvent.create(bundle)!!.receiptUuid)

                    Toast.makeText(context, "Data: Check пробит", Toast.LENGTH_SHORT).show()
                    var evoReceipt = ReceiptApi.getReceipt(context, ReceiptClosedEvent.create(bundle)!!.receiptUuid)

                    val checkTocheck = Check.fromEvoReceipt(evoReceipt!!)
                    if (checkTocheck.position[0].uuid == App.prefs.lastOrder.positionsList[0].uuid){
                        var tmpOrder =  App.prefs.lastOrder
                        tmpOrder.setPaid(context, evoReceipt!!.header.uuid, false)
                        App.prefs.lastOrder = tmpOrder

                        val intent = Intent(context, OrderFinalActivity::class.java)
                        intent.putExtra(ru.acurresearch.dombyta.Services.WorkService.EXTRA_NAME_OPERATION, "sell")
                        intent.putExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL, tmpOrder.toJson())
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //если делать синхронизацию до перехода, то бывает, что проходит время на
                        // обработку события пробиванияя чека и на новый экран попат не получеатся
                        syncOrder(context, tmpOrder)

                        try {
                            //code review: наличие взаимодействия с ui в сервисе "гарантировано" приводит к крашам
                            context.startActivity(intent)
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                    }

                }



            }
        }
    }
}
