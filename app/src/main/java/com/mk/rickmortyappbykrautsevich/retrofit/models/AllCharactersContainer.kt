package com.mk.rickmortyappbykrautsevich.retrofit.models

data class AllCharactersContainer(
    var info: Info, var results: List<CharacterRetrofitModel>
)
