package ru.acurresearch.dombyta.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.content_x.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.dombyta.*
import ru.acurresearch.dombyta.Adapters.OrderViewAdapter
import ru.acurresearch.dombyta.App.App
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
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEUTRAL
import androidx.appcompat.app.AlertDialog
import com.redmadrobot.inputmask.MaskedTextChangedListener


class MainActivity : AppCompatActivity() {
    lateinit var adapter: androidx.recyclerview.widget.RecyclerView.Adapter<OrderViewAdapter.ViewHolder>
    lateinit var currOrder: Order
    lateinit var pay_type: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_x)



        val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", ed_txt_phone)

        ed_txt_phone.addTextChangedListener(listener)
        ed_txt_phone.onFocusChangeListener = listener

        var intent = getIntent()
        pay_type = intent.getStringExtra(Constants.INTENT_PAY_TYPE_FIELD)

        clearSelectedGoods()
        displayTypeByPaymentType()
        initSelectedPositionsList()

        refreshAllowedProds()
        currOrder = if (pay_type == Constants.BillingType.PREPAY)  Order.newPrePaid() else Order.newPostPaid()


        add_good.setOnClickListener {
            startActivity(Intent(this, ru.acurresearch.dombyta.Activities.SelectPositionActivity::class.java))
        }

        create_pre_pay_order.setOnClickListener {
            if (ed_txt_name.text.toString() == "" || ed_txt_phone.text.toString() == ""){
                Toast.makeText(this, "Введите данные клиента. ", Toast.LENGTH_SHORT).show()
            }else if(currOrder.price == 0.0){
                Toast.makeText(this, "Добавьте услуги", Toast.LENGTH_SHORT).show()
            }else {
                currOrder.client = Client(ed_txt_name.text.toString(), ed_txt_phone.text.toString())
                App.prefs.lastOrder=currOrder

                var intent = Intent(this, ru.acurresearch.dombyta.Activities.OrderFinalActivity::class.java)
                intent.putExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL, currOrder.toJson())
                startActivity(intent)
            }

        }

        create_post_pay_order.setOnClickListener {
            if (ed_txt_name.text.toString() == "" || ed_txt_phone.text.toString() == ""){
                Toast.makeText(this, "Введите данные клиента. ", Toast.LENGTH_SHORT).show()
            }else if(currOrder.price == 0.0){
                Toast.makeText(this, "Добавьте услуги", Toast.LENGTH_SHORT).show()
            }else {
                currOrder.client = Client(ed_txt_name.text.toString(), ed_txt_phone.text.toString())
                App.prefs.lastOrder = currOrder


                var intent = Intent(this, ru.acurresearch.dombyta.Activities.OrderFinalActivity::class.java)
                intent.putExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL, currOrder.toJson())

                startActivity(intent)
            }
        }
        //initPrinter()


        //sendCheck()

    }

    override fun onResume() {
        super.onResume()
        currOrder.positionsList= ArrayList(App.prefs.selectedPositions)
        refreshTotal()

        adapter= OrderViewAdapter(currOrder.positionsList, this)
        order_list_view.adapter = adapter


    }
    fun refreshTotal(){
        total_count_txt.text = "Услуги ( " + currOrder.price  +" руб.)"
    }

    fun refreshAllowedProds(){

        fun onSuccess(resp_data: List<ServiceItemCustom>){
            App.prefs.allAllowedProducts = resp_data
        }

        val call = App.api.fetchAllowedProds(App.prefs.cashBoxServerData.authHeader)
        call.enqueue(object : Callback<List<ServiceItemCustom>> {
            override fun onResponse(call: Call<List<ServiceItemCustom>>, response: Response<List<ServiceItemCustom>>) {
                Log.e("processServerRqueAAA",response.errorBody().toString() )
                if (response.isSuccessful)
                    onSuccess(response.body()!!)
                else
                    Toast.makeText(this@MainActivity,"Ошибка на сервере. Мы устраняем проблему. Повторите позже.", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<List<ServiceItemCustom>>, t: Throwable) {
                Toast.makeText(getApplicationContext(),"ВНИМАНИЕ! Не удалось получить товары!", Toast.LENGTH_SHORT).show()
            }
        })

    }



    fun initSelectedPositionsList(){
        var layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        order_list_view.layoutManager = layoutManager
        adapter= OrderViewAdapter(ArrayList(), this)
    }


    fun displayTypeByPaymentType(){

        if ( pay_type ==Constants.BillingType.POSTPAY){
            create_pre_pay_order.visibility = View.INVISIBLE


        } else if( pay_type ==Constants.BillingType.PREPAY){
            create_post_pay_order.visibility = View.INVISIBLE
        }


    }


    fun clearSelectedGoods(){
        App.prefs.selectedPositions = listOf()
    }







}
