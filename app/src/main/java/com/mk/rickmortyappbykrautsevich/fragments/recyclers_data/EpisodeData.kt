package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.db.entities.EpisodeEntity
import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel

data class EpisodeData(
    var id: Int = 0,
    var name: String?,
    var episode: String?,
    var airDate: String?,
    var url: String?,
    var created: String?,
    var characters: List<String>?
) {

    constructor() : this(
        id = -1,
        name = "",
        episode = "",
        airDate = "",
        url = "",
        created = "",
        characters = ArrayList<String>()
    )

    constructor(ep: EpisodeRetrofitModel) : this(
        id = ep.id,
        name = ep.name,
        episode = ep.episode,
        airDate = ep.airDate,
        url = ep.url,
        created = ep.created,
        characters = ep.characters
    )

    constructor(ep: EpisodeEntity) : this(
        id = ep.id,
        name = ep.name,
        airDate = ep.air_date,
        episode = ep.episode,
        characters = ep.characters,
        url = ep.url,
        created = ep.created
    )
}

