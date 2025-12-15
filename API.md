# API 문서 (요약)

## 공통 정보
- Base URL: `/`
- 요청/응답: `application/json`
- 인증: JWT `Authorization: Bearer {accessToken}` 헤더 사용(회원가입/로그인 제외)

## 챗봇 API
- Base path: `/api/chat`
- 인증: 로그인 시 `Authorization: Bearer {accessToken}` 로 사용자 컨텍스트를 연결. 비로그인도 대화 가능하지만 세션 목록 조회는 빈 배열.
- 모델/컨텍스트: GPT-3.5 기반 `ChatClient` + Chroma VectorStore(RAG, topK=5, similarity ≥ 0.7), 메시지 윈도 메모리 10개, Tool Calling(welfareSearchTool, personalizedWelfareRecommendationTool).
- QueryType 분류: DISCOVERY(데이터 파악), TOPIC(주제 기반 탐색), SERVICE_FOCUS(특정 서비스 상세 질의), GENERAL(일반 대화). `queryType` 필드로 응답.
- 첫 시작은 null / session을 이어가고 싶으면 응답 받은 세션 ID를 요청 본문에 넣으면 됨.

### 대화 생성(비스트리밍) - **POST** `/api/chat`
- 설명: 단건 요청/단건 응답. 세션 ID가 없으면 서버가 UUID로 생성 후 ChatMemory/DB에 저장.
- 요청 본문 예시:
```json
{
  "sessionId": null,
  "message": "수원 청년 창업 지원금 알려줘"
}
```
- 응답 본문 예시:
```json
{
  "sessionId": "4c95a0a6-24f7-4cc8-9b6d-0d1b2af0c9ee",
  "message": "경기도 수원시 청년 창업을 지원하는 주요 사업은...",
  "queryType": "TOPIC"
}
```
- 동작: 사용자 메시지를 `chat_messages`에 저장 → QueryType 분류 → QueryType별 시스템 프롬프트 + 벡터스토어/메모리/Tool(필요 시)로 호출 → 응답 저장 및 세션 제목 자동 생성(처음 응답 10~30자).

### 대화 기록 조회 - **GET** `/api/chat/history/{sessionId}`
- 설명: 세션의 모든 메시지를 생성 시각 오름차순으로 반환.
- 응답 본문 예시:
```json
[
  {
    "id": 10,
    "session_id": "4c95a0a6-24f7-4cc8-9b6d-0d1b2af0c9ee",
    "role": "USER",
    "message_type": "TEXT",
    "sender": "5",
    "content": "수원 청년 창업 지원금 알려줘",
    "created_at": "2024-12-15T02:00:00"
  },
  {
    "id": 11,
    "session_id": "4c95a0a6-24f7-4cc8-9b6d-0d1b2af0c9ee",
    "role": "ASSISTANT",
    "message_type": "TEXT",
    "sender": "ASSISTANT",
    "content": "경기도 수원시에서 받을 수 있는 주요 창업 지원금은...",
    "created_at": "2024-12-15T02:00:02"
  }
]
```

### 내 세션 목록 - **GET** `/api/chat/sessions`
- 설명: 현재 로그인 사용자의 활성 세션 목록을 최신 생성 순으로 반환. 미인증 시 `[]`.
- 응답 필드: `sessionId`, `title`, `userId`, `isActive`, `lastActivity`, `createdAt`, `updatedAt`.

### 세션 삭제 - **DELETE** `/api/chat/sessions/{sessionId}`
- 설명: 세션/메시지/메모리 모두 삭제. 성공 시 `204 No Content`.

### 벡터스토어 초기화(관리자) - **POST** `/api/chat/admin/init-vector-store`
- 설명: `welfare_services` 테이블 데이터를 문서화하여 Chroma VectorStore에 재적재. 응답: `"VectorStore initialized"`.
- 비고: 운영 시 관리자 보호 필요.

### WebSocket 스트리밍 - `/ws/chat`
- 프로토콜: 텍스트 WebSocket. 핸들러 `ChatWebSocketHandler`.
- 요청 메시지(JSON):
```json
{
  "sessionId": "4c95a0a6-24f7-4cc8-9b6d-0d1b2af0c9ee",
  "message": "전북에서 받을 수 있는 월세 지원 알려줘",
  "userId": 5
}
```
- 서버 응답 흐름:
  - `START`: `{ "sessionId": "...", "type": "START" }`
  - `STREAMING`: 다수 전송. `{ "sessionId": "...", "content": "조각 텍스트", "type": "STREAMING" }`
  - `END`: `{ "sessionId": "...", "type": "END" }`
  - 오류 시 `ERROR`: `{ "sessionId": "...", "content": "메시지 처리 중 오류가 발생했습니다.", "type": "ERROR" }`
- 비고: REST와 동일하게 세션 생성/메모리 관리 후 QueryType 분류 및 Tool/RAG를 적용하며, 스트림 응답이 완료되면 전체 메시지를 저장.

## 회원가입 - **POST** `/users/register`
- 설명: 신규 사용자 등록, 추가 프로필 정보 포함
- 요청 본문 예시:
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!",
  "name": "홍길동",
  "life_cycle": "청년",
  "household_status": ["다자녀", "저소득"],
  "interest_theme": ["생활지원", "주거", "교육"],
  "age": 29,
  "sido_name": "서울특별시",
  "sigungu_name": "강남구"
}
```
- 허용 값
  - `life_cycle`: 영유아, 아동, 청소년, 청년, 중장년, 노년, 임신·출산 (단일 값)
  - `household_status`: 다문화·탈북민, 다자녀, 보훈대상자, 장애인, 저소득, 한부모·조손 (배열, 중복 가능)
  - `interest_theme`: 신체건강, 정신건강, 생활지원, 주거, 일자리, 문화·여가, 안전·위기, 임신·출산, 보육, 교육, 입양·위탁, 보호·돌봄, 서민금융, 법률 (배열, 중복 가능)
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
  "household_status": ["다자녀", "저소득"],
  "interest_theme": ["생활지원", "주거", "교육"],
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
  "household_status": ["보훈대상자", "장애인"],
  "interest_theme": ["주거", "문화·여가"],
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

## 맞춤형 복지 서비스 목록 조회 - **GET** `/welfare-services`
- 설명: 현재 로그인한 사용자의 프로필 정보를 기반으로 맞춤형 복지 서비스 목록 조회
- 헤더: `Authorization: Bearer {accessToken}` (인증 필요)
- Query 파라미터:
  - `limit` (선택, 기본값: 50, 최대: 100) - 조회 개수
- 동작:
  - 사용자의 생애주기, 가구상태(배열), 관심테마(배열), 지역 정보를 기반으로 검색
  - 조회수 많은 순으로 정렬
- 응답 예시:
```json
[
  {
    "service_id": "WF12345",
    "service_name": "청년 주거 지원 사업",
    "ai_summary": "만 19-34세 청년에게 월세 보증금 지원",
    "ctpv_nm": "서울특별시",
    "sgg_nm": "강남구",
    "support_type": "현금",
    "service_type": "LOCAL",
    "inquiry_count": 1523
  },
  {
    "service_id": "WF67890",
    "service_name": "다자녀 가구 교육비 지원",
    "ai_summary": "3자녀 이상 가구 교육비 지원",
    "ctpv_nm": "서울특별시",
    "sgg_nm": "강남구",
    "support_type": "현금",
    "service_type": "LOCAL",
    "inquiry_count": 1245
  }
]
```

## 복지 서비스 이름으로 검색 - **GET** `/welfare-services/search`
- 설명: 서비스 이름으로 복지 서비스 검색 (인증 불필요)
- Query 파라미터:
  - `keyword` (필수) - 검색 키워드
  - `limit` (선택, 기본값: 50, 최대: 100) - 조회 개수
- 동작:
  - `service_name`, `service_summary`, `ai_summary`, `service_content` 필드에서 키워드 검색
  - 조회수 많은 순으로 정렬
- 요청 예시: `GET /welfare-services/search?keyword=청년&limit=20`
- 응답 예시:
```json
[
  {
    "service_id": "WF12345",
    "service_name": "청년 주거 지원 사업",
    "ai_summary": "만 19-34세 청년에게 월세 보증금 지원",
    "ctpv_nm": "서울특별시",
    "sgg_nm": "강남구",
    "support_type": "현금",
    "service_type": "LOCAL",
    "inquiry_count": 1523
  }
]
```

## 지역별 복지 서비스 개수 비교 (미리보기) - **GET** `/welfare-services/region-comparison`
- 설명: 프로필 수정 전 지역 변경 미리보기 (인증 필요)
- 헤더: `Authorization: Bearer {accessToken}`
- Query 파라미터:
  - `newSidoName` (필수) - 새로운 시도명
  - `newSigunguName` (필수) - 새로운 시군구명
- 동작:
  - 현재 로그인한 사용자의 기존 지역과 새로운 지역의 복지 서비스 개수 비교
  - 사용자의 생애주기를 기준으로 필터링된 개수 반환
  - 차이(difference)는 새로운 지역 개수 - 현재 지역 개수
- 요청 예시: `GET /welfare-services/region-comparison?newSidoName=경기도&newSigunguName=수원시`
- 응답 예시:
```json
{
  "current_sido_name": "서울특별시",
  "current_sigungu_name": "강남구",
  "current_count": 45,
  "new_sido_name": "경기도",
  "new_sigungu_name": "수원시",
  "new_count": 62,
  "difference": 17
}
```

## 최근 프로필 수정에 따른 지역 복지 개수 차이 - **GET** `/welfare-services/latest-region-change`
- 설명: 프로필 수정 후 이전 지역과 새 지역의 복지 서비스 개수 차이 조회 (인증 필요)
- 헤더: `Authorization: Bearer {accessToken}`
- 동작:
  - 가장 최근 프로필 변경 이력에서 이전 지역과 새 지역의 복지 서비스 개수 비교
  - 생애주기 변경도 함께 고려하여 각 시점의 생애주기로 필터링
  - 프로필 수정 이력이 없으면 에러 반환
- 사용 시나리오:
  1. 사용자가 `PATCH /users/me`로 프로필 수정
  2. 수정 완료 후 이 API 호출하여 변경 전후 복지 개수 차이 표시
- 요청 예시: `GET /welfare-services/latest-region-change`
- 응답 예시:
```json
{
  "current_sido_name": "서울특별시",
  "current_sigungu_name": "강남구",
  "current_count": 45,
  "new_sido_name": "경기도",
  "new_sigungu_name": "수원시",
  "new_count": 62,
  "difference": 17
}
```
- 참고:
  - `current_*` 필드는 프로필 수정 **이전** 지역 정보
  - `new_*` 필드는 프로필 수정 **이후** 지역 정보
  - 지역만 변경되고 생애주기가 같으면 순수 지역 차이만 반영
  - 지역과 생애주기가 모두 변경되면 양쪽 다 고려하여 계산

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

## 지자체 복지 서비스 상세 조회 (외부 API) - **GET** `/api/welfare/detail`
- 설명: 공공데이터포털의 지자체 복지 서비스 API를 통해 상세 정보 조회 (인증 불필요)
- Query 파라미터: `servId` - 서비스 ID
- 요청 예시: `GET /api/welfare/detail?servId=WF12345`
- 응답 예시:
```json
{
  "result_code": "00",
  "result_message": "정상",
  "serv_id": "WF12345",
  "serv_nm": "청년 주거 지원 사업",
  "enfc_bgng_ymd": "20240101",
  "enfc_end_ymd": "20241231",
  "biz_chr_dept_nm": "주거복지과",
  "ctpv_nm": "서울특별시",
  "sgg_nm": "강남구",
  "serv_dgst": "청년층의 주거 안정을 위한 지원",
  "life_nm_array": "청년",
  "trgter_indvdl_nm_array": "저소득",
  "intrs_thema_nm_array": "주거",
  "sprt_cyc_nm": "월별",
  "srv_pvsn_nm": "현금",
  "aply_mtd_nm": "온라인",
  "sprt_trgt_cn": "만 19-34세 청년 중 소득 기준 충족자",
  "slct_crit_cn": "중위소득 150% 이하",
  "alw_serv_cn": "월세 최대 20만원, 보증금 최대 1000만원 지원",
  "aply_mtd_cn": "복지로 홈페이지에서 온라인 신청",
  "inq_num": "02-1234-5678",
  "last_mod_ymd": "20241201",
  "inqpl_ctadr_list": {
    "wlfare_info_reld_nm": "문의처",
    "wlfare_info_reld_cn": "02-1234-5678",
    "wlfare_info_dtl_cd": "001"
  },
  "inqpl_hmpg_reld_list": [
    {
      "wlfare_info_reld_nm": "홈페이지",
      "wlfare_info_reld_cn": "https://example.com",
      "wlfare_info_dtl_cd": "002"
    }
  ]
}
```

## 토큰 포맷
- Access Token: JWT, 인증이 필요한 요청에 `Authorization: Bearer {accessToken}` 헤더 사용
