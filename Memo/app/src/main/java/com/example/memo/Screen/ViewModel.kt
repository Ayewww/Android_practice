package com.example.memo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class TodoData(val id: Long = System.currentTimeMillis(), var title: String,
                    var content: String, var isCompleted: Boolean = false
)

class CreateViewModel : ViewModel() {
    // 입력/편집 중인 제목
    private val _currentTitle = MutableLiveData<String>("")
    val currentTitle: LiveData<String> get() = _currentTitle

    // 입력/편집 중인 내용
    private val _currentContent = MutableLiveData<String>("")
    val currentContent: LiveData<String> get() = _currentContent

    // 전체 메모 목록
    private val _todoList = MutableLiveData<List<TodoData>>(emptyList())
    val todoList: LiveData<List<TodoData>> get() = _todoList

    // 선택된 메모 (상세 보기/수정용)
    private val _selectedTodo = MutableLiveData<TodoData?>()
    val selectedTodo: LiveData<TodoData?> get() = _selectedTodo

    // 제목 업데이트 함수
    fun updateCurrentTitle(newTitle: String) {
        _currentTitle.value = newTitle
    }

    // 내용 업데이트 함수
    fun updateCurrentContent(newContent: String) {
        _currentContent.value = newContent
    }

    // 새로운 메모 추가 함수
    fun addTodo() {
        val title = _currentTitle.value ?: ""
        val content = _currentContent.value ?: ""

        // 제목이나 내용 둘 중 하나라도 비어있지 않으면 추가 (또는 둘 다 필수로 할 수도 있음)
        if (title.isNotBlank() || content.isNotBlank()) {
            val newMemo = TodoData(title = title, content = content)
            val updatedList = _todoList.value?.toMutableList() ?: mutableListOf()
            updatedList.add(0, newMemo) // 새 메모를 맨 위에 추가 (선택)
            _todoList.value = updatedList

            _currentTitle.value = "" // 입력 필드 초기화
            _currentContent.value = "" // 입력 필드 초기화
        }
    }

    // 기존 메모 수정 함수
    fun updateList(memoId: Long, newTitle: String, newContent: String) {
        val currentList = _todoList.value ?: return
        // 제목이나 내용 둘 중 하나라도 비어있지 않으면 수정 (또는 둘 다 필수로 할 수도 있음)
        if (newTitle.isNotBlank() || newContent.isNotBlank()) {
            val updatedList = currentList.map { memo ->
                if (memo.id == memoId) {
                    memo.copy(title = newTitle, content = newContent)
                } else {
                    memo
                }
            }
            _todoList.value = updatedList

            // 수정 후 입력 필드 및 선택된 메모 초기화
            _currentTitle.value = ""
            _currentContent.value = ""
            _selectedTodo.value = null
        }
    }

    // 특정 메모를 목록에서 제거하는 함수
    fun removeTodo(item: TodoData) {
        val updatedList = _todoList.value?.toMutableList() ?: mutableListOf()
        if (updatedList.remove(item)) {
            _todoList.value = updatedList
        }
        // 삭제된 아이템이 현재 선택된 아이템과 같다면 초기화
        if (_selectedTodo.value?.id == item.id) {
            _selectedTodo.value = null
            _currentTitle.value = ""
            _currentContent.value = ""
        }
    }

    // 완료 상태 토글 함수
    fun toggleCompleted(item: TodoData) {
        val currentList = _todoList.value ?: return
        val updatedList = currentList.map { todo ->
            if (todo.id == item.id) {
                todo.copy(isCompleted = !todo.isCompleted)
            } else {
                todo
            }
        }
        _todoList.value = updatedList
    }

    // ID로 메모를 선택하고, 해당 메모의 제목과 내용을 편집 필드에 설정
    fun selectTodoById(memoId: Long) {
        val memo = _todoList.value?.find { it.id == memoId }
        _selectedTodo.value = memo
        _currentTitle.value = memo?.title ?: ""
        _currentContent.value = memo?.content ?: ""
    }

    // 선택된 메모 및 편집 필드 초기화
    fun clearSelectedTodoAndFields() {
        _selectedTodo.value = null
        _currentTitle.value = ""
        _currentContent.value = ""
    }
}

