package com.example.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getAllUsers(uid: Int): List<User>

    @Delete
    suspend fun deleteUser(user: User)



}

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table ORDER BY id DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Insert
    suspend fun insert(todo: Todo)
    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

}

