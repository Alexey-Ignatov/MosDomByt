package ru.acurresearch.dombyta_new.data.common.model

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity data class Client(
    @Id val id: String = UUID.randomUUID().toString(),
    @SerializedName("name") val name: String?,
    @SerializedName("phone") val phone: String?
) {
    lateinit var order: ToOne<Order>

    companion object {
        val serializer = jsonSerializer<Client> { (src, type, context) -> jsonObject(
            "name" to src.name,
            "phone" to src.phone
        ) }

        val deserializer = jsonDeserializer { (src, type, context) -> Client(
            name = src["name"].asString,
            phone = src["phone"].asString
        ) }
    }
}