package com.example.dearyourday.ui.screens.diary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dearyourday.data.model.MonthlyDiariesViewModel
import com.example.dearyourday.data.model.Mood
import com.example.dearyourday.data.model.diary.DiaryMonthResponse
import com.example.dearyourday.ui.components.DiaryScaffold
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyDiariesScreen(
    navController: NavController,
    viewModel: MonthlyDiariesViewModel = viewModel()
) {
    // 토스트 전용 메세지 저장 변수
    val context = LocalContext.current
    // 오늘 날짜 변수
    val today = remember { LocalDate.now() }

    // 월별 일기 뷰모델 데이터
    val diariesMap by viewModel.monthlyDiaries.collectAsState()

    // 캘린더 설정 변수
    // 현재 달
    val currentMonth = remember { YearMonth.now() }
    // 몇 개월 전 달력부터 시작할지
    val startMonth = remember { currentMonth.minusMonths(120) } // 10년 전
    // 몇 개월 후 달력까지 조회할지
    val endMonth = remember { currentMonth.plusMonths(1) } // 1달 뒤
    // 무슨 요일부터 시작할지
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // 사용자의 핸드폰 국가 설정에 맞춰서

    // 달력 상태 변수들
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth, // 달력 상 표시 달
        firstDayOfWeek = firstDayOfWeek
    )

    // 달력 스크롤 감지 및 데이터 갱신
    LaunchedEffect(state.firstVisibleMonth.yearMonth) {
        viewModel.updateMonth(state.firstVisibleMonth.yearMonth)
    }

    DiaryScaffold(
        navController = navController,
        title = "하루 보관함"
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            HorizontalCalendar(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                // 헤더 영역 (년도와 요일 표시)
                monthHeader = { month ->
                    Column {
                        // 년도
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${month.yearMonth.year}.${String.format("%02d", month.yearMonth.monthValue)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // 요일
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val days = listOf("일", "월", "화", "수", "목", "금", "토")
                            days.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    color = when (dayName) {
                                        "일" -> Color.Red
                                        "토" -> Color.Blue
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                },
                // 날짜 영역
                dayContent = { day ->
                    val diary = diariesMap[day.date] // 해당 날짜의 일기 데이터
                    val isFuture = day.date.isAfter(today) // 오늘날 기준으로 미래인지
                    val hasDiary = diary != null // 해당 날짜에 쓴 일기가 있는지

                    // 1. 글자 색상 결정
                    val textColor = when {
                        isFuture -> Color.LightGray
                        hasDiary -> Color.White
                        else -> Color.Black
                    }

                    // 2. 배경 색상 결정
                    val backgroundColor = if (hasDiary) {
                        Color(0xFF6A5AE0).copy(alpha = 0.5f)
                    } else {
                        Color.Transparent
                    }
                    
                    // 3. 공통 테투리 모양 설정
                    val shape = RoundedCornerShape(8.dp)

                    // 날짜 칸 디자인
                    Column(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                if (isFuture) { // 미래일 경우
                                    Toast.makeText(context, "일기를 미리 쓸 수 없어요.", Toast.LENGTH_SHORT).show()
                                } else { // 미래가 아닐 경우
                                    navController.navigate("main_diary/${day.date}")
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (day.position == DayPosition.MonthDate) { // 이번달에 혹한 날짜일 때만 화면에 그리기
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = backgroundColor,
                                        shape = shape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${day.date.dayOfMonth}",
                                    color = textColor,
                                    fontWeight = if (hasDiary) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            // 하단 해당 월별 일기 리스트
            Text(
                text = "${state.firstVisibleMonth.yearMonth.monthValue}월의 하루들",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(all = 16.dp)
            )

            // 일기 순서 정렬 (오래된 순)
            val sortedDiaries = diariesMap.values.sortedBy { it.writtenDate }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 기록된 일기가 없을 경우
                if (sortedDiaries.isEmpty()) {
                    item {
                        Text(
                            text = "아직 기록된 하루가 없어요.",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
                // 기록된 일기가 있을 경우
                else {
                    items(sortedDiaries) { diary ->
                        DiaryListItem(diary) {
                            navController.navigate("main_diary/${diary.writtenDate}")
                        }
                    }
                }
            }
        }
    }
}

// 전달 받은 일기 데이터로 Card를 그리는 메소드
@Composable
fun DiaryListItem(diary: DiaryMonthResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 쓰여진 일기의 날짜
            val date = LocalDate.parse(diary.writtenDate)

            // N일
            Text(
                text = "${date.dayOfMonth}일",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 해당 일기에 저장된 기분 이모지
            Text(text = Mood.from(diary.moodCode)?.emoji ?: "?")

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = "-   일기 확인하러 가기", color = Color.Gray, fontSize = 14.sp)
        }
    }
}