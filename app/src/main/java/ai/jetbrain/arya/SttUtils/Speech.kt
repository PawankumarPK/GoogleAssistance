package ai.jetbrain.arya.SttUtils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

object Speech: UtteranceProgressListener(), TextToSpeech.OnInitListener {

    private val REQUEST_AUDIO_PERMISSION_REQ_ID = 1

    private var onCompleteListerner:()->Unit = {}

     lateinit var tts: TextToSpeech

    private var TTS_INIT = false

    override fun onStart(p0: String?) {
    }

    override fun onDone(p0: String?) {
        onCompleteListerner()
    }

    override fun onError(p0: String?) {
    }

    override fun onInit(p0: Int) {
        TTS_INIT = true
        onCompleteListerner()
    }

    fun setOnCompleteListener(callback:()->Unit) {
        onCompleteListerner = callback
    }

    fun initTTS(activity: Activity) {
        tts = TextToSpeech(activity, this)
    }

    fun initSTT(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var check = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            if (check == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO))
                    Toast.makeText(activity, "App required access to audio", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_AUDIO_PERMISSION_REQ_ID
                );
            }
        }
    }

    fun speakOutText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && TTS_INIT) {
            tts.language = Locale("en", "IN")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ARYA_Text")
            tts.setOnUtteranceProgressListener(this)
        }
    }

    fun onPermissionResult(requestCode:Int, result:Int) {
        if (requestCode == REQUEST_AUDIO_PERMISSION_REQ_ID) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(, "Application will not have audio on record", Toast.LENGTH_SHORT).show()
            }
        }
    }
}