package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private AccountDao dao;

    public AccountController(AccountDao accountDao) {
        this.dao = accountDao;
    }

    //Gets the account balance of a user by username
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(params = "username", method = RequestMethod.GET)
    public Double getUserAccountBalance(@RequestParam(value = "username") String username) {
        return dao.getUserAccountBalance(username);
    }

    //Gets the acccount balance of principal
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/me", method = RequestMethod.GET)
    public Double getPrincipalAccountBalance(Principal principal) {
        return dao.getUserAccountBalance(principal.getName());
    }

}
