from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data"


def write_csv(name, header, rows):
    path = DATA / name
    with path.open("w", encoding="utf-8", newline="") as file:
        file.write(header + "\n")
        for row in rows:
            file.write(",".join(str(value) for value in row) + "\n")


def scale_data():
    professors = [(f"P{i:03d}", f"Professor {i:03d}") for i in range(1, 301)]
    courses = []
    for i in range(1, 361):
        professor = i if i <= 300 else (i - 300) % 100 + 1
        courses.append((f"C{i:03d}", f"Course {i:03d}", f"P{professor:03d}", 50))

    rooms = [(f"R{i:02d}", 50 + ((i - 1) % 6) * 20) for i in range(1, 51)]
    students = []
    enrolments = {f"C{i:03d}": 0 for i in range(1, 361)}
    for i in range(1, 5001):
        group = (i - 1) // 50 + 1
        course_numbers = [group, 100 + group, 200 + group, 300 + ((group - 1) % 60) + 1]
        course_ids = [f"C{number:03d}" for number in course_numbers]
        for course_id in course_ids:
            enrolments[course_id] += 1
        students.append((f"S{i:06d}", f"Student {i:06d}", f"G{group:03d}", ";".join(course_ids)))

    courses = [(course_id, name, professor, enrolments[course_id]) for course_id, name, professor, _ in courses]
    settings = [(1, "Monday;Tuesday;Wednesday;Thursday;Friday", "09:00", "18:00", 180)]
    write_csv("professors_test.csv", "id,name", professors)
    write_csv("courses_test.csv", "id,name,professor_id,enrolled_students", courses)
    write_csv("rooms_test.csv", "id,capacity", rooms)
    write_csv("students_test.csv", "id,name,group_id,course_ids", students)
    write_csv("schedule_settings_test.csv", "id,working_days,day_start_time,day_end_time,slot_minutes", settings)


scale_data()
