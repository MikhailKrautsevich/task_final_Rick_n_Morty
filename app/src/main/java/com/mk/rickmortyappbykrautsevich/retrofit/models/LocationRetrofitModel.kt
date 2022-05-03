package com.mk.rickmortyappbykrautsevich.retrofit.models

data class LocationRetrofitModel(
    var id: Int = 0,
    var name: String?,
    var type: String?,
    var dimension: String?,
    var residents: List<String>?,
    var url: String?,
    var created: String?
)
