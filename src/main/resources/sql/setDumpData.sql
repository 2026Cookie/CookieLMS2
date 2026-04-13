USE CoookieLMS;

INSERT INTO `users` (`email`, `id`, `password`, `name`, `nickname`, `phone`, `role`, `status`, `isDeleted`, `deletedAt`) VALUES


-- 관리자 1명
('admin@cookielms.com', 'admin01', '$2a$10$hashedpassword001', '홍성희', 'CookieAdmin', '010-0000-0001', 'ADMIN', 'ACTIVE', false, NULL),


-- 강사 8명
('instructor01@cookielms.com', 'inst01', '$2a$10$hashedpassword002', '이강사', '자바마스터', '010-1001-0001', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor02@cookielms.com', 'inst02', '$2a$10$hashedpassword003', '박강사', '스프링왕', '010-1001-0002', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor03@cookielms.com', 'inst03', '$2a$10$hashedpassword004', '최강사', 'DB전문가', '010-1001-0003', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor04@cookielms.com', 'inst04', '$2a$10$hashedpassword005', '정강사', '알고리즘', '010-1001-0004', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor05@cookielms.com', 'inst05', '$2a$10$hashedpassword006', '한강사', '프론트엔드', '010-1001-0005', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor06@cookielms.com', 'inst06', '$2a$10$hashedpassword007', '윤강사', 'DevOps왕', '010-1001-0006', 'INSTRUCTOR', 'DORMANT', false, NULL),
('instructor07@cookielms.com', 'inst07', '$2a$10$hashedpassword008', '강강사', 'AI전문가', '010-1001-0007', 'INSTRUCTOR', 'ACTIVE', false, NULL),
('instructor08@cookielms.com', 'inst08', '$2a$10$hashedpassword009', '조강사', '보안전문가', '010-1001-0008', 'INSTRUCTOR', 'ACTIVE', false, NULL),


-- 수강생 20명
('user01@gmail.com', 'user01', '$2a$10$hashedpassword010', '김민수', '민수짱', '010-2001-0001', 'USER', 'ACTIVE', false, NULL),
('user02@gmail.com', 'user02', '$2a$10$hashedpassword011', '이지은', '지은이', '010-2001-0002', 'USER', 'ACTIVE', false, NULL),
('user03@gmail.com', 'user03', '$2a$10$hashedpassword012', '박준호', '준호킴', '010-2001-0003', 'USER', 'ACTIVE', false, NULL),
('user04@gmail.com', 'user04', '$2a$10$hashedpassword013', '최유진', '유진짱', '010-2001-0004', 'USER', 'ACTIVE', false, NULL),
('user05@gmail.com', 'user05', '$2a$10$hashedpassword014', '정태양', '태양이', '010-2001-0005', 'USER', 'DORMANT', false, NULL),
('user06@gmail.com', 'user06', '$2a$10$hashedpassword015', '한소희', '소희야', '010-2001-0006', 'USER', 'ACTIVE', false, NULL),
('user07@gmail.com', 'user07', '$2a$10$hashedpassword016', '윤재원', '재원이', '010-2001-0007', 'USER', 'ACTIVE', false, NULL),
('user08@gmail.com', 'user08', '$2a$10$hashedpassword017', '강다은', '다은이', '010-2001-0008', 'USER', 'ACTIVE', false, NULL),
('user09@gmail.com', 'user09', '$2a$10$hashedpassword018', '조민재', '민재야', '010-2001-0009', 'USER', 'BANNED', false, NULL),
('user10@gmail.com', 'user10', '$2a$10$hashedpassword019', '임수빈', '수빈이', '010-2001-0010', 'USER', 'ACTIVE', false, NULL),
('user11@gmail.com', 'user11', '$2a$10$hashedpassword020', '오현우', '현우짱', '010-2001-0011', 'USER', 'ACTIVE', false, NULL),
('user12@gmail.com', 'user12', '$2a$10$hashedpassword021', '신예린', '예린이', '010-2001-0012', 'USER', 'ACTIVE', false, NULL),
('user13@gmail.com', 'user13', '$2a$10$hashedpassword022', '류성민', '성민이', '010-2001-0013', 'USER', 'DORMANT', false, NULL),
('user14@gmail.com', 'user14', '$2a$10$hashedpassword023', '배나영', '나영이', '010-2001-0014', 'USER', 'ACTIVE', false, NULL),
('user15@gmail.com', 'user15', '$2a$10$hashedpassword024', '전도현', '도현이', '010-2001-0015', 'USER', 'ACTIVE', false, NULL),
('user16@gmail.com', 'user16', '$2a$10$hashedpassword025', '고은지', '은지야', '010-2001-0016', 'USER', 'ACTIVE', false, NULL),
('user17@gmail.com', 'user17', '$2a$10$hashedpassword026', '문지훈', '지훈이', '010-2001-0017', 'USER', 'ACTIVE', false, NULL),
('user18@gmail.com', 'user18', '$2a$10$hashedpassword027', '양서연', '서연이', '010-2001-0018', 'USER', 'ACTIVE', false, NULL),
('user19@gmail.com', 'user19', '$2a$10$hashedpassword028', '남기현', '기현이', '010-2001-0019', 'USER', 'ACTIVE', true, '2026-03-15 14:22:00'),
('user20@gmail.com', 'user20', '$2a$10$hashedpassword029', '홍다빈', '다빈이', '010-2001-0020', 'USER', 'ACTIVE', false, NULL);