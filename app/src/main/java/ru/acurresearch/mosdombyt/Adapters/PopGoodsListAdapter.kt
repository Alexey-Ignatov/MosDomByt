package ru.acurresearch.mosdombyt.Adapters


import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.R
import ru.acurresearch.mosdombyt.Activities.SelectPositionActivity
import ru.acurresearch.mosdombyt.ServiceItemCustom


class PopGoodsListAdapter(val items : List<ServiceItemCustom>, val context: Context,var activity: Activity) : RecyclerView.Adapter<PopGoodsListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_popup, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to



        fun setData(value: ServiceItemCustom, pos: Int) {
            view.item_text_popup.text = value.name
            view.setOnClickListener {
                (activity as SelectPositionActivity).hideList()
                (activity as SelectPositionActivity).showPriceCard()
                (activity as SelectPositionActivity).showEditingPostionName()
                (activity as SelectPositionActivity).setCardPricePopProps(value)
                (activity as SelectPositionActivity).initDeadlineTimePicker(value)
                (activity as SelectPositionActivity).currProdInPopUpPrice = value
                //(activity as SelectPositionActivity).setCurrProdInPopUpPrice(value)
                //App.prefs.currProdInPopUpPrice = value

            }
            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
        }
    }

}

