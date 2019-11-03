package ru.acurresearch.dombyta_new.data.network

import retrofit2.Call
import retrofit2.http.*
import ru.acurresearch.dombyta.*

interface Api {
    @GET("/checkneedpoll/{ID}/")
    fun checkPollNeeded(
        @Path("ID") customerId: String
    ) : Call<NeedPollResult>

    @GET("/compl_tels/{NUM}/")
    fun getComplTels(
        @Path("NUM") numberToShow: Int
    ) : Call<List<String>>


    @PUT("/setphone/{ID}/")
    fun setPhone(
        @Body phoneNumber: PhoneNumber,
        @Path("ID") uuid: String
    ): Call<String>


    @POST("/api/orders/")
    fun sendOrder(
        @Body orderToSend: Order,
        @Header("Authorization-Acur") token: String
    ): Call<Order>


    @GET("/api/servitems/")
    fun fetchAllowedProds(
        @Header("Authorization-Acur") token: String
    ): Call<List<ServiceItemCustom>>

    @GET("/api/masters/")
    fun fetchMasters(@Header("Authorization-Acur") token: String) : Call<List<Master>>


    @GET("/api/tasks/")
    fun fetchTasks(
        @Header("Authorization-Acur") token: String
    ): Call<List<Task>>

    @GET("/api/orders/search/{SEARCHSTR}/")
    fun searchOrder(
        @Path("SEARCHSTR") searchStr: String,
        @Header("Authorization-Acur") token: String
    ): Call<List<Order>>

    @PUT("/api/tasks/{ID}/")
    fun syncServerTask(
        @Body task: Task,
        @Path("ID") task_id: Int,
        @Header("Authorization-Acur") token: String
    ): Call<Task>

    @PUT("/api/orders/update/{ID}/")
    fun syncOrderStatus(
        @Body order: Order,
        @Path("ID") order_id: Int,
        @Header("Authorization-Acur") token: String
    ): Call<Order>

    @POST("/api/cashbox/register-token/")
    fun getToken(
        @Body token: SiteToken
    ):  Call<CashBoxServerData>


}

