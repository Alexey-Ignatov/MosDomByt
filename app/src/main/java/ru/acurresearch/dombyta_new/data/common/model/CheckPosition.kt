package ru.acurresearch.dombyta_new.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import com.google.gson.annotations.SerializedName
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.receipt.Position
import java.math.BigDecimal

@Entity data class CheckPosition(
    @Id var id: Long,
    val uuid: String,
    val productUUID: String?,
    val name: String,
    val quantity: Double,
    val price: Double
) {
    lateinit var check: ToOne<Check>

    fun toEvotorPositionAdd() =
        PositionAdd(
            Position.Builder.newInstance(
                uuid,
                productUUID,
                name,
                "шт",
                0,
                BigDecimal(quantity),
                BigDecimal(price)
            ).build()
        )

    companion object {
        val serializer = jsonSerializer<CheckPosition> { (src, type, context) -> jsonObject(
            "pos_uuid" to src.uuid,
            "product_uuid" to src.productUUID,
            "product_name" to src.name,
            "quantity" to src.quantity,
            "price" to src.price
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> CheckPosition(
            id = 0,
            uuid = src["pos_uuid"].asString,
            productUUID = src.getOrNull("product_uuid")?.asString,
            name = src["product_name"].asString,
            quantity = src["quantity"].asDouble,
            price = src["price"].asDouble
        ) }

        fun fromEvoPosition(evoPos : Position) =
            CheckPosition(
                id = 0,
                uuid = evoPos.uuid,
                productUUID = evoPos.productUuid,
                name = evoPos.name,
                quantity = evoPos.quantity.toDouble(),
                price = evoPos.price.toDouble()
            )
    }


}