package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Double getUserAccountBalance(String username) {

        // create user
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?";
        String sql2 = "SELECT balance FROM account WHERE user_id = ?";

        Integer newUserId;
        Double newBalance = 0.0;

        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username);
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql2, newUserId);
            while(results.next()) {
                newBalance += results.getDouble("balance");
            }
        } catch (DataAccessException e) {
            return null;
        }
        return newBalance;


    }


    public Double getUserAccountBalanceById(Integer account_id) {


        String sql = "SELECT balance FROM account WHERE account_id = ?";

        Double newBalance;

        try {
            newBalance = jdbcTemplate.queryForObject(sql, Double.class, account_id);

        } catch (DataAccessException e) {
            return null;
        }

        return newBalance;
    }

}
