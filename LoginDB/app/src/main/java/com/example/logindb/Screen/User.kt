package com.example.logindb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "user_table",

    indices = [
        Index(value = ["email"], unique = true), // email만 unique로 가정 (필요에 따라 조정)
        Index(value = ["phonenum"], unique = true),
        Index(value = ["address"], unique = true),
        Index(value = ["regDate"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) // uid 자동 생성
    val uid: Int = 0, // 기본값 제공 (자동 생성을 위해)
    val name: String,
    val email: String,
    val phonenum: String? = null,
    @ColumnInfo(name = "address")
    val address: String? = null,
    val regDate: String? = null,
    val isDone: Boolean = false

)