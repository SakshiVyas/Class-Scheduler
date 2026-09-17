USE class_scheduler;

DELETE FROM students;
DELETE FROM courses;
DELETE FROM rooms;
DELETE FROM professors;
DELETE FROM schedule_settings;

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/professors_test.csv'
REPLACE INTO TABLE professors
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n' IGNORE 1 LINES
(id, name);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/courses_test.csv'
REPLACE INTO TABLE courses
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n' IGNORE 1 LINES
(id, name, professor_id, enrolled_students);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/rooms_test.csv'
REPLACE INTO TABLE rooms
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n' IGNORE 1 LINES
(id, capacity);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/students_test.csv'
REPLACE INTO TABLE students
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n' IGNORE 1 LINES
(id, name, group_id, course_ids);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/schedule_settings_test.csv'
REPLACE INTO TABLE schedule_settings
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n' IGNORE 1 LINES
(id, working_days, day_start_time, day_end_time, slot_minutes);

SELECT 'professors' AS table_name, COUNT(*) AS row_count FROM professors
UNION ALL SELECT 'courses', COUNT(*) FROM courses
UNION ALL SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL SELECT 'students', COUNT(*) FROM students
UNION ALL SELECT 'schedule_settings', COUNT(*) FROM schedule_settings;
