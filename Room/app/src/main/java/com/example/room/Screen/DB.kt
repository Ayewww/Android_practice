package com.example.room

import android.R.attr.value
import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import java.util.Date

@Database(
    entities = [User::class, Todo::class],
    version = 1,

    )
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao():UserDao
}

@Database(entities = [Todo::class],version = 1)
abstract class TodoDatabase: RoomDatabase(){
    abstract fun todoDao(): TodoDao

    companion object{
        @Volatile private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE?: Room.databaseBuilder(
                    context.applicationContext, TodoDatabase::class.java,
                    "todo_db"
                ).build().also{INSTANCE = it}
            }
    }
}

class Converters{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let{Date(it)}

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
