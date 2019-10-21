package ai.jetbrain.arya.fragment


import ai.jetbrain.arya.R
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_eye.*
import java.util.*


class EyeFragment : BaseFragment() {

    private var eyeMaxX = 0F
    private var eyeMaxY = 0F
    private val defaultDuration = 300
    private val maxDuration = 3000
    private var eyeX = 0F
    private var eyeY = 0F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eye, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speechEnabled = false

        observeLayoutLoad()
        // randomMoves()

        mRelativeLayout.setOnClickListener {
            listen()
        }
        // blinkAnimationEyeLeft()
        blinkEye()
    }

    fun observeLayoutLoad() {
        eyeContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    eyeContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    eyeContainer.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }

                val size = Point()
                baseActivity.windowManager.defaultDisplay.getSize(size)
                eyeMaxX = eyeContainer.measuredWidth.toFloat()
                eyeMaxY = eyeContainer.measuredHeight.toFloat() * 0.75F
            }
        })
    }


    private fun blinkAnimationEyeLeft() {
        val firstSet =
            AnimatorInflater.loadAnimator(baseActivity, R.animator.eye_animate) as AnimatorSet

        val secondSet = firstSet.clone()
        firstSet.setTarget(mEyeBallLeft)
        secondSet.setTarget(mEyeBallLeft)
        firstSet.duration = 300
        secondSet.duration = 300

        val anim = AnimatorSet()
        anim.playTogether(firstSet, secondSet)
        anim.start()

    }

    private fun blinkAnimationEyeRight() {
        val firstSet =
            AnimatorInflater.loadAnimator(baseActivity, R.animator.eye_animate) as AnimatorSet

        val secondSet = firstSet.clone()
        firstSet.setTarget(mEyeBallRight)
        secondSet.setTarget(mEyeBallRight)
        firstSet.duration = 300
        secondSet.duration = 300

        val anim = AnimatorSet()
        anim.playTogether(firstSet, secondSet)
        anim.start()

    }

    private fun blinkEye() {
        val handler = Handler()
        val delay = 10000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                blinkAnimationEyeLeft()
                blinkAnimationEyeRight()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
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
        val x = ObjectAnimator.ofFloat(target, "translationX", eyeX, targetX)
        val y = ObjectAnimator.ofFloat(target, "translationY", eyeY, targetY)
        animSetXY.playTogether(x, y)
        animSetXY.interpolator = LinearInterpolator() as TimeInterpolator?
        animSetXY.duration = defaultDuration.toLong()
        animSetXY.start()
    }

    override fun onFaceUpdate(facex: Float, facey: Float) {
        super.onFaceUpdate(facex, facey)

        Log.d("faceUpadet","----->>>>")

        var x = facex
        var y = facey
        if (x < 0 || x > 1) x = 0.5F
        if (y < 0 || y > 1) y = 0.5F
        x *= eyeMaxX
        y *= eyeMaxY

        y += (0.25F * eyeMaxY / 0.75F)

        if (eyeX != x || eyeY != y) {
            moveEyeBall(mEyeBallLeft, x, y)
//            moveEyeBall(mEyeBallLeftLine, x, y)

            moveEyeBall(mEyeBallRight, x, y)
            //          moveEyeBall(mEyeBallRightLine, x, y)
            eyeX = x
            eyeY = y

        }
    }

}
