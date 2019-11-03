package ru.acurresearch.dombyta_new.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.annotations.SerializedName
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.receipt.Position
import java.math.BigDecimal
import java.util.*

@Entity data class OrderPosition(
    @Id val uuid: String,
    val quantity: Double,
    val price: Double,
    val productName: String,
    val expDate: Date?
) {
    lateinit var serviceItem: ToOne<ServiceItemCustom>
    lateinit var order: ToOne<Order>

    fun toEvotorPositionAdd() =
        PositionAdd(
            Position.Builder.newInstance(
                uuid,
                null,
                serviceItem.target.name,
                "шт",
                0,
                BigDecimal(price),
                BigDecimal(quantity)
            ).build()
        )

    companion object {
        val serializer = jsonSerializer<OrderPosition> { (src, type, context) -> jsonObject(
            "uuid" to src.uuid,
            "serv_item_full" to context.serialize(src.serviceItem.target),
            "quantity" to src.quantity,
            "price" to src.price,
            "product_name" to src.productName,
            "expires_in" to src.expDate
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> OrderPosition(
            uuid = src["uuid"].asString,
            quantity = src["quantity"].asDouble,
            price = src["price"].asDouble,
            productName = src["product_name"].asString,
            expDate = context.deserialize(src.getOrNull("expires_in") ?: JsonNull.INSTANCE)
        ).apply {
            val serviceItem = context.deserialize<ServiceItemCustom>(src["serv_item_full"])
            this.serviceItem.target = serviceItem
            serviceItem.orderPosition.target = this
        } }
    }
}