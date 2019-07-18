package ru.acurresearch.mosdombyt.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.content_x.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.mosdombyt.*
import ru.acurresearch.mosdombyt.Adapters.OrderViewAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.framework.inventory.ProductQuery
import java.math.BigDecimal
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import kotlin.collections.ArrayList
import ru.evotor.framework.core.IntegrationException
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit
import java.io.IOException


class MainActivity : AppCompatActivity() {
    lateinit var adapter: RecyclerView.Adapter<OrderViewAdapter.ViewHolder>
    lateinit var currOrder: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_x)

        clearSelectedGoods()
        displayTypeByPaymentType()
        initSelectedPositionsList()

        currOrder = Order("1", ArrayList(listOf()), null, null)
        refreshAllowedProds()

        add_good.setOnClickListener {
            startActivity(Intent(this, SelectPositionActivity::class.java))
        }
        create_pre_pay_order.setOnClickListener {
            //TODO send all to server

            currOrder.client = getClientFromForm()
            App.prefs.lastOrder=currOrder
            currOrder.realize(this)
        }
        create_post_pay_order.setOnClickListener {
            currOrder.client = getClientFromForm()
            App.prefs.lastOrder=currOrder
            startActivity(Intent(this, OrderFinalActivity::class.java))
        }
        //initPrinter()



        //sendCheck()

    }

    override fun onResume() {
        super.onResume()

        refreshTotal()
        currOrder.positionsList= ArrayList(App.prefs.selectedPositions)

        adapter= OrderViewAdapter(currOrder.positionsList, this)
        order_list_view.adapter = adapter


    }
    fun refreshTotal(){
        total_count_txt.text = "Услуги ( " + currOrder.price  +" руб.)"
    }

    fun refreshAllowedProds(){
        //TODO get from server data
        val my_goods = ProductQuery().price.notEqual(BigDecimal.valueOf(0)).execute(this).toList()
        App.prefs.allAllowedProducts = my_goods.map {
            ServiceItemCustom.fromEvoProductItem(it!!,10.0, 3600*24)}
    }



    fun initSelectedPositionsList(){
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        order_list_view.layoutManager = layoutManager
        adapter= OrderViewAdapter(ArrayList(), this)
    }


    fun displayTypeByPaymentType(){
        var intent = getIntent()
        val type = intent.getStringExtra("pay_type")
        if ( type ==Constants.INENT_PAY_TYPE_POSTPAY){
            create_pre_pay_order.visibility = View.INVISIBLE


        } else if( type ==Constants.INENT_PAY_TYPE_PREPAY){
            create_post_pay_order.visibility = View.INVISIBLE
        }


    }

    fun sendCheck(){
        val evoReceipt = ReceiptApi.getReceipt(this, Receipt.Type.SELL)
        val checkToSend = Check.fromEvoReceipt(evoReceipt!!)
        App.prefs.currCheck = checkToSend


        fun onSuccess(resp_data: String){

        }

        val call = App.api.sendCheckServ(App.prefs.currCheck)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    if (response.isSuccessful)
                            onSuccess(response.body()!!)
                    else
                        Log.e("sendPhone", "Sorry, failure on request "+ response.errorBody())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("sendPhone", "Sorry, unable to make request", t)
                Toast.makeText(getApplicationContext(),"Sorry, unable to make request" + t.toString(),Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun clearSelectedGoods(){
        App.prefs.selectedPositions = listOf()
    }



    fun createCheckFromSelected(){
        val changes = ArrayList<PositionAdd>()
        var listItem = ArrayList(App.prefs.selectedPositions.map { it.toEvotorPositionAdd() })
        listItem.map { changes.add(it) }


        OpenSellReceiptCommand(changes, null).process(
            this
        ) { integrationManagerFuture ->
            try {
                val result = integrationManagerFuture.result
                if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                    //Чтобы открыть другие документы используйте методы NavigationApi.
                    startActivity(createIntentForSellReceiptEdit())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IntegrationException) {
                e.printStackTrace()
            }
        }
    }

    fun getClientFromForm(): Client{
        return Client(ed_txt_name.text.toString(), ed_txt_phone.text.toString())
    }



}
