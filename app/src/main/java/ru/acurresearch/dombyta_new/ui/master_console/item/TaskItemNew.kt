package ru.acurresearch.dombyta_new.ui.master_console.item

import android.graphics.Color
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_new_tasks_list.view.*
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TaskItemNew(
    private val task: Task,
    private val onTaskItemClicked: (Task) -> Unit
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.list_item_new_tasks_list

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val time_diff = task.expDate!!.getTime() - Date().getTime()
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN)
        val hoursLeft = TimeUnit.HOURS.convert(time_diff, TimeUnit.MILLISECONDS)
        viewHolder.itemView.new_task_item_exp_in_holder.text = simpleDateFormat.format(task.expDate)
        viewHolder.itemView.new_task_item_name.text = task.name ?: "???"
        viewHolder.itemView.new_task_item_order_no_holder.text = task.orderInternalId?.toString() ?: "???"
        viewHolder.itemView.new_task_items_days_left.text = hoursLeft.toString()
        viewHolder.itemView.new_task_item_master.text = task.master.target.name

        if (hoursLeft < 0) viewHolder.itemView.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#80EF5350"))
        else if(hoursLeft < 1) viewHolder.itemView.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#4FFFEE58"))

        viewHolder.itemView.setOnClickListener { onTaskItemClicked(task) }
    }

}