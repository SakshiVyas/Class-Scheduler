USE class_scheduler;

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/professors_test.csv'
REPLACE INTO TABLE professors
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, name);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/courses_test.csv'
REPLACE INTO TABLE courses
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, name, professor_id, enrolled_students);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/rooms_test.csv'
REPLACE INTO TABLE rooms
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, capacity);

LOAD DATA LOCAL INFILE '/Users/sakshivyas/Downloads/Class Scheduler/data/students_test.csv'
REPLACE INTO TABLE students
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, name, group_id, course_ids);

SELECT 'professors' AS table_name, COUNT(*) AS row_count FROM professors
UNION ALL SELECT 'courses', COUNT(*) FROM courses
UNION ALL SELECT 'rooms', COUNT(*) FROM rooms
UNION ALL SELECT 'students', COUNT(*) FROM students;
