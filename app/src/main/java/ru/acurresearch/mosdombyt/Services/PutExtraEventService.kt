package ru.acurresearch.mosdombyt.Services


import android.os.RemoteException

import java.util.ArrayList
import java.util.HashMap

import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor

/**
 * Печать внутри кассового чека возврата
 * В манифесте добавить права <uses-permission android:name="ru.evotor.permission.receipt.printExtra.SET"></uses-permission>
 * В манифесте для сервиса указать:
 * - печать внутри чека возврата <action android:name="evo.v2.receipt.payback.printExtra.REQUIRED"></action>
 * Штрихкод должен быть с контрольной суммой
 */
class PutExtraEventService : IntegrationService() {

    /**
     * Получение картинки из каталога asset приложения
     *
     * @param fileName имя файла
     * @return значение типа Bitmap
     */

    override fun createProcessors(): Map<String, ActionProcessor>? {
        val map = HashMap<String, ActionProcessor>()
        map[PrintExtraRequiredEvent.NAME_SELL_RECEIPT] = object : PrintExtraRequiredEventProcessor() {
            override fun call(
                s: String,
                printExtraRequiredEvent: PrintExtraRequiredEvent,
                callback: ActionProcessor.Callback
            ) {
                val setPrintExtras = ArrayList<SetPrintExtra>()

                try {
                    callback.onResult(PrintExtraRequiredEventResult(setPrintExtras).toBundle())
                } catch (exc: RemoteException) {
                    exc.printStackTrace()
                }

            }
        }
        return map
    }
}
