package ru.acurresearch.dombyta_new.data.network.model

data class CashBoxServerData(
    val token: String,
    val cashbox_uuid: String
) {
    val authHeader: String
        get() = "Bearer $token"
}