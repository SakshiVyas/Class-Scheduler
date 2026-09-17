# Class Scheduler

Class Scheduler builds a university timetable for 5,000 students, 300 professors and 50 rooms. It combines greedy scheduling, Welsh-Powell graph colouring, dynamic programming and bounded backtracking with MySQL, a REST API and a browser UI.

## Run

Requirements: Java 21 and MySQL 8.

Download [`class-scheduler.jar`](https://github.com/SakshiVyas/Class-Scheduler/releases/download/v0.0/class-scheduler.jar) from the [v0.0 release](https://github.com/SakshiVyas/Class-Scheduler/releases/tag/v0.0).

```bash
DB_USERNAME=root DB_PASSWORD=your_password \
java -jar class-scheduler.jar --spring.profiles.active=demo
```

Open [http://localhost:8080](http://localhost:8080). The `demo` profile imports the bundled CSV dataset when the database is empty. Connect DataGrip to `localhost:3306/class_scheduler` with the same credentials.

Release SHA-256: `bc6a08174737fbd4fd50bc8bc0474ffa59e3f015e2cf8b521d8240ec60659780`

## Algorithms

| Stage | Purpose | Complexity |
|---|---|---|
| Greedy | Places larger classes first for a fast baseline | Polynomial placement search |
| Welsh-Powell | Separates courses sharing a professor or student group | `O(C²)` |
| Dynamic programming | Minimises wasted room capacity for fixed slots | `O(C × R)` per slot |
| Backtracking | Revisits room and slot choices when earlier stages fail | Exponential worst case, bounded to 30 seconds or 1,000,000 nodes |

`C` is the number of courses and `R` is the number of rooms.

## Constraints

- A professor or student group cannot attend two classes simultaneously.
- A room cannot be double-booked.
- Room capacity must meet course enrolment.
- Wasted seats are minimised after feasibility.
- Unscheduled courses are returned with a reason for manual intervention.

The database is converted into the algorithm input at `GET /api/data/constraints`.

## Data and API

The UI imports `professors.csv`, `courses.csv`, `rooms.csv` and `students.csv` together. Test files are in [`data`](data), and MySQL scripts are in [`sql`](sql).

| Endpoint | Use |
|---|---|
| `POST /api/data/import` | Validate and replace the four-file dataset |
| `GET /api/data/constraints` | View the generated scheduling constraints |
| `GET /api/schedules?algorithm=backtracking` | Generate one schedule |
| `GET /api/schedules/comparison` | Run all four stages |

## Conflict report

Every run returns scheduled entries, unscheduled courses and total wasted seats. The UI and Java console identify unresolved courses and their blocking constraint. A university manager can use this list to add a room or teaching slot, change a professor assignment, or approve a manual placement.

## Source layout

- `src/main/java/com/campusscheduler/algorithm` - four algorithm stages
- `src/main/java/com/campusscheduler/service` - database conversion, import and scheduling services
- `src/main/java/com/campusscheduler/api` - REST endpoints
- `src/main/resources/static` - browser UI
- `data` - scale-test CSV files
- `sql` - MySQL schema and import script

## Build

```bash
./mvnw -DskipTests package
```

The executable output is `target/class-scheduler.jar`.
