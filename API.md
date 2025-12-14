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
  "lifeCycle": "청년",
  "householdStatus": "다자녀",
  "interestTheme": "생활지원",
  "age": 29,
  "sidoName": "서울특별시",
  "sigunguName": "강남구"
}
```
- 허용 값
  - `lifeCycle`: 영유아, 아동, 청소년, 청년, 중장년, 노년, 임신·출산
  - `householdStatus`: 다문화·탈북민, 다자녀, 보훈대상자, 장애인, 저소득, 한부모·조손
  - `interestTheme`: 신체건강, 정신건강, 생활지원, 주거, 일자리, 문화·여가, 안전·위기, 임신·출산, 보육, 교육, 입양·위탁, 보호·돌봄, 서민금융, 법률
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
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
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
  "lifeCycle": "청년",
  "householdStatus": "다자녀",
  "interestTheme": "생활지원",
  "age": 29,
  "sidoName": "서울특별시",
  "sigunguName": "강남구",
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
  "lifeCycle": "중장년",
  "householdStatus": "보훈대상자",
  "interestTheme": "주거",
  "age": 35,
  "sidoName": "경기도",
  "sigunguName": "수원시"
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
  "serviceId": "WF12345",
  "serviceName": "청년 주거 지원 사업",
  "serviceSummary": "청년층의 주거 안정을 위한 지원",
  "aiSummary": "만 19-34세 청년에게 월세 보증금 지원",
  "ctpvNm": "서울특별시",
  "sggNm": "강남구",
  "bizChrDeptNm": "주거복지과",
  "supportType": "현금",
  "supportCycle": "월별",
  "applicationMethod": "온라인",
  "lifeCycleArray": "[\"004\"]",
  "targetArray": "[\"050\"]",
  "interestThemeArray": "[\"040\"]",
  "supportTargetContent": "만 19-34세 청년 중 소득 기준 충족자",
  "selectionCriteria": "중위소득 150% 이하",
  "serviceContent": "월세 최대 20만원, 보증금 최대 1000만원 지원",
  "applicationMethodContent": "복지로 홈페이지에서 온라인 신청",
  "inquiryCount": 1523,
  "detailLink": "https://example.com/welfare/12345",
  "lastModifiedDate": "2024-12-01",
  "serviceType": "LOCAL",
  "serviceUrl": "https://example.com",
  "site": "복지로",
  "contact": "02-1234-5678",
  "department": "주거복지과",
  "organization": "서울특별시청",
  "baseYear": 2024,
  "organizationName": "서울시 주거복지센터",
  "projectStartDate": "2024-01-01",
  "projectEndDate": "2024-12-31",
  "requiredDocuments": "신분증, 소득증빙서류",
  "etc": "기타 상세 정보",
  "householdStatus": "청년 1인 가구"
}
```
- 참고: 이 API 호출 후 사용자가 로그인한 상태라면 `POST /recent-views/{serviceId}`도 함께 호출 필요

## 토큰 포맷
- Access Token: JWT, 인증이 필요한 요청에 `Authorization: Bearer {accessToken}` 헤더 사용
- Refresh Token: 토큰 재발급 시 사용
