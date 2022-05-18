package com.mk.rickmortyappbykrautsevich.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mk.rickmortyappbykrautsevich.data.db.entities.LocationEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(locations: List<LocationEntity>)

    @Query("SELECT * from locations")
    fun getAllLocations(): Single<List<LocationEntity>>

    @Query(
        "SELECT * from locations " +
                "WHERE name LIKE :name AND type LIKE :type AND dimension LIKE :dimension"
    )
    fun getLocations(
        name: String?,
        type: String?,
        dimension: String?
    ): Single<List<LocationEntity>>

    @Query("SELECT * from locations WHERE id = :id")
    fun getTheLocation(id: Int): Single<LocationEntity>
}