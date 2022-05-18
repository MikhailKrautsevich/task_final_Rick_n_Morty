package com.mk.rickmortyappbykrautsevich.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mk.rickmortyappbykrautsevich.db.dao.CharacterDao
import com.mk.rickmortyappbykrautsevich.db.dao.EpisodeDao
import com.mk.rickmortyappbykrautsevich.db.dao.LocationDao
import com.mk.rickmortyappbykrautsevich.db.entities.CharacterEntity
import com.mk.rickmortyappbykrautsevich.db.entities.EpisodeEntity
import com.mk.rickmortyappbykrautsevich.db.entities.LocationEntity

@Database(
    entities = [CharacterEntity::class, EpisodeEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class RMDatabase : RoomDatabase() {
    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEpisodeDao(): EpisodeDao
    abstract fun getLocationDao(): LocationDao
}