package ai.jetbrain.arya.api

import ai.jetbrain.arya.api.model.Face
import ai.jetbrain.arya.api.model.StandardModel.DeviceStatsModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/face-xy")
    fun faceXY(): Call<Face>


    @GET("/device-stats")
    fun deviceStats(): Call<DeviceStatsModel>


}