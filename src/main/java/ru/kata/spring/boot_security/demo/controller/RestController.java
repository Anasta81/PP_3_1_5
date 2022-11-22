package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceDetailsImp;

import java.security.Principal;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest")
public class RestController {

    private final UserService userService;

    private final UserServiceDetailsImp userD;

    private final RoleService roleService;

    @Autowired
    public RestController(UserService userService, RoleService roleService, UserServiceDetailsImp userD) {
        this.userService = userService;
        this.roleService =  roleService;
        this.userD = userD;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUserList(){
        List<User> list = userService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@ModelAttribute("user") User user, @RequestParam("role") String[] role){
        userService.saveUser(user, role);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        if (userService.findById(id) == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK); }

    @PutMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@ModelAttribute("user") User user, @PathVariable("id") Long id,
                                           @RequestParam(name = "role", required = false) String[] role) {
        if (userService.findById(id) == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        userService.update(user, id, role);
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

}
