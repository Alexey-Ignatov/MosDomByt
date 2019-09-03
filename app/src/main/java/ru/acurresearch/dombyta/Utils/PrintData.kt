package ru.acurresearch.dombyta.Utils

import ru.evotor.devices.commons.printer.printable.PrintableBarcode
import ru.evotor.framework.core.IntegrationException
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.MainActivity
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.IntegrationManagerCallback
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand
import ru.evotor.framework.receipt.Receipt.PrintReceipt
import ru.evotor.framework.receipt.Payment
import ru.evotor.framework.receipt.PrintGroup
import java.util.UUID.randomUUID
import ru.evotor.framework.payment.PaymentType
import ru.evotor.framework.payment.PaymentSystem





internal class PrintData {

    var barType: PrintableBarcode.BarcodeType? = null
    var printType: PrintType? = null
    var data: String? = null

    internal enum class PrintType {
        TEXT, BARCODE, IMAGE
    }

    constructor() {}

    constructor(barType: PrintableBarcode.BarcodeType, printType: PrintType, data: String) {
        this.barType = barType
        this.printType = printType
        this.data = data
    }
}

fun openReceiptAndEmail() {
    //Создание списка товаров чека

}