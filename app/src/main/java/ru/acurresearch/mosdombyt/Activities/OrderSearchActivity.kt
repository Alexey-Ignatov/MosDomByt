package ru.acurresearch.mosdombyt.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_order_search.*
import kotlinx.android.synthetic.main.content_x.*
import kotlinx.android.synthetic.main.list_item_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.mosdombyt.Adapters.OrderSearchListAdapter
import ru.acurresearch.mosdombyt.Adapters.OrderViewAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.R
import ru.acurresearch.mosdombyt.Task

class OrderSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_search)



        search_act_search_btn.setOnClickListener {
            fetchOrdersAndShow(search_act_search_input.text.toString())
            //initOrdersListView(ArrayList((1..10).map{Order.newPostPaid()}))
            //TODO fetch data from server
        }

    }

    fun initOrdersListView(items: ArrayList<Order>){
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        search_orders_list.layoutManager = layoutManager
        var adapter= OrderSearchListAdapter(items, this)
        search_orders_list.adapter = adapter
    }

    fun fetchOrdersAndShow(searchString: String){
        fun onSuccess(resp_data: List<Order>){
            initOrdersListView(ArrayList(resp_data))

        }

        val call = App.api.searchOrder(searchString, App.prefs.cashBoxServerData.authHeader)
        call.enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful)
                    onSuccess(response.body()!!)
                else
                    Toast.makeText(this@OrderSearchActivity,"Ошибка на сервере. Мы устраняем проблему. Повторите позже.", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(getApplicationContext(),"Проверьте подключение к интернету!", Toast.LENGTH_LONG).show()
            }
        })
    }
}
