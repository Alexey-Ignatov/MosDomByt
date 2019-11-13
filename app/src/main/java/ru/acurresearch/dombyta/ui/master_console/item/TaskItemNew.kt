package ru.acurresearch.dombyta.ui.master_console.item

import android.graphics.Color
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_new_tasks_list.view.*
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.common.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class TaskItemNew(
    private val task: Task,
    private val onTaskItemClicked: (Task) -> Unit
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.list_item_new_tasks_list

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val timeDiff = task.expDate!!.time - Date().time
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault())
        val hoursLeft = TimeUnit.HOURS.convert(timeDiff, TimeUnit.MILLISECONDS)

        viewHolder.itemView.new_task_item_exp_in_holder.text = simpleDateFormat.format(task.expDate)
        viewHolder.itemView.new_task_item_name.text = task.name ?: "???"
        viewHolder.itemView.new_task_item_order_no_holder.text = task.orderInternalId?.toString() ?: "???"
        if(hoursLeft < 0) viewHolder.itemView.time_text.text = "Часов просрочено"
        viewHolder.itemView.new_task_items_days_left.text = abs(hoursLeft).toString()
        viewHolder.itemView.new_task_item_master.text = task.master.target?.name ?: "???"

        if (hoursLeft < 0) viewHolder.itemView.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#80EF5350"))
        else if(hoursLeft < 1) viewHolder.itemView.new_task_item_list_card.setCardBackgroundColor(Color.parseColor("#4FFFEE58"))

        viewHolder.itemView.setOnClickListener { onTaskItemClicked(task) }
    }

}