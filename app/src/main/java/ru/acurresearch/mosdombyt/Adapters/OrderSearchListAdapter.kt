package ru.acurresearch.mosdombyt.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_search.view.*
import ru.acurresearch.mosdombyt.Activities.MainActivity
import ru.acurresearch.mosdombyt.Activities.OrderFinalActivity
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.OrderPostition
import ru.acurresearch.mosdombyt.R

class OrderSearchListAdapter(val items : ArrayList<Order>, val context: Context) : RecyclerView.Adapter<OrderSearchListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : RecyclerView.ViewHolder(view) {



        // Holds the TextView that will add each animal to
        fun setData(value: Order, pos: Int) {
            view.search_act_name_ph.text = value.client.name
            view.search_act_phone_ph.text = value.client.phone
            view.search_act_sum_ph.text = value.price.toString() + " руб."
            view.search_act_order_id_ph.text = "?????"


            view.setOnClickListener {
                var intent = Intent(context, OrderFinalActivity::class.java)
                intent.putExtra(Constants.INTENT_ORDER_TO_ORDER_FINAL, value.toJson())
                context.startActivity(intent)
            }

            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
        }
    }

}