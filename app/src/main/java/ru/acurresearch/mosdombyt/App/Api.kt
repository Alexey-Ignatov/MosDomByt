package ru.acurresearch.mosdombyt.App

import retrofit2.Call
import retrofit2.http.*
import ru.acurresearch.mosdombyt.Check
import ru.acurresearch.mosdombyt.NeedPollResult
import ru.acurresearch.mosdombyt.PhoneNumber


interface Api {

    @GET("/checkneedpoll/{ID}/")
    fun checkPollNeeded(@Path("ID") customerId: String ) : Call<NeedPollResult>

    @GET("/compl_tels/{NUM}/")
    fun getComplTels(@Path("NUM") numberToShow: Int ) : Call<List<String>>

    @POST("/servcheck/")
    fun sendCheckServ(@Body checkToSend: Check): Call<String>


    @PUT("/setphone/{ID}/")
    fun setPhone(@Body phoneNumber: PhoneNumber, @Path("ID") uuid: String ): Call<String>




}

