package com.effort.local.database.restaurant.parkinglot

import androidx.room.Database
import androidx.room.RoomDatabase
import com.effort.local.dao.restaurant.parkinglot.ParkingLotDao
import com.effort.local.model.restaurant.parkinglot.ParkingLotLocal

@Database(entities = [ParkingLotLocal::class], version = 1, exportSchema = false)
abstract class ParkingLotRoomDatabase : RoomDatabase() {
    abstract fun parkingLotDao(): ParkingLotDao
}