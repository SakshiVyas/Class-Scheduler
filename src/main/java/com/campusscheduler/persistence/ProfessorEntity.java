package com.campusscheduler.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professors")
public class ProfessorEntity {
    @Id
    private String id;
    private String name;

    protected ProfessorEntity() {
    }

    public ProfessorEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
