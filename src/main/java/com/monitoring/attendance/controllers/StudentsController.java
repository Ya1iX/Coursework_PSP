package com.monitoring.attendance.controllers;

import com.monitoring.attendance.enities.Group;
import com.monitoring.attendance.enities.Lesson;
import com.monitoring.attendance.enities.Student;
import com.monitoring.attendance.repos.GroupRepository;
import com.monitoring.attendance.repos.LessonRepository;
import com.monitoring.attendance.repos.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentsController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @PostMapping
    public String searchStudent(@RequestParam String search, Model model) {
        Iterable<Student> students = studentRepository.findBySurnameContaining(search);
        model.addAttribute("students", students);
        return "students/students";
    }

    @PostMapping("/add")
    public String addStudent(@RequestParam String name, @RequestParam String surname, @RequestParam long groupId) {
        if (name.isEmpty() || surname.isEmpty() || groupId < 0)
            return "redirect:/error";

        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty())
            return "redirect:/error";

        Student student = new Student(name, surname, group.get());
        studentRepository.save(student);
        return "redirect:/students";
    }

    @GetMapping("{student}")
    public String editStudent(@PathVariable Student student, Model model) {
        Iterable<Group> groups = groupRepository.findAllOrderByName();
        model.addAttribute("groups", groups);
        model.addAttribute("student", student);
        return "students/editStudent";
    }

    @PostMapping("{student}")
    public String saveEditStudent(@PathVariable Student student, @RequestParam String name, @RequestParam String surname, @RequestParam long groupId) {
        if (name.isEmpty() || surname.isEmpty() || groupId < 0)
            return "redirect:/error";

        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty())
            return "redirect:/error";

        student.setName(name);
        student.setSurname(surname);
        student.setGroup(group.get());
        studentRepository.save(student);
        return "redirect:/students";
    }

    @PostMapping("{student}/remove")
    public String removeStudent(@PathVariable Student student) {
        Iterable<Lesson> lessons = lessonRepository.findByStudent(student);
        lessonRepository.deleteAll(lessons);
        studentRepository.delete(student);
        return "redirect:/students";
    }
}
