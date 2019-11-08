package ru.acurresearch.dombyta_new.services

import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.ChoosePayTypeActivity
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent
import ru.evotor.framework.receipt.event.handler.receiver.BuyReceiptBroadcastReceiver


class CheckClosedService : BuyReceiptBroadcastReceiver() {
    internal fun handleReceiptOpenedEvent(context: Context, receiptOpenedEvent: ReceiptOpenedEvent) {
        Toast.makeText(context,"OPENED", Toast.LENGTH_LONG).show()
    }

    internal fun handleReceiptClosedEvent(context: Context, receiptClosedEvent: ReceiptClosedEvent) {
        Toast.makeText(context,"Check Closed", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(context, ChoosePayTypeActivity::class.java))
    }
};