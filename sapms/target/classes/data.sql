-- Academic Year
INSERT IGNORE INTO academic_year(year_label, is_active) VALUES ('2024/2025', 1);

-- Majors
INSERT IGNORE INTO major_info(major_no, major_name) VALUES
('001', 'Software Technology'),
('002', 'Big Data Technology'),
('003', 'Computer Network Technology');

-- Classes
INSERT IGNORE INTO class_info(class_no, major_no, class_name, current_seq, max_count, student_count) VALUES
('01', '001', 'Software Class 01', '000', 50, 0),
('02', '001', 'Software Class 02', '000', 50, 0),
('01', '002', 'Big Data Class 01', '000', 50, 0),
('01', '003', 'Network Class 01',  '000', 50, 0);

-- Admin user (password: admin123)
INSERT IGNORE INTO users(username, password, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Lecturer user (password: lecturer123)
INSERT IGNORE INTO users(username, password, role) VALUES
('lect01', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNWYy9cJQXSAhlr72K6', 'LECTURER');

INSERT IGNORE INTO lecturer(user_id, lecturer_no, full_name, email)
SELECT id, 'L001', 'Dr. John Smith', 'john.smith@sapms.edu' FROM users WHERE username='lect01';

-- Course examples
INSERT IGNORE INTO course(course_code, course_name, credit_hours, major_no, academic_year_id, lecturer_id)
SELECT 'CS101', 'Introduction to Programming', 3, '001', a.id, l.id
FROM academic_year a, lecturer l WHERE a.is_active=1 AND l.lecturer_no='L001';

INSERT IGNORE INTO course(course_code, course_name, credit_hours, major_no, academic_year_id, lecturer_id)
SELECT 'CS102', 'Data Structures & Algorithms', 3, '001', a.id, l.id
FROM academic_year a, lecturer l WHERE a.is_active=1 AND l.lecturer_no='L001';

INSERT IGNORE INTO course(course_code, course_name, credit_hours, major_no, academic_year_id, lecturer_id)
SELECT 'CS103', 'Web Development', 3, '001', a.id, l.id
FROM academic_year a, lecturer l WHERE a.is_active=1 AND l.lecturer_no='L001';
