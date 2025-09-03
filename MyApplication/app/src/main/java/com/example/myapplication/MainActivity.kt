package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // layout 관련 import 통합
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // material3 관련 import 통합
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController // NavHostController import
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.Home // Home 아이콘 import
import androidx.compose.material.icons.filled.Star // Star 아이콘 import
import androidx.compose.material.icons.filled.Person // Person 아이콘 import
import androidx.navigation.NavDestination.Companion.hierarchy // hierarchy import (이미 있을 수 있음)


data class ScreenInfo(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
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
                                title = { Text("My App") },
                                navigationIcon = {IconButton(onClick = { /* TODO */ }) {
                                        Icon(Icons.Filled.Menu, "메뉴")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { }) {
                                        Icon(Icons.Filled.Search, "검색")
                                    }
                                    IconButton(onClick = { navController.navigate("login") }) {
                                        Icon(Icons.Filled.ExitToApp, "로그인")
                                    }
                                },

                                colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Transparent
                                        )
                            )
                        },
                        bottomBar = {
                            AppBottomNavigationBar(navController = navController)
                        },

                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        // NavHost를 Scaffold의 content로 설정
                        AppNavigation(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding) // TopAppBar 패딩 적용
                        )
                    }
                }
            }
        }
    }
}
// BottomNavigationBar 컴포저블 정의
@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    // 하단 네비게이션 아이템들 정의
    val items = listOf(
        ScreenInfo("home", "홈", Icons.Filled.Home),
        ScreenInfo("counter/DefaultUser", "카운터", Icons.Filled.Star), // Counter 경로에 기본 사용자 이름 전달
        ScreenInfo("profile/DefaultUser", "프로필", Icons.Filled.Person)  // Profile 경로에 기본 사용자 이름 전달
    )

    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.8f) // 반투명 흰색 배경 (원하는 색상으로 조절)
        // contentColor = MaterialTheme.colorScheme.onSurface // 아이콘/텍스트 색상 (기본값)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any {
                    // 현재 목적지 경로가 아이템 경로와 시작 부분이 일치하는지 확인
                    // 예: "counter/{userName}"과 "counter/DefaultUser"는 매칭됨
                    it.route?.startsWith(screen.route.substringBefore('/')) == true || // 기본 경로 부분 비교
                            it.route == screen.route // 전체 경로 비교
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // 백스택 상단까지 pop하여 동일한 목적지가 여러 개 쌓이는 것을 방지
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 이미 선택된 아이템을 다시 클릭했을 때 동일한 화면을 다시 로드하지 않도록 설정
                        launchSingleTop = true
                        // 이전에 방문했던 화면의 상태를 복원
                        restoreState = true
                    }
                }                // colors = NavigationBarItemDefaults.colors(...) // 아이템별 색상 커스텀 가능
            )
        }
    }
}
// NavHost 설정을 담당하는 컴포저블
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home", // 시작 화면은 "home"
        modifier = modifier // Scaffold로부터 받은 패딩 적용
    ) {
        composable("home") {
            // HomeScreen에 navController와 modifier 전달
            HomeScreen(navController = navController, modifier = Modifier.fillMaxSize()) // HomeScreen이 전체 채우도록
        }
        composable("counter") {
            // ProfileScreen에 navController와 modifier 전달
            CounterScreen(navController = navController, modifier = Modifier.fillMaxSize()) // ProfileScreen이 전체 채우도록
        }

        composable("profile/{userName}") {
            backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
            ProfileScreen(navController = navController, name = userName, modifier = Modifier.fillMaxSize()) // ProfileScreen이 전체 채우도록
        }
        composable("login") {
            // HomeScreen에 navController와 modifier 전달
            LoginScreen(navController = navController, modifier = Modifier.fillMaxSize()) // HomeScreen이 전체 채우도록
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var showTextField by remember { mutableStateOf(false) }
    val id = navController.currentBackStackEntry?.savedStateHandle?.get<String>("id")
    val pw = navController.currentBackStackEntry?.savedStateHandle?.get<String>("pw")
    var userName by remember { mutableStateOf("") }// 초기 텍스트 변경

    Column(
        modifier = modifier.padding(16.dp), // 외부에서 fillMaxSize가 적용되었으므로 내부 패딩만
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.images), // 'images.png' 또는 해당 파일명 확인
            contentDescription = "My wonderful image",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Text("ID: $id", color = Color.White, fontSize = 18.sp)
        Text("PW: $pw", color = Color.White, fontSize = 18.sp)
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
fun CounterScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var count by remember { mutableStateOf(0) }
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
                count++
                // 프로필 화면에서 다시 프로필 버튼을 누르는 경우 (예시)
                println("+1")

            }) {
                Text("증가")
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                count--
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

@Composable
fun ProfileScreen(navController: NavHostController, name: String?, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$name 의 프로필", color = Color.White, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(32.dp))



        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = {
            navController.popBackStack() // 이전 화면 (홈)으로 돌아가기

            // 또는 navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }) {
            Text("돌아가기")
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var ID by remember { mutableStateOf("") }
    var PW by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("로그인", color = Color.Black, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = ID,
            onValueChange = { newText -> ID = newText },
            label = { Text("ID") }
        )
        TextField(
            value = PW,
            onValueChange = { newText -> PW = newText },
            label = { Text("PW") }
        )

        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = {
            navController.navigate("home") // 이전 화면 (홈)으로 돌아가기
            navController.currentBackStackEntry?.savedStateHandle?.set("id", ID)
            navController.currentBackStackEntry?.savedStateHandle?.set("pw", PW)

            // 또는 navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }, modifier = Modifier.width(270.dp)) {
            Text("로그인")
        }
        Button(onClick = {
            navController.popBackStack() // 이전 화면 (홈)으로 돌아가기

            // 또는 navController.navigate("home") { popUpTo("home") { inclusive = true } }
        }, modifier = Modifier.width(270.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Text("돌아가기")
        }
    }
}

// --- Preview 함수들 ---

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "App Main Preview (Home)")
@Composable
fun AppMainPreview() {
    MyApplicationTheme {
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
                AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            HomeScreen(navController = rememberNavController(), modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showBackground = true, name = "CounterScreen Preview")
@Composable
fun CounterScreenPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            CounterScreen(navController = rememberNavController(), modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showBackground = true, name = "CounterScreen Preview")
@Composable
fun ProfileScreenPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            CounterScreen(navController = rememberNavController(), modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showBackground = true, name = "CounterScreen Preview")
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) { // 배경색 지정
            LoginScreen(navController = rememberNavController(), modifier = Modifier.fillMaxSize())
        }
    }
}

// Greeting 관련 함수는 HomeScreen으로 기능이 이전되었으므로, 필요 없다면 삭제하거나
// 별도의 컴포넌트 테스트용으로 남겨둘 수 있습니다.
// 여기서는 주석 처리합니다.
/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) { ... }

@Preview(showBackground = true, name = "Greeting Component Only")
@Composable
fun GreetingComponentPreview() { ... }
*/

