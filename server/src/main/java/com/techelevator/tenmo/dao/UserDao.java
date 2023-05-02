package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    String getUsernameByUserId(Integer id);

    public String getUsernameByAccountId(Integer account_id);

    public List<Integer> getAccountIdByUsername(String username);
}
