package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.retrofit.models.EpisodeRetrofitModel

data class EpisodeRecData(
    var id: Int = 0,
    var name: String?,
    var episode: String?,
    var airDate: String?,
    var url: String?,
    var created: String?,
    var characters: List<String>?
) {
    constructor(ep: EpisodeRetrofitModel) : this(
        id = ep.id,
        name = ep.name,
        episode = ep.episode,
        airDate = ep.airDate,
        url = ep.url,
        created = ep.created,
        characters = ep.characters
    )
}
