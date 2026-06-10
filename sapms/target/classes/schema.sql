CREATE DATABASE IF NOT EXISTS sapms_db DEFAULT CHARACTER SET utf8mb4;
USE sapms_db;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL COMMENT 'ADMIN, LECTURER, STUDENT',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS major_info (
    major_no    VARCHAR(10)  PRIMARY KEY,
    major_name  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS class_info (
    class_no        VARCHAR(10) NOT NULL,
    major_no        VARCHAR(10) NOT NULL,
    class_name      VARCHAR(100) NOT NULL,
    current_seq     VARCHAR(10)  NOT NULL DEFAULT '000',
    max_count       INT          NOT NULL DEFAULT 50,
    student_count   INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (major_no, class_no),
    FOREIGN KEY (major_no) REFERENCES major_info(major_no)
);

CREATE TABLE IF NOT EXISTS academic_year (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    year_label  VARCHAR(20) NOT NULL UNIQUE COMMENT 'e.g. 2024/2025',
    is_active   TINYINT(1)  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS lecturer (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT      NOT NULL UNIQUE,
    lecturer_no     VARCHAR(20) NOT NULL UNIQUE,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS student (
    student_no      VARCHAR(20)  PRIMARY KEY,
    user_id         BIGINT       NOT NULL UNIQUE,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100),
    major_no        VARCHAR(10)  NOT NULL,
    class_no        VARCHAR(10)  NOT NULL,
    student_seq     VARCHAR(10)  NOT NULL,
    academic_year_id BIGINT      NOT NULL,
    FOREIGN KEY (user_id)          REFERENCES users(id),
    FOREIGN KEY (major_no, class_no) REFERENCES class_info(major_no, class_no),
    FOREIGN KEY (academic_year_id) REFERENCES academic_year(id)
);

CREATE TABLE IF NOT EXISTS course (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code     VARCHAR(20)  NOT NULL UNIQUE,
    course_name     VARCHAR(100) NOT NULL,
    credit_hours    INT          NOT NULL DEFAULT 3,
    major_no        VARCHAR(10)  NOT NULL,
    academic_year_id BIGINT      NOT NULL,
    lecturer_id     BIGINT,
    FOREIGN KEY (major_no)         REFERENCES major_info(major_no),
    FOREIGN KEY (academic_year_id) REFERENCES academic_year(id),
    FOREIGN KEY (lecturer_id)      REFERENCES lecturer(id)
);

CREATE TABLE IF NOT EXISTS grade (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no      VARCHAR(20) NOT NULL,
    course_id       BIGINT      NOT NULL,
    assignment_score DECIMAL(5,2),
    midterm_score    DECIMAL(5,2),
    final_score      DECIMAL(5,2),
    total_score      DECIMAL(5,2),
    grade_letter     VARCHAR(5),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, PASS, FAIL, AT_RISK',
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_grade (student_no, course_id),
    FOREIGN KEY (student_no) REFERENCES student(student_no),
    FOREIGN KEY (course_id)  REFERENCES course(id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_no      VARCHAR(20) NOT NULL,
    course_id       BIGINT      NOT NULL,
    session_date    DATE        NOT NULL,
    status          VARCHAR(10) NOT NULL COMMENT 'PRESENT, ABSENT, LATE',
    note            VARCHAR(255),
    FOREIGN KEY (student_no) REFERENCES student(student_no),
    FOREIGN KEY (course_id)  REFERENCES course(id)
);
