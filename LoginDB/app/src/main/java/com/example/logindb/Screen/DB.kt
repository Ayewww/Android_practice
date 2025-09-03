package com.example.logindb

import android.R.attr.value
import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import java.util.Date

@Database(
    entities = [User::class],
    version = 1,

    )
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao():UserDao
}

@Database(entities = [User::class],version = 1)
abstract class UserDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao

    companion object{
        @Volatile private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE?: Room.databaseBuilder(
                    context.applicationContext, UserDatabase::class.java,
                    "user_db"
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
