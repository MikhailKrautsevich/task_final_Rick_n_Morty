package com.mk.rickmortyappbykrautsevich.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.EpisodeRetrofitModel

@Entity(tableName = "episodes",indices = [Index(value = arrayOf("name", "id"), unique = true)])
data class EpisodeEntity constructor(
    @PrimaryKey
    var id: Int,
    var name: String?,
    var air_date: String?,
    var episode: String?,
    var characters: List<String>?,
    var url: String?,
    var created: String?
) {
    constructor(ep: EpisodeRetrofitModel) : this(
    id = ep.id,
    name = ep.name,
    air_date = ep.airDate,
    episode = ep.episode,
    characters = ep.characters,
    url = ep.url,
    created = ep.created
    )
}
