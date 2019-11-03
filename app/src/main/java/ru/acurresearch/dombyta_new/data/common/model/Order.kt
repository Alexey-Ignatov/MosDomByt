package ru.acurresearch.dombyta_new.data.common.model

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.annotations.SerializedName
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import java.util.*

@Entity data class Order(
    @Id val id: Int,
    var customPrice: Double?,
    var billType: String,
    var status: String,
    var dateCreated: Date?,
    var internalId: Int?,
    var printLabel: String?,
    var printKvitok: String?,
    var evoResUuid: String?,
    var isPaid: Boolean = false
) {
    @Backlink(to = "order") lateinit var positionsList: ToMany<OrderPosition>
    lateinit var client: ToOne<Client>
    val price: Double
        get() = customPrice ?: positionsList.map { it.price*it.quantity }.sum()

    fun suggestAction(): String =
        when {
            billType == BillingType.PREPAY ->
                when {
                    !isPaid -> OrderSuggestedAction.PAY
                    status == OrderStatus.PRE_CREATED -> OrderSuggestedAction.CREATE
                    status == OrderStatus.READY -> OrderSuggestedAction.CLOSE
                    else -> OrderSuggestedAction.NOTHING
                }

            billType == BillingType.POSTPAY -> when {
                status == OrderStatus.PRE_CREATED -> OrderSuggestedAction.CREATE
                (status == OrderStatus.READY) && (!isPaid) -> OrderSuggestedAction.PAY
                (status == OrderStatus.READY) && (isPaid) -> OrderSuggestedAction.CLOSE
                else -> OrderSuggestedAction.NOTHING
            }
            !isPaid -> OrderSuggestedAction.PAY
            else -> OrderSuggestedAction.NOTHING
        }

    companion object {
        val serializer = jsonSerializer<Order> { (src, type, context) -> jsonObject(
            "id" to src.id,
            "custom_price" to src.customPrice,
            "billing_type" to src.billType,
            "order_status" to src.status,
            "created_at" to src.dateCreated,
            "id_in_store" to src.internalId,
            "in_house_kvitok" to src.printLabel,
            "clients_kvitok" to src.printKvitok,
            "evotor_receipt_uuid" to src.evoResUuid,
            "positions_list" to jsonArray(src.positionsList.mapTo(ArrayList<JsonElement>()) { context.serialize(it) }),
            "client" to context.serialize(src.client),
            "is_paid" to src.isPaid
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Order(
            id = src["id"].asInt,
            customPrice = src.getOrNull("custom_price")?.asDouble,
            billType = src["billing_type"].asString,
            status = src["status"].asString,
            dateCreated = context.deserialize(src.getOrNull("created_at") ?: JsonNull.INSTANCE),
            internalId = src.getOrNull("id_in_store")?.asInt,
            printLabel = src.getOrNull("in_house_kvitok")?.asString,
            printKvitok = src.getOrNull("clients_kvitok")?.asString,
            evoResUuid = src.getOrNull("evotor_receipt_uuid")?.asString,
            isPaid = src.getOrNull("is_paid")?.asBoolean ?: false
        ).apply {
            val client = context.deserialize<Client>(src["client"])
            client.order.target = this
            this.client.target = client

            src["positions_list"].asJsonArray.map { context.deserialize<OrderPosition>(it) }
                .forEach {
                    it.order.target = this
                    this.positionsList.add(it)
                }
        } }

        fun empty(): Order{
            return Order(
                id = 0,
                customPrice = null,
                billType = BillingType.PREPAY,
                status = OrderStatus.PRE_CREATED,
                dateCreated = null,
                internalId = null,
                printLabel = null,
                printKvitok = null,
                evoResUuid = null
            )
        }
        fun newPrePaid(): Order{
            return Order(
                id =0,
                customPrice = null,
                billType = BillingType.PREPAY,
                status = OrderStatus.PRE_CREATED,
                dateCreated = null,
                internalId = null,
                printLabel = null,
                printKvitok = null,
                evoResUuid = null
            )
        }

        fun newPostPaid(): Order{
            return Order(
                id = 0,
                customPrice = null,
                billType = BillingType.POSTPAY,
                status = OrderStatus.PRE_CREATED,
                dateCreated = null,
                internalId = null,
                printLabel = null,
                printKvitok = null,
                evoResUuid = null
            )
        }
    }

    object BillingType {
        const val PREPAY = "PREP"
        const val POSTPAY = "POST"
    }

    object OrderStatus {
        const val PRE_CREATED = "PRE_CREATED"
        const val CREATED = "CRTD"
        const val PENDING = "PEND"
        const val IN_PROGRESS = "INWK"
        const val READY = "REDY"
        const val CLOSED = "CLSD"

        val MAP_TO_RUSSIAN = mapOf(
            PRE_CREATED to "НОВЫЙ",
            CREATED to "ОЖИДАНИЕ",
            READY to "ВЫДАЧА",
            CLOSED to "ЗАКРЫТ",
            IN_PROGRESS to "В РАБОТЕ"
        )
    }

    object OrderSuggestedAction{
        const val PAY = "PAY"
        const val CLOSE = "CLOSE"
        const val CREATE = "CREATE"
        const val NOTHING = "NOTHING"
    }

}