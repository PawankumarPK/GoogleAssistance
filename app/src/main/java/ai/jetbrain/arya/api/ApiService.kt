package ai.jetbrain.arya.api

import ai.jetbrain.arya.api.model.Face
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("/xy")
    fun getFaceRecog(): Call<Face>


}