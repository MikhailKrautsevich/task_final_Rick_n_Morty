package com.mk.rickmortyappbykrautsevich.data.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.room.Room
import com.mk.rickmortyappbykrautsevich.data.db.RMDatabase
import com.mk.rickmortyappbykrautsevich.di.DaggerRMComponent

class App : Application(), DBProvider, NetworkChecker {

    companion object {
        var instance: App? = null
    }

    private var appContext: Context? = null
    private var dataBase: RMDatabase? = null

    val component by lazy {
        DaggerRMComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        appContext = baseContext
        instance = this
        appContext?.let {
            dataBase =
                Room.databaseBuilder(it, RMDatabase::class.java, "db").build()
        }
    }

    override fun getDataBase(): RMDatabase? = dataBase

    override fun isNetworkAvailable(): Boolean {
        val conManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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