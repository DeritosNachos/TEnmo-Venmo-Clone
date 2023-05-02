package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/users")
public class UserController {

    private UserDao dao;

    public UserController(UserDao userDao) {
        this.dao = userDao;
    }


    @RequestMapping(path = "/findAllUsers", method = RequestMethod.GET)
    public List<User> listUsers() {
        return dao.findAll();
    }


    @RequestMapping(path = "/whoami")
    public String whoAmI(Principal principal) {

            return principal.getName();
        }



    }

