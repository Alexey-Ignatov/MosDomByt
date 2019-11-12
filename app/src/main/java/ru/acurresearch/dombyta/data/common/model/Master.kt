package ru.acurresearch.dombyta.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import ga.nk2ishere.dev.utils.getOrNull
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity data class Master(
    @Id(assignable = true) var id: Long,
    val name: String,
    val specialization: String
) {
    constructor(): this(0, "", "")

    companion object {
        val serializer = jsonSerializer<Master> { (src, type, context) -> jsonObject(
            "id" to src.id,
            "name" to src.name,
            "specialization" to src.specialization
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Master(
            id = src.getOrNull("id")?.asLong ?: 0,
            name = src["name"].asString,
            specialization = src["specialization"].asString
        ) }
    }
}