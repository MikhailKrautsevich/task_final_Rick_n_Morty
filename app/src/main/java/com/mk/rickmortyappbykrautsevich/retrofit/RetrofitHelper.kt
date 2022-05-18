package com.mk.rickmortyappbykrautsevich.retrofit

import com.mk.rickmortyappbykrautsevich.retrofit.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"

        fun getOkHttpClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder().addInterceptor(interceptor).build()
        }

        fun getRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        fun getCharsApi(retrofit: Retrofit): GetCharactersApi =
            retrofit.create(GetCharactersApi::class.java)

        fun getLocsApi(retrofit: Retrofit): GetLocationsApi =
            retrofit.create(GetLocationsApi::class.java)

        fun getEpsApi(retrofit: Retrofit): GetEpisodesApi =
            retrofit.create(GetEpisodesApi::class.java)

        fun getTheEpApi(retrofit: Retrofit): GetTheEpisodeApi =
            retrofit.create(GetTheEpisodeApi::class.java)

        fun getTheCharApi(retrofit: Retrofit): GetTheCharacterApi =
            retrofit.create(GetTheCharacterApi::class.java)

        fun getTheLocApi(retrofit: Retrofit) : GetTheLocationApi =
            retrofit.create(GetTheLocationApi::class.java)
    }
}