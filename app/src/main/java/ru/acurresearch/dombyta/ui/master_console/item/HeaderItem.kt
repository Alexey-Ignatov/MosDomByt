package ru.acurresearch.dombyta.ui.master_console.item

import android.view.View
import androidx.annotation.DrawableRes
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_header.view.*
import ru.acurresearch.dombyta.R

open class HeaderItem(
    private val title: String,
    @DrawableRes private val iconResId: Int? = null,
    private val onIconClickListener: View.OnClickListener? = null
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int = R.layout.item_header

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.title.text = title

        viewHolder.itemView.icon.apply {
            visibility = View.GONE
            iconResId?.let {
                visibility = View.VISIBLE
                setImageResource(it)
                setOnClickListener(onIconClickListener)
            }
        }
    }
}