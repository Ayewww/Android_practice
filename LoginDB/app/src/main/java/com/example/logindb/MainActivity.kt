package com.example.logindb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // layout 관련 import 통합
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.* // material3 관련 import 통합
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController // NavHostController import
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.logindb.ui.theme.LoginDBTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginDBTheme {
                val navController = rememberNavController() // NavController를 여기서 생성
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color(0xFFCBC3E3)
                        )
                ) {
                    Scaffold(
                        containerColor = Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text("사용자 정보 관리") },
                                actions = {
                                    IconButton(onClick = { navController.navigate("login") }) {
                                        Icon(Icons.Filled.ExitToApp, "로그인")
                                    }
                                },

                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Transparent
                                )
                            )
                        },

                        modifier = Modifier.fillMaxSize(),

                        ) { innerPadding ->
                        // NavHost를 Scaffold의 content로 설정
                        com.example.logindb.AppNavigation(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding) // TopAppBar 패딩 적용
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login", // 시작 화면은 "home"
        modifier = modifier // Scaffold로부터 받은 패딩 적용
    ) {
        composable("home") {
            // HomeScreen에 navController와 modifier 전달
            com.example.logindb.HomeScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            ) // HomeScreen이 전체 채우도록
        }
        composable("adduser") {
            // HomeScreen에 navController와 modifier 전달
            com.example.logindb.AddUserScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            ) // HomeScreen이 전체 채우도록
        }
        composable(
            route = "detail_user/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            if (userId != null) {
                DetailUserScreen(
                    navController = navController,
                    userId = userId,
                    // 만약 여기서 modifier를 전달하고 있다면, DetailUserScreen 정의에 modifier 파라미터가 필요합니다.
                    modifier = Modifier.fillMaxSize() // 예시
                )
            } else {
                Text("그런 ID 없어요.")
            }
        }
        composable("login") {
            // HomeScreen에 navController와 modifier 전달
            LoginScreen(navController = navController, modifier = Modifier.fillMaxSize()) // HomeScreen이 전체 채우도록
        }
    }
}
