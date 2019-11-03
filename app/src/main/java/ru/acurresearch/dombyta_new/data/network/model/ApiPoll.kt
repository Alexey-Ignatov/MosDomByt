package ru.acurresearch.dombyta_new.data.network.model

import ru.acurresearch.dombyta.ApiQuestion

data class ApiPoll(
    val poll_id: Int,
    val poll_name: String,
    val result: Boolean,
    val question: List<ApiQuestion>
)