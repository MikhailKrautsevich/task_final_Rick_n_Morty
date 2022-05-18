package com.mk.rickmortyappbykrautsevich.di

import com.mk.rickmortyappbykrautsevich.data.utils.NetworkChecker
import com.mk.rickmortyappbykrautsevich.data.utils.NetworkCheckerImpl
import dagger.Module
import dagger.Provides

@Module
class NetworkCheckerModule {

    @Provides
    fun providesNetworkChecker(impl: NetworkCheckerImpl): NetworkChecker {
        return impl as NetworkChecker
    }
}