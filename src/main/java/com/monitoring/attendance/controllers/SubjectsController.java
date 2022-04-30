package com.monitoring.attendance.controllers;

import com.monitoring.attendance.enities.Lesson;
import com.monitoring.attendance.enities.Subject;
import com.monitoring.attendance.repos.LessonRepository;
import com.monitoring.attendance.repos.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
public class SubjectsController {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @PostMapping
    public String searchSubject(@RequestParam String search, Model model) {
        Iterable<Subject> subjects = subjectRepository.findByNameContaining(search);
        model.addAttribute("subjects", subjects);
        return "subjects/subjects";
    }

    @PostMapping("/add")
    public String addSubject(@RequestParam String name) {
        if (name.isEmpty())
            return "redirect:/subjects";

        if (subjectRepository.findByName(name) != null)
            return "redirect:/subjects";

        Subject subject = new Subject(name);
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @GetMapping("{subject}")
    public String editSubject(@PathVariable Subject subject, Model model) {
        model.addAttribute(subject);
        return "subjects/editSubject";
    }

    @PostMapping("{subject}")
    public String saveEditSubject(@PathVariable Subject subject, @RequestParam String name) {
        if (name.isEmpty())
            return "redirect:/subjects";

        if (subjectRepository.findByName(name) != null)
            return "redirect:/subjects";

        subject.setName(name);
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @PostMapping("{subject}/remove")
    public String removeSubject(@PathVariable Subject subject) {
        Iterable<Lesson> lessons = lessonRepository.findBySubject(subject);
        lessonRepository.deleteAll(lessons);
        subjectRepository.delete(subject);
        return "redirect:/subjects";
    }
}
