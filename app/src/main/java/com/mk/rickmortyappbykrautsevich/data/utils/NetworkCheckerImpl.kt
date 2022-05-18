package com.mk.rickmortyappbykrautsevich.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.mk.rickmortyappbykrautsevich.data.app.NetworkChecker
import javax.inject.Inject

class NetworkCheckerImpl @Inject constructor(val context: Context) : NetworkChecker {

    override fun isNetworkAvailable() : Boolean {
        val conManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = conManager.activeNetwork ?: return false
            val actNw = conManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = conManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}