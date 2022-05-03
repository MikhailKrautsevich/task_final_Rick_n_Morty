package com.mk.rickmortyappbykrautsevich.retrofit.models

data class AllLocationsContainer(
    var info: ContainerInfo,
    var results: List<LocationRetrofitModel>
)
