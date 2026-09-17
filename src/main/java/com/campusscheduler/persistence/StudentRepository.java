package com.campusscheduler.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentRepository extends JpaRepository<StudentEntity, String> {}
