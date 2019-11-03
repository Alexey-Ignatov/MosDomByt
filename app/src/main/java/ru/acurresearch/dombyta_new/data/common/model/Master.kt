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

@Entity data class Master(
    @Id val id: Int,
    val name: String,
    val specialization: String
) {
    lateinit var task: ToOne<Task>

    companion object {
        val serializer = jsonSerializer<Master> { (src, type, context) -> jsonObject(
            "id" to src.id,
            "name" to src.name,
            "specialization" to src.specialization
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Master(
            id = src.getOrNull("id")?.asInt ?: 0,
            name = src["name"].asString,
            specialization = src["specialization"].asString
        ) }
    }
}