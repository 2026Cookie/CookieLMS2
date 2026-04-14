-- 1. 회원 테이블 (가장 최상위 부모)
CREATE TABLE `users` (
                         `user_id`    BIGINT                                 NOT NULL AUTO_INCREMENT,
                         `email`      VARCHAR(255)                           NOT NULL,
                         `id`         VARCHAR(20)                            NOT NULL,
                         `password`   VARCHAR(255)                           NOT NULL,
                         `name`       VARCHAR(5)                             NOT NULL,
                         `nickname`   VARCHAR(20)                            NOT NULL,
                         `phone`      VARCHAR(20)                            NOT NULL,
                         `created_at` DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `role`       ENUM('USER', 'ADMIN', 'INSTRUCTOR')    NOT NULL COMMENT '권한',
                         `status`     ENUM('ACTIVE', 'DORMANT', 'BANNED')    NOT NULL COMMENT '현재 상태',
                         `isDeleted`  BOOLEAN                                NOT NULL DEFAULT false COMMENT '탈퇴 여부',
                         `deletedAt`  DATETIME                               NULL     COMMENT '탈퇴 일시',
                         PRIMARY KEY (`user_id`)
);

-- 2. 강좌 테이블 (users를 참조함)
CREATE TABLE `lecture` (
                           `lecture_id`         BIGINT       NOT NULL AUTO_INCREMENT,
                           `title`              VARCHAR(100) NOT NULL COMMENT '강의 제목',
                           `description`        TEXT         NULL     COMMENT '강의 상세 정보',
                           `max_capacity`       INT          NOT NULL COMMENT '최대 정원',
                           `current_enrollment` INT          NOT NULL DEFAULT 0 COMMENT '현재 수강 인원',
                           `status`             VARCHAR(20)  NOT NULL COMMENT '수강 상태',
                           `lecture_day`        VARCHAR(10)  NOT NULL COMMENT '수업 요일',
                           `start_time`         TIME         NOT NULL COMMENT '시작 시간',
                           `end_time`           TIME         NOT NULL COMMENT '종료 시간',
                           `video_url`          VARCHAR(500) NULL     COMMENT '영상 주소',
                           `material_id`        VARCHAR(500) NULL     COMMENT '강의 자료',
                           `created_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '강의 생성 일시',
                           `user_id`            BIGINT       NOT NULL COMMENT '강사 ID',
                           PRIMARY KEY (`lecture_id`),
                           FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
);

-- 3. 유저 벤 테이블
CREATE TABLE `user_bans` (
                             `ban_id`     BIGINT   NOT NULL AUTO_INCREMENT,
                             `reason`     TEXT     NOT NULL COMMENT '벤 사유',
                             `is_active`  BOOLEAN  NOT NULL DEFAULT true COMMENT '활성 유무',
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                             `user_id`    BIGINT   NOT NULL COMMENT '회원 식별 번호',
                             PRIMARY KEY (`ban_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
);

-- 4. 유저 로그 테이블
CREATE TABLE `user_logs` (
                             `log_id`      BIGINT                            NOT NULL AUTO_INCREMENT,
                             `action_type` ENUM('LOGIN', 'TRANSACTION')      NOT NULL COMMENT '로그 유형',
                             `severity`    ENUM('INFO', 'WARNING', 'DANGER') NOT NULL COMMENT '중요도',
                             `ip_address`  VARCHAR(45)                       NOT NULL COMMENT 'IP 주소',
                             `created_at`  DATETIME                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                             `user_id`     BIGINT                            NOT NULL COMMENT '회원 식별 번호',
                             PRIMARY KEY (`log_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
);

-- 5. API 성능 로그 테이블
CREATE TABLE `api_performance_logs` (
                                        `log_id`            BIGINT                               NOT NULL AUTO_INCREMENT,
                                        `user_id`           BIGINT                               NULL     COMMENT '유저 식별 번호 (비회원 요청 시 NULL)',
                                        `endpoint`          VARCHAR(255)                         NOT NULL COMMENT '호출된 API 주소',
                                        `http_method`       ENUM('GET', 'POST', 'PUT', 'DELETE') NOT NULL COMMENT 'Http 응답상태코드',
                                        `status_code`       INT                                  NOT NULL,
                                        `execution_time_ms` INT                                  NOT NULL COMMENT '실행 속도',
                                        `client_ip`         VARCHAR(45)                          NOT NULL COMMENT '접속 IP',
                                        `created_at`        DATETIME                             NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                                        PRIMARY KEY (`log_id`),
                                        FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
);

-- 6. 수강 테이블 (enrollment)
CREATE TABLE `enrollment` (
                              `enrollment_id` BIGINT      NOT NULL AUTO_INCREMENT,
                              `status`        VARCHAR(20) NOT NULL COMMENT '수강 상태',
                              `user_id`       BIGINT      NOT NULL COMMENT '회원 식별 번호',
                              `lecture_id`    BIGINT      NOT NULL COMMENT '강좌 ID',
                              PRIMARY KEY (`enrollment_id`),
                              FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
                              FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE
);

-- 7. 수강 대기열 테이블 (waitlist)
CREATE TABLE `waitlist` (
                            `waitlist_id` BIGINT      NOT NULL AUTO_INCREMENT,
                            `status`      VARCHAR(20) NOT NULL COMMENT '대기 상태',
                            `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '대기 등록 일시',
                            `user_id`     BIGINT      NOT NULL COMMENT '학생 ID',
                            `lecture_id`  BIGINT      NOT NULL COMMENT '강의 ID',
                            PRIMARY KEY (`waitlist_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
                            FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE
);

-- 8. 과제 테이블
CREATE TABLE `assignments` (
                               `assignment_id` BIGINT       NOT NULL AUTO_INCREMENT,
                               `title`         VARCHAR(100) NOT NULL COMMENT '강의 제목',
                               `content`       VARCHAR(255) NOT NULL COMMENT '과제 내용',
                               `due_date`      DATETIME     NOT NULL COMMENT '마감 일시',
                               `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                               `lecture_id`    BIGINT       NOT NULL COMMENT '강의 ID',
                               PRIMARY KEY (`assignment_id`),
                               FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE
);

-- 9. 과제 제출 테이블 (assignment_submissions)
CREATE TABLE `assignment_submissions` (
                                          `submission_id` BIGINT       NOT NULL AUTO_INCREMENT,
                                          `assignment_id` BIGINT       NOT NULL COMMENT '제출 과제 ID',
                                          `student_id`    BIGINT       NOT NULL COMMENT '제출 학생 ID',
                                          `file_id`       BIGINT       NOT NULL COMMENT '파일 ID',
                                          `submitted_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '과제 제출 일시',
                                          `score`         BIGINT       NULL     COMMENT '과제 점수',
                                          `feedback`      VARCHAR(255) NULL     COMMENT '강사 피드백',
                                          PRIMARY KEY (`submission_id`),
                                          FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`assignment_id`) ON DELETE CASCADE,
                                          FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
);