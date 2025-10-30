# ⚽ MatchOn — 실시간 스포츠 매칭 플랫폼

> 종목 · 지역 · 시간대 기반으로 팀원을 **실시간 매칭**하고,  
> **채팅/게시판**으로 소통하는 스포츠 커뮤니티 플랫폼

---

## 📅 프로젝트 개요

- **기간**: 2025.03 ~ 2025.05 (약 5주)  
- **팀 구성**: 프론트엔드 1명 + 백엔드 3명  
- **역할(전준혁)**:  
  - 로그인 / 회원가입 / 탈퇴 / 신고 / 정지 기능 구현  
  - CRUD 게시판(글, 댓글, 신고, 좋아요)  
  - DB 설계 (회원, 게시판, 댓글, 신고 테이블)  
  - UI/UX 개선 (신고/정지 안내 UX 개선, 폼 검증, 피드백)  

---

## 🧠 프로젝트 개요

MatchOn은 사용자가 **운동 종목, 지역, 시간대**를 선택해  
함께 운동할 사람을 실시간으로 매칭해주는 플랫폼입니다.  
매칭 후에는 **실시간 채팅**으로 소통하며,  
커뮤니티 게시판과 신고 시스템을 통해 **건전한 커뮤니티**를 유지합니다.

---

## 🛠️ 기술 스택

| 분야 | 기술 |
|------|------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4, Spring Security, WebSocket |
| **DB / ORM** | MySQL 8, JPA, MyBatis |
| **Auth** | JWT + HttpOnly Cookie |
| **Infra** | AWS EC2, RDS, S3 |
| **Frontend** | Thymeleaf, HTML, CSS, JavaScript |
| **Build Tool** | Gradle |
| **Etc** | Validation, AOP Logging, Redis(확장 고려) |

---

## ✨ 주요 기능

### 👤 회원 기능
- 로그인 / 회원가입 / 탈퇴 / 정지 / 신고  
- JWT + HttpOnly Cookie 기반 인증 구조  
- 관리자 권한에 따른 신고/정지 관리 기능  

### 🧾 커뮤니티
- 게시판 CRUD (페이징 / 검색 / 정렬 지원)  
- 댓글 / 대댓글 / 좋아요 기능  
- 게시글 및 댓글 신고 → 관리자 심사 → 정지 처리

### ⚽ 매칭 시스템
- 종목/지역/시간대 기준 매칭 큐  
- WebSocket 기반 실시간 매칭 알림 및 팀 채팅

### 🧭 관리자 페이지
- 신고/정지 목록 관리  
- 사용자 상태(활성/정지/탈퇴) 변경  
- 게시글/댓글 차단 관리

---

## 🧩 시스템 아키텍처

![Architecture](docs/architecture.png)

**구성 요약**
- Spring Boot 서버 중심의 백엔드 구조  
- JWT 인증, WebSocket, MySQL, AWS S3 연동  
- Redis(옵션) 및 외부 API(Kakao, Dialogflow) 확장 구조  

---

## 🗄️ ERD (Entity Relationship Diagram)

![ERD](docs/ERD.png)

**주요 엔티티 관계**
- Member 1 — N Board  
- Board 1 — N Comment  
- Member / Board / Comment 1 — N Report  

---

## 🧱 DB 설계 요약

| 테이블 | 주요 컬럼 |
|--------|-----------|
| **Member** | id, username, email, password, role, status, suspended_until, created_at |
| **Board** | id, member_id(FK), category, title, content, like_count, report_count, created_at |
| **Comment** | id, board_id(FK), member_id(FK), parent_comment_id, content, report_count |
| **Report** | id, target_type, target_id, reporter_id, reason_code, status, decision_by, decision_at |

---

## 🔐 환경 변수 (.env.example)

```bash
# DB
DB_URL=jdbc:mysql://localhost:3306/matchon?serverTimezone=Asia/Seoul
DB_USER=matchon
DB_PASSWORD=****

# JWT
JWT_SECRET=****
JWT_ISSUER=matchon

# AWS
AWS_ACCESS_KEY=****
AWS_SECRET_KEY=****
S3_BUCKET=matchon-bucket
S3_BASE_URL=https://matchon-bucket.s3.amazonaws.com

# REDIS (선택)
REDIS_PROD_IP=127.0.0.1
REDIS_PROD_PORT=6379

# KAKAO / Dialogflow
KAKAO_API_KEY=****
DIALOGFLOW_KEY_PATH=/app/keys/dialogflow.json
