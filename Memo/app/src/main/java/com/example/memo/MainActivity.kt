package com.example.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // layout 관련 import 통합
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // material3 관련 import 통합
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController // NavHostController import
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.memo.ui.theme.MemoTheme



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoTheme {
                val navController = rememberNavController() // NavController를 여기서 생성
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color(0xFF6A0DAD),   // 위쪽 보라색
                                0.65f to Color(0xFFD8BFD8), // 중간 연보라
                                1.0f to Color(0xFFE4E724)
                            )
                        )
                ) {
                    Scaffold(
                        containerColor = Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text("오늘 할 일") },
                                navigationIcon = {IconButton(onClick = { /* TODO */ }) {
                                    Icon(Icons.Filled.Menu, "메뉴")
                                }
                                },
                                actions = {
                                    IconButton(onClick = { }) {
                                        Icon(Icons.Filled.Search, "검색")
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
                        com.example.memo.AppNavigation(
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
    val createViewModel: CreateViewModel = viewModel() // ViewModel 인스턴스 생성

    NavHost(
        navController = navController,
        startDestination = "home", // 시작 화면은 "home"
        modifier = modifier // Scaffold로부터 받은 패딩 적용
    ) {
        composable("home") {
            // HomeScreen에 navController와 modifier 전달
            com.example.memo.HomeScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                createViewModel = createViewModel
            ) // HomeScreen이 전체 채우도록
        }
        composable("AddMemo") {
            // ProfileScreen에 navController와 modifier 전달
            com.example.memo.AddMemoScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                createViewModel = createViewModel
            ) // ProfileScreen이 전체 채우도록
        }
        composable(
            route = "detail/{memoId}", // memoId를 경로의 일부로 받음
            arguments = listOf(navArgument("memoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val memoId = backStackEntry.arguments?.getLong("memoId")

            if (memoId != null) {
                DetailScreen(
                    navController = navController,
                    createViewModel = createViewModel,
                    memoId = memoId
                )
            } else {
                // memoId가 없는 경우의 처리 (예: 홈으로 리다이렉트 또는 오류 메시지)
                navController.popBackStack("home", false)
            }
        }

    }
}


//------------------------------------프리뷰---------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "App Main Preview (Home)")
@Composable
fun AppMainPreview() {
    MemoTheme {
        val navController = rememberNavController()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xFF6A0DAD),   // 위쪽 보라색
                        0.65f to Color(0xFFD8BFD8), // 중간 연보라
                        1.0f to Color(0xFFE4E724)  // 아래쪽 하늘색
                    )
                )
        ) {
            Scaffold(
                containerColor = Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("프리뷰") },
                        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Filled.Menu, "메뉴") } },
                        actions = { IconButton(onClick = {}) { Icon(Icons.Filled.Search, "검색") } },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Transparent,
                            titleContentColor = androidx . compose . ui . graphics . Color.White
                        )
                    )
                }
            ) { innerPadding ->
                // 프리뷰에서는 NavHost를 직접 사용하거나 특정 화면을 렌더링 할 수 있습니다.
                // 여기서는 AppNavigation을 사용하여 시작 화면(HomeScreen)을 보여줍니다.
                com.example.memo.AppNavigation(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    MemoTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            com.example.memo.HomeScreen(
                navController = rememberNavController(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "AddMemoScreen Preview")
@Composable
fun AddMemoScreenPreview() {
    MemoTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            com.example.memo.AddMemoScreen(
                navController = rememberNavController(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "DetailScreen Preview")
@Composable
fun DetailScreenPreview() {
    MemoTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            com.example.memo.DetailScreen(
                navController = rememberNavController(),
                modifier = Modifier.fillMaxSize(),
                memoId = 1L
            )
        }
    }
}