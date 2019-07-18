package ru.acurresearch.mosdombyt.Services

import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.acurresearch.mosdombyt.Activities.MainActivity
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent
import ru.evotor.framework.receipt.event.handler.receiver.BuyReceiptBroadcastReceiver


class CheckClosedReceiver : BuyReceiptBroadcastReceiver() {

    internal fun handleReceiptOpenedEvent(context: Context, receiptOpenedEvent: ReceiptOpenedEvent) {
        Toast.makeText(context,"OPENED", Toast.LENGTH_LONG).show()

    }

    internal fun handleReceiptClosedEvent(context: Context, receiptClosedEvent: ReceiptClosedEvent) {
        //Тело метода.
        Toast.makeText(context,"Check Closed", Toast.LENGTH_LONG).show()
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
};