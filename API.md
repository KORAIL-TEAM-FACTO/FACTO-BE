# API 문서 (요약)

## 공통 정보
- Base URL: `/`
- 요청/응답: `application/json`
- 인증: JWT `Authorization: Bearer {accessToken}` 헤더 사용(회원가입/로그인 제외)

## 회원가입 - **POST** `/users/register`
- 설명: 신규 사용자 등록, 추가 프로필 정보 포함
- 요청 본문 예시:
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!",
  "name": "홍길동",
  "life_cycle": "청년",
  "household_status": "다자녀",
  "interest_theme": "생활지원",
  "age": 29,
  "sido_name": "서울특별시",
  "sigungu_name": "강남구"
}
```
- 허용 값
  - `life_cycle`: 영유아, 아동, 청소년, 청년, 중장년, 노년, 임신·출산
  - `household_status`: 다문화·탈북민, 다자녀, 보훈대상자, 장애인, 저소득, 한부모·조손
  - `interest_theme`: 신체건강, 정신건강, 생활지원, 주거, 일자리, 문화·여가, 안전·위기, 임신·출산, 보육, 교육, 입양·위탁, 보호·돌봄, 서민금융, 법률
- 응답: `201 Created`, 바디 없음

## 로그인 - **POST** `/users/login`
- 설명: 이메일/비밀번호로 로그인 후 토큰 발급
- 요청 본문 예시:
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!"
}
```
- 응답 예시:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 내 정보 조회 - **GET** `/users/me`
- 설명: 현재 인증된 사용자의 정보 조회
- 헤더: `Authorization: Bearer {accessToken}`
- 응답 예시:
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "life_cycle": "청년",
  "household_status": "다자녀",
  "interest_theme": "생활지원",
  "age": 29,
  "sido_name": "서울특별시",
  "sigungu_name": "강남구",
  "role": "USER"
}
```

## 프로필 수정 - **PATCH** `/users/me`
- 설명: 현재 인증된 사용자의 프로필 정보 수정
- 헤더: `Authorization: Bearer {accessToken}`
- 요청 본문 예시:
```json
{
  "name": "김철수",
  "life_cycle": "중장년",
  "household_status": "보훈대상자",
  "interest_theme": "주거",
  "age": 35,
  "sido_name": "경기도",
  "sigungu_name": "수원시"
}
```
- 응답: `204 No Content`, 바디 없음

## 즐겨찾기 추가 - **POST** `/bookmarks/{welfareServiceId}`
- 설명: 복지 서비스를 즐겨찾기에 추가
- 헤더: `Authorization: Bearer {accessToken}`
- Path 파라미터: `welfareServiceId` - 복지 서비스 ID
- 응답: `201 Created`, 바디 없음

## 즐겨찾기 삭제 - **DELETE** `/bookmarks/{welfareServiceId}`
- 설명: 복지 서비스를 즐겨찾기에서 제거
- 헤더: `Authorization: Bearer {accessToken}`
- Path 파라미터: `welfareServiceId` - 복지 서비스 ID
- 응답: `204 No Content`, 바디 없음

## 내 즐겨찾기 목록 조회 - **GET** `/bookmarks`
- 설명: 현재 사용자의 즐겨찾기 목록 조회 (최신순)
- 헤더: `Authorization: Bearer {accessToken}`
- 응답 예시:
```json
[
  {
    "id": 1,
    "welfare_service_id": "WF12345",
    "created_at": "2024-12-14T10:30:00"
  },
  {
    "id": 2,
    "welfare_service_id": "WF67890",
    "created_at": "2024-12-13T15:20:00"
  }
]
```

## 즐겨찾기 여부 확인 - **GET** `/bookmarks/{welfareServiceId}/check`
- 설명: 특정 복지 서비스가 즐겨찾기에 추가되어 있는지 확인
- 헤더: `Authorization: Bearer {accessToken}`
- Path 파라미터: `welfareServiceId` - 복지 서비스 ID
- 응답: `true` 또는 `false`

## 최근 본 복지 서비스 추가 - **POST** `/recent-views/{welfareServiceId}`
- 설명: 복지 서비스를 최근 본 목록에 추가 (최대 100개, 중복 시 조회 시간 갱신)
- 헤더: `Authorization: Bearer {accessToken}`
- Path 파라미터: `welfareServiceId` - 복지 서비스 ID
- 응답: `201 Created`, 바디 없음

## 최근 본 복지 서비스 목록 조회 - **GET** `/recent-views`
- 설명: 현재 사용자의 최근 본 복지 서비스 목록 조회 (최신순)
- 헤더: `Authorization: Bearer {accessToken}`
- Query 파라미터: `limit` (선택, 기본값: 100, 최대: 100) - 조회 개수
- 응답 예시:
```json
[
  {
    "id": 1,
    "welfare_service_id": "WF12345",
    "viewed_at": "2024-12-14T10:30:00"
  },
  {
    "id": 2,
    "welfare_service_id": "WF67890",
    "viewed_at": "2024-12-14T09:15:00"
  }
]
```

## 인기 복지 서비스 TOP 10 조회 - **GET** `/recent-views/trending`
- 설명: 전체 사용자가 최근에 가장 많이 조회한 복지 서비스 TOP 10 (인증 불필요)
- Query 파라미터:
  - `days` (선택, 기본값: 7) - 최근 N일 기준
  - `limit` (선택, 기본값: 10, 최대: 100) - 조회 개수
- 응답 예시:
```json
[
  {
    "welfare_service_id": "WF12345",
    "view_count": 1523
  },
  {
    "welfare_service_id": "WF67890",
    "view_count": 1245
  },
  {
    "welfare_service_id": "WF11111",
    "view_count": 987
  }
]
```

## 복지 서비스 상세 조회 - **GET** `/welfare-services/{serviceId}`
- 설명: 복지 서비스 상세 정보 조회 (조회수 자동 증가, 인증 불필요)
- Path 파라미터: `serviceId` - 복지 서비스 ID
- 응답 예시:
```json
{
  "service_id": "WF12345",
  "service_name": "청년 주거 지원 사업",
  "service_summary": "청년층의 주거 안정을 위한 지원",
  "ai_summary": "만 19-34세 청년에게 월세 보증금 지원",
  "ctpv_nm": "서울특별시",
  "sgg_nm": "강남구",
  "biz_chr_dept_nm": "주거복지과",
  "support_type": "현금",
  "support_cycle": "월별",
  "application_method": "온라인",
  "life_cycle_array": "[\"004\"]",
  "target_array": "[\"050\"]",
  "interest_theme_array": "[\"040\"]",
  "support_target_content": "만 19-34세 청년 중 소득 기준 충족자",
  "selection_criteria": "중위소득 150% 이하",
  "service_content": "월세 최대 20만원, 보증금 최대 1000만원 지원",
  "application_method_content": "복지로 홈페이지에서 온라인 신청",
  "inquiry_count": 1523,
  "detail_link": "https://example.com/welfare/12345",
  "last_modified_date": "2024-12-01",
  "service_type": "LOCAL",
  "service_url": "https://example.com",
  "site": "복지로",
  "contact": "02-1234-5678",
  "department": "주거복지과",
  "organization": "서울특별시청",
  "base_year": 2024,
  "organization_name": "서울시 주거복지센터",
  "project_start_date": "2024-01-01",
  "project_end_date": "2024-12-31",
  "required_documents": "신분증, 소득증빙서류",
  "etc": "기타 상세 정보",
  "household_status": "청년 1인 가구"
}
```
- 참고: 이 API 호출 후 사용자가 로그인한 상태라면 `POST /recent-views/{serviceId}`도 함께 호출 필요

## 토큰 포맷
- Access Token: JWT, 인증이 필요한 요청에 `Authorization: Bearer {accessToken}` 헤더 사용
