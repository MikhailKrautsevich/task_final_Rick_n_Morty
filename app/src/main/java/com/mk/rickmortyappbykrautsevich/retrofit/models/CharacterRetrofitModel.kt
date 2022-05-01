package com.mk.rickmortyappbykrautsevich.retrofit.models

data class CharacterRetrofitModel(
    var id: Int = 0,
    var name: String?,
    var species: String?,
    var status: String?,
    var gender: String?,
    var image: String?,
)
