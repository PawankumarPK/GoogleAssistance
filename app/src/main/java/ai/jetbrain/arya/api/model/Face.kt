package ai.jetbrain.arya.api.model

import com.google.gson.annotations.SerializedName

class Face {

    @SerializedName("face")
    var faceRecog : EyePosition? = null
}