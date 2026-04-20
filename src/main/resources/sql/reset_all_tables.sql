-- =====================================================
-- CoookieLMS 전체 테이블 초기화 스크립트
-- 실행 전 주의: 모든 데이터가 삭제됩니다.
-- =====================================================

USE CoookieLMS;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- DROP (자식 테이블 먼저)
-- =====================================================
DROP TABLE IF EXISTS api_performance_logs;
DROP TABLE IF EXISTS assignment_submissions;
DROP TABLE IF EXISTS assignments;
DROP TABLE IF EXISTS business_service_logs;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS error_logs;
DROP TABLE IF EXISTS flyway_schema_history;
DROP TABLE IF EXISTS lecture;
DROP TABLE IF EXISTS logs;
DROP TABLE IF EXISTS metrics;
DROP TABLE IF EXISTS user_bans;
DROP TABLE IF EXISTS user_logs;
DROP TABLE IF EXISTS waitlist;
DROP TABLE IF EXISTS users;

-- =====================================================
-- CREATE (부모 테이블 먼저)
-- =====================================================

CREATE TABLE users (
    user_id     BIGINT          NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL,
    id          VARCHAR(255)    NULL,
    password    VARCHAR(255)    NOT NULL,
    name        VARCHAR(255)    NULL,
    nickname    VARCHAR(255)    NULL,
    phone       VARCHAR(255)    NULL,
    role        ENUM('ADMIN','INSTRUCTOR','USER')  NULL,
    status      ENUM('ACTIVE','BANNED','DORMANT')  NULL,
    isDeleted   TINYINT(1)      NOT NULL DEFAULT 0,
    is_deleted  BIT(1)          NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE lecture (
    lecture_id          BIGINT          NOT NULL AUTO_INCREMENT,
    title               VARCHAR(255)    NULL,
    description         TEXT            NULL,
    max_capacity        INT             NOT NULL,
    current_enrollment  INT             NOT NULL DEFAULT 0,
    status              VARCHAR(20)     NOT NULL,
    lecture_day         VARCHAR(255)    NULL,
    start_time          TIME            NOT NULL,
    end_time            TIME            NOT NULL,
    video_url           VARCHAR(255)    NULL,
    thumbnail           VARCHAR(255)    NULL,
    material_id         VARCHAR(255)    NULL,
    file_origin_name    VARCHAR(255)    NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id             BIGINT          NOT NULL,
    PRIMARY KEY (lecture_id),
    CONSTRAINT lecture_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE assignments (
    assignment_id   BIGINT          NOT NULL AUTO_INCREMENT,
    title           VARCHAR(100)    NOT NULL,
    content         VARCHAR(255)    NOT NULL,
    due_date        DATETIME        NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lecture_id      BIGINT          NOT NULL,
    PRIMARY KEY (assignment_id),
    CONSTRAINT assignments_ibfk_1 FOREIGN KEY (lecture_id) REFERENCES lecture (lecture_id)
);

CREATE TABLE assignment_submissions (
    submission_id   BIGINT          NOT NULL AUTO_INCREMENT,
    assignment_id   BIGINT          NOT NULL,
    student_id      BIGINT          NOT NULL,
    file_id         BIGINT          NOT NULL,
    submitted_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    score           BIGINT          NULL,
    feedback        VARCHAR(255)    NULL,
    PRIMARY KEY (submission_id),
    CONSTRAINT assignment_submissions_ibfk_1 FOREIGN KEY (assignment_id) REFERENCES assignments (assignment_id),
    CONSTRAINT assignment_submissions_ibfk_2 FOREIGN KEY (student_id)    REFERENCES users (user_id)
);

CREATE TABLE enrollment (
    enrollment_id   BIGINT          NOT NULL AUTO_INCREMENT,
    status          VARCHAR(20)     NOT NULL,
    user_id         BIGINT          NOT NULL,
    lecture_id      BIGINT          NOT NULL,
    enrolled_at     DATETIME(6)     NOT NULL,
    PRIMARY KEY (enrollment_id),
    CONSTRAINT enrollment_ibfk_1 FOREIGN KEY (user_id)    REFERENCES users (user_id),
    CONSTRAINT enrollment_ibfk_2 FOREIGN KEY (lecture_id) REFERENCES lecture (lecture_id)
);

CREATE TABLE waitlist (
    waitlist_id BIGINT      NOT NULL AUTO_INCREMENT,
    status      VARCHAR(20) NOT NULL,
    wait_number INT         NOT NULL,
    user_id     BIGINT      NOT NULL,
    lecture_id  BIGINT      NOT NULL,
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (waitlist_id),
    CONSTRAINT waitlist_ibfk_1 FOREIGN KEY (user_id)    REFERENCES users (user_id),
    CONSTRAINT waitlist_ibfk_2 FOREIGN KEY (lecture_id) REFERENCES lecture (lecture_id)
);

CREATE TABLE api_performance_logs (
    log_id              BIGINT                                  NOT NULL AUTO_INCREMENT,
    user_id             BIGINT                                  NULL,
    endpoint            VARCHAR(255)                            NOT NULL,
    http_method         ENUM('GET','POST','PUT','DELETE')        NOT NULL,
    status_code         INT                                     NOT NULL,
    execution_time_ms   INT                                     NOT NULL,
    client_ip           VARCHAR(255)                            NULL,
    created_at          DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    CONSTRAINT api_performance_logs_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id),
    INDEX idx_endpoint   (endpoint),
    INDEX idx_created_at (created_at)
);

CREATE TABLE business_service_logs (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    class_method        VARCHAR(255)    NOT NULL,
    created_at          DATETIME(6)     NOT NULL,
    execution_time_ms   BIGINT          NOT NULL,
    is_success          BIT(1)          NOT NULL,
    method_name         VARCHAR(100)    NOT NULL,
    service_name        VARCHAR(100)    NOT NULL,
    trace_id            VARCHAR(36)     NULL,
    user_id             BIGINT          NULL,
    PRIMARY KEY (id),
    INDEX idx_class_method (class_method),
    INDEX idx_created_at   (created_at),
    INDEX idx_is_success   (is_success),
    INDEX idx_trace_id     (trace_id)
);

CREATE TABLE error_logs (
    id              BIGINT                              NOT NULL AUTO_INCREMENT,
    client_ip       VARCHAR(50)                         NULL,
    created_at      DATETIME(6)                         NULL,
    error_code      VARCHAR(50)                         NOT NULL,
    error_message   VARCHAR(500)                        NOT NULL,
    exception_name  VARCHAR(100)                        NOT NULL,
    http_method     VARCHAR(10)                         NOT NULL,
    request_uri     VARCHAR(500)                        NOT NULL,
    severity        ENUM('CRITICAL','INFO','WARNING')   NULL,
    stack_trace     TEXT                                NULL,
    trace_id        VARCHAR(36)                         NULL,
    user_id         VARCHAR(50)                         NULL,
    PRIMARY KEY (id),
    INDEX idx_trace_id (trace_id),
    INDEX idx_user_id  (user_id)
);

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- INSERT: users (lecture FK 대상이므로 먼저 삽입)
-- =====================================================

INSERT INTO users (email, id, password, name, nickname, phone, role, status, isDeleted, is_deleted, created_at, updated_at, deleted_at)
VALUES
-- ADMIN 2명
('admin@cookielms.com', 'admin', '$2a$10$hashedpassword1234567890abcdef', '운영자', '관리자', '010-0000-0001', 'ADMIN', 'ACTIVE', 0, 0, '2024-01-01 09:00:00', '2024-01-01 09:00:00', NULL),
('user54@cookielms.com', 'admin1', '$2a$10$wfGTAjR94RnWzUYmUTjovurHITcpSrl4KgqGfL2jEKTaRIrYjowoq', '운영자', '관리자', '010-0000-0002', 'ADMIN', 'ACTIVE', 0, 0, '2024-01-01 09:00:00', '2024-01-01 09:00:00', NULL),


-- INSTRUCTOR 11명
('inst01@cookielms.com', 'inst01', '$2a$10$hashedpassword1234567890abcdef', '김자바',   '자바킹',      '010-1001-0001', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-05 10:00:00', '2024-01-05 10:00:00', NULL),
('inst02@cookielms.com', 'inst02', '$2a$10$hashedpassword1234567890abcdef', '이스프링', '스프링마스터', '010-1001-0002', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-06 10:00:00', '2024-01-06 10:00:00', NULL),
('inst03@cookielms.com', 'inst03', '$2a$10$hashedpassword1234567890abcdef', '박리액트', '프론트고수',   '010-1001-0003', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-07 10:00:00', '2024-01-07 10:00:00', NULL),
('inst04@cookielms.com', 'inst04', '$2a$10$hashedpassword1234567890abcdef', '최데이터', 'DB전문가',     '010-1001-0004', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-08 10:00:00', '2024-01-08 10:00:00', NULL),
('inst05@cookielms.com', 'inst05', '$2a$10$hashedpassword1234567890abcdef', '정파이썬', '파이썬마법사', '010-1001-0005', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-09 10:00:00', '2024-01-09 10:00:00', NULL),
('inst06@cookielms.com', 'inst06', '$2a$10$hashedpassword1234567890abcdef', '한클라우드','클라우드guru', '010-1001-0006', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-10 10:00:00', '2024-01-10 10:00:00', NULL),
('inst07@cookielms.com', 'inst07', '$2a$10$hashedpassword1234567890abcdef', '오알고리즘','알고리즘왕',  '010-1001-0007', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-11 10:00:00', '2024-01-11 10:00:00', NULL),
('inst08@cookielms.com', 'inst08', '$2a$10$hashedpassword1234567890abcdef', '서네트워크','네트워크박사', '010-1001-0008', 'INSTRUCTOR', 'DORMANT', 0, 0, '2024-01-12 10:00:00', '2024-03-01 10:00:00', NULL),
('inst09@cookielms.com', 'inst09', '$2a$10$hashedpassword1234567890abcdef', '윤보안',   '보안전문가',  '010-1001-0009', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-13 10:00:00', '2024-01-13 10:00:00', NULL),
('inst10@cookielms.com', 'inst10', '$2a$10$hashedpassword1234567890abcdef', '임도커',   '도커쿠버왕',  '010-1001-0010', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-14 10:00:00', '2024-01-14 10:00:00', NULL),
('inst11@cookielms.com', 'inst1', '$2a$10$5LnyRwCc3f9TrgXdB8aQEeYX.2Eg2nWxq4HrKgoRU5/Z8hpEKV2ti', '오데이터',   '자바마스터',  '010-1001-0012', 'INSTRUCTOR', 'ACTIVE',  0, 0, '2024-01-14 10:00:00', '2024-01-14 10:00:00', NULL),


-- USER (학생) 50명
('user01@cookielms.com', 'user01', '$2a$10$hashedpassword1234567890abcdef', '강민준', '코딩초보',       '010-2001-0001', 'USER', 'ACTIVE',  0, 0, '2024-02-01 11:00:00', '2024-02-01 11:00:00', NULL),
('user02@cookielms.com', 'user02', '$2a$10$hashedpassword1234567890abcdef', '고서연', '열공러',         '010-2001-0002', 'USER', 'ACTIVE',  0, 0, '2024-02-01 11:05:00', '2024-02-01 11:05:00', NULL),
('user03@cookielms.com', 'user03', '$2a$10$hashedpassword1234567890abcdef', '권도윤', '개발지망생',     '010-2001-0003', 'USER', 'ACTIVE',  0, 0, '2024-02-02 11:00:00', '2024-02-02 11:00:00', NULL),
('user04@cookielms.com', 'user04', '$2a$10$hashedpassword1234567890abcdef', '김하은', '자바공부중',     '010-2001-0004', 'USER', 'ACTIVE',  0, 0, '2024-02-02 11:10:00', '2024-02-02 11:10:00', NULL),
('user05@cookielms.com', 'user05', '$2a$10$hashedpassword1234567890abcdef', '나지훈', '스프링입문',     '010-2001-0005', 'USER', 'ACTIVE',  0, 0, '2024-02-03 11:00:00', '2024-02-03 11:00:00', NULL),
('user06@cookielms.com', 'user06', '$2a$10$hashedpassword1234567890abcdef', '남수빈', '프론트지망',     '010-2001-0006', 'USER', 'DORMANT', 0, 0, '2024-02-03 11:15:00', '2024-05-01 09:00:00', NULL),
('user07@cookielms.com', 'user07', '$2a$10$hashedpassword1234567890abcdef', '노재원', '백엔드꿈나무',   '010-2001-0007', 'USER', 'ACTIVE',  0, 0, '2024-02-04 11:00:00', '2024-02-04 11:00:00', NULL),
('user08@cookielms.com', 'user08', '$2a$10$hashedpassword1234567890abcdef', '류지아', '열정개발자',     '010-2001-0008', 'USER', 'ACTIVE',  0, 0, '2024-02-04 11:20:00', '2024-02-04 11:20:00', NULL),
('user09@cookielms.com', 'user09', '$2a$10$hashedpassword1234567890abcdef', '문승현', '취준생',         '010-2001-0009', 'USER', 'ACTIVE',  0, 0, '2024-02-05 11:00:00', '2024-02-05 11:00:00', NULL),
('user10@cookielms.com', 'user10', '$2a$10$hashedpassword1234567890abcdef', '박지민', '코테준비중',     '010-2001-0010', 'USER', 'ACTIVE',  0, 0, '2024-02-05 11:25:00', '2024-02-05 11:25:00', NULL),
('user11@cookielms.com', 'user11', '$2a$10$hashedpassword1234567890abcdef', '배현우', '신입개발자',     '010-2001-0011', 'USER', 'ACTIVE',  0, 0, '2024-02-06 11:00:00', '2024-02-06 11:00:00', NULL),
('user12@cookielms.com', 'user12', '$2a$10$hashedpassword1234567890abcdef', '백소희', '디자인개발',     '010-2001-0012', 'USER', 'BANNED',  1, 1, '2024-02-06 11:30:00', '2024-04-10 09:00:00', NULL),
('user13@cookielms.com', 'user13', '$2a$10$hashedpassword1234567890abcdef', '서준혁', '알고리즘연습',   '010-2001-0013', 'USER', 'ACTIVE',  0, 0, '2024-02-07 11:00:00', '2024-02-07 11:00:00', NULL),
('user14@cookielms.com', 'user14', '$2a$10$hashedpassword1234567890abcdef', '성다은', '풀스택목표',     '010-2001-0014', 'USER', 'ACTIVE',  0, 0, '2024-02-07 11:35:00', '2024-02-07 11:35:00', NULL),
('user15@cookielms.com', 'user15', '$2a$10$hashedpassword1234567890abcdef', '손민서', '자료구조공부',   '010-2001-0015', 'USER', 'ACTIVE',  0, 0, '2024-02-08 11:00:00', '2024-02-08 11:00:00', NULL),
('user16@cookielms.com', 'user16', '$2a$10$hashedpassword1234567890abcdef', '신예준', '클라우드입문',   '010-2001-0016', 'USER', 'DORMANT', 0, 0, '2024-02-08 11:40:00', '2024-05-15 09:00:00', NULL),
('user17@cookielms.com', 'user17', '$2a$10$hashedpassword1234567890abcdef', '심지유', '데이터분석관심', '010-2001-0017', 'USER', 'ACTIVE',  0, 0, '2024-02-09 11:00:00', '2024-02-09 11:00:00', NULL),
('user18@cookielms.com', 'user18', '$2a$10$hashedpassword1234567890abcdef', '안도현', 'CS기초다지기',   '010-2001-0018', 'USER', 'ACTIVE',  0, 0, '2024-02-09 11:45:00', '2024-02-09 11:45:00', NULL),
('user19@cookielms.com', 'user19', '$2a$10$hashedpassword1234567890abcdef', '양하진', '파이썬배우는중', '010-2001-0019', 'USER', 'ACTIVE',  0, 0, '2024-02-10 11:00:00', '2024-02-10 11:00:00', NULL),
('user20@cookielms.com', 'user20', '$2a$10$hashedpassword1234567890abcdef', '엄재훈', '네트워크공부',   '010-2001-0020', 'USER', 'ACTIVE',  0, 0, '2024-02-10 11:50:00', '2024-02-10 11:50:00', NULL),
('user21@cookielms.com', 'user21', '$2a$10$hashedpassword1234567890abcdef', '오수정', '리눅스입문',     '010-2001-0021', 'USER', 'ACTIVE',  0, 0, '2024-02-11 11:00:00', '2024-02-11 11:00:00', NULL),
('user22@cookielms.com', 'user22', '$2a$10$hashedpassword1234567890abcdef', '우태양', 'git배우는중',    '010-2001-0022', 'USER', 'ACTIVE',  0, 0, '2024-02-11 11:55:00', '2024-02-11 11:55:00', NULL),
('user23@cookielms.com', 'user23', '$2a$10$hashedpassword1234567890abcdef', '원지은', 'JPA공부중',      '010-2001-0023', 'USER', 'ACTIVE',  0, 0, '2024-02-12 11:00:00', '2024-02-12 11:00:00', NULL),
('user24@cookielms.com', 'user24', '$2a$10$hashedpassword1234567890abcdef', '유승민', 'REST공부',       '010-2001-0024', 'USER', 'BANNED',  1, 1, '2024-02-12 12:00:00', '2024-04-20 09:00:00', NULL),
('user25@cookielms.com', 'user25', '$2a$10$hashedpassword1234567890abcdef', '윤채원', '보안관심多',     '010-2001-0025', 'USER', 'ACTIVE',  0, 0, '2024-02-13 11:00:00', '2024-02-13 11:00:00', NULL),
('user26@cookielms.com', 'user26', '$2a$10$hashedpassword1234567890abcdef', '이건우', 'MSA공부중',      '010-2001-0026', 'USER', 'ACTIVE',  0, 0, '2024-02-13 12:05:00', '2024-02-13 12:05:00', NULL),
('user27@cookielms.com', 'user27', '$2a$10$hashedpassword1234567890abcdef', '이나현', '도커입문자',     '010-2001-0027', 'USER', 'ACTIVE',  0, 0, '2024-02-14 11:00:00', '2024-02-14 11:00:00', NULL),
('user28@cookielms.com', 'user28', '$2a$10$hashedpassword1234567890abcdef', '이도훈', '쿠버네티스공부', '010-2001-0028', 'USER', 'ACTIVE',  0, 0, '2024-02-14 12:10:00', '2024-02-14 12:10:00', NULL),
('user29@cookielms.com', 'user29', '$2a$10$hashedpassword1234567890abcdef', '이민아', 'CI/CD공부',      '010-2001-0029', 'USER', 'DORMANT', 0, 0, '2024-02-15 11:00:00', '2024-06-01 09:00:00', NULL),
('user30@cookielms.com', 'user30', '$2a$10$hashedpassword1234567890abcdef', '이서윤', 'TDD연습중',      '010-2001-0030', 'USER', 'ACTIVE',  0, 0, '2024-02-15 12:15:00', '2024-02-15 12:15:00', NULL),
('user31@cookielms.com', 'user31', '$2a$10$hashedpassword1234567890abcdef', '이재현', 'Redis공부',      '010-2001-0031', 'USER', 'ACTIVE',  0, 0, '2024-02-16 11:00:00', '2024-02-16 11:00:00', NULL),
('user32@cookielms.com', 'user32', '$2a$10$hashedpassword1234567890abcdef', '임수연', 'Kafka입문',      '010-2001-0032', 'USER', 'ACTIVE',  0, 0, '2024-02-16 12:20:00', '2024-02-16 12:20:00', NULL),
('user33@cookielms.com', 'user33', '$2a$10$hashedpassword1234567890abcdef', '장민호', 'OAuth공부중',    '010-2001-0033', 'USER', 'ACTIVE',  0, 0, '2024-02-17 11:00:00', '2024-02-17 11:00:00', NULL),
('user34@cookielms.com', 'user34', '$2a$10$hashedpassword1234567890abcdef', '전소율', 'JWT배우는중',    '010-2001-0034', 'USER', 'ACTIVE',  0, 0, '2024-02-17 12:25:00', '2024-02-17 12:25:00', NULL),
('user35@cookielms.com', 'user35', '$2a$10$hashedpassword1234567890abcdef', '정다훈', '테스트코드연습', '010-2001-0035', 'USER', 'ACTIVE',  0, 0, '2024-02-18 11:00:00', '2024-02-18 11:00:00', NULL),
('user36@cookielms.com', 'user36', '$2a$10$hashedpassword1234567890abcdef', '조은서', '리팩토링공부',   '010-2001-0036', 'USER', 'ACTIVE',  0, 0, '2024-02-18 12:30:00', '2024-02-18 12:30:00', NULL),
('user37@cookielms.com', 'user37', '$2a$10$hashedpassword1234567890abcdef', '주현진', '디자인패턴공부', '010-2001-0037', 'USER', 'DORMANT', 0, 0, '2024-02-19 11:00:00', '2024-06-10 09:00:00', NULL),
('user38@cookielms.com', 'user38', '$2a$10$hashedpassword1234567890abcdef', '차지호', 'SQL고수목표',    '010-2001-0038', 'USER', 'ACTIVE',  0, 0, '2024-02-19 12:35:00', '2024-02-19 12:35:00', NULL),
('user39@cookielms.com', 'user39', '$2a$10$hashedpassword1234567890abcdef', '채수민', '인덱스공부중',   '010-2001-0039', 'USER', 'ACTIVE',  0, 0, '2024-02-20 11:00:00', '2024-02-20 11:00:00', NULL),
('user40@cookielms.com', 'user40', '$2a$10$hashedpassword1234567890abcdef', '최나연', '쿼리최적화중',   '010-2001-0040', 'USER', 'ACTIVE',  0, 0, '2024-02-20 12:40:00', '2024-02-20 12:40:00', NULL),
('user41@cookielms.com', 'user41', '$2a$10$hashedpassword1234567890abcdef', '최민재', '트랜잭션공부',   '010-2001-0041', 'USER', 'ACTIVE',  0, 0, '2024-02-21 11:00:00', '2024-02-21 11:00:00', NULL),
('user42@cookielms.com', 'user42', '$2a$10$hashedpassword1234567890abcdef', '추서현', '동시성공부중',   '010-2001-0042', 'USER', 'ACTIVE',  0, 0, '2024-02-21 12:45:00', '2024-02-21 12:45:00', NULL),
('user43@cookielms.com', 'user43', '$2a$10$hashedpassword1234567890abcdef', '탁준서', 'AOP입문자',      '010-2001-0043', 'USER', 'ACTIVE',  0, 0, '2024-02-22 11:00:00', '2024-02-22 11:00:00', NULL),
('user44@cookielms.com', 'user44', '$2a$10$hashedpassword1234567890abcdef', '편지원', '필터인터셉터공부','010-2001-0044', 'USER', 'ACTIVE',  0, 0, '2024-02-22 12:50:00', '2024-02-22 12:50:00', NULL),
('user45@cookielms.com', 'user45', '$2a$10$hashedpassword1234567890abcdef', '하승연', '시큐리티공부',   '010-2001-0045', 'USER', 'ACTIVE',  0, 0, '2024-02-23 11:00:00', '2024-02-23 11:00:00', NULL),
('user46@cookielms.com', 'user46', '$2a$10$hashedpassword1234567890abcdef', '한가영', '스케줄러공부',   '010-2001-0046', 'USER', 'BANNED',  1, 1, '2024-02-23 12:55:00', '2024-05-05 09:00:00', NULL),
('user47@cookielms.com', 'user47', '$2a$10$hashedpassword1234567890abcdef', '허민찬', '배치처리공부',   '010-2001-0047', 'USER', 'ACTIVE',  0, 0, '2024-02-24 11:00:00', '2024-02-24 11:00:00', NULL),
('user48@cookielms.com', 'user48', '$2a$10$hashedpassword1234567890abcdef', '홍다인', 'WebSocket입문',  '010-2001-0048', 'USER', 'ACTIVE',  0, 0, '2024-02-24 13:00:00', '2024-02-24 13:00:00', NULL),
('user49@cookielms.com', 'user49', '$2a$10$hashedpassword1234567890abcdef', '황준영', '백엔드입문',     '010-2001-0049', 'USER', 'ACTIVE',  0, 0, '2024-02-25 11:00:00', '2024-02-25 11:00:00', NULL),
('user50@cookielms.com', 'user50', '$2a$10$hashedpassword1234567890abcdef', '황지수', '개발공부중',     '010-2001-0050', 'USER', 'ACTIVE',  0, 0, '2024-02-25 13:05:00', '2024-02-25 13:05:00', NULL),
('user51@cookielms.com', 'user1', '$2a$10$gLacJKgCmIdEFGsWhLKkpOpuSxe.l8zT9HTQD2vc3hSEoGEZvKGie', '하승주', '백엔드고수',     '010-2001-0051', 'USER', 'ACTIVE',  0, 0, '2024-02-25 13:05:00', '2024-02-25 13:05:00', NULL),
('user52@cookielms.com', 'user2', '$2a$10$z4BHQYZK5W2egSonVECBX.q6X3dSruZdIMIT4JrCBXir.xie4R7z6', '홍다희', '자바초보',     '010-2002-0051', 'USER', 'ACTIVE',  0, 0, '2024-02-25 13:05:00', '2024-02-25 13:05:00', NULL),
('user53@cookielms.com', 'user3', '$2a$10$eH5TnPmyJGRLVW0wvel1Vejj31JFa9bPeRgpcRoTIe2wF1ioxABwK', '최민원', '트랜잭션초보',     '010-2402-0051', 'USER', 'ACTIVE',  0, 0, '2024-02-25 13:05:00', '2024-02-25 13:05:00', NULL);




-- =====================================================
-- INSERT: lecture (users 삽입 후)
-- =====================================================

INSERT INTO lecture (title, description, max_capacity, current_enrollment, status, lecture_day, start_time, end_time, video_url, thumbnail, material_id, user_id)
VALUES
('스프링 부트 입문',      '초보자를 위한 완벽 가이드',      30, 10, 'ACTIVE', 'MON', '10:00:00', '12:00:00', 'http://video.url/1',   'thumb_01.png',  'pdf_spring_01.pdf',      2),
('자바 객체지향의 원리',   '객체지향 설계부터 적용까지',     25,  5, 'ACTIVE', 'TUE', '14:00:00', '16:00:00', 'http://video.url/2',   'thumb_02.png',  'pdf_java_01.pdf',        2),
('JPA 실무 완전 정복',    'N+1 문제 해결 노하우',           40, 39, 'ACTIVE', 'WED', '19:00:00', '21:00:00', 'http://video.url/3',   'thumb_03.png',  'pdf_jpa_01.pdf',         2),
('AWS 배포 기초',         'EC2와 RDS 세팅하기',             20, 20, 'ACTIVE', 'THU', '10:00:00', '13:00:00', 'http://video.url/4',   'thumb_04.png',  'pdf_aws_01.pdf',         2),
('클린 코드 작성법',       '유지보수하기 좋은 코드',         50, 15, 'ACTIVE', 'FRI', '15:00:00', '18:00:00', 'http://video.url/5',   'thumb_05.png',  'pdf_clean_01.pdf',       2),
('프론트엔드 연동',        'REST API 통신 완벽 이해',        30,  2, 'ACTIVE', 'SAT', '13:00:00', '15:00:00', 'http://video.url/6',   'thumb_06.png',  'pdf_front_01.pdf',       2),
('팀 프로젝트 설계',       'ERD와 API 명세서 작성법',        30,  8, 'ACTIVE', 'SUN', '20:00:00', '22:00:00', 'http://video.url/7',   'thumb_07.png',  'pdf_project_01.pdf',     2),
('도커 입문',             '컨테이너 기술의 시작',            35, 20, 'ACTIVE', 'MON', '09:00:00', '11:00:00', 'http://video.url/8',   'thumb_08.png',  'pdf_docker_01.pdf',      3),
('쿠버네티스 기초',        'K8s 클러스터 운영하기',          25, 12, 'ACTIVE', 'TUE', '18:00:00', '20:00:00', 'http://video.url/9',   'thumb_09.png',  'pdf_k8s_01.pdf',         3),
('MySQL 튜닝',            '인덱스와 쿼리 최적화',            30, 18, 'ACTIVE', 'WED', '10:00:00', '12:00:00', 'http://video.url/10',  'thumb_10.png',  'pdf_mysql_01.pdf',       3),
('Redis 캐싱 전략',       '성능 향상을 위한 캐시 설계',      20,  9, 'ACTIVE', 'THU', '14:00:00', '16:00:00', 'http://video.url/11',  'thumb_11.png',  'pdf_redis_01.pdf',       3),
('카프카 메시지 큐',       '분산 메시징 시스템 이해',         30, 14, 'ACTIVE', 'FRI', '19:00:00', '21:00:00', 'http://video.url/12',  'thumb_12.png',  'pdf_kafka_01.pdf',       3),
('Git 브랜치 전략',       'GitFlow와 협업 노하우',           40, 30, 'ACTIVE', 'SAT', '10:00:00', '12:00:00', 'http://video.url/13',  'thumb_13.png',  'pdf_git_01.pdf',         3),
('CI/CD 파이프라인',      'Jenkins와 GitHub Actions',       25, 11, 'ACTIVE', 'SUN', '14:00:00', '16:00:00', 'http://video.url/14',  'thumb_14.png',  'pdf_cicd_01.pdf',        3),
('Spring Security',       '인증과 인가 완전 정복',           35, 25, 'ACTIVE', 'MON', '15:00:00', '17:00:00', 'http://video.url/15',  'thumb_15.png',  'pdf_security_01.pdf',    4),
('OAuth2 소셜 로그인',    'JWT와 OAuth2 실전 적용',          30, 17, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/16',  'thumb_16.png',  'pdf_oauth_01.pdf',       4),
('마이크로서비스 아키텍처', 'MSA 설계와 운영',               20,  6, 'ACTIVE', 'WED', '14:00:00', '17:00:00', 'http://video.url/17',  'thumb_17.png',  'pdf_msa_01.pdf',         4),
('GraphQL API 설계',      'REST를 넘어선 API',               25, 10, 'ACTIVE', 'THU', '19:00:00', '21:00:00', 'http://video.url/18',  'thumb_18.png',  'pdf_graphql_01.pdf',     4),
('TDD 테스트 주도 개발',   'JUnit5와 Mockito 실전',          30, 22, 'ACTIVE', 'FRI', '10:00:00', '12:00:00', 'http://video.url/19',  'thumb_19.png',  'pdf_tdd_01.pdf',         4),
('Thymeleaf 템플릿',      '서버사이드 렌더링 마스터',         35, 19, 'ACTIVE', 'SAT', '15:00:00', '17:00:00', 'http://video.url/20',  'thumb_20.png',  'pdf_thymeleaf_01.pdf',   4),
('리액트 기초',            '컴포넌트와 상태 관리',            40, 35, 'ACTIVE', 'SUN', '10:00:00', '13:00:00', 'http://video.url/21',  'thumb_21.png',  'pdf_react_01.pdf',       5),
('타입스크립트 입문',      '정적 타입으로 안전한 코드',        30, 16, 'ACTIVE', 'MON', '19:00:00', '21:00:00', 'http://video.url/22',  'thumb_22.png',  'pdf_ts_01.pdf',          5),
('Vue.js 실전',           'Composition API 완벽 가이드',     25, 13, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/23',  'thumb_23.png',  'pdf_vue_01.pdf',         5),
('Next.js SSR',           '서버 사이드 렌더링 최적화',        20,  7, 'ACTIVE', 'WED', '15:00:00', '17:00:00', 'http://video.url/24',  'thumb_24.png',  'pdf_next_01.pdf',        5),
('Tailwind CSS',          '유틸리티 퍼스트 스타일링',         35, 28, 'ACTIVE', 'THU', '10:00:00', '12:00:00', 'http://video.url/25',  'thumb_25.png',  'pdf_tailwind_01.pdf',    5),
('파이썬 데이터 분석',     'Pandas와 NumPy 실전',             30, 21, 'ACTIVE', 'FRI', '14:00:00', '17:00:00', 'http://video.url/26',  'thumb_26.png',  'pdf_pandas_01.pdf',      5),
('머신러닝 입문',          'Scikit-learn으로 시작하기',        25,  9, 'ACTIVE', 'SAT', '10:00:00', '13:00:00', 'http://video.url/27',  'thumb_27.png',  'pdf_ml_01.pdf',          6),
('딥러닝 기초',            'PyTorch로 신경망 구현',            20,  4, 'ACTIVE', 'SUN', '15:00:00', '18:00:00', 'http://video.url/28',  'thumb_28.png',  'pdf_dl_01.pdf',          6),
('자연어 처리 NLP',        'BERT와 트랜스포머 이해',           15,  3, 'ACTIVE', 'MON', '20:00:00', '22:00:00', 'http://video.url/29',  'thumb_29.png',  'pdf_nlp_01.pdf',         6),
('컴퓨터 비전',            'CNN으로 이미지 분류하기',          20,  8, 'ACTIVE', 'TUE', '19:00:00', '21:00:00', 'http://video.url/30',  'thumb_30.png',  'pdf_cv_01.pdf',          6),
('알고리즘과 자료구조',    '코딩테스트 완전 정복',             50, 45, 'ACTIVE', 'WED', '10:00:00', '12:00:00', 'http://video.url/31',  'thumb_31.png',  'pdf_algo_01.pdf',        6),
('운영체제 기초',          '프로세스와 메모리 관리',           30, 11, 'ACTIVE', 'THU', '10:00:00', '12:00:00', 'http://video.url/32',  'thumb_32.png',  'pdf_os_01.pdf',          6),
('네트워크 기초',          'TCP/IP와 HTTP 완전 이해',          30, 14, 'ACTIVE', 'FRI', '14:00:00', '16:00:00', 'http://video.url/33',  'thumb_33.png',  'pdf_network_01.pdf',     7),
('데이터베이스 설계',      '정규화와 ERD 작성',                25, 18, 'ACTIVE', 'SAT', '10:00:00', '13:00:00', 'http://video.url/34',  'thumb_34.png',  'pdf_db_01.pdf',          7),
('NoSQL MongoDB',         '도큐먼트 DB 실전 활용',             20,  7, 'ACTIVE', 'SUN', '14:00:00', '16:00:00', 'http://video.url/35',  'thumb_35.png',  'pdf_mongo_01.pdf',       7),
('Elasticsearch',         '검색엔진 구축과 활용',              25, 12, 'ACTIVE', 'MON', '19:00:00', '21:00:00', 'http://video.url/36',  'thumb_36.png',  'pdf_es_01.pdf',          7),
('Gradle 빌드 자동화',    '멀티모듈 프로젝트 구성',            20,  5, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/37',  'thumb_37.png',  'pdf_gradle_01.pdf',      7),
('Lombok 완전 정복',      '보일러플레이트 코드 제거',           35, 29, 'ACTIVE', 'WED', '15:00:00', '17:00:00', 'http://video.url/38',  'thumb_38.png',  'pdf_lombok_01.pdf',      7),
('QueryDSL 실전',         '타입 안전 쿼리 작성법',             25, 16, 'ACTIVE', 'THU', '19:00:00', '21:00:00', 'http://video.url/39',  'thumb_39.png',  'pdf_querydsl_01.pdf',    8),
('Spring Batch',          '대용량 데이터 처리 전략',           20,  6, 'ACTIVE', 'FRI', '10:00:00', '13:00:00', 'http://video.url/40',  'thumb_40.png',  'pdf_batch_01.pdf',       8),
('WebSocket 채팅',        '실시간 통신 구현하기',              30, 23, 'ACTIVE', 'SAT', '14:00:00', '16:00:00', 'http://video.url/41',  'thumb_41.png',  'pdf_ws_01.pdf',          8),
('Spring Cloud',          '마이크로서비스 인프라 구성',         15,  4, 'ACTIVE', 'SUN', '19:00:00', '22:00:00', 'http://video.url/42',  'thumb_42.png',  'pdf_cloud_01.pdf',       8),
('Feign Client',          '선언형 HTTP 클라이언트',             20,  9, 'ACTIVE', 'MON', '10:00:00', '12:00:00', 'http://video.url/43',  'thumb_43.png',  'pdf_feign_01.pdf',       8),
('API 게이트웨이',         'Spring Cloud Gateway 설정',         20,  7, 'ACTIVE', 'TUE', '14:00:00', '16:00:00', 'http://video.url/44',  'thumb_44.png',  'pdf_gateway_01.pdf',     8),
('서킷 브레이커',          'Resilience4j 장애 대응',            15,  3, 'ACTIVE', 'WED', '20:00:00', '22:00:00', 'http://video.url/45',  'thumb_45.png',  'pdf_circuit_01.pdf',     9),
('분산 트랜잭션',          'Saga 패턴 구현하기',                15,  2, 'ACTIVE', 'THU', '10:00:00', '13:00:00', 'http://video.url/46',  'thumb_46.png',  'pdf_saga_01.pdf',        9),
('이벤트 소싱',            'CQRS 패턴과 이벤트 드리븐',         20,  8, 'ACTIVE', 'FRI', '15:00:00', '18:00:00', 'http://video.url/47',  'thumb_47.png',  'pdf_event_01.pdf',       9),
('헥사고날 아키텍처',      '포트와 어댑터 패턴',                20,  6, 'ACTIVE', 'SAT', '10:00:00', '13:00:00', 'http://video.url/48',  'thumb_48.png',  'pdf_hexa_01.pdf',        9),
('디자인 패턴 GoF',        '23가지 패턴 완전 정복',             35, 27, 'ACTIVE', 'SUN', '10:00:00', '12:00:00', 'http://video.url/49',  'thumb_49.png',  'pdf_gof_01.pdf',         9),
('SOLID 원칙',            '객체지향 설계 5원칙',                40, 31, 'ACTIVE', 'MON', '14:00:00', '16:00:00', 'http://video.url/50',  'thumb_50.png',  'pdf_solid_01.pdf',       9),
('리팩토링 기법',          '레거시 코드 개선 전략',             30, 19, 'ACTIVE', 'TUE', '19:00:00', '21:00:00', 'http://video.url/51',  'thumb_51.png',  'pdf_refactor_01.pdf',    10),
('코드 리뷰 문화',         '효과적인 피드백 주고받기',           25, 14, 'ACTIVE', 'WED', '10:00:00', '12:00:00', 'http://video.url/52',  'thumb_52.png',  'pdf_review_01.pdf',      10),
('기술 문서 작성',         'README와 API 문서화',               30, 10, 'ACTIVE', 'THU', '15:00:00', '17:00:00', 'http://video.url/53',  'thumb_53.png',  'pdf_docs_01.pdf',        10),
('Swagger 문서화',         'OpenAPI 3.0 완벽 가이드',           25, 13, 'ACTIVE', 'FRI', '10:00:00', '12:00:00', 'http://video.url/54',  'thumb_54.png',  'pdf_swagger_01.pdf',     10),
('Postman 활용',           'API 테스트 자동화하기',             30, 22, 'ACTIVE', 'SAT', '14:00:00', '16:00:00', 'http://video.url/55',  'thumb_55.png',  'pdf_postman_01.pdf',     10),
('성능 테스트',            'JMeter와 Gatling 실전',             20,  5, 'ACTIVE', 'SUN', '15:00:00', '17:00:00', 'http://video.url/56',  'thumb_56.png',  'pdf_perf_01.pdf',        10),
('모니터링 구축',          'Prometheus와 Grafana',              20,  8, 'ACTIVE', 'MON', '20:00:00', '22:00:00', 'http://video.url/57',  'thumb_57.png',  'pdf_monitor_01.pdf',      2),
('로그 관리',              'ELK 스택 구성하기',                 20,  6, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/58',  'thumb_58.png',  'pdf_elk_01.pdf',          2),
('알림 시스템 설계',       'FCM 푸시와 이메일 발송',            25, 11, 'ACTIVE', 'WED', '14:00:00', '16:00:00', 'http://video.url/59',  'thumb_59.png',  'pdf_notify_01.pdf',       3),
('파일 업로드 처리',       'S3 연동과 이미지 리사이징',          30, 17, 'ACTIVE', 'THU', '10:00:00', '12:00:00', 'http://video.url/60',  'thumb_60.png',  'pdf_s3_01.pdf',           3),
('결제 연동',              '포트원 PG 연동 실전',               20,  4, 'ACTIVE', 'FRI', '19:00:00', '22:00:00', 'http://video.url/61',  'thumb_61.png',  'pdf_payment_01.pdf',      4),
('이메일 인증',            'SMTP와 인증 토큰 구현',             25, 13, 'ACTIVE', 'SAT', '10:00:00', '12:00:00', 'http://video.url/62',  'thumb_62.png',  'pdf_email_01.pdf',        4),
('소셜 로그인',            '카카오 네이버 구글 연동',            35, 29, 'ACTIVE', 'SUN', '14:00:00', '16:00:00', 'http://video.url/63',  'thumb_63.png',  'pdf_social_01.pdf',       4),
('쿠키와 세션',            '상태 관리 완전 이해',               30, 20, 'ACTIVE', 'MON', '10:00:00', '12:00:00', 'http://video.url/64',  'thumb_64.png',  'pdf_session_01.pdf',      5),
('JWT 토큰 인증',          'Access/Refresh 토큰 전략',          40, 36, 'ACTIVE', 'TUE', '15:00:00', '17:00:00', 'http://video.url/65',  'thumb_65.png',  'pdf_jwt_01.pdf',          5),
('Spring AOP',            '횡단 관심사 분리하기',               25, 12, 'ACTIVE', 'WED', '19:00:00', '21:00:00', 'http://video.url/66',  'thumb_66.png',  'pdf_aop_01.pdf',          5),
('트랜잭션 관리',          '@Transactional 완전 정복',          30, 21, 'ACTIVE', 'THU', '10:00:00', '12:00:00', 'http://video.url/67',  'thumb_67.png',  'pdf_tx_01.pdf',           6),
('예외 처리 전략',         'GlobalExceptionHandler 설계',       35, 24, 'ACTIVE', 'FRI', '14:00:00', '16:00:00', 'http://video.url/68',  'thumb_68.png',  'pdf_exception_01.pdf',    6),
('페이징과 정렬',          'Pageable 완벽 활용',                30, 18, 'ACTIVE', 'SAT', '10:00:00', '12:00:00', 'http://video.url/69',  'thumb_69.png',  'pdf_page_01.pdf',         6),
('검색 기능 구현',         '동적 쿼리와 필터링',                25,  9, 'ACTIVE', 'SUN', '15:00:00', '17:00:00', 'http://video.url/70',  'thumb_70.png',  'pdf_search_01.pdf',       7),
('스케줄러 구현',          '@Scheduled 배치 작업',              20,  7, 'ACTIVE', 'MON', '20:00:00', '22:00:00', 'http://video.url/71',  'thumb_71.png',  'pdf_schedule_01.pdf',     7),
('비동기 처리',            '@Async와 CompletableFuture',        25, 10, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/72',  'thumb_72.png',  'pdf_async_01.pdf',        7),
('멀티스레드 프로그래밍',  '동시성 문제 해결하기',              20,  5, 'ACTIVE', 'WED', '14:00:00', '17:00:00', 'http://video.url/73',  'thumb_73.png',  'pdf_thread_01.pdf',       8),
('JVM 메모리 구조',        'GC 튜닝과 메모리 분석',             20,  4, 'ACTIVE', 'THU', '19:00:00', '21:00:00', 'http://video.url/74',  'thumb_74.png',  'pdf_jvm_01.pdf',          8),
('자바 스트림 API',        'Lambda와 함수형 프로그래밍',         35, 26, 'ACTIVE', 'FRI', '10:00:00', '12:00:00', 'http://video.url/75',  'thumb_75.png',  'pdf_stream_01.pdf',       8),
('제네릭과 컬렉션',        '타입 안전 컬렉션 활용',             30, 15, 'ACTIVE', 'SAT', '14:00:00', '16:00:00', 'http://video.url/76',  'thumb_76.png',  'pdf_generic_01.pdf',      9),
('리플렉션과 어노테이션',  '메타 프로그래밍 기법',              15,  3, 'ACTIVE', 'SUN', '10:00:00', '13:00:00', 'http://video.url/77',  'thumb_77.png',  'pdf_reflect_01.pdf',      9),
('직렬화와 역직렬화',      'Jackson JSON 완전 정복',            30, 19, 'ACTIVE', 'MON', '15:00:00', '17:00:00', 'http://video.url/78',  'thumb_78.png',  'pdf_jackson_01.pdf',      9),
('MapStruct 매핑',         'DTO 변환 자동화하기',               25, 14, 'ACTIVE', 'TUE', '19:00:00', '21:00:00', 'http://video.url/79',  'thumb_79.png',  'pdf_mapstruct_01.pdf',    10),
('Validation 처리',        '@Valid와 커스텀 검증',              30, 20, 'ACTIVE', 'WED', '10:00:00', '12:00:00', 'http://video.url/80',  'thumb_80.png',  'pdf_valid_01.pdf',        10),
('Rate Limiting',          'API 요청 속도 제한 구현',           20,  6, 'ACTIVE', 'THU', '14:00:00', '16:00:00', 'http://video.url/81',  'thumb_81.png',  'pdf_rate_01.pdf',          2),
('헬스체크 API',           'Spring Actuator 활용',              20,  8, 'ACTIVE', 'FRI', '10:00:00', '12:00:00', 'http://video.url/82',  'thumb_82.png',  'pdf_actuator_01.pdf',      2),
('멀티 모듈 프로젝트',     '모듈 분리와 의존성 관리',           20,  5, 'ACTIVE', 'SAT', '15:00:00', '18:00:00', 'http://video.url/83',  'thumb_83.png',  'pdf_module_01.pdf',        3),
('설정 외부화',            'Config Server와 환경 변수',         20,  7, 'ACTIVE', 'SUN', '10:00:00', '12:00:00', 'http://video.url/84',  'thumb_84.png',  'pdf_config_01.pdf',        3),
('시큐리티 필터',          'FilterChain 커스터마이징',          25, 11, 'ACTIVE', 'MON', '19:00:00', '21:00:00', 'http://video.url/85',  'thumb_85.png',  'pdf_filter_01.pdf',        4),
('CORS 설정',              '크로스 오리진 완전 이해',           30, 22, 'ACTIVE', 'TUE', '10:00:00', '12:00:00', 'http://video.url/86',  'thumb_86.png',  'pdf_cors_01.pdf',          4),
('XSS와 CSRF 방어',        '웹 보안 취약점 대응',               25, 10, 'ACTIVE', 'WED', '14:00:00', '16:00:00', 'http://video.url/87',  'thumb_87.png',  'pdf_security2_01.pdf',     5),
('SQL 인젝션 방어',        '안전한 쿼리 작성법',                30, 15, 'ACTIVE', 'THU', '19:00:00', '21:00:00', 'http://video.url/88',  'thumb_88.png',  'pdf_sqlinject_01.pdf',     5),
('H2 인메모리 DB',         '개발 환경 빠른 구성법',             35, 28, 'ACTIVE', 'FRI', '10:00:00', '12:00:00', 'http://video.url/89',  'thumb_89.png',  'pdf_h2_01.pdf',            6),
('테스트 컨테이너',        'Testcontainers DB 통합 테스트',     20,  4, 'ACTIVE', 'SAT', '14:00:00', '17:00:00', 'https://www.youtube.com/watch?v=sly2u8BIi9E',  'thumb_90.png',  'pdf_testcontainer_01.pdf', 6),
('깃허브 기초',            '깃 관리 방법의 이해',            25,  9, 'ACTIVE', 'SUN', '10:00:00', '12:00:00', 'https://www.youtube.com/watch?v=sly2u8BIi9E',  'thumb_91.png',  'pdf_acceptance_01.pdf',    7),
('아키텍처 테스트',        'ArchUnit 의존성 검증',              15,  2, 'ACTIVE', 'MON', '20:00:00', '22:00:00', 'http://video.url/92',  'thumb_92.png',  'pdf_archunit_01.pdf',      7),
('Flyway 마이그레이션',    'DB 스키마 버전 관리',               25, 12, 'ACTIVE', 'TUE', '14:00:00', '16:00:00', 'http://video.url/93',  'thumb_93.png',  'pdf_flyway_01.pdf',        8),
('Liquibase 활용',         '선언적 DB 변경 관리',               20,  5, 'ACTIVE', 'WED', '10:00:00', '12:00:00', 'http://video.url/94',  'thumb_94.png',  'pdf_liquibase_01.pdf',     8),
('Spring Data REST',       '레포지토리 자동 API화',             20,  7, 'ACTIVE', 'THU', '15:00:00', '17:00:00', 'http://video.url/95',  'thumb_95.png',  'pdf_datarest_01.pdf',      9),
('HATEOAS 적용',           '하이퍼미디어 API 설계',             15,  3, 'ACTIVE', 'FRI', '19:00:00', '21:00:00', 'http://video.url/96',  'thumb_96.png',  'pdf_hateoas_01.pdf',       9),
('API 버저닝',             'URL과 헤더 버전 전략',              25, 11, 'ACTIVE', 'SAT', '10:00:00', '12:00:00', 'http://video.url/97',  'thumb_97.png',  'pdf_versioning_01.pdf',    10),
('개발자 커리어 설계',     '포트폴리오와 이직 전략',            50, 47, 'ACTIVE', 'SUN', '14:00:00', '16:00:00', 'http://video.url/98',  'thumb_98.png',  'pdf_career_01.pdf',        10),
('오픈소스 기여하기',      'GitHub 오픈소스 PR 전략',           30, 13, 'ACTIVE', 'MON', '10:00:00', '12:00:00', 'http://video.url/99',  'thumb_99.png',  'pdf_opensource_01.pdf',     2),
('기술 블로그 운영',       '개발자 브랜딩과 글쓰기',            40, 33, 'ACTIVE', 'TUE', '19:00:00', '21:00:00', 'http://video.url/100', 'thumb_100.png', 'pdf_blog_01.pdf',           2);
