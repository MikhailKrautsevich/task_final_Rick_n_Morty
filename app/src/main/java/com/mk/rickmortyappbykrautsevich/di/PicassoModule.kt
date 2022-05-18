package com.mk.rickmortyappbykrautsevich.di

import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides

@Module
class PicassoModule {

    @ApplicationScope
    @Provides
    fun providesPicasso() : Picasso = Picasso.get()
}