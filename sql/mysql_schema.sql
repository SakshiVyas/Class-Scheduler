CREATE DATABASE IF NOT EXISTS class_scheduler;

USE class_scheduler;

CREATE TABLE IF NOT EXISTS students (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  group_id VARCHAR(128) NOT NULL,
  course_ids TEXT
);
CREATE TABLE IF NOT EXISTS courses (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  professor_id VARCHAR(64) NOT NULL,
  enrolled_students INT NOT NULL
);
CREATE TABLE IF NOT EXISTS professors (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS rooms (
  id VARCHAR(64) PRIMARY KEY,
  capacity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS schedule_settings (
  id INT PRIMARY KEY,
  working_days VARCHAR(255) NOT NULL,
  day_start_time VARCHAR(5) NOT NULL,
  day_end_time VARCHAR(5) NOT NULL,
  slot_minutes INT NOT NULL
);

SELECT COUNT(*) FROM professors;
SELECT COUNT(*) FROM courses;
SELECT COUNT(*) FROM rooms;
SELECT COUNT(*) FROM students;
