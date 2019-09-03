package ru.acurresearch.dombyta.Adapters


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_final_order.view.*
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.CheckPostition
import ru.acurresearch.dombyta.OrderPostition
import ru.acurresearch.dombyta.R


class OrderFinalViewAdapter(val items : ArrayList<OrderPostition>, val context: Context) : RecyclerView.Adapter<OrderFinalViewAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_final_order, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : RecyclerView.ViewHolder(view) {

        // Holds the TextView that will add each animal to
        fun setData(value: OrderPostition, pos: Int) {
            view.serv_item_name.text = value.productName
            view.position_price.text = value.price.toString() + " руб."
            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
            }
    }

}

