package com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data

import com.mk.rickmortyappbykrautsevich.data.retrofit.models.RetroLocationReference

data class LocationReference(var name: String?, var url: String?) {
    constructor(ref: RetroLocationReference) : this(name = ref.name, url = ref.url)
}
