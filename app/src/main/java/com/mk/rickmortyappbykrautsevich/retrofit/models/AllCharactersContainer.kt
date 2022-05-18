package com.mk.rickmortyappbykrautsevich.retrofit.models

data class AllCharactersContainer(
    var info: ContainerInfo,
    var results: List<CharacterRetrofitModel>
)
