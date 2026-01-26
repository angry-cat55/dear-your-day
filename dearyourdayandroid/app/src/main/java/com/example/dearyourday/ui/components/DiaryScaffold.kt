package com.example.dearyourday.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

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
                            // 메뉴 클릭 시 이동 및 서랍 닫기
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                // TODO: 오늘의 일기 화면 제외 스택 쌓기 방지 로직 구현
                                launchSingleTop = true
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
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        // 햄버거 버튼
                        IconButton(onClick = {
                            scope.launch { drawerState.open() } // 서랍 열기
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "메뉴")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // 3. 화면을 여기에 표시
            content(innerPadding)
        }
    }
}