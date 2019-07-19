package ru.acurresearch.mosdombyt.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import ru.acurresearch.mosdombyt.Activities.OrderFinalActivity
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Check
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Order

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
                    Toast.makeText(
                        context,
                        action + "\nData:" + ReceiptOpenedEvent.create(bundle)!!.receiptUuid,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Позиция была добавлена в чек продажи
                "evotor.intent.action.receipt.sell.POSITION_ADDED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionAddedEvent.create(bundle)!!.receiptUuid + " " + PositionAddedEvent.create(
                            bundle
                        )!!.position.name
                    )
                    Toast.makeText(
                        context,
                        action + "\nData: " + PositionAddedEvent.create(bundle)!!.receiptUuid + " " + PositionAddedEvent.create(
                            bundle
                        )!!.position.name,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Позиция была отредактирована в чеке продажи
                "evotor.intent.action.receipt.sell.POSITION_EDITED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionEditedEvent.create(bundle)!!.receiptUuid + " " + PositionEditedEvent.create(
                            bundle
                        )!!.position.name
                    )
                    Toast.makeText(
                        context,
                        action + "\nData: " + PositionEditedEvent.create(bundle)!!.receiptUuid + " " + PositionEditedEvent.create(
                            bundle
                        )!!.position.name,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Позиция была удалена из чека продажи
                "evotor.intent.action.receipt.sell.POSITION_REMOVED" -> {
                    Log.e(
                        javaClass.simpleName,
                        "Data:" + PositionRemovedEvent.create(bundle)!!.receiptUuid + " " + PositionRemovedEvent.create(
                            bundle
                        )!!.position.name
                    )
                    Toast.makeText(
                        context,
                        action + "\nData: " + PositionEditedEvent.create(bundle)!!.receiptUuid + " " + PositionRemovedEvent.create(
                            bundle
                        )!!.position.name,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Обновление базы товаров
                "evotor.intent.action.inventory.PRODUCTS_UPDATED" -> {
                    Log.e(javaClass.simpleName, "Data: PRODUCTS_UPDATED")
                    Toast.makeText(context, "Data: PRODUCTS_UPDATED", Toast.LENGTH_SHORT).show()
                }
                //Чек продажи был очищен
                "evotor.intent.action.receipt.sell.CLEARED" -> {
                    Log.e(javaClass.simpleName, "Data:" + ReceiptClearedEvent.create(bundle)!!.receiptUuid)
                    Toast.makeText(
                        context,
                        action + "\nData: " + ReceiptClearedEvent.create(bundle)!!.receiptUuid,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //Чек продажи был успешно закрыт
                "evotor.intent.action.receipt.sell.RECEIPT_CLOSED" -> {
                    Log.e(javaClass.simpleName, "Data:" + ReceiptClosedEvent.create(bundle)!!.receiptUuid)
                    Toast.makeText(
                        context,
                        action + "\nData: " + ReceiptClosedEvent.create(bundle)!!.receiptUuid,
                        Toast.LENGTH_SHORT
                    ).show()

                    var evoReceipt = ReceiptApi.getReceipt(context, ReceiptClosedEvent.create(bundle)!!.receiptUuid)
                    val checkTocheck = Check.fromEvoReceipt(evoReceipt!!)
                    if (checkTocheck.position[0].uuid == App.prefs.lastOrder.positionsList[0].uuid){
                        Toast.makeText(context, "Нашелся!!!!", Toast.LENGTH_SHORT).show()
                        var tmpOrder =  App.prefs.lastOrder
                        tmpOrder.isPaid = true
                        App.prefs.lastOrder = tmpOrder

                        val intent = Intent(context, OrderFinalActivity::class.java)
                        intent.putExtra(WorkService.EXTRA_NAME_OPERATION, "sell")
                        intent.putExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL, tmpOrder.toJson())
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
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