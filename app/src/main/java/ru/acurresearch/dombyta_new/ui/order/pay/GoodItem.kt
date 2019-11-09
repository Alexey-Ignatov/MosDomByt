package ru.acurresearch.dombyta_new.ui.order.pay

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition

class GoodItem(
    private val orderPosition: OrderPosition,
    private val onDeleteClicked: (OrderPosition) -> Unit
): Item<GroupieViewHolder>() {
    override fun getLayout(): Int = R.layout.list_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.item_text_popup.text = orderPosition.productName
        viewHolder.itemView.item_price_holder.text = orderPosition.price.toString() + " руб."
        viewHolder.itemView.close_btn.setOnClickListener { onDeleteClicked(orderPosition) }
    }

}