package com.mk.rickmortyappbykrautsevich.di

import android.content.Context
import com.mk.rickmortyappbykrautsevich.presentation.fragments.CharacterDetailFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.CharactersListFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.EpisodeDetailFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.LocationDetailFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [PicassoModule::class])
interface RMComponent {

    fun inject(fragment: CharacterDetailFragment)
    fun inject(fragment: EpisodeDetailFragment)
    fun inject(fragment: LocationDetailFragment)
    fun inject(fragment: CharactersListFragment)

    @Component.Factory
    interface RMComponentFactory{
        fun create(@BindsInstance context: Context) : RMComponent
    }
}