🍽️ **Eaty - 스마트한 음식점 검색 & 리뷰 공유 애플리케이션**  

> **더 스마트한 음식점 탐색!**  
> 현재 위치 기반으로 맛집을 검색하고, 리뷰를 확인하며, 카카오톡 공유까지 한 번에!  

──────────────────────────────────────────────────────────  

## 🔍 **주요 기능**  

### 1️. **스마트한 음식점 검색 & 추천**  
- **현재 위치 기반 음식점 검색** (거리순 정렬)  
- **Kakao Local API 활용** → 가까운 거리순으로 최적의 음식점 추천  
- **자동 완성 검색 지원** → 사용자가 입력한 키워드에 맞춰 추천 음식 제공  
  - 예: `'돼'` 입력 → `'돼지갈비', '돼지국밥'` 등 연관 음식 제안  

──────────────────────────────────────────────────────────  
  
### 2️. **리뷰 & 평점 관리**  
- **Kakao Blog & Local API 연동** → 음식점별 평점, 이용시간, 메뉴, 리뷰 제공  
- **사용자 리뷰 작성 기능** → 방문한 음식점에 솔직한 후기 작성 가능  
- **Firestore 실시간 데이터 반영** → 댓글 작성 후 즉시 업데이트  

──────────────────────────────────────────────────────────  

### 3️. **즐겨찾기 기능**  
- **자주 가는 음식점 즐겨찾기 등록 및 해제**  
- **Firebase Firestore 연동** → 사용자별 즐겨찾기 목록 저장 및 관리  
- **즐겨찾기 목록에서 음식점 정보 간편 확인**  

──────────────────────────────────────────────────────────  

### 4️. **내 주변 정보 탐색**  
- **음식점 위치 기반 날씨 및 교통 정보 제공**  
  - 식당 방문 계획을 세우고, 주변 교통편을 쉽게 확인  
- **OpenWeather API & Kakao Local API 연동** → 실시간 날씨 및 교통 정보 제공  

──────────────────────────────────────────────────────────  

### 5️. **카카오톡 공유 기능**  
- **음식점 정보를 카카오톡으로 간편 공유**  
- **Kakao Share SDK 활용** → 맞춤형 링크 생성 및 공유  
- **앱 연동 스마트 공유 기능 지원**  

──────────────────────────────────────────────────────────  

### 6️. **사용자 프로필 & 마이페이지**  
- **닉네임 및 프로필 사진 변경 지원**  
- **Firebase Authentication 연동** → Google 계정 로그인 지원  
- **FAQ 및 공지사항 확인 가능**  

──────────────────────────────────────────────────────────  

## 🛠 **기술 스택**  

| **Category**            | **Technologies**  |
|-------------------------|-----------------------------------------------|
| **Architecture**        | Multi Module, Clean Architecture, MVVM, SAA  |
| **DI**                 | Dagger-Hilt                                  |
| **Networking**         | Retrofit, OkHttp, Interceptor, Gson          |
| **Database**           | RoomDB                                       |
| **Data Storage**       | Firebase Firestore                           |
| **UI/UX**              | Glide, Material 3, ViewPager2 + TabLayout   |
| **Navigation**         | Navigation Graph                             |
| **Location & Maps**    | Naver Map API, FusedLocationProviderClient  |
| **API**                | Kakao Local API, Kakao BLOG API, Kakao Share API, OpenWeather API |
| **Permissions**        | Runtime Permission                           |
| **UI State**           | StateFlow, LiveData                         |
| **Login & Social**     | Firebase Google Login, Kakao Share          |
| **Concurrency**        | Thread, Coroutine, Flow                     |
| **Data Binding**       | ViewBinding, FlowBinding                    |

──────────────────────────────────────────────────────────  

## 🚀 **주요 기술 적용**  

### **최적화 & 성능 향상**  
✔ **멀티 모듈 프로젝트 적용** → 유지보수 용이 & 빌드 속도 최적화  
✔ **코드 모듈화 (DI - Hilt 적용)** → 종속성 주입을 통해 의존성 관리  
✔ **StateFlow 기반 UI 상태 관리** → 반응형 데이터 처리 및 UI 업데이트  
✔ **Timber 활용한 로깅 시스템 구축** → Debug 모드에서만 로그 출력  

### **비동기 처리 & 데이터 흐름 최적화**  
✔ **Flow & Coroutine 적용** → 비동기 데이터 스트림 활용  
✔ **Repository 패턴 적용** → Local & Remote 데이터 일관성 유지  
✔ **LiveData & MutableStateFlow 사용** → UI 상태 최적화  

──────────────────────────────────────────────────────────  

📌 **캡처 GIF**:  
## 홈화면

| **시작화면** | **로그인 화면** |
|----------------------------------|----------------------------------|
| ![starting](https://github.com/user-attachments/assets/dbb425aa-a2eb-4973-a9a5-fbe97b8560dc) |![googleLogin](https://github.com/user-attachments/assets/85778562-209d-4374-9e01-e6396b78382a)|


| **홈화면 카테고리별 리스트** | **리스트를 통해 Detail 진입** | **검색창(추천어)으로 음식점 검색** |
|----------------------------------|----------------------------------|--------------------------------|
| ![home_category_list](https://github.com/user-attachments/assets/ceca3c52-59e6-4861-b6d3-51417d530a2f) | ![entering_the_detail_with_list](https://github.com/user-attachments/assets/3287f423-f3fc-46ce-84e0-a07855814309) | ![searching_with_suggestion](https://github.com/user-attachments/assets/96cb54d8-0194-41da-b2d5-3e52374af4b3) |

| **현재 위치로부터 식당까지 네비 <br> (전화, 카카오 맵 리뷰, 네이버블로그)** | **어플 내 리뷰달기** | **음식점 주변의 환경(날씨, 지하철)**|
|----------------------------------|----------------------------------|---------------------------------------------------------------------|
| ![call_kakaomap_naverblog](https://github.com/user-attachments/assets/8d67f7d5-b5dc-4aae-ba34-e0752bbd60e5) |![review](https://github.com/user-attachments/assets/969bd27f-133a-47fe-b7f1-45e3180093be) | ![surrounding](https://github.com/user-attachments/assets/a6dd8f66-7fbf-4122-abf8-8575d7c38407) |

## 찜화면

| **마음에 드는 음식점 찜하기(등록,해제)** |
|----------------------------------|
| ![favorites](https://github.com/user-attachments/assets/bfcf78ed-7d1f-4faa-9e8a-62dfbb141fee) |

## Map 화면

| **Map 이용 음식점 찾기 <br> (필터, 클러스터링, 마커)** | **마커를 통해 음식점 Detail 진입** |
|----------------------------------|----------------------------------|
| ![navermap_filter_cluster](https://github.com/user-attachments/assets/eada105b-cc6f-4f7e-8778-6af1545e01d7) |![entering_the_detail_with_marker](https://github.com/user-attachments/assets/6de47a2b-3830-4090-b765-b097f4707577) |

## 마이페이지 화면

| **FAQ & Notice** | **프로필 수정** | **로그아웃** |
|----------------------------------|----------------------------------|------------------------------------------------|
| ![starting](https://github.com/user-attachments/assets/dbb425aa-a2eb-4973-a9a5-fbe97b8560dc) |![edit_profile](https://github.com/user-attachments/assets/b741abf0-eb45-4d07-9234-968e004daefc) | ![signout](https://github.com/user-attachments/assets/2f5d1b6f-7894-4003-9683-8c001aeb25e6) |

