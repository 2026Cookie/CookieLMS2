<img width="1751" height="894" alt="image" src="https://github.com/user-attachments/assets/9d7bac7d-1e3d-465d-b640-bcfba3e87ba5" />

# CookieLMS2

역할 기반(학생/강사/관리자) 기능을 제공하는 팀 프로젝트형 LMS 백엔드입니다.  
학습 흐름(강의 조회, 수강, 과제 제출)과 운영 흐름(회원 관리, 로그/에러 모니터링)을 함께 다루도록 설계했습니다.

---

## 프로젝트 개요
- **권한별 사용자 경험 제공**: 학생, 강사, 관리자 기능 분리
- **강의/수강/과제 중심**: 학습 관리 기능 제공
- **운영 효율화**: 관리자 운영 관점의 트래픽/성능/에러 로그 조회 기능 제공

---
<img width="1506" height="1190" alt="image" src="https://github.com/user-attachments/assets/4309b42d-27e3-4667-9626-966825850d80" />
---

## 기술 스택

### Backend
- Java 17
- Spring Boot 3.5.13
- Spring Data JPA
- Spring Security
- Thymeleaf
- Spring Validation
- ModelMapper
- Lombok

### Frontend
- Claude

### Database
- MySQL 8.x (mysql-connector-j)

### Build / Test
- Gradle Wrapper
- JUnit5
- Spring Security Test

---
## Cookie ERD
<img width="1304" height="1071" alt="image" src="https://github.com/user-attachments/assets/c8ea40d8-f0bb-40ea-97c1-7631c8fcf902" />
---

## 핵심 기능

### 1) 학생 영역
- 강의 목록 조회/검색 (`GET /lectures`)
- 내 강의 조회 (`GET /lectures/my`)
- 강의 상세/영상/자료 접근 권한 처리
- 수강 신청 및 취소 (`/api/enrollments/**`)
- 대기열 신청/취소/대기번호 조회 (`/api/waitlist/**`, `/user/waitlist/{lectureId}`)
- 과제 조회/제출/성공 페이지 (`/student/assignments/**`)

### 2) 강사 영역
- 강사 메인 및 내 강의 목록 조회 (`/instructor/main`, `/instructor/lectures`)
- 강의 등록/수정 (`/instructor/lecture/regist`, `/instructor/lecture/edit/{id}`)
- 강의 자료(PDF), 썸네일 파일 업로드 처리
- 과제 생성/수정 연계
- 과제 제출 현황 대시보드 (`/instructor/lecture/{lectureId}/assignment/{assignmentId}/status`)

### 3) 관리자 영역
- 회원 목록/밴 회원/탈퇴 회원 조회
- 회원 밴/해제 처리
- API 트래픽/서비스 성능/에러/트레이스 로그 조회
- 익명 Critical 에러 조회 및 인사이트 집계

### 4) 공통 영역
- 회원가입/로그인/아이디 찾기/비밀번호 재설정
- 마이페이지 조회/수정/탈퇴
- Spring Security 기반 권한 제어 (USER / INSTRUCTOR / ADMIN)
- AOP 기반 비즈니스 로깅, 파일 검증
- 글로벌 예외 처리 및 커스텀 에러 페이지

---

## 실행 방법

### 사전 준비
- JDK 17
- MySQL 실행 환경
- Gradle Wrapper 사용 가능 환경


프로젝트 구조
<img width="878" height="920" alt="image" src="https://github.com/user-attachments/assets/1941f69b-d633-4967-ade5-9341d642a63e" />


 팀 문서 링크 

Discussions: https://github.com/2026Cookie/CookieLMS2/discussions

Pull Requests: https://github.com/2026Cookie/CookieLMS2/pulls

Issues: https://github.com/2026Cookie/CookieLMS2/issues

Notion: https://www.notion.so/ohgiraffers/336649136c1180f6a16dfbbbbf56af8a


