package com.mk.rickmortyappbykrautsevich.data.retrofit.models

data class CharacterRetrofitModel(
    var id: Int = 0,
    var name: String?,
    var species: String?,
    var status: String?,
    var type: String?,
    var gender: String?,
    var origin: RetroLocationReference?,
    var location: RetroLocationReference?,
    var image: String?,
    var episode: List<String>?,
    var url: String?,
    var created: String?
)
