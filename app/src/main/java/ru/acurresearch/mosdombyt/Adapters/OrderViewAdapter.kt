package ru.acurresearch.mosdombyt.Adapters


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import ru.acurresearch.mosdombyt.Activities.MainActivity
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.CheckPostition
import ru.acurresearch.mosdombyt.OrderPostition
import ru.acurresearch.mosdombyt.R


class OrderViewAdapter(val items : ArrayList<OrderPostition>, val context: Context) : RecyclerView.Adapter<OrderViewAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.close_btn.setOnClickListener {
                val removeIndex = getAdapterPosition()
                items.removeAt(removeIndex)


                App.prefs.selectedPositions = items
                notifyItemRemoved(removeIndex)
                (context as MainActivity).refreshTotal()
            }
        }

        // Holds the TextView that will add each animal to
        fun setData(value: OrderPostition, pos: Int) {
            view.item_text_popup.text = value.serviceItem.name
            view.item_price_holder.text = value.price.toString() + " руб."
            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
            }
    }

}

