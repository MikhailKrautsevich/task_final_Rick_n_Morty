package com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces

import androidx.lifecycle.LiveData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.CharacterData
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.LocationData

interface LocationDetailViewModelInterface {

    fun getLocLoadingLiveData(): LiveData<Boolean>

    fun getLocationLiveData(): LiveData<LocationData>

    fun getListLoadingLiveData(): LiveData<Boolean>

    fun getListLiveData(): LiveData<List<CharacterData>>

    fun loadData(id: Int)

    fun loadList(list: List<String>?)
}