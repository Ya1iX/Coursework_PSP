package com.monitoring.attendance.repos;

import com.monitoring.attendance.enities.Group;
import com.monitoring.attendance.enities.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Iterable<Student> findByNameContaining(String name);
    Iterable<Student> findBySurnameContaining(String surname);

    @Query(value = "SELECT * FROM t_students ORDER BY group_id, surname", nativeQuery = true)
    Iterable<Student> findAllOrderByGroup();

    Iterable<Student> findByGroupOrderBySurname(Group group);
}
