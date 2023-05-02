package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    Boolean updateRequest(Transfer transfer);

    Boolean create(Integer sender, Integer recipient, BigDecimal amount, String transfer_type);

    List<Transfer> listRequestsReceived(String username);

    List<Transfer> listRequestsSent(String username);

    List<Transfer> list();
}
