package com.echo.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MemoryEntity::class], version = 1, exportSchema = true)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
}
