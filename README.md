# CMS (회사/기관 홈페이지)

Spring Boot 3 + Thymeleaf 기반 콘텐츠 관리 시스템 (학습용).
기존 `dmktweb` MySQL 데이터베이스의 게시글/페이지를 조회한다.

## 현재 구현 (1단계 — 읽기 전용)

| 화면 | URL | 데이터 출처 |
|------|-----|------------|
| 홈 | `/` | - |
| 게시판 목록 | `/posts?boardId=41` | `bbs_board` |
| 게시글 상세 | `/posts/{id}` | `bbs_board` |
| 페이지 목록 | `/contents` | `contents` |
| 페이지 상세 | `/contents/{id}` | `contents` |

> ⚠️ 아직 **읽기 전용**이다. 작성/수정/삭제는 다음 단계에서 추가한다.

## 실행 방법

이 PC에는 시스템 PATH에 Java 17+ 와 Maven 이 없으므로, 아래 경로를 직접 지정한다.

```powershell
# JDK 21 (Microsoft OpenJDK)
$env:JAVA_HOME = "C:\Users\NTRO_01\.jdks\ms-21.0.11"

# IntelliJ 번들 Maven
$MVN = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\plugins\maven\lib\maven3\bin\mvn.cmd"

# 실행
& $MVN spring-boot:run
```

→ 브라우저에서 http://localhost:8080 접속

> IntelliJ 에서 열 경우: Project SDK 를 21 로, `CmsApplication` 을 실행하면 된다.

## DB 연결 설정

`src/main/resources/application.yml`
- URL: `jdbc:mysql://localhost:3306/dmktweb`
- 계정: `root` / `root1234`
- **`ddl-auto: none`** — Hibernate 가 기존 운영 스키마를 절대 변경하지 못하게 고정.

## 구조

```
controller → service → repository → (JPA) → MySQL
   얇게      비즈니스    DB접근
```

- `domain/` — 엔티티 (`Post` → bbs_board, `Content` → contents). 대문자 컬럼에 명시 매핑.
- `repository/` — Spring Data JPA. 소프트 삭제(`DEL_YN<>'Y'`) 필터 포함.
- `service/` — 비즈니스 로직 (현재 readOnly).
- `controller/` — 화면 라우팅.
- `resources/templates/` — Thymeleaf 뷰.

자세한 설계는 [DESIGN.md](DESIGN.md) 참고.
