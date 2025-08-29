package com.example.memo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.filled.Add // FAB용 아이콘
import androidx.compose.material3.* // Scaffold, FloatingActionButton 등 포함
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Done // Done 아이콘
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class) // Scaffold 사용을 위해 필요할s 수 있음
@Composable
fun HomeScreen(
    navController: NavHostController, // 필요 없다면 제거 가능
    modifier: Modifier = Modifier, // Scaffold에 이 modifier를 적용할 수 있음
    createViewModel: CreateViewModel = viewModel()
) {
    val todoListValue: List<TodoData> by createViewModel.todoList.observeAsState(initial = emptyList())

    // Scaffold를 사용하여 FAB를 배치
    Scaffold(
        modifier = modifier.fillMaxSize(), // 전달된 modifier를 Scaffold에 적용
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createViewModel.addTodo()
                    navController.navigate("AddMemo")
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "메모 추가")
            }
        },
        floatingActionButtonPosition = FabPosition.End // 오른쪽 하단에 배치 (기본값)
    ) { innerPadding -> // Scaffold의 content 영역에 적용될 패딩 값

        // 기존 Column 내용을 Scaffold의 content 람다 안으로 이동
        Column(
            modifier = Modifier
                .padding(innerPadding) // Scaffold로부터 받은 패딩 적용
                .padding(horizontal = 16.dp, vertical = 16.dp) // Column 내부 컨텐츠를 위한 추가 패딩
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                                text = "메모가 없습니다. 추가해 보세요!",
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
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { // <-- 아이템 클릭 시 DetailScreen으로 이동
                                navController.navigate("detail/${todoItemData.id}")
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ){
                            TodoItem(
                                todoData = todoItemData,
                                onDeleteClick = {
                                    createViewModel.removeTodo(todoItemData)
                                },
                                onToggleCompleted = {
                                    createViewModel.toggleCompleted(todoItemData)
                                }
                            )
                        }
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
                text = todoData.title,
                fontSize = 16.sp,
                textDecoration = if (todoData.isCompleted) TextDecoration.LineThrough else TextDecoration.None, // 완료 시 취소선
                color = if (todoData.isCompleted) Color.Gray else Color.Black, // 완료 시 색상 변경 (선택)
                modifier = Modifier.weight(1f)
            )
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel = viewModel()
) {
    // ViewModel의 currentTitle 및 currentContent LiveData 관찰
    val currentTitleToSave: String by createViewModel.currentTitle.observeAsState(initial = "")
    val currentContentToSave: String by createViewModel.currentContent.observeAsState(initial = "")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("새 메모 작성") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 제목이나 내용 중 하나라도 입력되었으면 저장
                if (currentTitleToSave.isNotBlank() || currentContentToSave.isNotBlank()) {
                    createViewModel.addTodo() // ViewModel의 addTodo 함수 호출 (내부적으로 title, content 사용)
                    navController.popBackStack()
                }
            }) {
                Icon(Icons.Filled.Done, contentDescription = "메모 저장")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // TextField들 사이 간격
        ) {
            // 제목 입력 TextField
            TextField(
                value = currentTitleToSave,
                onValueChange = { newTitle: String ->
                    createViewModel.updateCurrentTitle(newTitle) // ViewModel의 updateCurrentTitle 호출
                },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true // 제목은 한 줄로
            )

            // 내용 입력 TextField
            TextField(
                value = currentContentToSave,
                onValueChange = { newContent: String ->
                    createViewModel.updateCurrentContent(newContent) // ViewModel의 updateCurrentContent 호출
                },
                label = { Text("내용") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간을 모두 차지하도록
                singleLine = false // 내용은 여러 줄 입력 가능
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel = viewModel(),
    memoId: Long
) {
    LaunchedEffect(key1 = memoId) {
        createViewModel.selectTodoById(memoId)
    }

    // ViewModel의 currentTitle 및 currentContent LiveData 관찰
    val currentTitleToEdit: String by createViewModel.currentTitle.observeAsState("")
    val currentContentToEdit: String by createViewModel.currentContent.observeAsState("")
    val selectedMemo by createViewModel.selectedTodo.observeAsState(null)

    DisposableEffect(Unit) {
        onDispose {
            createViewModel.clearSelectedTodoAndFields() // ViewModel 함수 이름 변경됨
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (selectedMemo != null) "메모 수정" else "메모 상세") },
                navigationIcon = {
                    // 이제 popBackStack()을 사용하거나, 특정 상황에 따라 "home"으로 navigate 할 수 있습니다.
                    // 일반적으로는 popBackStack()이 더 적절합니다.
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    if (selectedMemo != null) {
                        IconButton(onClick = {
                            selectedMemo?.let { memo ->
                                createViewModel.removeTodo(memo)
                                navController.popBackStack()
                            }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "메모 삭제")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedMemo != null) {
                FloatingActionButton(onClick = {
                    // 제목이나 내용 중 하나라도 비어있지 않으면 저장
                    if (currentTitleToEdit.isNotBlank() || currentContentToEdit.isNotBlank()) {
                        // ViewModel의 updateList 함수는 이제 title과 content를 모두 받음
                        createViewModel.updateList(memoId, currentTitleToEdit, currentContentToEdit)
                        navController.popBackStack()
                    }
                }) {
                    Icon(Icons.Filled.Done, contentDescription = "수정 완료")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // TextField들 사이 간격
        ) {
            // 로딩 상태 또는 데이터 없음 상태 처리
            if (selectedMemo == null && memoId != 0L && currentTitleToEdit.isBlank() && currentContentToEdit.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // 제목 입력/수정 TextField
                TextField(
                    value = currentTitleToEdit,
                    onValueChange = { newTitle: String ->
                        createViewModel.updateCurrentTitle(newTitle) // ViewModel의 updateCurrentTitle 호출
                    },
                    label = { Text("제목") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 내용 입력/수정 TextField
                TextField(
                    value = currentContentToEdit,
                    onValueChange = { newContent: String ->
                        createViewModel.updateCurrentContent(newContent) // ViewModel의 updateCurrentContent 호출
                    },
                    label = { Text("내용") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // 남은 공간을 모두 차지하도록
                    singleLine = false
                )
            }
        }
    }
}

