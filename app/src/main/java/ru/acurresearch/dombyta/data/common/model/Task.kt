package ru.acurresearch.dombyta.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import com.google.gson.JsonNull
import com.google.gson.annotations.SerializedName
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity data class Task(
    @Id(assignable = true) @SerializedName("id") var id: Long,
    @SerializedName("name")  val name: String?,
    @SerializedName("exp_date")  val expDate: Date?,
    @SerializedName("order_internal_id")  val orderInternalId: Int?,
    @SerializedName("status") var status: String
) {
    constructor(): this(0, "", Date(), 0, "")

    @SerializedName("master") lateinit var master: ToOne<Master>

    companion object {
        val serializer = jsonSerializer<Task> { (src, type, context) -> jsonObject(
            "id" to src.id,
            "name" to src.name,
            "exp_date" to context.serialize(src.expDate),
            "order_internal_id" to src.orderInternalId,
            "status" to src.status,
            "master" to context.serialize(src.master.target)
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Task(
            id = src.getOrNull("id")?.asLong ?: 0,
            name = src.getOrNull("name")?.asString,
            expDate = context.deserialize(src.getOrNull("exp_date") ?: JsonNull.INSTANCE),
            orderInternalId = src.getOrNull("order_internal_id")?.asInt,
            status = src["status"].asString
        ).apply {
            val master = context.deserialize<Master>(src["master"])
            this.master.target = master
        } }

        fun fromOrder(order: Order) =
            order.positionsList.map { Task(
                id = 0,
                name = it.serviceItem.target.name,
                expDate = it.expDate,
                orderInternalId = order.internalId,
                status = TaskStatus.NEW
            ) }
    }

    object TaskStatus {
        const val NEW = "CRTD"
        const val IN_WORK = "INWK"
        const val COMPLETE = "REDY"
    }
}