
USE sapms_db;

CREATE TABLE IF NOT EXISTS announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT NOT NULL,
    created_by  VARCHAR(100) NOT NULL,
    target_role VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT 'ALL, STUDENT, LECTURER',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    message     VARCHAR(500) NOT NULL,
    is_read     TINYINT(1) NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Seed announcements
INSERT IGNORE INTO announcement(title, content, created_by, target_role) VALUES
('Welcome to SAPMS 2024/2025', 'Welcome to the new academic year! Please check your course assignments and attendance records regularly.', 'Administrator', 'ALL'),
('Mid-term Exam Schedule', 'Mid-term exams will be held from May 20-27, 2026. Please prepare accordingly.', 'Administrator', 'STUDENT'),
('Grade Submission Deadline', 'All lecturers must submit final grades by June 30, 2026. Late submissions will not be accepted.', 'Administrator', 'LECTURER'),
('Attendance Policy Reminder', 'Students must maintain at least 75% attendance to be eligible for final exams.', 'Administrator', 'ALL');
