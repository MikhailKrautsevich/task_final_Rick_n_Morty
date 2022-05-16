package com.mk.rickmortyappbykrautsevich.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mk.rickmortyappbykrautsevich.db.entities.CharacterEntity
import com.mk.rickmortyappbykrautsevich.db.entities.LocationEntity
import com.mk.rickmortyappbykrautsevich.enums.Gender
import io.reactivex.rxjava3.core.Single

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(characters: List<CharacterEntity>)

    @Query("SELECT * from characters")
    fun getAllCharacters(): Single<List<CharacterEntity>>

    @Query(
        "SELECT * from characters " +
                "WHERE name LIKE :name AND type LIKE :type AND species LIKE :species " +
                "AND gender =:gender AND status =:status"
    )
    fun getCharacters(
        name: String?,
        species: String?,
        type: String?,
        gender: String?,
        status: String?
    ): Single<List<CharacterEntity>>
}