package com.campusscheduler.model;

public class Course {

    private String id;
    private int enrolledStudents;
    private String professorId;
    private String name;

    public Course(){

    }
    public Course( String id, int enrolledStudents, String professorId){
        this.id = id;
        this.enrolledStudents = enrolledStudents;
        this.professorId = professorId;
    }

    public Course(String id, String name, String professorId, int enrolledStudents) {
        this(id, enrolledStudents, professorId);
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
