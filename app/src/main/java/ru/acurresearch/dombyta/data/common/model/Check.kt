package ru.acurresearch.dombyta.data.common.model

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import ru.evotor.framework.receipt.Receipt
import java.util.*


@Entity data class Check(
    @Id(assignable = true) var id: Long,
    val uuid: String,
    val date: Date,
    val number: String?
) {
    constructor(): this(0, "", Date(), "")

    @Backlink(to = "check") lateinit var position: ToMany<CheckPosition>

    companion object {
        val serializer = jsonSerializer<Check> { (src, type, context) -> jsonObject(
            "uuid" to src.uuid,
            "check_date" to src.date,
            "check_number" to src.number,
            "check_pos" to jsonArray(src.position.mapTo(ArrayList<JsonElement>()) { context.serialize(it) })
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Check(
            id = 0,
            uuid = src["uuid"].asString,
            date = context.deserialize(src["check_date"]),
            number = src.getOrNull("check_number")?.asString
        ).apply {
            src["check_pos"].asJsonArray.map { context.deserialize<CheckPosition>(it) }
                .forEach {
                    it.check.target = this
                    this.position.add(it)
                }
        } }

        fun fromEvoReceipt(receipt: Receipt) =
            Check(
                id = 0,
                uuid = receipt.header.uuid,
                date = receipt.header.date ?: Date(),
                number = receipt.header.number
            ).apply {
                receipt.getPositions().map { CheckPosition.fromEvoPosition(it)  }
                    .forEach {
                        it.check.target = this
                        this.position.add(it)
                    }
            }
    }
}