package ru.acurresearch.mosdombyt.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_print_order_label.*
import ru.acurresearch.mosdombyt.Adapters.OrderFinalViewAdapter
import ru.acurresearch.mosdombyt.Adapters.OrderViewAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.R
import ru.evotor.devices.commons.ConnectionWrapper
import ru.evotor.devices.commons.DeviceServiceConnector
import ru.evotor.devices.commons.exception.DeviceServiceException
import ru.evotor.devices.commons.printer.PrinterDocument
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.devices.commons.services.IPrinterServiceWrapper
import ru.evotor.devices.commons.services.IScalesServiceWrapper


class OrderFinalActivity : AppCompatActivity() {
    lateinit var lastOrder: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_order_label)
        lastOrder = App.prefs.lastOrder

        initUi()


        print_label_btn.setOnClickListener {
            initPrinter()
            print("#######   #####    ######  \r\n##   ##  ##   ##      ##   \r\n    ##       ###     ##    \r\n   ##      ####     ####   \r\n  ##      ####         ##  \r\n  ##     ###      ##   ##  \r\n  ##     #######   #####   \r\n                           \r\n")


            refreshCart()
            finish()

        }



        initPosistionsListView()



    }

    fun refreshCart(){
        App.prefs.selectedPositions = listOf()
    }


    fun initUi(){
        order_client_name_holder.text = lastOrder.client!!.name
        order_client_phone_holder.text = lastOrder.client!!.phone
    }

    fun initPosistionsListView(){
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        order_list_final.layoutManager = layoutManager
        var adapter= OrderFinalViewAdapter(lastOrder.positionsList, this)
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
