package ru.acurresearch.dombyta.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_complete_tasks_list.view.*
import kotlinx.android.synthetic.main.list_item_search.view.*
import ru.acurresearch.dombyta.*
import ru.acurresearch.dombyta.Activities.MainActivity
import ru.acurresearch.dombyta.Activities.OrderFinalActivity
import ru.acurresearch.dombyta.App.App
import android.content.DialogInterface
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.MasterConsolActivity
import java.text.SimpleDateFormat
import java.time.Period
import java.util.*
import javax.xml.datatype.DatatypeConstants.DAYS
import java.util.concurrent.TimeUnit

//code review: (all adapters) возможна замена groupie или аналогами
class CompleteTaskListAdapter(val items : List<Task>, val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<CompleteTaskListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_complete_tasks_list, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {



        // Holds the TextView that will add each animal to
        fun setData(value: Task, pos: Int) {
            val time_diff = value.expDate!!.getTime() - Date().getTime()
            val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault())



            view.complete_item_exp_in_holder.text = simpleDateFormat.format(value.expDate!!)
            view.complete_item_name.text = value.name ?: "???"
            view.complete_item_order_no_holder.text = value.orderInternalId?.toString() ?: "???"
            view.complete_items_days_left.text = TimeUnit.HOURS.convert(time_diff, TimeUnit.MILLISECONDS).toString()
            view.complete_item_master.text = value.master?.name ?: "???"



            view.setOnClickListener {


            }



            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
        }
    }

}
