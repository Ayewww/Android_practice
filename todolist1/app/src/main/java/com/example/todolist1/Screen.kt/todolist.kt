package com.example.todolist1

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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Add // FAB용 아이콘
import androidx.compose.material3.* // Scaffold, FloatingActionButton 등 포함
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterial3Api::class) // Scaffold 사용을 위해 필요할 수 있음
@Composable
fun HomeScreen(
    navController: NavHostController, // 필요 없다면 제거 가능
    modifier: Modifier = Modifier, // Scaffold에 이 modifier를 적용할 수 있음
    createViewModel: CreateViewModel = viewModel()
) {
    val currentTodoTextValue: String by createViewModel.todo.observeAsState(initial = "")
    val todoListValue: List<TodoData> by createViewModel.todoList.observeAsState(initial = emptyList())

    // Scaffold를 사용하여 FAB를 배치
    Scaffold(
        modifier = modifier.fillMaxSize(), // 전달된 modifier를 Scaffold에 적용
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createViewModel.addTodo()
                    // println("등록 버튼 클릭, 현재 입력값: $currentTodoTextValue")
                    // currentTodoTextValue를 직접 참조하는 것보다 ViewModel 내부 로직으로 처리하는 것이 좋음
                },
                // contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // 아이콘/텍스트 색상 (선택)
                // containerColor = MaterialTheme.colorScheme.primaryContainer, // FAB 배경색 (선택)
            ) {
                // Text("+") 대신 아이콘을 사용하는 것이 일반적임
                Icon(Icons.Filled.Add, contentDescription = "할 일 추가")
            }
        },
        floatingActionButtonPosition = FabPosition.End // 오른쪽 하단에 배치 (기본값)
        // 다른 FabPosition 값: FabPosition.Center
    ) { innerPadding -> // Scaffold의 content 영역에 적용될 패딩 값

        // 기존 Column 내용을 Scaffold의 content 람다 안으로 이동
        Column(
            modifier = Modifier
                .padding(innerPadding) // Scaffold로부터 받은 패딩 적용
                .padding(horizontal = 16.dp, vertical = 16.dp) // Column 내부 컨텐츠를 위한 추가 패딩
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 입력 섹션 (TextField)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = currentTodoTextValue,
                    onValueChange = { newText: String ->
                        createViewModel.UpdateTodo(newText)
                    },
                    label = { Text("새로운 할 일") },
                    modifier = Modifier.weight(1f)
                )
                // TextField 옆의 "+" 버튼은 FAB로 대체되었으므로 여기서는 제거하거나 다른 용도로 변경
                // 만약 TextField 옆에도 버튼이 필요하다면 유지할 수 있지만, FAB와 기능이 중복될 수 있음
                // Spacer(modifier = Modifier.width(8.dp))
                // Button(onClick = { createViewModel.addTodo() }, enabled = currentTodoTextValue.isNotBlank()) {
                //    Text("추가") // 또는 다른 아이콘
                // }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 할 일 목록 표시 섹션 (LazyColumn)
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
                        key = { todoItem -> todoItem.id }
                    ) { todoItemData ->
                        TodoItem(
                            todoData = todoItemData,
                            onDeleteClick = {
                                createViewModel.removeTodo(todoItemData)
                            },
                            onToggleCompleted = {
                                createViewModel.toggleCompleted(todoItemData)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            // 기존 "+" 버튼은 FAB로 대체되었으므로 이 부분은 제거합니다.
            // Spacer(modifier = Modifier.height(32.dp))
            // Button(onClick = { /* createViewModel.addTodo() ... */ }) { Text("+") }
        }
    }
}

@Composable
fun TodoItem(
    todoData: TodoData, // String 대신 TodoData를 받음
    onDeleteClick: () -> Unit,
    onToggleCompleted: () -> Unit // 완료 상태 변경을 위한 콜백
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp), // 패딩 약간 조절
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todoData.isCompleted,
                onCheckedChange = { onToggleCompleted() } // 체크박스 상태 변경 시 콜백 호출
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = todoData.text,
                fontSize = 16.sp,
                textDecoration = if (todoData.isCompleted) TextDecoration.LineThrough else TextDecoration.None, // 완료 시 취소선
                color = if (todoData.isCompleted) Color.Gray else Color.Black, // 완료 시 색상 변경 (선택)
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


