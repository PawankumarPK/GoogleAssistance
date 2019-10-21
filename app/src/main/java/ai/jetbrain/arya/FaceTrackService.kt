package ai.jetbrain.arya

import ai.jetbrain.arya.api.RetrofitClient
import ai.jetbrain.arya.api.model.Face
import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response

class FaceTrackService : IntentService("FaceTrackService") {

    var faceX = -1F
    var faceY = -1F

    fun getFaceXY() {

        val api = RetrofitClient.apiService
        val call = api.faceXY()

        call.enqueue(object : retrofit2.Callback<Face> {
            override fun onFailure(call: Call<Face>?, t: Throwable?) {
                Toast.makeText(applicationContext, "Service Fail : ", Toast.LENGTH_SHORT).show()
                //DeviceStats.ServerConnected = false
                Log.d("--Faillll","failll")

                val intent = Intent()
                intent.action = "ai.jetbrain.FaceXY"
                intent.putExtra("error", true)
                sendBroadcast(intent)

                //Thread.sleep(5000)
                Handler().postDelayed({
                    getFaceXY()
                }, 1000)
            }

            override fun onResponse(call: Call<Face>, response: Response<Face>) {
                if (response.isSuccessful) {
                    //DeviceStats.ServerConnected = true
                    //Toast.makeText(applicationContext, "Service success : ", Toast.LENGTH_SHORT).show()
                    val face =  response.body().face!!
                    val x = face.x
                    val y = face.y

                    if(faceX!=x || faceY!=y) {
                        val intent = Intent()
                        intent.action = "ai.jetbrain.FaceXY"
                        intent.putExtra("x", x)
                        intent.putExtra("y", y)
                        intent.putExtra("error", false)
                        sendBroadcast(intent)
                    }
                    Handler().postDelayed({
                        getFaceXY()
                    }, 1000)
                }
            }
        })
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            getFaceXY()
        } catch (e: InterruptedException) {
            Toast.makeText(applicationContext, "Thread Exception!!", Toast.LENGTH_SHORT).show()
            //Thread.currentThread().interrupt()
            Handler().postDelayed({
                getFaceXY()
            }, 1000)
        }
    }



}

