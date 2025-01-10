package com.effort.local.dao.restaurant.parkinglot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.effort.local.model.restaurant.parkinglot.ParkingLotLocal


@Dao
interface ParkingLotDao {

    @Query("""
        SELECT * FROM parkingLots
        ORDER BY ((latitude - :latitude) * (latitude - :latitude) + 
                  (longitude - :longitude) * (longitude - :longitude)) ASC
        LIMIT 5
    """)
    suspend fun getNearestParkingLots(latitude: Double, longitude: Double): List<ParkingLotLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parkingLots: List<ParkingLotLocal>)

    @Query("SELECT COUNT(*) FROM parkingLots")
    suspend fun countAll(): Int // 데이터 개수 조회
}