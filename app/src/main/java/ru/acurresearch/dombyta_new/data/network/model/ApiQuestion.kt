package ru.acurresearch.dombyta_new.data.network.model

import ru.acurresearch.dombyta.ApiAnswer

data class ApiQuestion(
    val id: Int,
    val name: String, val answer:List<ApiAnswer>
)