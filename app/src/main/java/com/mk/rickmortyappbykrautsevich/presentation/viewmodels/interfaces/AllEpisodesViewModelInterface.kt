package com.mk.rickmortyappbykrautsevich.presentation.viewmodels.interfaces

import androidx.lifecycle.LiveData
import com.mk.rickmortyappbykrautsevich.data.retrofit.models.queries.EpisodeQuery
import com.mk.rickmortyappbykrautsevich.presentation.fragments.recyclers_data.EpisodeData

interface AllEpisodesViewModelInterface: UsesPagination, WithSearchView {

    fun getLoadingLiveData(): LiveData<Boolean>

    fun getPaginationLiveData(): LiveData<Boolean>

    fun getEpisodesList(): LiveData<List<EpisodeData>>

    fun getData(query: EpisodeQuery?)
}