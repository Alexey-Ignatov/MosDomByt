package ru.acurresearch.dombyta.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item_new_tasks_list.view.*
import kotlinx.android.synthetic.main.list_item_search.view.*
import ru.acurresearch.dombyta.*
import ru.acurresearch.dombyta.Activities.MainActivity
import ru.acurresearch.dombyta.Activities.OrderFinalActivity
import ru.acurresearch.dombyta.App.App
import android.content.DialogInterface
import kotlinx.android.synthetic.main.list_item_complete_tasks_list.view.*
import kotlinx.android.synthetic.main.list_item_in_work_tasks_list.view.*
import ru.acurresearch.dombyta.Activities.MasterConsolActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.R.attr.data
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewTaskListAdapter(val items : ArrayList<Task>, val context: Context) : RecyclerView.Adapter<NewTaskListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        val tmp = items.size
        return tmp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_item_new_tasks_list, parent, false)
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

            val hoursLeft = TimeUnit.HOURS.convert(time_diff, TimeUnit.MILLISECONDS)
            view.new_task_item_exp_in_holder.text = simpleDateFormat.format(value.expDate!!)
            view.new_task_item_name.text = value.name ?: "???"
            view.new_task_item_order_no_holder.text = value.orderInternalId?.toString() ?: "???"
            view.new_task_items_days_left.text = hoursLeft.toString()
            view.new_task_item_master.text = value.master?.name ?: "???"

            if (hoursLeft < 0) {
                view.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#80EF5350"))
            }else if(hoursLeft < 1){
                view.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#4FFFEE58"))
            }


            view.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Чтобы взять заказ в работу, выберете мастера:")
                //alertDialog.setMessage("Alert message to be shown")

                val masters = App.prefs.allMasters.map { it.name }.toTypedArray()
                alertDialog.setItems(masters) { dialog, which ->

                    value.takeInWork(App.prefs.allMasters[which], context)

                    val removeIndex = getAdapterPosition()
                    items.removeAt(removeIndex)
                    notifyItemRemoved(removeIndex)

                    val position = (context as MasterConsolActivity).inWorkTasksItems.size
                    (context as MasterConsolActivity).inWorkTasksItems.add(position, value)
                    (context as MasterConsolActivity).adapterInWorkTask.notifyItemInserted(position)
                    (context as MasterConsolActivity).dumpTasksToDisk()

                    //(context as MasterConsolActivity).fetchAndBuildTasks()
                }
                alertDialog.create().show()

            }



            //view.item_list_card.setCardBackgroundColor(Color.RED)
            //if (phone in App.prefs.completePollPhonesList)
            //    view.item_list_card.setCardBackgroundColor(Color.GREEN)
        }
    }

}