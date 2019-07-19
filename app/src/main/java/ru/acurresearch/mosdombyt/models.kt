package ru.acurresearch.mosdombyt

import android.app.Activity
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.App.fromJson
import ru.evotor.framework.core.IntegrationException
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.inventory.ProductItem
import ru.evotor.framework.navigation.NavigationApi
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import java.io.IOException
import java.math.BigDecimal

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
    fun toEvotorPositionAdd(): PositionAdd{
        return   PositionAdd(Position.Builder.newInstance(
            uuid,
            productUUID,
            name,
            "шт",
            0,
            BigDecimal(quantity),
            BigDecimal(price)
        ).build())
    }

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




data class Client(@SerializedName("name") val name: String?,
                  @SerializedName("phone") val phone: String?)






data class ServiceItemCustom(@SerializedName("product_uuid") val productUUID: String?,
                             @SerializedName("product_name") val name: String,
                             @SerializedName("default_price") val defPrice: Double?,
                             @SerializedName("default_expires_in") val defExpiresIn: Int?){
    companion object{
        fun fromEvoProductItem(evoPos : ProductItem , defPrice: Double? = null,defExpiresIn: Int?  ): ServiceItemCustom{
            return ServiceItemCustom(evoPos.uuid, evoPos.name, defPrice,defExpiresIn )
        }

    }
}



data class OrderPostition(@SerializedName("uuid")          val uuid: String,
                          @SerializedName("serv_item")     val serviceItem: ServiceItemCustom,
                          @SerializedName("quantity")      val quantity: Double,
                          @SerializedName("price")         val price: Double,
                          @SerializedName("expires_in")    val expDate: Date?){
    fun toEvotorPositionAdd(): PositionAdd{
        return   PositionAdd(Position.Builder.newInstance(
            uuid,
            null,
            serviceItem.name,
            "шт",
            0,
            BigDecimal(quantity),
            BigDecimal(price)
        ).build())
    }
}



data class Order(@SerializedName("id")             val uuid: String,
                 @SerializedName("positions_list") var positionsList: ArrayList<OrderPostition>,
                 @SerializedName("custom_price")   var custom_price: Double?,
                 @SerializedName("client")         var client: Client,
                 @SerializedName("billing_type")   var billType: String,
                 @SerializedName("status")         var status: String
) {

    @SerializedName("is_paid")
    var isPaid:Boolean = false


    val price: Double
        get() = custom_price ?: positionsList.map { it.price*it.quantity }.sum()


    fun realize(activity: Activity){
        val changes = ArrayList<PositionAdd>()
        var listItem = ArrayList(positionsList.map { it.toEvotorPositionAdd() })
        listItem.map { changes.add(it) }

        OpenSellReceiptCommand(changes, null).process(
            activity
        ) { integrationManagerFuture ->
            try {
                val result = integrationManagerFuture.result
                val tmp = result.data
                if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                    //Чтобы открыть другие документы используйте методы NavigationApi.
                    activity.startActivity(NavigationApi.createIntentForSellReceiptEdit())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IntegrationException) {
                e.printStackTrace()
            }
        }
    }


    fun toJson(): String{
        return GsonBuilder().create().toJson(this)
    }

    fun suggestAction(): String{
        if (billType == Constants.BillingType.PREPAY){
            if (!isPaid)
                return Constants.OrderSuggestedAction.PAY

            if (status == Constants.OrderStatus.PRE_CREATED)
                return Constants.OrderSuggestedAction.CREATE

            if (status == Constants.OrderStatus.READY)
                return Constants.OrderSuggestedAction.CLOSE

        }

        if (billType == Constants.BillingType.POSTPAY){
            if (status == Constants.OrderStatus.PRE_CREATED)
                return Constants.OrderSuggestedAction.CREATE

            if ((status == Constants.OrderStatus.READY) && (!isPaid))
                return Constants.OrderSuggestedAction.PAY

            if ((status == Constants.OrderStatus.READY) && (isPaid))
                return Constants.OrderSuggestedAction.CLOSE
        }

        return Constants.OrderSuggestedAction.NOTHING
    }



    companion object {
        fun fromJson(inpJson : String ): Order{
            return Gson().fromJson<Order>(inpJson)
        }

        fun empty(): Order{
            return Order("",
                            ArrayList(listOf<OrderPostition>()),
                null,
                            Client("", ""),
                            Constants.BillingType.PREPAY ,
                            Constants.OrderStatus.PRE_CREATED)
        }
        fun newPrePaid(): Order{
            return Order(UUID.randomUUID().toString(),
                    ArrayList(listOf<OrderPostition>()),
                    null,
                    Client("", ""),
                    Constants.BillingType.PREPAY ,
                    Constants.OrderStatus.PRE_CREATED)
        }

        fun newPostPaid(): Order{
            return Order(UUID.randomUUID().toString(),
                ArrayList(listOf<OrderPostition>()),
                null,
                Client("", ""),
                Constants.BillingType.POSTPAY ,
                Constants.OrderStatus.PRE_CREATED)
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

