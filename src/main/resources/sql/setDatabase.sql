-- 1. 데이터베이스(스키마) 생성 (문자셋 설정 포함)
CREATE DATABASE CoookieLMS CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 새 유저 생성 및 비밀번호 설정 (로컬 접속 전용)
CREATE USER 'cookie'@'localhost' IDENTIFIED BY 'cookie';

-- 3. 생성한 유저에게 CoookieLMS 데이터베이스에 대한 모든 권한 부여
GRANT ALL PRIVILEGES ON CoookieLMS.* TO 'cookie'@'localhost';

-- 4. 변경된 권한을 메모리에 즉시 적용
FLUSH PRIVILEGES;