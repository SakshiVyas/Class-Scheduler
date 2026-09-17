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

SELECT COUNT(*) FROM professors; -- 300
SELECT COUNT(*) FROM courses;    -- 300
SELECT COUNT(*) FROM rooms;      -- 50
SELECT COUNT(*) FROM students;   -- after CSV import