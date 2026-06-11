# 회사/기관 홈페이지 CMS 설계 문서

> Spring Boot + Thymeleaf 기반 콘텐츠 관리 시스템 (학습용)

## 0. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 목적 | Java/Spring 학습 |
| 콘텐츠 유형 | 회사/기관 홈페이지 (공지사항, 페이지 관리 등) |
| 아키텍처 | Spring Boot + Thymeleaf 모놀리식 (서버사이드 렌더링) |
| 향후 확장 | 필요 시 관리자 화면만 React로 점진적 분리 |
| DB | **기존 `dmktweb` 스키마 재사용** (신규 생성 X) |

> ⚠️ **중요 — 접근법 변경**: 처음엔 새 `Post` 테이블을 생성할 계획이었으나,
> 활용할 기존 운영 DB(`dmktweb`, 196개 테이블)가 있어 **기존 스키마에 엔티티를 매핑**하는
> 방식으로 전환했다. 따라서 `ddl-auto: none` 으로 고정하고, 엔티티는 기존 대문자 컬럼에
> `@Column(name=...)` 으로 명시 매핑한다. 1단계는 **읽기 전용**으로 시작한다.

### 왜 Thymeleaf 모놀리식인가?
- **CMS와 궁합이 좋음** — "글 작성 → 저장 → 페이지로 보여주기"는 SSR이 자연스럽다 (워드프레스, 티스토리도 SSR 기반).
- **단순함** — 프로젝트 하나, 배포 하나.
- **SEO 유리** — 검색엔진이 완성된 HTML을 바로 읽음 (콘텐츠 사이트에 중요).
- **백엔드(Java) 한 언어에 집중** — 인증·CORS·API 통신 같은 곁가지 없이 Spring MVC·JPA·Thymeleaf 핵심에 집중.

---

## 1. 전체 그림 — 요청이 흐르는 길

Thymeleaf 기반 Spring MVC에서 브라우저 요청 하나가 처리되는 흐름:

```
[브라우저]
   │  GET /posts/1 요청
   ▼
[Controller]   ← 요청을 받고, 어떤 화면을 줄지 결정
   │  service.findById(1) 호출
   ▼
[Service]      ← 비즈니스 로직 (트랜잭션 경계)
   │  repository.findById(1) 호출
   ▼
[Repository]   ← DB 접근 (Spring Data JPA)
   │  SELECT * FROM post WHERE id=1
   ▼
[Database]     ← H2 / MySQL
   │
   ▼ (Entity → 데이터를 담아 거꾸로 올라옴)
[Controller]   ← Model에 데이터 담고 "post/detail" 뷰 이름 반환
   │
   ▼
[Thymeleaf]    ← detail.html + 데이터 = 완성된 HTML
   │
   ▼
[브라우저]      ← 완성된 페이지 표시
```

핵심은 **계층 분리(Layered Architecture)**. 각 계층이 한 가지 책임만 지도록 나눠서,
나중에 React로 바꾸더라도 Service·Repository는 그대로 두고 Controller만 API용으로 교체하면 된다.

---

## 2. 각 계층의 역할

| 계층 | 책임 | 핵심 규칙 |
|------|------|-----------|
| **Controller** | HTTP 요청/응답, 화면 라우팅 | 비즈니스 로직 ❌, 얇게 유지 |
| **Service** | 비즈니스 로직, 트랜잭션 | DB·HTTP 세부사항 모름 |
| **Repository** | DB CRUD | 인터페이스만 선언, 구현은 Spring이 자동 생성 |
| **Entity** | DB 테이블과 매핑되는 객체 | JPA가 객체↔테이블 변환 |
| **DTO** | 계층 간 데이터 전달용 객체 | Entity를 화면에 직접 노출 안 하기 위함 |

> **왜 DTO를 따로?** Entity를 화면에 바로 쓰면 편하지만, 비밀번호 같은 필드가 노출되거나
> 화면 요구사항이 DB 구조에 묶인다. 처음엔 어렵게 느껴지니 1단계에선 Entity를 직접 쓰고,
> 필요해지는 시점에 DTO를 도입하며 "왜 필요한지" 체감하는 걸 추천.

---

## 3. 기술 스택

| 구분 | 선택 | 이유 |
|------|------|------|
| Spring Boot | 3.x | 최신, Java 17+ |
| Java | 17 | LTS 버전 |
| 빌드 | Maven | pom.xml로 의존성 관리 |
| View | Thymeleaf | SSR |
| ORM | Spring Data JPA | JPA 학습 핵심 |
| DB | H2(개발) → MySQL(운영) | H2로 바로 시작, 나중에 교체 |
| 인증 | Spring Security | 관리자 로그인 (2단계) |
| 기타 | Lombok, Validation | 보일러플레이트 제거 |

### 핵심 의존성 (pom.xml)
- `spring-boot-starter-web` — Spring MVC
- `spring-boot-starter-thymeleaf` — 뷰 템플릿
- `spring-boot-starter-data-jpa` — JPA/DB
- `h2database` — 개발용 인메모리 DB (설치 없이 바로 실행)
- `lombok` — getter/setter 자동 생성
- `spring-boot-starter-validation` — 입력값 검증

---

## 4. 폴더 구조

```
cms/
├── pom.xml                          ← Maven 설정 (의존성 관리)
├── src/
│   ├── main/
│   │   ├── java/com/example/cms/
│   │   │   ├── CmsApplication.java          ← 시작점 (main 메서드)
│   │   │   │
│   │   │   ├── domain/                      ← Entity (DB 테이블)
│   │   │   │   ├── Post.java
│   │   │   │   └── Category.java
│   │   │   │
│   │   │   ├── repository/                  ← DB 접근 인터페이스
│   │   │   │   └── PostRepository.java
│   │   │   │
│   │   │   ├── service/                     ← 비즈니스 로직
│   │   │   │   └── PostService.java
│   │   │   │
│   │   │   └── controller/                  ← 요청 처리
│   │   │       └── PostController.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml              ← 앱 설정 (DB 접속정보 등)
│   │       ├── templates/                   ← Thymeleaf HTML
│   │       │   ├── layout/                  ← 공통 레이아웃(헤더/푸터)
│   │       │   └── post/
│   │       │       ├── list.html
│   │       │       ├── detail.html
│   │       │       └── form.html
│   │       └── static/                      ← CSS, JS, 이미지
│   │           ├── css/
│   │           └── js/
│   │
│   └── test/java/com/example/cms/           ← 테스트 코드
```

**패키지 나누는 기준** — 위는 "계층별(layer)" 구조다. 규모가 커지면 `post/`, `category/`처럼
"기능별(feature)" 구조로 가는 게 더 좋지만, 학습 단계에선 계층별이 각 역할을 눈으로 익히기에
더 명확해서 이걸로 시작한다.

---

## 5. 단계별 개발 계획

학습 목적이니 한 번에 다 만들지 말고 단계별로 쌓는다.

### 1단계 — 핵심 CRUD (Spring MVC + JPA + Thymeleaf 기본기)
- 게시글(Post): 공지사항/보도자료 작성·수정·삭제·목록·상세
- 카테고리 분류

### 2단계 — 관리자 영역 (Spring Security)
- 관리자 로그인
- 일반 방문자용 화면(public)과 관리자 화면(admin) 분리

### 3단계 — 회사 홈페이지 특화
- 페이지 관리 (회사소개 등 정적 페이지를 DB로 관리)
- 메뉴/네비게이션 관리
- 배너/이미지 업로드

### 4단계 — 마무리
- 페이징·검색
- 파일 업로드
- 운영 DB(MySQL) 전환

---

## 6. 1단계 상세 — 게시글 CRUD

회사 홈페이지의 "공지사항" 게시판.

### URL 설계

| URL | HTTP | 화면/동작 | 파일 |
|-----|------|----------|------|
| `/posts` | GET | 목록 | list.html |
| `/posts/{id}` | GET | 상세 | detail.html |
| `/posts/new` | GET | 작성 폼 | form.html |
| `/posts` | POST | 저장 처리 | → 목록으로 이동 |
| `/posts/{id}/edit` | GET | 수정 폼 | form.html |
| `/posts/{id}` | POST | 수정 처리 | → 상세로 이동 |
| `/posts/{id}/delete` | POST | 삭제 처리 | → 목록으로 이동 |

### 엔티티 ↔ 기존 테이블 매핑 (실제 구현)

**`Post` → `bbs_board`** (80여 컬럼 중 핵심만 매핑)
| 엔티티 필드 | 컬럼 | 타입 | 설명 |
|------|------|------|------|
| `id` | `B_IDX` | Long | PK |
| `boardId` | `BS_IDX` | Integer | 소속 게시판 |
| `categoryId` | `BC_IDX` | Integer | 카테고리 |
| `subject` | `SUBJECT` | String | 제목 |
| `content` | `REMARK` | String(longtext) | 내용 |
| `writer` | `WRITER` | String | 작성자 |
| `viewCount` | `VIEW_CNT` | Integer | 조회수 |
| `noticeYn` | `NOTICE_YN` | String | 공지 여부 |
| `writeDate` | `WRITE_DATE` | **String** | 작성일 (레거시 varchar) |
| `delYn` | `DEL_YN` | String | 소프트 삭제 |

**`Content` → `contents`**
| 엔티티 필드 | 컬럼 | 타입 |
|------|------|------|
| `id` | `CI_IDX` | Long |
| `subject` | `SUBJECT` | String |
| `content` | `REMARK` | String(longtext) |
| `viewYn` | `VIEW_YN` | String |
| `regDate` | `REG_DATE` | LocalDateTime |
| `delYn` | `DEL_YN` | String |

> 핵심 컬럼만 매핑하고, 카테고리·첨부파일 등은 다음 단계에 붙인다.

### 기존 DB 의 핵심 관례
- **소프트 삭제**: 모든 조회에 `DEL_YN <> 'Y'` 필터 적용
- **노출 여부**: `VIEW_YN = 'Y'`
- **감사 컬럼**: `REG_IDX/REG_DATE`, `MOD_IDX/MOD_DATE`
- **PK**: `*_IDX` (auto_increment), 컬럼명 대문자
- **멀티사이트**: `SITE_IDX` 로 구분
- **날짜 타입 불일치**: `bbs_board` 는 varchar, `contents` 는 datetime

---

## 7. 1단계 작업 순서

1. 프로젝트 골격 (pom.xml, application.yml, 메인 클래스)
2. `Post` 엔티티 → Repository → Service → Controller 순서로 (데이터 흐름 따라)
3. Thymeleaf 화면 4개 (list, detail, form, 공통 레이아웃)
4. 실행 → 브라우저에서 글 작성/조회 확인
