package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleServiceImp;
import ru.kata.spring.boot_security.demo.service.UserServiceImp;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImp userServiceImp;

    private final RoleServiceImp roleServiceImp;

    @Autowired
    public AdminController(UserServiceImp userServiceImp, RoleServiceImp roleServiceImp) {
        this.userServiceImp = userServiceImp;
        this.roleServiceImp =  roleServiceImp;
    }

    @GetMapping
    public String findAll(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("thisUser", user);
        List<User> list = userServiceImp.findAll();
        model.addAttribute("users", list);
        model.addAttribute("allRoles", roleServiceImp.getAllRoles());
        return "admin";
    }

    @GetMapping("/add")
    public String addNewUser(Model model, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        model.addAttribute("thisUser", user);
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleServiceImp.getAllRoles());
        return "add";
    }

    @PostMapping
    public String create(@ModelAttribute("user") User user, @RequestParam("role") String[] role) {
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            for(String r: role) {
                roles.add(roleServiceImp.getRoleByName(r));
            }
            user.setRoles(roles);
        }
        userServiceImp.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userServiceImp.findById(id));
        return "edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User user,@PathVariable("id") Long id,
                         @RequestParam(name = "role", required = false) String[] role ) {
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            for(String r: role) {
                roles.add(roleServiceImp.getRoleByName(r));
            }
            user.setRoles(roles);
        } else {
            user.setRoles(userServiceImp.findById(id).getRoles());
        }
        userServiceImp.update(user, id);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        userServiceImp.delete(id);
        return "redirect:/admin";
    }

}
