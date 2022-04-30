package com.monitoring.attendance.repos;

import com.monitoring.attendance.enities.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
    Subject findByName(String name);
    Iterable<Subject> findByNameContaining(String name);

    @Query(value = "SELECT * FROM t_subjects ORDER BY name", nativeQuery = true)
    Iterable<Subject> findAllOrderByName();
}
