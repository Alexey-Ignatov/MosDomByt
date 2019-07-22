package ru.acurresearch.mosdombyt.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_order_search.*
import kotlinx.android.synthetic.main.content_x.*
import kotlinx.android.synthetic.main.list_item_search.*
import ru.acurresearch.mosdombyt.Adapters.OrderSearchListAdapter
import ru.acurresearch.mosdombyt.Adapters.OrderViewAdapter
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.R

class OrderSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_search)


        initOrdersListView(ArrayList((1..10).map{Order.newPostPaid()}))
        search_act_search_btn.setOnClickListener {
            //TODO perform search
        }

    }

    fun initOrdersListView(items: ArrayList<Order>){
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        search_orders_list.layoutManager = layoutManager
        var adapter= OrderSearchListAdapter(items, this)
        search_orders_list.adapter = adapter
    }
}
