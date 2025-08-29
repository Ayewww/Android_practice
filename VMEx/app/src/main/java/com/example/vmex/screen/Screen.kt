package com.example.vmex

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var showTextField by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }// 초기 텍스트 변경

    Column(
        modifier = modifier.padding(16.dp), // 외부에서 fillMaxSize가 적용되었으므로 내부 패딩만
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.image), // 'images.png' 또는 해당 파일명 확인
            contentDescription = "My wonderful image",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = userName,
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Spacer(modifier = Modifier.height(150.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 버튼 사이 간격
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { showTextField = true }) {
                Text("입력")
            }
            Button(onClick = { showTextField = false }) {
                Text("등록: ${userName}")



            }
        }

        if (showTextField) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userName,
                onValueChange = { newText -> userName = newText },
                label = { Text("Enter the word") }
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // 네비게이션 버튼 그룹과의 간격

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                navController.navigate("counter") // "profile" 경로로 이동
                println("카운터")
            }) {
                Text("카운터")
            }
            Button(onClick = {
                navController.navigate("profile/$userName") // "profile" 경로로 이동
                println("프로필 버튼 클릭")
                Color.White
            }) {
                Text("프로필")
            }
        }

    }
}

@Composable
fun CounterScreen(navController: NavHostController, modifier: Modifier = Modifier, MyViewModel: MyViewModel = viewModel()) {
    val count: Int by MyViewModel.counter.observeAsState(initial=0)

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("${count}", color = Color.White, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(100.dp))

        Row{
            Button(onClick = {
                MyViewModel.increase()
                // 프로필 화면에서 다시 프로필 버튼을 누르는 경우 (예시)
                println("+1")

            }) {
                Text("증가")
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                MyViewModel.decrease()
                // 프로필 화면에서 다시 프로필 버튼을 누르는 경우 (예시)
                println("-1")

            }) {
                Text("감소")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            navController.popBackStack("home", inclusive = false) // 이전 화면 (홈)으로 돌아가기

            // 또는 navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }) {
            Text("돌아가기")
        }
    }
}

