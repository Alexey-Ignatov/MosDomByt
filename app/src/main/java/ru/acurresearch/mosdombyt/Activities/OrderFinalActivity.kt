package ru.acurresearch.mosdombyt.Activities

import android.app.AlertDialog
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_order_final.*
import ru.acurresearch.mosdombyt.Adapters.OrderFinalViewAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.R
import ru.evotor.devices.commons.ConnectionWrapper
import ru.evotor.devices.commons.DeviceServiceConnector
import ru.evotor.devices.commons.exception.DeviceServiceException
import ru.evotor.devices.commons.printer.PrinterDocument
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.devices.commons.services.IPrinterServiceWrapper
import ru.evotor.devices.commons.services.IScalesServiceWrapper

//TODO проверить чтобы список оплаченных совпадал с нашим заказом. Но совпадает - удалить что-то
//TODO написать ВЫдан, если заказ выдан
class OrderFinalActivity : AppCompatActivity() {
    lateinit var currOrder: Order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_final)

        var intent = getIntent()
        currOrder = Order.fromJson(intent.getStringExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL))
        initUi()
        initListeners()


        val suggestedAction = currOrder.suggestAction()
        updateUIByAction(suggestedAction)



        initPosistionsListView()



    }

    fun initListeners(){
        order_final_pay_btn.setOnClickListener {
            currOrder.realize(this)
            orderSendFinishEtc()
        }

        order_final_close_order_btn.setOnClickListener{
            //TODO Send data to server закрытие заказа
            currOrder.status = Constants.OrderStatus.CLOSED
            orderSendFinishEtc()

        }

        order_final_client_print_btn.setOnClickListener{
            //TODO Send data to server открытие заказа
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Распечатать ярляк с номер заказа для внутреннего использования?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Да") { dialog, id ->
                //TODO получить с сервера номер заказа и картинку для печати
                initPrinter()
                print("#######   #####    ######  \r\n##   ##  ##   ##      ##   \r\n    ##       ###     ##    \r\n   ##      ####     ####   \r\n  ##      ####         ##  \r\n  ##     ###      ##   ##  \r\n  ##     #######   #####   \r\n                           \r\n")
                orderSendFinishEtc()
            }
            alertDialog.setNegativeButton("Нет") { dialog, id ->
                dialog.cancel()
            }
            alertDialog.create().show()


        }

    }

    fun orderSendFinishEtc(){
        refreshCart()
        finish()
    }

    fun updateUIByAction(suggestedAction: String){
        if (suggestedAction == Constants.OrderSuggestedAction.PAY){
            order_final_pay_btn.visibility = View.VISIBLE
            order_final_close_order_btn.visibility = View.INVISIBLE
            order_final_client_print_btn.visibility = View.INVISIBLE
        }
        if (suggestedAction == Constants.OrderSuggestedAction.CLOSE){
            order_final_pay_btn.visibility = View.INVISIBLE
            order_final_close_order_btn.visibility = View.VISIBLE
            order_final_client_print_btn.visibility = View.INVISIBLE
        }
        if (suggestedAction == Constants.OrderSuggestedAction.CREATE){
            order_final_pay_btn.visibility = View.INVISIBLE
            order_final_close_order_btn.visibility = View.INVISIBLE
            order_final_client_print_btn.visibility = View.VISIBLE
        }
        if (suggestedAction == Constants.OrderSuggestedAction.NOTHING){
            order_final_pay_btn.visibility = View.INVISIBLE
            order_final_close_order_btn.visibility = View.INVISIBLE
            order_final_client_print_btn.visibility = View.INVISIBLE
        }
    }

    fun refreshCart(){
        App.prefs.selectedPositions = listOf()
    }


    fun initUi(){
        order_client_name_holder.text = currOrder.client!!.name
        order_client_phone_holder.text = currOrder.client!!.phone
        order_final_total_price.text = currOrder.price.toString() + " руб."
        if (currOrder.isPaid ){
            order_is_paid_text.text = "ОПЛАЧЕН"
            order_is_paid_text.setTextColor(Color.GREEN)
        }


    }

    fun initPosistionsListView(){
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        order_list_final.layoutManager = layoutManager
        var adapter= OrderFinalViewAdapter(currOrder.positionsList, this)
        order_list_final.adapter = adapter


    }


    fun print(myStr: String){
        object : Thread() {
            override fun run() {
                try {

                    DeviceServiceConnector.getPrinterService().printDocument(
                        //В настоящий момент печать возможна только на ККМ, встроенной в смарт-терминал,
                        //поэтому вместо номера устройства всегда следует передавать константу
                        ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                        PrinterDocument(PrintableText(myStr))
                    )
                } catch (e: DeviceServiceException) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    fun initPrinter(){
        //Инициализация оборудования
        DeviceServiceConnector.startInitConnections(applicationContext)
        DeviceServiceConnector.addConnectionWrapper(object : ConnectionWrapper {
            override fun onPrinterServiceConnected(printerService: IPrinterServiceWrapper) {
                Log.e(javaClass.simpleName, "onPrinterServiceConnected")
            }

            override fun onPrinterServiceDisconnected() {
                Log.e(javaClass.simpleName, "onPrinterServiceDisconnected")
            }

            override fun onScalesServiceConnected(scalesService: IScalesServiceWrapper) {
                Log.e(javaClass.simpleName, "onScalesServiceConnected")
            }

            override fun onScalesServiceDisconnected() {
                Log.e(javaClass.simpleName, "onScalesServiceDisconnected")
            }
        })
    }
}
