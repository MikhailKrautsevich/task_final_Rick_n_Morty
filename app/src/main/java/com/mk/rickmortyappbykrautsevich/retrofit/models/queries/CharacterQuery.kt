package com.mk.rickmortyappbykrautsevich.retrofit.models.queries

import com.mk.rickmortyappbykrautsevich.enums.Gender
import com.mk.rickmortyappbykrautsevich.enums.Status

class CharacterQuery(
    var name: String?,
    var status: Status?,
    var species: String?,
    var type: String?,
    var gender: Gender?
)