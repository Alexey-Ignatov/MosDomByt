package ru.acurresearch.dombyta_new.ui.master_console.item

import android.graphics.drawable.Animatable
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.item_header.view.*
import ru.acurresearch.dombyta.R

class ExpandableHeaderItem(
    title: String
) : HeaderItem(title), ExpandableItem {

    var clickListener: ((ExpandableHeaderItem) -> Unit)? = null

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)

        // Initial icon state -- not animated.
        viewHolder.itemView.icon.apply {
            visibility = View.VISIBLE
            setImageResource(if (expandableGroup.isExpanded) R.drawable.collapse else R.drawable.expand)
        }

        viewHolder.itemView.setOnClickListener {
            expandableGroup.onToggleExpanded()
            bindIcon(viewHolder)
        }
    }

    private fun bindIcon(viewHolder: GroupieViewHolder) {
        viewHolder.itemView.icon.apply {
            visibility = View.VISIBLE
            setImageResource(if (expandableGroup.isExpanded) R.drawable.collapse else R.drawable.expand)
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}