package com.example.logindb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.Add // FAB용 아이콘
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Scaffold, FloatingActionButton 등 포함
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    UserViewModel: UserViewModel = viewModel()
) {
    var currentInputText by remember { mutableStateOf("") }
    val UserListValue: List<User> by UserViewModel.users.collectAsState()
    var selectedUserIds by remember { mutableStateOf<Set<Int>>(emptySet()) }


    val filteredUserList by remember(UserListValue, currentInputText) {
        derivedStateOf {
            if (currentInputText.isBlank()) {
                UserListValue
            } else {
                UserListValue.filter { user ->
                    user.name.contains(currentInputText, ignoreCase = true)

                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = currentInputText,
                    onValueChange = { newText: String ->
                        currentInputText = newText
                    },
                    label = { Text("이름 검색") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "검색 아이콘")
                    },
                    trailingIcon = {
                        if (currentInputText.isNotEmpty()) {
                            IconButton(onClick = { currentInputText = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "지우기")
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("adduser")
                },
                modifier = Modifier.width(380.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD6EAF8),
                    contentColor = MaterialTheme.colorScheme.onSurface

                )
            ) {
                Text("유자 추가")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // ... 전체 선택/해제 체크박스 (기존과 동일) ...
            if (filteredUserList.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = filteredUserList.isNotEmpty() && selectedUserIds.size == filteredUserList.size,
                        onCheckedChange = { isChecked ->
                            selectedUserIds = if (isChecked) {
                                filteredUserList.map { it.uid }.toSet()
                            } else {
                                emptySet()
                            }
                        }
                    )
                    Text("전체 선택/해제")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (filteredUserList.isEmpty()) {

                } else {
                    items(
                        items = filteredUserList,
                        key = { userItem -> userItem.uid }
                    ) { userItem ->
                        UserItem(
                            UserData = userItem,
                            isSelected = userItem.uid in selectedUserIds,
                            onCheckedChange = { isChecked ->
                                selectedUserIds = if (isChecked) {
                                    selectedUserIds + userItem.uid
                                } else {
                                    selectedUserIds - userItem.uid
                                }
                            },
                            onItemClick = { userId ->
                                navController.navigate("detail_user/$userId")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (selectedUserIds.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // 하단 영역 패딩
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)) // 배경색 추가 (선택 사항)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val usersToDelete = UserListValue.filter { it.uid in selectedUserIds }
                            usersToDelete.forEach { UserViewModel.delete(it) }
                            selectedUserIds = emptySet()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray, // 배경색을 마젠타로
                            contentColor = Color.White      // 글자색을 흰색으로
                        )
                    ) {
                        Text("선택 삭제")
                    }
                }
            }
        }



    }
}



@Composable
fun UserItem(
    UserData: User,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onItemClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(UserData.uid) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected, // (3) isSelected 사용
                onCheckedChange = onCheckedChange // (4) 콜백 호출
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = UserData.name,
                fontSize = 16.sp,
                // isDone 대신 isSelected를 사용하여 텍스트 스타일 변경 (선택 사항)
                // 또는 isDone은 그대로 두고, 선택된 항목에 대한 시각적 피드백은 다른 방식으로 제공
                textDecoration = if (UserData.isDone) TextDecoration.LineThrough else TextDecoration.None,
                color = if (UserData.isDone) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )
            // (5) 개별 삭제 IconButton 제거
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    prefilledUserId: String? = null,    // (4) 전달받을 사용자 ID (선택 사항)
    prefilledPassword: String? = null // (4) 전달받을 비밀번호 (선택 사항)
) {

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    // regdate는 자동 생성 또는 서버에서 처리하는 것이 일반적이므로 입력 필드 제거 고려

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("새 계정 정보 입력") }) // TopAppBar 추가 (선택 사항)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // (5) 모든 필수 입력 필드가 채워졌는지 확인
                    if (nameInput.isNotBlank() && emailInput.isNotBlank()) {
                        val newUser = User(
                            // uid는 자동 생성되므로 여기서 설정하지 않음
                            name = nameInput,
                            email = emailInput,
                            phonenum = phoneInput.ifBlank { null },
                            address = addressInput.ifBlank { null }
                            // regDate는 Room에서 @Insert 시 자동 생성되도록 하거나, 서버에서 처리
                        )
                        userViewModel.insert(newUser)
                        navController.navigate("home") // 이전 화면(LoginScreen)으로 돌아가거나,
                        // 또는 로그인 화면으로 명시적으로 이동
                        // navController.navigate("login") { popUpTo("adduser_with_credentials") { inclusive = true } }
                    } else {
                        navController.navigate("home")
                    }
                }
            ) {
                Icon(Icons.Filled.Done, contentDescription = "사용자 정보 저장")
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
            // 전달받은 ID와 PW 표시 (수정 불가하게 하려면 Text 사용)
            if (prefilledUserId != null) {
                Text("ID: $prefilledUserId", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
            }
            // 비밀번호는 보안상 보통 직접 표시하지 않거나, TextField 초기값으로만 사용
            // if (prefilledPassword != null) {
            //     Text("비밀번호: ********", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            //     Spacer(modifier = Modifier.height(16.dp))
            // }

            // 만약 ID와 PW를 여기서도 수정 가능하게 하려면 TextField 사용
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("이름*") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("이메일*") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                label = { Text("전화번호") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = addressInput,
                onValueChange = { addressInput = it },
                label = { Text("주소") },
                modifier = Modifier.fillMaxWidth()
            )
            // ... (기존 "홈으로" 버튼은 제거하거나 다른 동작으로 변경)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailUserScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userId: Int, // (8) 전달받은 사용자 ID
    userViewModel: UserViewModel = viewModel()
) {
    val userFromDb by userViewModel.getUserById(userId).collectAsState(initial = null)
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    var regDateInput by remember { mutableStateOf("") }
    var isDoneState by remember { mutableStateOf(false) }

    // userFromDb가 변경될 때마다 입력 필드 상태를 업데이트합니다.
    LaunchedEffect(userFromDb) {
        userFromDb?.let { user ->
            nameInput = user.name
            emailInput = user.email
            phoneInput = user.phonenum ?: ""
            addressInput = user.address ?: ""
            regDateInput = user.regDate ?: ""
            isDoneState = user.isDone
        }
    }

    val context = LocalContext.current // Toast 메시지 등에 사용

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사용자 정보 수정") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                userFromDb?.let { // 현재 DB에서 가져온 user 객체가 있어야 수정 가능
                    if (nameInput.isNotBlank() && emailInput.isNotBlank()) {
                        val updatedUser = it.copy( // 기존 uid와 isDone은 유지, 나머지는 입력값으로
                            name = nameInput,
                            email = emailInput,
                            phonenum = phoneInput.ifBlank { null },
                            address = addressInput.ifBlank { null },
                            regDate = regDateInput.ifBlank { null },
                            isDone = isDoneState // isDone 상태도 업데이트 (필요시)
                        )
                        userViewModel.update(updatedUser)
                        navController.popBackStack() // 수정 후 이전 화면으로

                    } else {

                    }
                }
            }) {
                Icon(Icons.Filled.Done, "수정 완료")
            }
        }
    ) { innerPadding ->
        if (userFromDb == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // 로딩 중 또는 사용자 없음
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("이름*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("이메일*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("전화번호") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text("주소") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = regDateInput,
                    onValueChange = { regDateInput = it },
                    label = { Text("등록일자") },
                    modifier = Modifier.fillMaxWidth(),

                )
                Spacer(modifier = Modifier.weight(1f)) // 버튼들을 하단으로 밀기
                Button(
                    onClick = {
                        userFromDb?.let {
                            userViewModel.delete(it)
                            navController.popBackStack() // 삭제 후 이전 화면으로

                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("삭제하기")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var ID by remember { mutableStateOf("") } // id로 변경
    var PW by remember { mutableStateOf("") } // password로 변경

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("로그인", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField( // OutlinedTextField 사용 (선택 사항)
            value = ID,
            onValueChange = { ID = it },
            label = { Text("사용자 ID") }, // 레이블 변경
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = PW,
            onValueChange = { PW = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("adduser")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}



