package com.example.mymaps.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymaps.model.place

@Database(entities = [place::class], version = 1)
abstract class PlaceDb : RoomDatabase() {
    abstract fun PlaceDao(): PlaceDao
}