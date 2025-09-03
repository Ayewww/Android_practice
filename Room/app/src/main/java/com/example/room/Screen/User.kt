package com.example.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index


@Entity(
    tableName = "user",
    primaryKeys = ["uid"], // uid를 기본 키로 사용 (또는 uid 프로퍼티에 @PrimaryKey)
    indices = [
        Index(value = ["email", "address"], unique = true) // 이 인덱스 때문에 address 프로퍼티 필요
    ]
)
data class User(
    val uid: Int, // 기본 키 필드
    val name: String,
    val email: String,
    val phonenum: String? = null,
    @ColumnInfo(name = "address")
    val address: String? = null,
    val regiDate: String? = null


) {
    @Ignore
    val tempData: String = ""
}
