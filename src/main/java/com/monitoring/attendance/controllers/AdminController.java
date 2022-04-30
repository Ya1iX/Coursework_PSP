package com.monitoring.attendance.controllers;

import com.monitoring.attendance.enities.Role;
import com.monitoring.attendance.enities.User;
import com.monitoring.attendance.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model) {
        Iterable<User> users = userRepository.findAllByOrderByUsername();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping
    public String searchUser(@RequestParam String search, Model model) {
        Iterable<User> users = userRepository.findByUsernameContaining(search);
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam Map<String, String> form, User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "redirect:/users";
        }

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        Set<Role> userRoles = new TreeSet<>();

        for (String key : form.keySet()) {
            if(roles.contains(key))
                userRoles.add(Role.valueOf(key));
        }

        user.setActive(true);
        user.setRoles(userRoles);
        user.setPassword(new BCryptPasswordEncoder(8).encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/users";
    }

    @GetMapping("/{user}")
    public String editUser(@PathVariable User user, Model model) {
        model.addAttribute("user",user);
        model.addAttribute("roles", Role.values());
        return "admin/editUser";
    }

    @PostMapping("/{user}/edit")
    public String applyChanges(@PathVariable User user, @RequestParam String username, @RequestParam String password, @RequestParam Map<String, String> form) {
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if(roles.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }

        if (!password.isEmpty())
            user.setPassword(new BCryptPasswordEncoder(8).encode(password));
        user.setUsername(username);
        userRepository.save(user);
        return "redirect:/users";
    }

    @PostMapping("/{user}/disable")
    public String disableUser(@PathVariable User user, @RequestParam boolean activity) {
        user.setActive(activity);
        userRepository.save(user);
        return "redirect:/users";
    }
}
