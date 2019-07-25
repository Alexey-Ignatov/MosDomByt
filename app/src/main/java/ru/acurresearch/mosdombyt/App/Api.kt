package ru.acurresearch.mosdombyt.App

import retrofit2.Call
import retrofit2.http.*
import ru.acurresearch.mosdombyt.*


interface Api {

    @GET("/checkneedpoll/{ID}/")
    fun checkPollNeeded(@Path("ID") customerId: String ) : Call<NeedPollResult>

    @GET("/compl_tels/{NUM}/")
    fun getComplTels(@Path("NUM") numberToShow: Int ) : Call<List<String>>


    @PUT("/setphone/{ID}/")
    fun setPhone(@Body phoneNumber: PhoneNumber, @Path("ID") uuid: String ): Call<String>







    @POST("/api/orders/")
    fun sendOrder(@Body orderToSend: Order): Call<Order>


    @GET("/api/servitems/")
    fun fetchAllowedProds() : Call<List<ServiceItemCustom>>

    @GET("/api/masters/")
    fun fetchMasters() : Call<List<Master>>


    @GET("/api/tasks/")
    fun fetchTasks() : Call<List<Task>>

    @GET("/api/orders/search/{SEARCHSTR}/")
    fun searchOrder(@Path("SEARCHSTR") searchStr: String) : Call<List<Order>>

    @PUT("/api/tasks/{ID}/")
    fun updTask(@Body task: Task, @Path("ID") task_id: Int) :  Call<Task>

    @PUT("/api/orders/update/{ID}/")
    fun updOrderStatus(@Body order: Order, @Path("ID") order_id: Int) :  Call<Order>


}

