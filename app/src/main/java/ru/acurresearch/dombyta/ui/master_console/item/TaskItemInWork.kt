package ru.acurresearch.dombyta.ui.master_console.item

import android.graphics.Color
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_in_work_tasks_list.view.*
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.common.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TaskItemInWork(
    private val task: Task,
    private val onTaskItemClicked: (Task) -> Unit
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.list_item_in_work_tasks_list

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val timeDiff = task.expDate!!.time - Date().time
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault())
        val hoursLeft = TimeUnit.HOURS.convert(timeDiff, TimeUnit.MILLISECONDS)

        viewHolder.itemView.in_work_item_exp_in_holder.text = simpleDateFormat.format(task.expDate)
        viewHolder.itemView.in_work_item_name.text = task.name ?: "???"
        viewHolder.itemView.in_work_item_order_no_holder.text = task.orderInternalId?.toString() ?: "???"
        viewHolder.itemView.in_work_items_days_left.text = when {
            hoursLeft < 0 -> "0, просрочено на ${-hoursLeft}"
            else -> hoursLeft.toString()
        }
        viewHolder.itemView.in_work_item_master.text = task.master.target.name

        when {
            hoursLeft < 0 -> viewHolder.itemView.in_work_item_list_card.setCardBackgroundColor(Color.parseColor("#80EF5350"))
            hoursLeft < 1 -> viewHolder.itemView.in_work_item_list_card.setCardBackgroundColor(Color.parseColor("#4FFFEE58"))
        }

        viewHolder.itemView.setOnClickListener { onTaskItemClicked(task) }
    }

}