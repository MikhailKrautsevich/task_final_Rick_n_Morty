package com.mk.rickmortyappbykrautsevich.data.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.mk.rickmortyappbykrautsevich.data.db.RMDatabase
import com.mk.rickmortyappbykrautsevich.di.DaggerRMComponent

class App : Application(), DBProvider{

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
}