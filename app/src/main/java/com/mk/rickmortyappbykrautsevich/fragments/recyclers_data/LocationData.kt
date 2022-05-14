package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel

data class LocationData(
    var id: Int = 0,
    var name: String?,
    var type: String?,
    var dimension: String?,
    var residents: List<String>?,
    var url: String?,
    var created: String?
) {
    constructor() : this(
        id = -1,
        name = null,
        type = null,
        dimension = null,
        residents = null,
        url = null,
        created = null
    )

    constructor(loc: LocationRetrofitModel) : this(
        id = loc.id,
        name = loc.name,
        type = loc.type,
        dimension = loc.dimension,
        residents = loc.residents,
        url = loc.url,
        created = loc.created
    )
}
