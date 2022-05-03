package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel

data class CharacterRecData(
    var id: Int = 0,
    var name: String?,
    var species: String?,
    var status: String?,
    var type: String?,
    var gender: String?,
    var origin: LocationReference?,
    var location: LocationReference?,
    var image: String?,
    var episode: List<String>?,
    var url: String?,
    var created: String?
) {
    constructor(character: CharacterRetrofitModel) : this(
        id = character.id,
        name = character.name,
        species = character.species,
        status = character.status,
        type = character.type,
        gender = character.gender,
        origin = character.origin?.let { LocationReference(it) },
        location = character.location?.let { LocationReference(it) },
        image = character.image,
        episode = character.episode,
        url = character.url,
        created = character.created
    )
}
