package ru.acurresearch.dombyta.data.network.model

data class CashBoxServerData(
    val token: String,
    val cashbox_uuid: String
) {
    val authHeader: String
        get() = "Bearer $token"
}