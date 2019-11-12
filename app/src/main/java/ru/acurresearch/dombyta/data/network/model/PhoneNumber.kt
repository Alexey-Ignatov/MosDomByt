package ru.acurresearch.dombyta.data.network.model

import com.google.gson.annotations.SerializedName

data class PhoneNumber(
    @SerializedName("tel_str") val telStr: String
)
