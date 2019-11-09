package ru.acurresearch.dombyta_new.ui.order.pay.add_good

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.ServiceItemCustom

class GoodItem(
    private val good: ServiceItemCustom,
    private val onGoodClicked: (ServiceItemCustom) -> Unit
): Item<GroupieViewHolder>() {
    override fun getLayout(): Int = R.layout.list_item_popup

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.item_text_popup.text = good.name
        viewHolder.itemView.setOnClickListener { onGoodClicked(good) }
    }

}