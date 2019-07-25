package ru.acurresearch.mosdombyt.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_in_work_tasks_list.view.*
import kotlinx.android.synthetic.main.list_item_search.view.*
import ru.acurresearch.mosdombyt.*
import ru.acurresearch.mosdombyt.Activities.MainActivity
import ru.acurresearch.mosdombyt.Activities.OrderFinalActivity
import ru.acurresearch.mosdombyt.App.App
import android.content.DialogInterface
import kotlinx.android.synthetic.main.list_item_complete_tasks_list.view.*
import kotlinx.android.synthetic.main.list_item_new_tasks_list.view.*
import ru.acurresearch.mosdombyt.Activities.MasterConsolActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class InWorkTaskListAdapter(val items : ArrayList<Task>, val context: Context) : RecyclerView.Adapter<InWorkTaskListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_in_work_tasks_list, parent, false)
        return ViewHolder(view)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(items[position], position)
    }


    inner class ViewHolder (var view: View) : RecyclerView.ViewHolder(view) {



        // Holds the TextView that will add each animal to
        fun setData(value: Task, pos: Int) {


            val time_diff = value.expDate!!.getTime() - Date().getTime()
            val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN)


            view.in_work_item_exp_in_holder.text = simpleDateFormat.format(value.expDate!!)
            view.in_work_item_name.text = value.name ?: "???"
            view.in_work_item_order_no_holder.text = value.orderInternalId?.toString() ?: "???"
            view.in_work_items_days_left.text = TimeUnit.HOURS.convert(time_diff, TimeUnit.MILLISECONDS).toString()
            view.in_work_item_master.text = value.master?.name ?: "???"




            view.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Завершить выполнение заказа?")
                //alertDialog.setMessage("Alert message to be shown"
                alertDialog.setCancelable(true)
                alertDialog.setPositiveButton("Да") { dialog, id ->
                        // if this button is clicked, close
                        // current activity
                    value.finish(context)

                    val removeIndex = getAdapterPosition()
                    items.removeAt(removeIndex)
                    notifyItemRemoved(removeIndex)

                    val position = (context as MasterConsolActivity).completeTasksItems.size
                    (context as MasterConsolActivity).completeTasksItems.add(position, value)
                    (context as MasterConsolActivity).adapterCompleteTask.notifyItemInserted(position)
                    (context as MasterConsolActivity).dumpTasksToDisk()

                    //(context as MasterConsolActivity).fetchAndBuildTasks()
                    }
                alertDialog.setNegativeButton("Нет") { dialog, id ->
                        dialog.cancel()
                    }


                //val dialog = builder.create()
                alertDialog.create().show()


            }



            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
        }
    }

}