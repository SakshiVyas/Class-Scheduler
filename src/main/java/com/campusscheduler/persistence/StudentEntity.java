package com.campusscheduler.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class StudentEntity {
    @Id private String id;
    private String name;
    private String groupId;
    private String courseIds;
    protected StudentEntity() {}
    public StudentEntity(String id, String name, String groupId) { this(id, name, groupId, ""); }
    public StudentEntity(String id, String name, String groupId, String courseIds) { this.id=id; this.name=name; this.groupId=groupId; this.courseIds=courseIds; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getGroupId() { return groupId; }
    public String getCourseIds() { return courseIds; }
}
