package com.mk.rickmortyappbykrautsevich.data.retrofit.models

data class AllCharactersContainer(
    var info: ContainerInfo,
    var results: List<CharacterRetrofitModel>
)
