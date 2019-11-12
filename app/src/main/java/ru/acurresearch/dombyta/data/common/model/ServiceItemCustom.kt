package ru.acurresearch.dombyta.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import ru.evotor.framework.inventory.ProductItem
import java.util.*


@Entity data class ServiceItemCustom (
    @Id var id: Long,
    val uuid: String,
    val productUUID: String?,
    val name: String,
    val defPrice: Double?,
    val defExpiresIn: Double?
) {
    constructor(): this(0, "", "", "", 0.0, 0.0)

    lateinit var orderPosition: ToOne<OrderPosition>

    companion object {
        val serializer = jsonSerializer<ServiceItemCustom> { (src, type, context) -> jsonObject(
            "uuid" to src.uuid,
            "product_uuid" to src.productUUID,
            "product_name" to src.name,
            "default_price" to src.defPrice,
            "default_expires_delta" to src.defExpiresIn
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> ServiceItemCustom(
            id = 0,
            uuid = src["uuid"].asString,
            productUUID = src.getOrNull("product_uuid")?.asString,
            name = src["product_name"].asString,
            defPrice = src.getOrNull("default_price")?.asDouble,
            defExpiresIn = src.getOrNull("default_expires_delta")?.asDouble
        ) }

        fun fromEvoProductItem(
            evoPos: ProductItem,
            defPrice: Double? = null,
            defExpiresIn: Double?
        ) =
            ServiceItemCustom(
                id = 0,
                uuid = UUID.randomUUID().toString(),
                productUUID = evoPos.uuid,
                name = evoPos.name,
                defPrice = defPrice,
                defExpiresIn = defExpiresIn
            )

    }
}