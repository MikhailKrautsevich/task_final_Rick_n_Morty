package com.mk.rickmortyappbykrautsevich.retrofit.models

import com.google.gson.annotations.SerializedName

data class EpisodeRetrofitModel(
    var id: Int = 0,
    var name: String?,
    var episode: String?,
    @SerializedName("air_date")
    var airDate: String?,
    var url: String?,
    var created: String?,
    var characters: List<String>?
)
