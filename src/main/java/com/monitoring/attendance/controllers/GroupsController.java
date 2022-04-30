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

@Controller
@RequestMapping("/groups")
public class GroupsController {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @PostMapping("/add")
    public String addGroup(@RequestParam String name) {
        if (name.isEmpty())
            return "redirect:/error";

        if(groupRepository.findByName(name) != null)
            return "redirect:/error";

        Group group = new Group(name);
        groupRepository.save(group);
        return "redirect:/groups";
    }

    @PostMapping
    public String searchGroup(@RequestParam String search, Model model) {
        Iterable<Group> groups = groupRepository.findByNameContaining(search);
        model.addAttribute("groups", groups);
        return "groups/groups";
    }

    @GetMapping("{group}")
    public String aboutGroup(@PathVariable Group group, Model model) {
        Iterable<Student> students = studentRepository.findByGroupOrderBySurname(group);
        model.addAttribute("group", group);
        model.addAttribute("students", students);
        return "groups/aboutGroup";
    }

    @GetMapping("{group}/edit")
    public String editGroup(@PathVariable Group group, Model model) {
        model.addAttribute("group", group);
        return "groups/groupEdit";
    }

    @PostMapping("{group}/edit")
    public String saveEditGroup(@PathVariable Group group, @RequestParam String name, Model model) {
        if (name.isEmpty())
            return "redirect:/error";

        if(groupRepository.findByName(name) != null)
            return "redirect:/error";

        group.setName(name);
        groupRepository.save(group);
        return "redirect:/groups";
    }

    @PostMapping("{group}/addStudent")
    public String addStudent(@PathVariable Group group, @RequestParam String name, @RequestParam String surname) {
        if (name.isEmpty() || surname.isEmpty())
            return "redirect:/error";

        Student student = new Student(name, surname, group);
        studentRepository.save(student);
        return "redirect:/groups/{group}";
    }

    @PostMapping("{group}/remove")
    public String removeGroup(@PathVariable Group group) {
        Iterable<Student> students = studentRepository.findByGroupOrderBySurname(group);
        Iterable<Lesson> lessons = lessonRepository.findByGroup(group);
        lessonRepository.deleteAll(lessons);
        studentRepository.deleteAll(students);
        groupRepository.delete(group);
        return "redirect:/groups";
    }
}
