package ai.jetbrain.arya.fragments


import ai.jetbrain.arya.R
import ai.jetbrain.arya.api.RetrofitClient
import ai.jetbrain.arya.api.model.EyePosition
import ai.jetbrain.arya.api.model.Face
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_eye.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class EyeFragment : BaseFragment() {

    private val eyeMaxX = 200F
    private val eyeMaxY = 400F
    private val defaultDuration = 300
    private val maxDuration = 3000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_eye, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //moveEyeBall( mEyeBallLeft,200F,100F)
        //moveEyeBall( mEyeBallRight,200F,100F)
        moveEye()
    }

    private fun moveEye() {
        val api = RetrofitClient.apiService
        val call = api.getFaceRecog()

        call.enqueue(object : Callback<Face> {
            override fun onFailure(call: Call<Face>?, t: Throwable?) {
                Toast.makeText(baseActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Face>?, response: Response<Face>?) {
                if (response!!.isSuccessful) {
                    val x = response.body().faceRecog!!.x!! * eyeMaxX
                    val y = response.body().faceRecog!!.y!! * eyeMaxY

                  //  Log.e("eyePosition","~~~~~~")
                    Toast.makeText(baseActivity, "$x,$y", Toast.LENGTH_SHORT).show()

                    moveEyeBall(mEyeBallLeft, x, y)
                    moveEyeBall(mEyeBallRight, x, y)
                }
                Handler().postDelayed({
                    moveEye()
                }, 2000)
            }

        })
    }

    private fun randomMoves() {
        val rand = Random()
        val x = rand.nextFloat() * eyeMaxX
        val y = rand.nextFloat() * eyeMaxY
        val max = maxDuration - defaultDuration

        moveEyeBall(mEyeBallLeft, x, y)
        moveEyeBall(mEyeBallRight, x, y)

        Handler().postDelayed({
            randomMoves()
        }, (rand.nextInt(max) + defaultDuration).toLong())

    }

    private fun moveEyeBall(target: ImageView, targetX: Float, targetY: Float) {
        val animSetXY = AnimatorSet()
        val x = ObjectAnimator.ofFloat(target, "translationX", mEyeBallLeft.x, targetX)
        val y = ObjectAnimator.ofFloat(target, "translationY", mEyeBallLeft.y, targetY)
        animSetXY.playTogether(x, y)
        animSetXY.interpolator = LinearInterpolator()
        animSetXY.duration = defaultDuration.toLong()
        animSetXY.start()
    }

}
