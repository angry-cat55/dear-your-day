# Dear Your Day (하루 보관함)

> **"오늘의 감정을 기록하면, AI가 따뜻한 공감을 전달합니다."**
>
> Google Gemma 3 AI를 활용한 감정 분석 데일리 다이어리 애플리케이션입니다.

  

## poject Overview
**Dear Your Day**는 단순한 기록을 넘어, 사용자의 **감정을 공감해주는 AI 다이어리 친구 서비스**입니다.
사용자가 하루의 일과와 기분을 기록하면, **AI**가 내용을 분석하여 상황에 맞는 위로와 격려, 혹은 축하의 코멘트를 남겨줍니다. **누군가 내 하루를 들어주길 바라는 마음**을 기술로 구현했습니다.

* **개발 기간**: 2026.01.02 ~ 진행 중
* **개발 인원**: 1인 (Android & Backend Full-stack)
* **핵심 가치**: 
    1. **Empathy (공감)**: AI를 통한 정서적 지지 제공
    2. **Archive (기록)**: 월별 일기 작성 기록을 한눈에 파악
    3. **Simplicity (간편함)**: 직관적인 UI로 매일 쓰는 습관 형성

  

## Tech Stack

### Android (Client)
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose - **Compose UI**
* **Network**: Retrofit2
* **Architecture**: MVVM Pattern, Repository Pattern

### Backend (Server)
* **Framework**: Spring Boot 3.5.9
* **Language**: Java 21
* **Database**: MySQL, JPA
* **AI Engine**: Google Gemma-3-4b

  

## Key Features

### 1. AI 공감 코멘트
* **Google AI API 연동**: 사용자가 작성한 일기 텍스트를 서버로 전송하면, Google Gemma 3 모델이 문맥과 감정을 심층 분석합니다.
* **맞춤형 피드백**: 단순한 요약이 아닌, "오늘 정말 고생 많으셨네요", "그런 일이 있었다니 정말 속상했겠어요"와 같은 **공감 코멘트**를 생성하여 DB에 저장하고 앱에 표시합니다.

### 2. 데일리 감정 일기
* **Mood Selector**: 그날의 대표 감정을 아이콘(기쁨, 슬픔, 평범 등)으로 선택하여 직관적으로 기록합니다.
* **Focus on Writing**: 불필요한 요소를 배제하고 글쓰기에만 집중할 수 있는 깔끔한 Compose UI 환경을 제공합니다.
* **Secure Storage**: 작성된 일기는 MySQL 서버에 저장됩니다.

### 3. 감정 아카이브
* 한 달 동안의 내 감정 변화를 한눈에 볼 수 있는 **모아보기(Calendar) 기능**을 제공합니다.
* **Visualized Emotion**: 날짜별로 내가 선택했던 감정 아이콘이나 테마 컬러가 표시되어, "이번 달은 행복한 날이 많았구나"를 시각적으로 인지할 수 있습니다.
* **Smart Navigation**: 미래 날짜 작성 방지 및 부드러운 월별 이동 UX를 구현했습니다.

### 4. 종합 공감 기능
* **Holistic Analysis**: 하루하루의 단편적인 기록을 넘어, 일정 기간(90일) 동안 축적된 사용자의 일기에 담긴 감정의 흐름을 AI가 종합적으로 분석합니다.
* **Deep Insight**: "이번 달은 초반에 힘들었지만, 후반으로 갈수록 안정을 찾으셨네요."와 같이 **깊이 있는 통찰과 위로**를 건넵니다.
* **Personalized Report**: 단순 통계가 아닌, 텍스트 기반의 정성적인 리포트를 통해 사용자가 자신의 멘탈 케어 상태를 점검할 수 있도록 돕습니다.
