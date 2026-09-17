package com.campusscheduler.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<CourseEntity, String> {}
