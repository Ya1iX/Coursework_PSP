package com.monitoring.attendance.repos;

import com.monitoring.attendance.enities.Group;
import com.monitoring.attendance.enities.Lesson;
import com.monitoring.attendance.enities.Student;
import com.monitoring.attendance.enities.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Optional<Lesson> findByDateAndStudent(LocalDate date, Student student);

    Iterable<Lesson> findByGroup(Group group);

    Iterable<Lesson> findByStudent(Student student);

    Iterable<Lesson> findBySubject(Subject subject);

    @Query(value = "SELECT * FROM t_lessons WHERE group_id = ? AND subject_id = ? AND date = ? ORDER BY date,student_id", nativeQuery = true)
    Iterable<Lesson> findByGroupAndSubjectAndDateOrdered(Group group, Subject subject, LocalDate date);

    @Query(value = "SELECT * FROM t_lessons WHERE group_id = ? AND subject_id = ? AND (date BETWEEN ? AND ?) ORDER BY date,student_id", nativeQuery = true)
    Iterable<Lesson> findByGroupAndSubjectAndDateBetweenOrdered(Group group, Subject subject, LocalDate dateStart, LocalDate dateEnd);
}
