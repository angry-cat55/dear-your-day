# Dear Your Day (하루 보관함)

> **"오늘의 감정을 기록하면, AI가 따뜻한 공감을 전달합니다."**
>
> Google Gemma 3 AI를 활용한 감정 분석 데일리 다이어리 애플리케이션입니다.

![Generic badge](https://img.shields.io/badge/Project-Personal-blue.svg) ![Generic badge](https://img.shields.io/badge/Status-Finished-green.svg)

## 📌 Project Overview
**Dear Your Day**는 단순한 기록을 넘어, 사용자의 **감정을 공감해주는 AI 다이어리 친구 서비스**입니다.
사용자가 하루의 일과와 기분을 기록하면, **AI**가 내용을 분석하여 상황에 맞는 위로와 격려, 혹은 축하의 코멘트를 남겨줍니다. **누군가 내 하루를 들어주길 바라는 마음**을 기술로 구현했습니다.

* **개발 기간**: 2026.01.02 ~ 2026.03.01 (v1.0.0 배포 완료)
* **개발 인원**: 1인 (Android & Backend Full-stack)
* **핵심 가치**: 
    1. **Empathy (공감)**: AI를 통한 정서적 지지 제공
    2. **Archive (기록)**: 월별 일기 작성 기록을 한눈에 파악
    3. **Simplicity (간편함)**: 직관적인 UI로 매일 쓰는 습관 형성

## 🛠 Tech Stack

| 분야 | 기술 스택 | 비고 |
| :--- | :--- | :--- |
| **Android (Client)** | ![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=flat-square&logo=kotlin&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white) | MVVM & Repository Pattern, Retrofit2 |
| **Backend (Server)** | ![Java 21](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white) | RESTful API 설계 |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white) | Spring Data JPA |
| **AI Engine** | ![Google Gemma 3](https://img.shields.io/badge/Google_Gemma_3-8E75B2?style=flat-square&logo=googlegemini&logoColor=white) | Gemma-3-4b 일기 분석 및 텍스트 생성 |
| **DevOps & Infra** | ![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white) ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black) | AWS EC2/RDS, CI/CD 파이프라인 구축 |

---

## 🏗 System Architecture

<img width="2259" height="1133" alt="Architecture_diagram" src="https://github.com/user-attachments/assets/23fcc961-ee34-4a25-93c2-5d0e1668ae50" />

---

## 💡 Key Features

### 1. AI 공감 코멘트
* **Google AI API 연동**: 사용자가 작성한 일기 텍스트를 서버로 전송하면, Google Gemma 3 모델이 문맥과 감정을 심층 분석합니다.
* **맞춤형 피드백**: 단순한 요약이 아닌, "오늘 정말 고생 많으셨네요", "그런 일이 있었다니 정말 속상했겠어요"와 같은 공감 코멘트를 생성하여 DB에 저장하고 앱에 표시합니다.

### 2. 데일리 감정 일기
* **Mood Selector**: 그날의 대표 감정을 아이콘으로 선택하여 직관적으로 기록합니다.
* **Focus on Writing**: 불필요한 요소를 배제하고 글쓰기에만 집중할 수 있는 깔끔한 Compose UI 환경을 제공합니다.
* **Secure Storage**: 작성된 일기는 AWS 환경에 구축된 MySQL 서버에 안전하게 저장됩니다.

### 3. 일기 아카이브
* **Calendar**: 한 달 동안의 내 감정 변화를 한눈에 볼 수 있는 **모아보기 기능**을 제공합니다.
* **Visualized Emotion**: 일기를 작성한 날짜별로 선택했던 감정 아이콘이 표시되어, 감정의 흐름을 시각적으로 인지할 수 있습니다.

### 4. 종합 공감 기능
* **Holistic Analysis**: 90일 동안 축적된 사용자 일기 중 최대 30개를 모아서, 그 안에 담긴 감정의 흐름을 AI가 종합적으로 분석합니다.
* **Deep Insight**: "이번 달은 초반에 힘들었지만, 후반으로 갈수록 안정을 찾으셨네요."와 같이 깊이 있는 통찰과 위로를 건넵니다.

---

## ⚙️ DevOps & Deployment
단순한 로컬 개발에 그치지 않고, 실제 서비스 운영 환경을 고려한 인프라 및 자동화 파이프라인을 구축했습니다.

* **AWS 클라우드 인프라 구축**: 백엔드 서버를 AWS 환경에 배포하여 언제든 클라이언트와 안정적으로 통신할 수 있는 실서비스 환경을 마련했습니다.
* **CI/CD 파이프라인 자동화**: 잦은 테스트 버전 배포로 인한 리소스와 시간 낭비를 줄이기 위해, GitHub Actions와 Firebase App Distribution을 연동했습니다. 버전 태그 푸시 시 자동으로 앱이 빌드되고 테스터에게 배포되는 파이프라인을 구축하여 생산성을 극대화했습니다.

---

## 📈 User Feedback & Future Work
v1.0.0 배포 후 실제 타겟 사용자들을 대상으로 테스트를 진행하였으며, 수집된 피드백을 바탕으로 다음 버전의 개선 과제를 도출했습니다.

**1. 핵심 가치 검증 (Positive Feedback)**
* "아무에게도 보여주고 싶지 않지만 누군가 공감해 주었으면 하는 마음"이라는 기획 의도가 실제 사용자들에게 높은 만족도를 주었음을 확인했습니다.

**2. UI/UX 사용성 개선 (UI/UX Improvements)**
* 감정 선택 이모지의 배열을 보다 직관적으로 재구성
* 캘린더 이동 시 '년/월' 단위로 한 번에 건너뛸 수 있는 Navigation 기능 추가
* 로그인 화면 내 비밀번호 가시성(Show/Hide) 토글 버튼 도입

**3. AI 모델 고도화 (AI Enhancements)**
* AI의 응답이 다소 보편적이라는 피드백을 수용하여, 프롬프트 엔지니어링을 통해 답변의 구체성과 개인화 수준을 높일 예정입니다.
* 사용자가 AI에게 이름을 붙여주고 친밀감을 형성할 수 있도록 'AI 페르소나 설정' 기능을 DB와 연동하여 기획 중입니다.

---

## 📞 Contact
* **Author:** Ji-Hwan Yu
* **Email:** applewlghks321@gmail.com
