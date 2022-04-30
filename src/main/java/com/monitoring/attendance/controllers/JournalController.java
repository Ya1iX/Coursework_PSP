package com.monitoring.attendance.controllers;

import com.monitoring.attendance.enities.Group;
import com.monitoring.attendance.enities.Lesson;
import com.monitoring.attendance.enities.Student;
import com.monitoring.attendance.enities.Subject;
import com.monitoring.attendance.repos.GroupRepository;
import com.monitoring.attendance.repos.LessonRepository;
import com.monitoring.attendance.repos.StudentRepository;
import com.monitoring.attendance.repos.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.standard.expression.LessOrEqualToExpression;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/journal")
public class JournalController {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public String searchSubject(@RequestParam String search, Model model) {
        Iterable<Subject> subjects = subjectRepository.findByNameContaining(search);
        model.addAttribute("subjects", subjects);
        return "lessons/journal";
    }

    @GetMapping("{subject}")
    public String selectGroup(@PathVariable Subject subject, Model model) {
        Iterable<Group> groups = groupRepository.findAllOrderByName();
        model.addAttribute("groups", groups);
        model.addAttribute("subject", subject);
        return "lessons/selectGroup";
    }

    @PostMapping("{subject}")
    public String searchGroup(@PathVariable Subject subject, @RequestParam String search, Model model) {
        Iterable<Group> groups = groupRepository.findByNameContaining(search);
        model.addAttribute("groups", groups);
        model.addAttribute("subject", subject);
        return "lessons/selectGroup";
    }

    @GetMapping("{subject}/{group}")
    public String selectDate(@PathVariable Subject subject, @PathVariable Group group, Model model) {
        model.addAttribute("subject", subject);
        model.addAttribute("group", group);
        return "lessons/selectAction";
    }

    @PostMapping("{subject}/{group}/add")
    public String addLesson(@PathVariable Subject subject, @PathVariable Group group, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        Iterable<Student> students = studentRepository.findByGroupOrderBySurname(group);
        Iterable<Lesson> lessons = lessonRepository.findByGroupAndSubjectAndDateOrdered(group, subject, date);

        model.addAttribute("lessons", lessons);
        model.addAttribute("subject", subject);
        model.addAttribute("group", group);
        model.addAttribute("date", date);
        model.addAttribute("students", students);
        return "lessons/addLesson";
    }

    @PostMapping("{subject}/{group}/save")
    public String saveLesson(@PathVariable Subject subject, @PathVariable Group group, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam long[] isAttended) {
        Iterable<Student> students = studentRepository.findByGroupOrderBySurname(group);

        for (Student student : students) {
            Optional<Lesson> lessonDB = lessonRepository.findByDateAndStudent(date, student);

            if (lessonDB.isPresent()) {
                Lesson lesson = lessonDB.get();
                lesson.setAttended(Arrays.stream(isAttended).anyMatch(id -> id == student.getId()));
                lessonRepository.save(lesson);
            } else {
                lessonRepository.save(new Lesson(date, student, subject, Arrays.stream(isAttended).anyMatch(id -> id == student.getId())));
            }
        }

        return "redirect:/journal/{subject}/{group}";
    }

    @PostMapping("{subject}/{group}/view")
    public String viewJournal(@PathVariable Subject subject, @PathVariable Group group, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd, Model model) {
        Iterable<Lesson> lessons = lessonRepository.findByGroupAndSubjectAndDateBetweenOrdered(group, subject, dateStart, dateEnd);
        Iterable<Student> students = studentRepository.findByGroupOrderBySurname(group);

        Set<LocalDate> dates = new TreeSet<>();
        lessons.forEach(lesson -> dates.add(lesson.getDate()));

        model.addAttribute("dates", dates);
        model.addAttribute("lessons", lessons);
        model.addAttribute("subject", subject);
        model.addAttribute("students", students);
        model.addAttribute("group", group);
        return "lessons/viewJournal";
    }
}
