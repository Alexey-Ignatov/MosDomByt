package ru.acurresearch.dombyta.ui.order.search

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_search.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.common.model.Order

class OrderSearchItem(
    private val order: Order,
    private val onOrderClicked: (Order) -> Unit
): Item<GroupieViewHolder>() {
    override fun getLayout(): Int = R.layout.list_item_search

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_act_name_ph.text = order.client.target.name
        viewHolder.itemView.search_act_phone_ph.text = order.client.target.phone
        viewHolder.itemView.search_act_sum_ph.text = order.price.toString() + " руб."
        viewHolder.itemView.search_act_order_id_ph.text = order.internalId?.toString() ?: "???"
        viewHolder.itemView.setOnClickListener { onOrderClicked(order) }
    }

}