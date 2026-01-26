package com.example.dearyourday.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 햄버거 메뉴 화면 이동 버튼 목록
data class DrawerMenuItem(val title: String, val route: String)
val menuItems = listOf(
    DrawerMenuItem("오늘의 하루", "main_diary"),
    DrawerMenuItem("하루 보관함", "monthly_diaries"),
    DrawerMenuItem("지금의 나", "diary_summary")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScaffold(
    navController: NavController,
    title: String, // 화면마다 바뀔 제목 ("2026.01.26", "오늘의 하루" 등)
    content: @Composable (PaddingValues) -> Unit
) {
    // 서랍이 열렸는지 닫혔는지 기억하는 변수
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 서랍을 열 때 애미메이션을 돌릴 객체
    val scope = rememberCoroutineScope()

    // 1. 전체를 감싸는 레이아웃
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { // 햄버거 눌렀을 때 보일 화면 디자인
            ModalDrawerSheet(
                modifier = Modifier
                    .width((250.dp)),
                drawerContainerColor = Color.White
            ) {
                Spacer(modifier = Modifier.height(24.dp)) // 상단 여백
                Text("너의 하루에게.", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
                HorizontalDivider()

                // 메뉴 목록 만들기
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (item.route == "main_diary") {
                                val today = java.time.LocalDate.now().toString()
                                navController.navigate("main_diary/$today") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // 2. 실제 화면 내용 (Scaffold)
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 8.dp),
                    title = {
                        // 폰트 크기 통일을 위해 스타일 정의
                        val mainTitleStyle = MaterialTheme.typography.titleLarge

                        // 예외 케이스("하루 보관함", "지금의 나")인지 확인
                        val isSpecialTitle = title == "하루 보관함" || title == "지금의 나"

                        if (isSpecialTitle) {
                            // 예외 타이틀은 그대로 표시
                            Text(
                                text = title,
                                style = mainTitleStyle
                            )
                        } else {
                            // 그 외에는 쉼표(,)를 기준으로 분리
                            val parts = title.split(",")

                            if (parts.size >= 2) {
                                val mainTitle = parts[0] // "오늘의 하루"
                                // 날짜 포맷 변경 (2026-01-26 -> 2026.01.26)
                                val date = parts[1].replace("-", ".")

                                // Column을 사용하여 상하 배치 및 중앙 정렬
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // [중요] 위치 보정을 위한 투명 텍스트
                                    // 아래에 있는 날짜만큼 위에도 공간을 차지하게 하여, 메인 타이틀을 정중앙에 고정시킵니다.
                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Transparent
                                    )

                                    Text(
                                        text = mainTitle,
                                        style = mainTitleStyle // 메인 타이틀 스타일
                                    )

                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.bodySmall, // 날짜는 조금 작게
                                        color = Color.Gray // 날짜 색상을 연하게
                                    )
                                }
                            } else {
                                // 만약 쉼표가 없는 일반 텍스트가 들어왔을 경우 안전장치
                                Text(
                                    text = title,
                                    style = mainTitleStyle
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "메뉴")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // 3. 화면 표시
            content(innerPadding)
        }
    }
}