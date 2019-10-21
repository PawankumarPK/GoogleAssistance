package ai.jetbrain.arya.SttUtils

interface ConversionCallback {

    fun onSuccess(result: String)

    fun onCompletion()

    fun onErrorOccurred(errorMessage: String)

}