package com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces

import androidx.lifecycle.LiveData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.CharacterQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData

interface AllCharactersViewModelInterface: UsesPagination, WithSearchView {

    fun getLoadingLiveData(): LiveData<Boolean>

    fun getPaginationLiveData(): LiveData<Boolean>

    fun getCharactersList(): LiveData<List<CharacterData>>

    fun getData(query: CharacterQuery?)
}