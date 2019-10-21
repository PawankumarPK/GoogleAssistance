package ai.jetbrain.arya.fragments


import ai.jetbrain.arya.R
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_welcome_screen.*
import java.util.*


class WelcomeScreen : BaseFragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(baseActivity, this)

    }

    override fun onInit(p0: Int) {
        textViewChanged()
    }


    private fun textViewChanged() {

        val textView = mTextview
        val array = intArrayOf(R.string.hello_everyone, R.string.imarya, R.string.welcometococubes)
        textView.post(object : Runnable {
            var i = 0
            override fun run() {
                textView.setText(array[i])
                i++
                if (i == 3)
                    i = 0
                speakOut()
                textView.postDelayed(this, 3000)

                if (textView.text == "Welcome To CoCubes") {
                    sayHelloImArya()
                    textView.removeCallbacks(this)

                }
            }
        })

    }

    private fun speakOut() {
        val text = mTextview.text.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            tts.language = Locale("en", "IN")

        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            tts.language = Locale("en", "IN")

        }
    }

    private fun sayHelloImArya() {
        val handler = Handler()
        handler.postDelayed({
            mTextview.text = resources.getString(R.string.helloimarya)
        }, 10000)
    }

    override fun onStart() {
        mTextview.text = ""
        super.onStart()
    }

    override fun onResume() {
        //textViewChanged()
        mTextview.text = ""
        super.onResume()
    }

    override fun onPause() {
        mTextview.text = ""
        tts.stop()
        tts.shutdown()
        super.onPause()
    }

    override fun onStop() {
        mTextview.text = ""
        tts.stop()
        tts.shutdown()
        super.onStop()
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

}