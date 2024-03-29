package ru.acurresearch.dombyta.Services

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.RemoteException

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.HashMap

import ru.evotor.devices.commons.printer.printable.IPrintable
import ru.evotor.devices.commons.printer.printable.PrintableBarcode
import ru.evotor.devices.commons.printer.printable.PrintableImage
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.ExtraKey
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionFooter
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop

/**
 * Печать внутри кассового чека возврата
 * В манифесте добавить права <uses-permission android:name="ru.evotor.permission.receipt.printExtra.SET"></uses-permission>
 * В манифесте для сервиса указать:
 * - печать внутри чека возврата <action android:name="evo.v2.receipt.payback.printExtra.REQUIRED"></action>
 * Штрихкод должен быть с контрольной суммой
 */
class MyPrintPaybackService : IntegrationService() {

    /**
     * Получение картинки из каталога asset приложения
     *
     * @param fileName имя файла
     * @return значение типа Bitmap
     */
    private fun getBitmapFromAsset(fileName: String): Bitmap {
        val assetManager = assets
        var stream: InputStream? = null
        try {
            stream = assetManager.open(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BitmapFactory.decodeStream(stream)
    }

    override fun createProcessors(): Map<String, ActionProcessor>? {
        val map = HashMap<String, ActionProcessor>()
        map[PrintExtraRequiredEvent.NAME_SELL_RECEIPT] = object : PrintExtraRequiredEventProcessor() {
            override fun call(
                s: String,
                printExtraRequiredEvent: PrintExtraRequiredEvent,
                callback: ActionProcessor.Callback
            ) {
                val setPrintExtras = ArrayList<SetPrintExtra>()
                setPrintExtras.add(
                    SetPrintExtra(
                        //Метод, который указывает место, где будут распечатаны данные.
                        //Данные печатаются после клише и до текста “Кассовый чек”
                        PrintExtraPlacePrintGroupTop(null),
                        //Массив данных, которые требуется распечатать.
                        arrayOf(
                            //Простой текст
                            PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                            //Изображение
                            PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                        )
                    )
                )
                setPrintExtras.add(
                    SetPrintExtra(
                        //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                        PrintExtraPlacePrintGroupHeader(null),
                        arrayOf(
                            PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                            PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                        )
                    )
                )
                //Добавляем к каждой позиции чека возврата необходимые данные
                val r = ReceiptApi.getReceipt(this@MyPrintPaybackService, Receipt.Type.PAYBACK)
                if (r != null) {
                    setPrintExtras.add(
                        SetPrintExtra(
                            //Данные печатаются после итога и списка оплат, до текста “всего оплачено”
                            PrintExtraPlacePrintGroupSummary(null),
                            arrayOf(PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."))
                        )
                    )
                    for (p in r.getPositions()) {
                        val list = ArrayList(p.extraKeys)
                        setPrintExtras.add(
                            SetPrintExtra(
                                //Данные печатаются в позиции в чеке, до подпозиций
                                PrintExtraPlacePositionFooter(p.uuid),
                                arrayOf(PrintableText("UUID:" + p.uuid + "\n EXTRA:" + if (list.size > 0) list[0].description else ""))
                            )
                        )
                        setPrintExtras.add(
                            SetPrintExtra(
                                //Данные печатаются в позиции в чеке, после всех подпозиций
                                PrintExtraPlacePositionAllSubpositionsFooter(p.uuid),
                                arrayOf(PrintableText("<Текст>\n" + p.uuid + "\n<Текст>"))
                            )
                        )

                    }
                }
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
