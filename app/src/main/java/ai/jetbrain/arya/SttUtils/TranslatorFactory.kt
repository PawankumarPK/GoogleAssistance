package ai.jetbrain.arya.SttUtils

import android.app.Activity

object TranslatorFactory {

    enum class TRANSLATORS {
        SPEECH_TO_TEXT
    }

    interface IConverter {
        fun initialize(appContext: Activity): IConverter
        fun getErrorText(errorCode: Int): String
    }

    fun with(TRANSLATORS: TRANSLATORS, conversionCallback: ConversionCallback): IConverter {
        return when (TRANSLATORS) {
            TranslatorFactory.TRANSLATORS.SPEECH_TO_TEXT ->
                SpeechRecognization(conversionCallback)
        }
    }
}