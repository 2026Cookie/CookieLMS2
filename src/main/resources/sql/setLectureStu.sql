USE CoookieLMS;

-- 🌟 외래키 제약조건 무시 (안전하게 데이터를 밀어버리기 위해 필수!)
SET FOREIGN_KEY_CHECKS = 0;

-- 🌟 기존 데이터 초기화 (몇 번을 다시 실행해도 중복 에러가 나지 않습니다)
TRUNCATE TABLE `assignment_submissions`;
TRUNCATE TABLE `assignments`;
TRUNCATE TABLE `lecture`;

-- lecture 테이블에 thumbnail 컬럼이 없다면 아래 주석을 풀고 한 번만 실행하세요.
-- ALTER TABLE `lecture` ADD COLUMN `thumbnail` VARCHAR(500) NULL COMMENT '강의 썸네일 이미지 파일명';

-- =========================================================================================

-- 1. Lecture 데이터 삽입 (강사 ID 2, 3, 4번 배정 완료)
INSERT INTO `lecture` (`title`, `description`, `max_capacity`, `current_enrollment`, `status`, `lecture_day`, `start_time`, `end_time`, `video_url`, `thumbnail`, `material_id`, `user_id`) VALUES
            ('스프링 부트 입문', '초보자를 위한 완벽 가이드', 30, 10, 'ACTIVE', 'MON', '10:00:00', '12:00:00', 'http://video.url/1', 'thumb_01.png', 'pdf_spring_01.pdf', 2),
            ('자바 객체지향의 원리', '객체지향 설계부터 적용까지', 25, 5, 'ACTIVE', 'TUE', '14:00:00', '16:00:00', 'http://video.url/2', 'thumb_02.png', 'pdf_java_01.pdf', 2),
            ('JPA 실무 완전 정복', 'N+1 문제 해결 노하우', 40, 39, 'ACTIVE', 'WED', '19:00:00', '21:00:00', 'http://video.url/3', 'thumb_03.png', 'pdf_jpa_01.pdf', 3),
            ('AWS 배포 기초', 'EC2와 RDS 세팅하기', 20, 20, 'ACTIVE', 'THU', '10:00:00', '13:00:00', 'http://video.url/4', 'thumb_04.png', 'pdf_aws_01.pdf', 3),
            ('클린 코드 작성법', '유지보수하기 좋은 코드', 50, 15, 'ACTIVE', 'FRI', '15:00:00', '18:00:00', 'http://video.url/5', 'thumb_05.png', 'pdf_clean_01.pdf', 4),
                                                                                                                                                                                                ('프론트엔드 연동', 'REST API 통신 완벽 이해', 30, 2, 'ACTIVE', 'SAT', '13:00:00', '15:00:00', 'http://video.url/6', 'thumb_06.png', 'pdf_front_01.pdf', 4),
                                                                                                                                                                                                ('팀 프로젝트 설계', 'ERD와 API 명세서 작성법', 30, 8, 'ACTIVE', 'SUN', '20:00:00', '22:00:00', 'http://video.url/7', 'thumb_07.png', 'pdf_project_01.pdf', 2);

-- 2. 과제 데이터 삽입 (기한 테스트를 위해 날짜를 조정하여 바로 삽입!)
INSERT INTO `assignments` (`title`, `content`, `due_date`, `lecture_id`) VALUES
           ('스프링 부트 1주차 과제', '게시판 REST API 설계 및 구현하기', '2020-01-01 00:00:00', 1), -- 🚨 1번: 기한 만료 테스트용 (과거)
           ('자바 객체지향 연습', '인터페이스를 활용한 다형성 구현', '2026-12-31 23:59:59', 2), -- ✅ 2번: 정상 제출 테스트용 (미래)
           ('JPA 성능 최적화', 'N+1 문제 해결 보고서 작성', '2026-12-31 23:59:59', 3);

-- 3. 과제 제출 데이터 삽입 (학생 ID 10번이 1번 과제를 제출했다고 가정, file_id는 임시로 999)
INSERT INTO `assignment_submissions` (`assignment_id`, `student_id`, `file_id`, `submitted_at`) VALUES
           (1, 10, 999, NOW());

-- =========================================================================================

-- 🌟 외래키 제약조건 복구 (다시 잠금)
SET FOREIGN_KEY_CHECKS = 1;