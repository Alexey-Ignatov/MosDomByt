package ru.acurresearch.dombyta_new.data.network

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import ru.acurresearch.dombyta_new.data.common.model.*
import ru.acurresearch.dombyta_new.data.network.model.*

interface Api {
    @GET("/checkneedpoll/{ID}/")
    fun checkPollNeeded(
        @Path("ID") customerId: String
    ) : Single<NeedPollResult>

    @GET("/compl_tels/{NUM}/")
    fun getComplTels(
        @Path("NUM") numberToShow: Int
    ) : Observable<String>

    @PUT("/setphone/{ID}/")
    fun setPhone(
        @Body phoneNumber: PhoneNumber,
        @Path("ID") uuid: String
    ): Single<String>


    @POST("/api/orders/")
    fun sendOrder(
        @Body orderToSend: Order,
        @Header("Authorization-Acur") token: String
    ): Single<Order>


    @GET("/api/servitems/")
    fun fetchAllowedProds(
        @Header("Authorization-Acur") token: String
    ): Observable<ServiceItemCustom>

    @GET("/api/masters/")
    fun fetchMasters(
        @Header("Authorization-Acur") token: String
    ): Observable<Master>

    @GET("/api/tasks/")
    fun fetchTasks(
        @Header("Authorization-Acur") token: String
    ): Observable<Task>

    @GET("/api/orders/search/{SEARCHSTR}/")
    fun searchOrder(
        @Path("SEARCHSTR") searchStr: String,
        @Header("Authorization-Acur") token: String
    ): Observable<Order>

    @PUT("/api/tasks/{ID}/")
    fun syncServerTask(
        @Body task: Task,
        @Path("ID") task_id: Int,
        @Header("Authorization-Acur") token: String
    ): Single<Task>

    @PUT("/api/orders/update/{ID}/")
    fun syncOrderStatus(
        @Body order: Order,
        @Path("ID") order_id: Int,
        @Header("Authorization-Acur") token: String
    ): Single<Order>

    @POST("/api/cashbox/register-token/")
    fun getToken(
        @Body token: SiteToken
    ):  Single<CashBoxServerData>


}

