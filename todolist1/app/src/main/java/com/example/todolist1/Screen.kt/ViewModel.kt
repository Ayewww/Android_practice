package com.example.todolist1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class TodoData(val id: Long = System.currentTimeMillis(),
                    val text: String, var isCompleted: Boolean = false
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

