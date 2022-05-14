package com.mk.rickmortyappbykrautsevich.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mk.rickmortyappbykrautsevich.retrofit.models.LocationRetrofitModel

@Entity(indices = [Index(value = arrayOf("name", "id"), unique = true)])
data class LocationEntity constructor(
    @PrimaryKey
    var id: Int,
    var name: String?,
    var type: String?,
    var dimension: String?,
    var residents: List<String>?,
    var url: String?,
    var created: String?
) {
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