package com.mk.rickmortyappbykrautsevich.data.app

import com.mk.rickmortyappbykrautsevich.data.db.RMDatabase

interface DBProvider {
    fun getDataBase(): RMDatabase?
}