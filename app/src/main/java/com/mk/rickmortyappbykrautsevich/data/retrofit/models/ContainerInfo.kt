package com.mk.rickmortyappbykrautsevich.data.retrofit.models

data class ContainerInfo(
    var count: Int = 0,
    var pages: Int = 0,
    var next: String?,
    var prev: String?
)
