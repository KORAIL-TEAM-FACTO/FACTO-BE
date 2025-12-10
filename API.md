# API 문서 (요약)

## 공통 정보
- Base URL: `/`
- 요청/응답: `application/json`
- 인증이 필요한 엔드포인트는 추후 JWT `Authorization: Bearer {accessToken}` 헤더 사용(현재 회원가입/로그인만 공개).

## 회원가입
- **POST** `/users/register`
- 설명: 신규 사용자 등록
- 요청 바디 예시:
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!",
  "name": "홍길동"
}
```
- 응답: `201 Created`, 바디 없음

## 로그인
- **POST** `/users/login`
- 설명: 이메일/비밀번호로 로그인 후 토큰 발급
- 요청 바디 예시:
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

## 토큰 포맷
- Access Token: JWT, 헤더에 `Authorization: Bearer {accessToken}`로 사용.
- Refresh Token: 별도 저장소/헤더 정책에 따라 사용(재발급 엔드포인트가 추가될 경우 필요).
