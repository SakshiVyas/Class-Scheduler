package com.campusscheduler.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id private String id;
    private String name;
    private String professorId;
    private int enrolledStudents;
    protected CourseEntity() {}
    public CourseEntity(String id, String name, String professorId, int enrolledStudents) { this.id=id; this.name=name; this.professorId=professorId; this.enrolledStudents=enrolledStudents; }
    public String getId(){return id;} public String getName(){return name;} public String getProfessorId(){return professorId;} public int getEnrolledStudents(){return enrolledStudents;}
}
