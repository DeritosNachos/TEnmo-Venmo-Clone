package com.techelevator.tenmo.dao;

public interface AccountDao {

    Double getUserAccountBalance(String username);

    Double getUserAccountBalanceById(Integer account_id);

}
