package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{


    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }



    public Boolean updateRequest(Transfer transfer) {

        //checks if theres enough balance to send
        if (!(accountDao.getUserAccountBalanceById(transfer.getSender()) - transfer.getAmount().doubleValue() >= 0)) {

            String sql4 = "UPDATE transfer SET request_status = 'FINALIZED' WHERE transfer_id = ?";
            Integer updateApproved;
            try {
                updateApproved = jdbcTemplate.update(sql4, transfer.getTransferId());
            } catch (DataAccessException e) {
                return false;
            }
            String sql = "UPDATE transfer SET request_approved = false WHERE transfer_id = ?;";

            Integer updateTransfer;

            try {
                updateTransfer = jdbcTemplate.update(sql, transfer.getTransferId());
            } catch (DataAccessException e) {
                return false;
            }


            return true;
        }


        String sql = "UPDATE transfer SET request_approved = ? WHERE transfer_id = ?;";

        Integer updateTransfer;

        try {
            updateTransfer = jdbcTemplate.update(sql, transfer.getRequestApproved(), transfer.getTransferId());
        } catch (DataAccessException e) {
            return false;
        }

        //Request has been approved so update accounts
        if(transfer.getRequestApproved()) {
            String sql2 = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
            Integer updateSender;

            try {
                updateSender = jdbcTemplate.update(sql2, transfer.getAmount(), transfer.getSender());
            } catch (DataAccessException e) {
                return false;
            }

            String sql3 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
            Integer updateRecipient;
            try {
                updateRecipient = jdbcTemplate.update(sql3, transfer.getAmount(), transfer.getRecipient());
            } catch (DataAccessException e) {
                return false;
            }


        }

        String sql4 = "UPDATE transfer SET request_status = 'FINALIZED' WHERE transfer_id = ?";
        Integer updateApproved;
        try {
            updateApproved = jdbcTemplate.update(sql4, transfer.getTransferId());
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }



    //Creates a new transfer in SQL, **also needs to UPDATE accounts table
    @Override
    public Boolean create(Integer sender, Integer recipient, BigDecimal amount, String transfer_type) {


        String sql = "INSERT INTO transfer (sender, recipient, amount, transfer_type)\n" +
                " VALUES (?, ?, ?, ?)" +
                " RETURNING transfer_id;";
        Integer newTransferId;
        try {
            newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, sender, recipient, amount.intValue(), transfer_type);
        } catch (DataAccessException e) {
            return false;
        }


        //checks if theres enough balance to send
        if (!(accountDao.getUserAccountBalanceById(sender) - amount.doubleValue() >= 0)) {

            String sql4 = "UPDATE transfer SET request_status = 'FINALIZED' WHERE transfer_id = ?";
            Integer updateApproved;
            try {
                updateApproved = jdbcTemplate.update(sql4, newTransferId);
            } catch (DataAccessException e) {
                return false;
            }
            String sql5 = "UPDATE transfer SET request_approved = false WHERE transfer_id = ?;";

            Integer updateTransfer;

            try {
                updateTransfer = jdbcTemplate.update(sql5, newTransferId);
            } catch (DataAccessException e) {
                return false;
            }


            return true;
        }





        if (transfer_type.equals("Transfer")) {
            String sql2 = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
            Integer updateSender;

            try {
                updateSender = jdbcTemplate.update(sql2, amount, sender);
            } catch (DataAccessException e) {
                return false;
            }

            String sql3 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
            Integer updateRecipient;
            try {
                updateRecipient = jdbcTemplate.update(sql3, amount, recipient);
            } catch (DataAccessException e) {
                return false;
            }

            String sql4 = "UPDATE transfer SET request_status = 'FINALIZED', request_approved = true WHERE transfer_id = ?";
            Integer updateApproved;
            try {
                updateApproved = jdbcTemplate.update(sql4, newTransferId);
            } catch (DataAccessException e) {
                return false;
            }

        }

        return true;
    }

    @Override
    public List<Transfer> listRequestsReceived(String username) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, sender, recipient, amount, transfer_type, request_approved, request_status\n" +
                " FROM transfer AS t\n" +
                " JOIN account AS a ON t.sender = a.account_id\n" +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id\n" +
                " WHERE username = ? AND request_status = 'Pending';";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfer.setSenderUsername(getUsernameByAccountId(transfer.getSender()));
                transfer.setRecipientUsername(getUsernameByAccountId(transfer.getRecipient()));
                transferList.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Cannot connect to DB");;
        } catch (BadSqlGrammarException e) {
            //throw new DaoException("SQL syntax error", e);
            System.out.println("Bad SQL grammar");
        }
        return transferList;
    }


    @Override
    public List<Transfer> listRequestsSent(String username) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, sender, recipient, amount, transfer_type, request_approved, request_status\n" +
                " FROM transfer AS t\n" +
                " JOIN account AS a ON t.recipient = a.account_id\n" +
                " JOIN tenmo_user AS tu ON a.user_id = tu.user_id\n" +
                " WHERE username = ? AND request_status = 'Pending';";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfer.setSenderUsername(getUsernameByAccountId(transfer.getSender()));
                transfer.setRecipientUsername(getUsernameByAccountId(transfer.getRecipient()));
                transferList.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Cannot connect to DB");;
        } catch (BadSqlGrammarException e) {
            //throw new DaoException("SQL syntax error", e);
            System.out.println("Bad SQL grammar");
        }
        return transferList;
    }





    //do a list all transfers
    @Override
    public List<Transfer> list() {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, sender, recipient, amount, transfer_type, request_approved, request_status " +
                "FROM transfer";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfer.setSenderUsername(getUsernameByAccountId(transfer.getSender()));
                transfer.setRecipientUsername(getUsernameByAccountId(transfer.getRecipient()));
                transferList.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Cannot connect to DB");;
        } catch (BadSqlGrammarException e) {
            //throw new DaoException("SQL syntax error", e);
            System.out.println("Bad SQL grammar");
        }
        return transferList;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setSender(rs.getInt("sender"));
        //transfer.setSenderUsername(userDao.getUsernameByAccountId(transfer.getSender()));
        transfer.setRecipient(rs.getInt("recipient"));
        //transfer.setRecipientUsername(userDao.getUsernameByAccountId(transfer.getRecipient()));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setTransferType(rs.getString("transfer_type"));
        Object obj = rs.getObject("request_approved");
        if (obj != null) {
            transfer.setRequestApproved((Boolean) obj);
        } else transfer.setRequestApproved(null);

        transfer.setTransferStatus(rs.getString("request_status"));
        return transfer;
    }

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


}
