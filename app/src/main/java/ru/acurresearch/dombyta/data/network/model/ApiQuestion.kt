package ru.acurresearch.dombyta.data.network.model

data class ApiQuestion(
    val id: Int,
    val name: String, val answer:List<ApiAnswer>
)