package ru.acurresearch.mosdombyt.Services

import android.os.RemoteException
import android.util.Log

import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.PrintGroup
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.framework.receipt.TaxationSystem

/**
 * Разделение чека на несколько печатных групп
 * В манифесте указать для сервиса <action android:name="evo.v2.receipt.sell.printGroup.REQUIRED"></action>
 */
class SplitGroupService : IntegrationService() {
    override fun createProcessors(): Map<String, ActionProcessor>? {
        val map = HashMap<String, ActionProcessor>()
        Log.d("SplitGroupService", "SplitGroupService")
        map[PrintGroupRequiredEvent.NAME_SELL_RECEIPT] = object : PrintGroupRequiredEventProcessor() {
            override fun call(
                s: String,
                printGroupRequiredEvent: PrintGroupRequiredEvent,
                callback: ActionProcessor.Callback
            ) {
                val setPrintGroups = ArrayList<SetPrintGroup>()
                //Печатная группа
                val printGroup = PrintGroup(
                    //Идентификатор печатной группы
                    UUID.randomUUID().toString(),
                    //Тип чека
                    //фискальный – CASH_RECEIPT
                    //квитанция (нефискальный чек) – INVOICE
                    //ЕНВД (нефискальный чек) – STRING_UTII
                    PrintGroup.Type.INVOICE,
                    //Реквизиты организации
                    "ООО \"Пример\"",
                    "012345678901",
                    "г. Москва",
                    //Систему налогообложения
                    TaxationSystem.PATENT,
                    //Необходимость печати чека
                    true
                )
                Log.d("SplitGroupService", "SplitGroupServiceINside")
                //Список идентификаторов платежей
                val paymentPurposeIds = ArrayList<String>()
                //paymentPurposeIds.add("-1-")
                paymentPurposeIds.add("-3-")
                //Список иденификаторов позиций в формате uuid4
                val positionUuids = ArrayList<String>()
                val a = ReceiptApi.getReceipt(applicationContext, Receipt.Type.SELL)
                if (a !=
                    //Дополнительные данные для приложения
                    null
                ) {
                    positionUuids.add(a.getPositions()[0].uuid)
                }
                setPrintGroups.add(
                    SetPrintGroup(
                        printGroup,
                        paymentPurposeIds,
                        positionUuids
                    )
                )
                try {
                    callback.onResult(
                        PrintGroupRequiredEventResult(
                            null,
                            setPrintGroups
                        )
                    )
                } catch (exc: RemoteException) {
                    exc.printStackTrace()
                }

            }
        }
        return map
    }
}
