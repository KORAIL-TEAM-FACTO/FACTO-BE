# Repository Guidelines

## 프로젝트 구조
- 소스: `src/main/java/team/java/facto_be` 레이어드 구조, 사용자 모듈은 `user/`(controller, service, repository, entity, dto).
- 설정/리소스: `src/main/resources`.
- 테스트: `src/test/java` (패키지 구조를 맞춰 추가).
- 빌드 스크립트: `build.gradle`, `settings.gradle`, 실행 래퍼 `gradlew`, `gradlew.bat`.

## 빌드·테스트·개발 명령
- `./gradlew build` — 컴파일 후 모든 테스트 실행.
- `./gradlew test` — 테스트만 실행.
- `./gradlew bootRun` — 로컬에서 Spring Boot 실행.
- `./gradlew clean` — 빌드 산출물 정리.

## 코딩 스타일 & 네이밍
- Java 21, 4-space 들여쓰기, 가급적 120자 이하.
- 패키지: 소문자+언더스코어 조합 `team.java.facto_be.user.service`.
- 클래스 PascalCase, 메서드/필드 camelCase.
- 의존성 주입은 생성자 방식(Lombok `@RequiredArgsConstructor`) 권장.
- 현재 저장소는 레이어드 아키텍처만 사용: controller → service → repository → entity. 제거된 DDD/헥사고날 패턴(aggregate, port/adapter 등)을 다시 추가하지 않음.

## 테스트 가이드
- 프레임워크: JUnit 5 (spring-boot-starter-test).
- 테스트 위치: `src/test/java` 아래 동일 패키지 경로, 클래스명 `*Test`.
- 푸시 전 `./gradlew test` 실행. 핵심 로직(회원가입, 로그인)에 대한 단위/통합 테스트를 우선 보강.

## 커밋 & PR 가이드
- 커밋 메시지: 현재형·짧은 범위(예: `Add user login validation`). 관련 변경만 묶고 불필요한 잡음 피하기.
- PR: 요약, 테스트 결과(`./gradlew test`), 영향 범위(예: user service/controller) 기술. 이슈가 있으면 링크.
- UI/API 문서 변경 시에만 스크린샷/스니펫 첨부, 나머지는 핵심 bullet로 충분.

## 보안·설정 팁
- 비밀값은 커밋 금지. 환경변수/외부 설정 사용.
- JWT/Redis/DB 설정은 `application.properties`나 프로파일로 관리하고 실 값은 VCS에 올리지 않기.

## 에이전트 작업 유의사항
- 기존 파일이 아닌 이상 ASCII 유지.
- 패키지 경로/이동 후 import 정상인지 확인하고, 삭제된 헥사고날 컴포넌트 재도입 금지.
