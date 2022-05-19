package com.mk.rickmortyappbykrautsevich.di

import com.mk.rickmortyappbykrautsevich.data.retrofit.RetrofitHelper
import com.mk.rickmortyappbykrautsevich.data.retrofit.api.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
class ApiModule {

    @ApplicationScope
    @Provides
    fun getOkHttpClient(): OkHttpClient = RetrofitHelper.getOkHttpClient()

    @ApplicationScope
    @Provides
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit = RetrofitHelper.getRetrofit(okHttpClient)

    @ApplicationScope
    @Provides
    fun provideGetTheCharacterApi(retrofit: Retrofit): GetTheCharacterApi =
        RetrofitHelper.getTheCharApi(retrofit)

    @ApplicationScope
    @Provides
    fun provideGetTheEpisodeApi(retrofit: Retrofit): GetTheEpisodeApi =
        RetrofitHelper.getTheEpApi(retrofit)

    @ApplicationScope
    @Provides
    fun provideGetTheLocationApii(retrofit: Retrofit): GetTheLocationApi =
        RetrofitHelper.getTheLocApi(retrofit)

    @ApplicationScope
    @Provides
    fun provideGetCharactersApi(retrofit: Retrofit): GetCharactersApi =
        RetrofitHelper.getCharsApi(retrofit)

    @ApplicationScope
    @Provides
    fun provideGetEpisodesApi(retrofit: Retrofit): GetEpisodesApi =
        RetrofitHelper.getEpsApi(retrofit)

    @ApplicationScope
    @Provides
    fun provideGetLocationsApi(retrofit: Retrofit): GetLocationsApi =
        RetrofitHelper.getLocsApi(retrofit)
}