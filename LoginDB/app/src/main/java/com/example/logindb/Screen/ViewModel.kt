package com.example.logindb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class userData(val id: Long = System.currentTimeMillis(),
                    val text: String, var isCompleted: Boolean = false
)


class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: userRepository

    // Repository의 Flow를 StateFlow로 변환
    val users: StateFlow<List<User>>

    init {
        val userDao = UserDatabase.getInstance(application).userDao() // userDatabase 가정
        repository = userRepository(userDao)

        users = repository.allusers // repository.allusers는 Flow<List<user>> 타입
            .catch { exception ->
                // 실제 앱에서는 로깅 또는 사용자에게 오류 알림 처리
                System.err.println("Error fetching users: ${exception.message}")
                emit(emptyList()) // 오류 발생 시 빈 리스트 방출
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // UI가 구독할 때만 활성
                initialValue = emptyList() // 초기 데이터는 빈 리스트
            )
    }

    fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(user)
    }

    fun getUserById(userId: Int): Flow<User?> { // User가 없을 수도 있으므로 Nullable
        return repository.getUserById(userId) // UserRepository에 해당 함수 필요
    }
}

class userRepository(private val userDao: UserDao) {

    val allusers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    fun getUserById(userId: Int): Flow<User?> { // (14) 추가
        return userDao.getUserById(userId)
    }
    // 필요시 다른 Repository 메소드 추가
    // 예: suspend fun deleteAll() { userDao.deleteAllusers() }
}

