package ru.acurresearch.dombyta_new.data.network.model

import com.google.gson.annotations.SerializedName

data class PhoneNumber(
    @SerializedName("tel_str") val telStr: String
)
