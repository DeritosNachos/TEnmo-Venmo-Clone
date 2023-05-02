package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public String getUsernameByUserId(Integer user_id) {
        String sql = "SELECT username FROM tenmo_user WHERE user_id ILIKE ? RETURNING username;";
        String username = jdbcTemplate.queryForObject(sql, String.class, user_id);
        return username;
    }

    @Override
    public String getUsernameByAccountId(Integer account_id) {
        String returnString = "";
        String sql = "SELECT username " +
            "FROM tenmo_user as tu " +
            "JOIN account as a " +
            "ON a.user_id = tu.user_id " +
            "WHERE a.account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
        if(results.next()) {
            returnString = results.getString("username");
        }

        return returnString;
    }


    @Override
    public List<Integer> getAccountIdByUsername(String username) {
        List<Integer> accountId = new ArrayList<>();
        String sql = "SELECT account_id " +
                "FROM account as a " +
                "JOIN tenmo_user as tu " +
                "ON tu.user_id = a.user_id " +
                "WHERE tu.username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);

        while(results.next()) {
            accountId.add(results.getInt("account_id"));
        }

        return accountId;
    }


    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }


        // Create the account record with initial balance
        String sql2 = "INSERT INTO account (user_id, balance) VALUES (?, ?) RETURNING account_id";
        Integer newAccountId;
        try {
            newAccountId = jdbcTemplate.queryForObject(sql2, Integer.class, newUserId, 1000);
        } catch (DataAccessException e) {
            return false;
        }
            return true;
    }



    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
