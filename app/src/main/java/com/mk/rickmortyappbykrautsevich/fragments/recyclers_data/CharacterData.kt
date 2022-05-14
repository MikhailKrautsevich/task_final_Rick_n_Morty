package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.db.entities.CharacterEntity
import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel

data class CharacterData(
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
    constructor() : this(
        id = -1,
        name = null,
        species = null,
        status = null,
        type = null,
        gender = null,
        origin = null,
        location = null,
        image = null,
        episode = null,
        url = null,
        created = null
    )

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

    constructor(char: CharacterEntity) : this(
        id = char.id,
        name = char.name,
        species = char.species,
        status = char.status,
        type = char.type,
        gender = char.gender,
        origin = LocationReference(char.origin_name, char.origin_url),
        location = LocationReference(char.location_name, char.location_url),
        image = char.image,
        episode = char.episode,
        url = char.url,
        created = char.created
    )
}
