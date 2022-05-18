package com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces

import androidx.lifecycle.LiveData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData

interface CharacterDetailViewModelInterface {

    fun getCharLoadingLiveData(): LiveData<Boolean>

    fun getCharacterLiveData(): LiveData<CharacterData>

    fun getListLoadingLiveData(): LiveData<Boolean>

    fun getListLiveData(): LiveData<List<EpisodeData>>

    fun loadData(id: Int)

    fun loadList(list: List<String>?)
}