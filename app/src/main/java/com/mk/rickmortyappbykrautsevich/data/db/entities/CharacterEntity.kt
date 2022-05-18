package com.mk.rickmortyappbykrautsevich.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.CharacterRetrofitModel

@Entity(tableName = "characters", indices = [Index(value = arrayOf("name", "id"), unique = true)])
data class CharacterEntity constructor(
    @PrimaryKey
    var id: Int,
    var name: String?,
    var status: String?,
    var species: String?,
    var type: String?,
    var gender: String?,
    var origin_name: String?,
    var origin_url: String?,
    var location_name: String?,
    var location_url: String?,
    var image: String?,
    var episode: List<String>?,
    var url: String?,
    var created: String?
) {
    constructor(char: CharacterRetrofitModel) : this(
        id = char.id,
        name = char.name,
        status = char.status,
        species = char.species,
        type = char.type,
        gender = char.gender,
        origin_name = char.origin?.name,
        origin_url = char.origin?.url,
        location_name = char.location?.name,
        location_url = char.location?.url,
        image = char.image,
        episode = char.episode,
        url = char.url,
        created = char.created
    )
}
