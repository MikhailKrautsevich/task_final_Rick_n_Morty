package com.mk.rickmortyappbykrautsevich.data.retrofit.models

data class AllLocationsContainer(
    var info: ContainerInfo,
    var results: List<LocationRetrofitModel>
)
