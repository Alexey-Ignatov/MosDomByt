package ru.acurresearch.mosdombyt

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi

import java.util.*
import java.util.ArrayList
import java.util.Date

//question:[answer:, [id, name], id , name,	], poll_name, result, poll_id

data class ApiAnswer(val id: Int, val name: String){
    fun toJson(): String{
        val gson = GsonBuilder().create()
        return gson.toJson(this)
    }



}

data class ApiQuestion(val id: Int, val name: String, val answer:List<ApiAnswer>){
    fun toJson(): String{
        val gson = GsonBuilder().create()
        return gson.toJson(this)
    }



}


data class ApiPoll(val poll_id: Int, val poll_name: String, val result: Boolean, val question: List<ApiQuestion> ){
    fun toJson(): String{
        val gson = GsonBuilder().create()
        return gson.toJson(this)
    }

   // fun toPollModel(): Poll{
    //
    //
     //   var apiQuestions =
   //         question.map { it -> Question(it.id,it.name, it.answer.map {l -> AnswerOption(l.id,l.name ) }) }

   //     return Poll(poll_id, "Давид",poll_name,apiQuestions)
    //}



}



data class ApiGetPollData (val getRandomPoll: Boolean, val checkUUID: String){
    fun toJson(): String{
        val gson = GsonBuilder().create()
        return gson.toJson(this)
    }
}


data class NeedPollResult(val result: Boolean)





data class Check(       @SerializedName("uuid")         val uuid: String,
                        @SerializedName("check_date")   val date: Date,
                        @SerializedName("check_number") val number: String?,
                        @SerializedName("check_pos")    val position: List<CheckPostition>){


    companion object{
        fun fromEvoReceipt(receipt: Receipt): Check{
            val header = receipt.header

            return Check(
                    receipt.header.uuid,
                    receipt.header.date ?: Date()  ,
                    receipt.header.number,
                    receipt.getPositions().map {CheckPostition.fromEvoPosition(it)  })
        }
    }
}
                        //val type: String,
// api fields fields = ('device_id', 'uuid', 'check_date', 'check_number',    'check_pos')




data class CheckPostition(@SerializedName("pos_uuid")     val uuid: String,
                          @SerializedName("product_uuid") val productUUID: String?,
                          @SerializedName("product_name") val name: String,
                          @SerializedName("quantity")     val quantity: Double,
                          @SerializedName("price")        val price: Double){

    companion object {
        fun fromEvoPosition(evoPos : Position ): CheckPostition{
            return CheckPostition(evoPos.getUuid(),
                                  evoPos.getProductUuid(),
                                  evoPos.getName(),
                                  evoPos.getQuantity().toDouble(),
                                  evoPos.getPrice().toDouble())
        }
    }

}
                               //val productCode: String,
                               //)
// api fields fields = ('pos_uuid', 'product_uuid', 'product_name', 'quantity', 'price')

data class PhoneNumber(@SerializedName("tel_str") val telStr: String)



object CheckExample{

    val check = Check( "uuid_0097", Date(),"product_name" ,listOf(CheckPostition("pos_uuid_1",
                                                                                                        "product_uuid_2",
                                                                                                        "Milk2.0",
                                                                                                        12.0,
                                                                                                        120.0 )))
}

