# MySQL and DataGrip setup

The application connects to MySQL on `localhost:3306` by default. The demo calendar uses three slots per day: 09:00-12:00, 12:00-15:00, and 15:00-18:00. In DataGrip, run [`sql/mysql_schema.sql`](sql/mysql_schema.sql), then run [`sql/load_test_data.sql`](sql/load_test_data.sql) to load all CSV fixtures with `LOAD DATA LOCAL INFILE`.

If DataGrip reports that `LOCAL INFILE` is disabled, enable **Allow local infile** (or the equivalent `allowLoadLocalInfile`) in the MySQL data source properties and reconnect.

```bash
DB_USERNAME=root DB_PASSWORD=your_password ./mvnw spring-boot:run
```

You can override `DB_URL` when MySQL uses another host or port. In DataGrip, add a MySQL data source for `localhost:3306`, database `class_scheduler`, using the same credentials.

Import the four CSV files together through `POST /api/data/import`. The converted algorithm input is available at `GET /api/data/constraints`.

Generate a schedule directly from MySQL data:

```bash
curl 'http://localhost:8080/api/schedules?algorithm=backtracking'
curl 'http://localhost:8080/api/schedules?algorithm=greedy'
```

The response contains `scheduledEntries`, `unscheduledCourses`, and `totalWastedSeats`.

Backtracking is best-effort and bounded to 30 seconds (or one million search nodes) by default for large datasets. Override the time budget when needed with `-Dscheduler.backtracking.maxMillis=60000`.

Open `http://localhost:8080/` for the browser UI. It provides student CSV upload and schedule generation controls.

Both `/api/data/constraints` and `/api/schedules` read the current MySQL tables.
