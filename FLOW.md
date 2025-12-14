# 복지 서비스 즐겨찾기 & 최근 본 서비스 동작 플로우

이 문서는 즐겨찾기와 최근 본 복지 서비스 기능의 전체 동작 플로우를 설명합니다.

## 목차
1. [즐겨찾기 기능](#1-즐겨찾기-기능)
2. [최근 본 복지 서비스 기능](#2-최근-본-복지-서비스-기능)
3. [인기 복지 서비스 TOP 10](#3-인기-복지-서비스-top-10)
4. [데이터 흐름 요약](#4-데이터-흐름-요약)

---

## 1. 즐겨찾기 기능

### 1.1 즐겨찾기 추가

```
[프론트엔드]
사용자가 복지 서비스 상세 페이지에서 "즐겨찾기 추가" 버튼 클릭
    ↓
POST /bookmarks/{welfareServiceId}
Authorization: Bearer {accessToken}
```

```
[백엔드]
BookmarkController.addBookmark()
    ↓
BookmarkService.addBookmark()
    ↓
1. UserFacade.currentUser()
   - SecurityContext에서 JWT 토큰 파싱
   - 토큰에서 이메일 추출
   - DB에서 사용자 조회
    ↓
2. 중복 체크
   - DB에서 userId + welfareServiceId 조합 존재 확인
   - 존재하면: IllegalArgumentException 발생
   - 존재하지 않으면: 다음 단계 진행
    ↓
3. 즐겨찾기 저장
   - BookmarkJpaEntity 생성
   - DB에 저장 (userId, welfareServiceId)
   - createdAt, updatedAt 자동 생성 (BaseTimeEntity)
    ↓
응답: 201 Created (바디 없음)
```

### 1.2 즐겨찾기 목록 조회

```
[프론트엔드]
사용자가 "내 즐겨찾기" 페이지 진입
    ↓
GET /bookmarks
Authorization: Bearer {accessToken}
```

```
[백엔드]
BookmarkController.getMyBookmarks()
    ↓
BookmarkService.getMyBookmarks()
    ↓
1. UserFacade.currentUser()
   - 현재 로그인한 사용자 정보 추출
    ↓
2. DB 조회
   - SELECT * FROM tbl_bookmark
     WHERE user_id = ?
     ORDER BY created_at DESC
    ↓
3. DTO 변환
   - BookmarkJpaEntity → BookmarkResponse
    ↓
응답:
[
  {
    "id": 1,
    "welfare_service_id": "WF12345",
    "created_at": "2024-12-14T16:30:00"
  },
  ...
]
```

```
[프론트엔드]
응답받은 welfare_service_id 배열로 복지 서비스 API 호출
    ↓
for each welfare_service_id:
    GET /welfare-services/{welfare_service_id}
    (다른 팀원이 개발한 API)
    ↓
복지 서비스 상세 정보 + 즐겨찾기 정보 결합하여 화면 표시
```

### 1.3 즐겨찾기 삭제

```
[프론트엔드]
사용자가 "즐겨찾기 삭제" 버튼 클릭
    ↓
DELETE /bookmarks/{welfareServiceId}
Authorization: Bearer {accessToken}
```

```
[백엔드]
BookmarkController.removeBookmark()
    ↓
BookmarkService.removeBookmark()
    ↓
1. UserFacade.currentUser()
   - 현재 로그인한 사용자 정보 추출
    ↓
2. DB에서 삭제
   - DELETE FROM tbl_bookmark
     WHERE user_id = ? AND welfare_service_id = ?
   - 존재하지 않아도 예외 발생하지 않음 (멱등성)
    ↓
응답: 204 No Content (바디 없음)
```

### 1.4 즐겨찾기 여부 확인

```
[프론트엔드]
복지 서비스 상세 페이지 로드 시
하트 아이콘 상태 표시를 위해 호출
    ↓
GET /bookmarks/{welfareServiceId}/check
Authorization: Bearer {accessToken}
```

```
[백엔드]
BookmarkController.checkBookmark()
    ↓
BookmarkService.isBookmarked()
    ↓
1. UserFacade.currentUser()
   - 현재 로그인한 사용자 정보 추출
    ↓
2. DB 존재 여부 확인
   - SELECT COUNT(*) FROM tbl_bookmark
     WHERE user_id = ? AND welfare_service_id = ?
    ↓
응답: true 또는 false
```

```
[프론트엔드]
응답값에 따라 UI 업데이트
- true: 하트 아이콘 채우기 (빨간색)
- false: 하트 아이콘 비우기 (회색)
```

---

## 2. 최근 본 복지 서비스 기능

### 2.1 최근 본 기록 추가

```
[프론트엔드]
사용자가 복지 서비스 상세 페이지 진입
    ↓
복지 서비스 상세 정보 API 호출
GET /welfare-services/{welfareServiceId}
    ↓
동시에 또는 직후에
POST /recent-views/{welfareServiceId}
Authorization: Bearer {accessToken}
```

**React 예시 코드:**
```jsx
useEffect(() => {
  // 상세 정보 조회
  fetchWelfareServiceDetail(welfareServiceId);

  // 최근 본 기록 추가
  axios.post(`/api/recent-views/${welfareServiceId}`, {}, {
    headers: { 'Authorization': `Bearer ${accessToken}` }
  }).catch(err => console.error('Failed to track view:', err));
}, [welfareServiceId]);
```

```
[백엔드]
RecentViewController.addRecentView()
    ↓
RecentViewService.addRecentView()
    ↓
1. UserFacade.currentUser()
   - 현재 로그인한 사용자 정보 추출
    ↓
2. 이미 본 적 있는지 확인
   - SELECT * FROM tbl_recent_view
     WHERE user_id = ? AND welfare_service_id = ?
    ↓
3-A. 이미 본 적 있으면 (existingView.isPresent())
     - viewedAt만 현재 시간으로 업데이트
     - UPDATE tbl_recent_view
       SET viewed_at = NOW(), updated_at = NOW()
       WHERE id = ?
    ↓
3-B. 처음 보는 것이면
     3-B-1. 사용자의 최근 본 기록 개수 확인
            - SELECT COUNT(*) FROM tbl_recent_view WHERE user_id = ?
     3-B-2. 100개 이상이면 가장 오래된 것 삭제
            - SELECT * FROM tbl_recent_view
              WHERE user_id = ?
              ORDER BY viewed_at DESC
            - 마지막 항목 DELETE
     3-B-3. 새로운 기록 저장
            - INSERT INTO tbl_recent_view
              (user_id, welfare_service_id, viewed_at, created_at, updated_at)
              VALUES (?, ?, NOW(), NOW(), NOW())
    ↓
응답: 201 Created (바디 없음)
```

**DB 상태 예시:**
```
tbl_recent_view 테이블:
| id | user_id | welfare_service_id | viewed_at           | created_at          | updated_at          |
|----|---------|-------------------|---------------------|---------------------|---------------------|
| 1  | 5       | WF12345          | 2024-12-14 16:30:00 | 2024-12-14 10:00:00 | 2024-12-14 16:30:00 |
| 2  | 5       | WF67890          | 2024-12-14 15:20:00 | 2024-12-14 15:20:00 | 2024-12-14 15:20:00 |
| 3  | 7       | WF12345          | 2024-12-14 14:10:00 | 2024-12-14 14:10:00 | 2024-12-14 14:10:00 |

// user_id=5가 WF12345를 다시 보면:
// id=1의 viewed_at만 현재 시간으로 업데이트 (중복 저장 방지)
```

### 2.2 최근 본 목록 조회

```
[프론트엔드]
사용자가 "최근 본 복지 서비스" 페이지 진입
    ↓
GET /recent-views?limit=20
Authorization: Bearer {accessToken}
```

```
[백엔드]
RecentViewController.getMyRecentViews()
    ↓
RecentViewService.getMyRecentViews()
    ↓
1. UserFacade.currentUser()
   - 현재 로그인한 사용자 정보 추출
    ↓
2. limit 검증
   - 0 이하 또는 100 초과면 100으로 조정
    ↓
3. DB 조회
   - SELECT * FROM tbl_recent_view
     WHERE user_id = ?
     ORDER BY viewed_at DESC
     LIMIT ?
    ↓
4. DTO 변환
   - RecentViewJpaEntity → RecentViewResponse
    ↓
응답:
[
  {
    "id": 1,
    "welfare_service_id": "WF12345",
    "viewed_at": "2024-12-14T16:30:00"
  },
  ...
]
```

```
[프론트엔드]
응답받은 welfare_service_id 배열로 복지 서비스 API 호출
    ↓
for each welfare_service_id:
    GET /welfare-services/{welfare_service_id}
    (다른 팀원이 개발한 API)
    ↓
복지 서비스 상세 정보 + 조회 시간 결합하여 화면 표시
```

---

## 3. 인기 복지 서비스 TOP 10

### 3.1 인기 복지 서비스 조회

```
[프론트엔드]
메인 페이지 로드 또는 "인기 복지 서비스" 섹션 표시
    ↓
GET /recent-views/trending?days=7&limit=10
(인증 불필요 - 로그인하지 않은 사용자도 조회 가능)
```

```
[백엔드]
RecentViewController.getTrendingWelfareServices()
    ↓
RecentViewService.getTrendingWelfareServices()
    ↓
1. 파라미터 검증
   - days가 0 이하면 기본값 7일
   - limit이 0 이하 또는 100 초과면 기본값 10
    ↓
2. 집계 시작 시점 계산
   - since = 현재 시간 - days일
   - 예: 2024-12-14 16:00:00 - 7일 = 2024-12-07 16:00:00
    ↓
3. DB 집계 쿼리 실행
   SELECT
     welfare_service_id,
     COUNT(DISTINCT user_id) as view_count
   FROM tbl_recent_view
   WHERE viewed_at >= ?  -- since (2024-12-07 16:00:00)
   GROUP BY welfare_service_id
   ORDER BY view_count DESC
   LIMIT ?  -- limit (10)
    ↓
4. DTO 변환
   - WelfareServiceViewCount → TrendingWelfareResponse
    ↓
응답:
[
  {
    "welfare_service_id": "WF12345",
    "view_count": 1523  // 1523명의 고유 사용자가 조회
  },
  {
    "welfare_service_id": "WF67890",
    "view_count": 1245
  },
  ...
]
```

**집계 예시:**
```
tbl_recent_view 데이터:
| user_id | welfare_service_id | viewed_at           |
|---------|-------------------|---------------------|
| 1       | WF12345          | 2024-12-14 10:00:00 |
| 2       | WF12345          | 2024-12-14 11:00:00 |
| 1       | WF12345          | 2024-12-14 15:00:00 | ← 중복 (같은 사용자)
| 3       | WF12345          | 2024-12-14 16:00:00 |
| 1       | WF67890          | 2024-12-14 14:00:00 |
| 2       | WF67890          | 2024-12-14 15:00:00 |

집계 결과:
WF12345: view_count = 3 (user_id 1, 2, 3) ← COUNT(DISTINCT user_id)
WF67890: view_count = 2 (user_id 1, 2)
```

```
[프론트엔드]
응답받은 welfare_service_id 배열로 복지 서비스 API 호출
    ↓
for each trending item:
    GET /welfare-services/{welfare_service_id}
    (다른 팀원이 개발한 API)
    ↓
복지 서비스 상세 정보 + 조회수 결합하여 인기 순위 화면 표시
```

---

## 4. 데이터 흐름 요약

### 4.1 데이터 저장

```
사용자 행동 → 프론트엔드 API 호출 → 백엔드 → DB 저장
```

**즐겨찾기:**
- 사용자 행동: 즐겨찾기 추가 버튼 클릭
- API: POST /bookmarks/{welfareServiceId}
- DB: tbl_bookmark에 (user_id, welfare_service_id) 저장
- 특징: 개수 제한 없음, 중복 불가

**최근 본 서비스:**
- 사용자 행동: 복지 서비스 상세 페이지 진입
- API: POST /recent-views/{welfareServiceId}
- DB: tbl_recent_view에 (user_id, welfare_service_id, viewed_at) 저장
- 특징: 최대 100개, 중복 시 viewed_at 업데이트

### 4.2 데이터 조회

**개인 데이터 조회:**
```
GET /bookmarks              → 내 즐겨찾기 목록
GET /recent-views           → 내 최근 본 목록
GET /bookmarks/{id}/check   → 즐겨찾기 여부 확인
```

**전체 사용자 데이터 집계:**
```
GET /recent-views/trending  → 인기 복지 서비스 TOP 10
```

### 4.3 복지 서비스 정보 결합

백엔드는 **복지 서비스 ID만 저장**하고, 프론트엔드에서 실제 복지 서비스 정보를 조회합니다:

```
1. 백엔드 API 호출 → welfare_service_id 배열 받기
   예: ["WF12345", "WF67890", "WF11111"]

2. 각 ID로 복지 서비스 API 호출
   for each id in ids:
       GET /welfare-services/{id}

3. 복지 서비스 상세 정보 + 메타 정보 결합
   - 즐겨찾기: 추가한 시간
   - 최근 본: 조회한 시간
   - 인기: 조회 수

4. 화면에 표시
```

### 4.4 인증 흐름

```
[즐겨찾기 & 최근 본 (인증 필요)]
클라이언트
    ↓
Authorization: Bearer {accessToken} 헤더 포함
    ↓
JwtTokenFilter (JWT 검증)
    ↓
SecurityContext에 Authentication 저장
    ↓
Controller → Service → UserFacade.currentUser()
    ↓
SecurityContext에서 인증 정보 추출
    ↓
사용자 정보로 비즈니스 로직 수행

[인기 복지 서비스 (인증 불필요)]
클라이언트
    ↓
Authorization 헤더 없이 요청
    ↓
Controller → Service → DB 집계 쿼리
    ↓
전체 사용자 데이터 집계하여 반환
```

---

## 5. 프론트엔드 개발자를 위한 체크리스트

### 5.1 즐겨찾기 구현

- [ ] 복지 서비스 상세 페이지에 즐겨찾기 버튼 추가
- [ ] 페이지 로드 시 `GET /bookmarks/{id}/check` 호출하여 버튼 상태 설정
- [ ] 버튼 클릭 시:
  - 즐겨찾기 추가: `POST /bookmarks/{id}`
  - 즐겨찾기 삭제: `DELETE /bookmarks/{id}`
- [ ] "내 즐겨찾기" 페이지 구현
  - `GET /bookmarks` 호출
  - 받은 ID 배열로 복지 서비스 정보 조회
  - 최신순으로 표시

### 5.2 최근 본 복지 서비스 구현

- [ ] 복지 서비스 상세 페이지 컴포넌트에 추가
  - useEffect/componentDidMount에서 `POST /recent-views/{id}` 호출
  - 에러 발생해도 무시 (사용자 경험에 영향 없음)
- [ ] "최근 본 복지 서비스" 페이지 구현
  - `GET /recent-views?limit=20` 호출
  - 받은 ID 배열로 복지 서비스 정보 조회
  - 최신순으로 표시

### 5.3 인기 복지 서비스 TOP 10 구현

- [ ] 메인 페이지에 "인기 복지 서비스" 섹션 추가
  - `GET /recent-views/trending?days=7&limit=10` 호출
  - 받은 ID 배열로 복지 서비스 정보 조회
  - 순위와 조회수 함께 표시
  - 로그인하지 않은 사용자도 볼 수 있도록 구현

---

## 6. 데이터베이스 테이블 구조

### tbl_bookmark
```sql
CREATE TABLE tbl_bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    welfare_service_id VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_user_welfare (user_id, welfare_service_id)
);
```

### tbl_recent_view
```sql
CREATE TABLE tbl_recent_view (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    welfare_service_id VARCHAR(255) NOT NULL,
    viewed_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_user_welfare (user_id, welfare_service_id)
);
```
