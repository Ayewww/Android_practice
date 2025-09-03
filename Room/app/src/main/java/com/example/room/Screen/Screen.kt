package com.example.room

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.Add // FAB용 아이콘
import androidx.compose.material3.* // Scaffold, FloatingActionButton 등 포함
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    // CreateViewModel 대신 TodoViewModel 사용
    todoViewModel: TodoViewModel = viewModel() // ViewModelProvider.Factory 필요할 수 있음
) {
    // ViewModel에서 TextField 입력 값을 관리하는 State 추가 (선택 사항)
    // 또는 TodoViewModel 내부에 currentInputText: MutableStateFlow<String> 같은 것을 만들 수 있음
    var currentInputText by remember { mutableStateOf("") }

    // TodoViewModel의 StateFlow를 관찰
    val todoListValue: List<Todo> by todoViewModel.todos.collectAsState() // Flow<List<Todo>>를 State<List<Todo>>로

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentInputText.isNotBlank()) {
                        // Todo 객체 생성 시 title, content 등 필드에 맞게 생성
                        // 여기서는 간단히 currentInputText를 title로 사용한다고 가정
                        val newTodo = Todo(title = currentInputText) // content 필드도 있다면 적절히 처리
                        todoViewModel.insert(newTodo)
                        currentInputText = "" // 입력 필드 초기화
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "할 일 추가")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = currentInputText,
                    onValueChange = { newText: String ->
                        currentInputText = newText
                    },
                    label = { Text("새로운 할 일 (제목)") }, // 레이블 명확히
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (todoListValue.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "할 일이 없습니다. 추가해 보세요!",
                                color = Color.Gray,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        items = todoListValue,
                        key = { todoItem -> todoItem.id } // Todo 엔티티의 id 사용
                    ) { todoItem -> // 이제 todoItem은 Todo 타입
                        TodoItem( // TodoItem은 Todo 객체를 받도록 수정되거나, TodoData와 Todo가 호환되어야 함
                            todoData = todoItem, // Todo 객체 전달
                            onDeleteClick = {
                                todoViewModel.delete(todoItem)
                            },
                            onToggleDone = {
                                // Todo 객체의 isDone 상태를 반전시켜 업데이트
                                val updatedTodo = todoItem.copy(isDone = !todoItem.isDone)
                                todoViewModel.update(updatedTodo)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// TodoItem Composable은 Todo 객체를 받도록 수정하거나,
// TodoData가 Todo와 호환되는 구조여야 합니다.
// 여기서는 TodoData가 Todo와 유사한 필드(id, title 또는 text, isDone)를 가졌다고 가정합니다.
// 만약 Todo에 title과 content가 있다면 TodoItem 표시 방식도 수정 필요.
@Composable
fun TodoItem(
    todoData: Todo, // 타입을 Todo로 변경 (또는 TodoData가 Todo와 동일 구조)
    onDeleteClick: () -> Unit,
    onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todoData.isDone,
                onCheckedChange = { onToggleDone() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                // Todo 엔티티의 title 필드를 사용한다고 가정 (또는 text 필드)
                text = todoData.title, // 또는 todoData.text
                fontSize = 16.sp,
                textDecoration = if (todoData.isDone) TextDecoration.LineThrough else TextDecoration.None,
                color = if (todoData.isDone) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "할 일 삭제",
                    tint = Color.Gray
                )
            }
        }
    }
}


