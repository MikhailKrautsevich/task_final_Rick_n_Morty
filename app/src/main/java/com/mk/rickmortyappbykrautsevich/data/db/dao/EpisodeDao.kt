package com.mk.rickmortyappbykrautsevich.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mk.rickmortyappbykrautsevich.data.db.entities.EpisodeEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(episodes: List<EpisodeEntity>)

    @Query("SELECT * from episodes")
    fun getAllEpisodes(): Single<List<EpisodeEntity>>

    @Query(
        "SELECT * from episodes " +
                "WHERE name LIKE :name AND episode LIKE :code"
    )
    fun getEpisodes(
        name: String?,
        code: String?,
    ): Single<List<EpisodeEntity>>

    @Query("SELECT * from episodes WHERE id = :id")
    fun getTheEpisode(id: Int): Single<EpisodeEntity>

    @Query("SELECT * from episodes WHERE url in (:list)")
    fun getEpisodes(list: List<String>): Single<List<EpisodeEntity>>
}