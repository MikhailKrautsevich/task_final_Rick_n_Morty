package com.mk.rickmortyappbykrautsevich.di

import android.content.Context
import com.mk.rickmortyappbykrautsevich.data.dataproviders.*
import com.mk.rickmortyappbykrautsevich.presentation.fragments.CharacterDetailFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.CharactersListFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.EpisodeDetailFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.LocationDetailFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [PicassoModule::class, NetworkCheckerModule::class, ApiModule::class])
interface RMComponent {

    // для фрагментов
    fun inject(fragment: CharacterDetailFragment)
    fun inject(fragment: EpisodeDetailFragment)
    fun inject(fragment: LocationDetailFragment)
    fun inject(fragment: CharactersListFragment)

    // для провайдеров
    fun inject(pr: CharacterDetailProvider)
    fun inject(pr: EpisodeDetailProvider)
    fun inject(pr: LocationDetailProvider)
    fun inject(pr: ListCharactersProvider)
    fun inject(pr: ListEpisodesProvider)
    fun inject(pr: ListLocationsProvider)

    @Component.Factory
    interface RMComponentFactory {
        fun create(@BindsInstance context: Context): RMComponent
    }
}