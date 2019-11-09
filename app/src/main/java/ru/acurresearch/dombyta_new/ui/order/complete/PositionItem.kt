package ru.acurresearch.dombyta_new.ui.order.complete

import android.annotation.SuppressLint
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_final_order.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition
import ru.acurresearch.dombyta_new.data.common.model.ServiceItemCustom

class PositionItem(
    private val orderPosition: OrderPosition
): Item<GroupieViewHolder>() {
    override fun getLayout(): Int = R.layout.list_item_final_order

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.serv_item_name.text = orderPosition.productName
        viewHolder.itemView.position_price.text = "${orderPosition.price} руб."
    }

}