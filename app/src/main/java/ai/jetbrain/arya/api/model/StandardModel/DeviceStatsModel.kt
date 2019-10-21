package ai.jetbrain.arya.api.model.StandardModel

import com.google.gson.annotations.SerializedName

/**
 * Created by ajayvishnu on 26/06/19.
 */
class DeviceStatsModel {

    @SerializedName("core")
    var Core: Int? = 0

    @SerializedName("lidar")
    var Lidar: Int? = 0

    @SerializedName("arduino1")
    var Arduino1: Int? = 0

    @SerializedName("battery")
    var Battery: Float? = 0.0F
}