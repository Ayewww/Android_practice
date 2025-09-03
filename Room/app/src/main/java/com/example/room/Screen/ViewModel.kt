package com.example.room

import android.app.Application
import androidx.activity.result.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoData(val id: Long = System.currentTimeMillis(),
                    val text: String, var isCompleted: Boolean = false
)

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title : String,
    val isDone: Boolean = false
)

class CreateViewModel: ViewModel(){
    private val _todo= MutableLiveData<String>("")
    val todo : LiveData<String>get() = _todo
    private val _todoList = MutableLiveData<List<TodoData>>(emptyList())
    val todoList: LiveData<List<TodoData>> get() = _todoList
    fun UpdateTodo(newText: String){
        _todo.value = newText
    }

    fun addTodo() {
        val currentText = _todo.value ?: ""
        if (currentText.isNotBlank()) { // 빈 내용은 추가하지 않음
            val newdo = TodoData(text = currentText)
            val updatedList = _todoList.value?.toMutableList() ?: mutableListOf()
            updatedList.add(newdo)
            _todoList.value = updatedList
            _todo.value = "" // TextField 내용 초기화
        }
    }

    // 특정 할 일을 목록에서 제거하는 함수 (선택 사항)
    fun removeTodo(item: TodoData) {
        val updatedList = _todoList.value?.toMutableList() ?: mutableListOf()
        updatedList.remove(item)
        _todoList.value = updatedList
    }
    fun toggleCompleted(item: TodoData) {
        val currentList = _todoList.value ?: return
        val updatedList = currentList.map { todo ->
            if (todo.id == item.id) {
                todo.copy(isCompleted = !todo.isCompleted) // 해당 아이템의 isCompleted 상태 반전
            } else {
                todo
            }
        }
        _todoList.value = updatedList
    }

}

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository

    // Repository의 Flow를 StateFlow로 변환
    val todos: StateFlow<List<Todo>>

    init {
        val todoDao = TodoDatabase.getInstance(application).todoDao() // TodoDatabase 가정
        repository = TodoRepository(todoDao)

        todos = repository.allTodos // repository.allTodos는 Flow<List<Todo>> 타입
            .catch { exception ->
                // 실제 앱에서는 로깅 또는 사용자에게 오류 알림 처리
                System.err.println("Error fetching todos: ${exception.message}")
                emit(emptyList()) // 오류 발생 시 빈 리스트 방출
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // UI가 구독할 때만 활성
                initialValue = emptyList() // 초기 데이터는 빈 리스트
            )
    }

    fun insert(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(todo)
    }

    fun update(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(todo)
    }

    fun delete(todo: Todo) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(todo)
    }
}

class TodoRepository(private val todoDao: TodoDao) {

    val allTodos: Flow<List<Todo>> = todoDao.getAllTodos()

    suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }

    // 필요시 다른 Repository 메소드 추가
    // 예: suspend fun deleteAll() { todoDao.deleteAllTodos() }
}

