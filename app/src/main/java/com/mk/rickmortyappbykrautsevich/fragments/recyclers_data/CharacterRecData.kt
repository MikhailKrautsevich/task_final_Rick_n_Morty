package com.mk.rickmortyappbykrautsevich.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.retrofit.models.CharacterRetrofitModel

data class CharacterRecData(
    var id: Int = 0,
    var name: String?,
    var species: String?,
    var status: String?,
    var gender: String?,
    var image: String?,
) {
    constructor(character: CharacterRetrofitModel) : this(
        id = character.id,
        name = character.name,
        species = character.species,
        status = character.status,
        gender = character.gender,
        image = character.image
    )
}
