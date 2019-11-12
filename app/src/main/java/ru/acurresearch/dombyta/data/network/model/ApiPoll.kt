package ru.acurresearch.dombyta.data.network.model

data class ApiPoll(
    val poll_id: Int,
    val poll_name: String,
    val result: Boolean,
    val question: List<ApiQuestion>
)