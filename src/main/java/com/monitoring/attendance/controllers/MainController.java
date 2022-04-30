package com.monitoring.attendance.controllers;

import com.monitoring.attendance.enities.*;
import com.monitoring.attendance.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.TreeSet;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/journal";
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        Iterable<Group> groups = groupRepository.findAllOrderByName();
        model.addAttribute("groups", groups);
        return "groups/groups";
    }

    @GetMapping("/students")
    public String students(Model model) {
        Iterable<Student> students = studentRepository.findAllOrderByGroup();
        Iterable<Group> groups = groupRepository.findAllOrderByName();
        model.addAttribute("students", students);
        model.addAttribute("groups", groups);
        return "students/students";
    }

    @GetMapping("/subjects")
    public String subjects(Model model) {
        Iterable<Subject> subjects = subjectRepository.findAllOrderByName();
        model.addAttribute("subjects", subjects);
        return "subjects/subjects";
    }

    @GetMapping("/journal")
    public String journal(Model model) {
        Iterable<Subject> subjects = subjectRepository.findAllOrderByName();
        model.addAttribute("subjects", subjects);
        return "lessons/journal";
    }

    @GetMapping("/firstuser")
    public String createUser(Model model) {
        Iterable<User> users = userRepository.findAll();
        if (users.iterator().hasNext()) {
            model.addAttribute("message", "База данных пользователей не пуста!");
            return "redirect:/";
        }
        return "admin/firstuser";
    }

    @PostMapping("/firstuser")
    public String addUser(@RequestParam String username, @RequestParam String password, Model model) {
        Iterable<User> users = userRepository.findAll();

        if (users.iterator().hasNext()) {
            model.addAttribute("message", "База данных пользователей не пуста!");
            return "admin/firstuser";
        }

        Set<Role> roles = new TreeSet<Role>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        User user = new User(username, new BCryptPasswordEncoder(8).encode(password), true, roles);
        userRepository.save(user);
        return "redirect:/";
    }
}