package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.RegisterUserDTO;
import com.techelevator.tenmo.model.TransferHandler;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/transfers")

public class TransferController {
    private TransferDao dao;
    private UserDao userDao;
    private AccountDao accountDao;

    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.dao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    //This method handles the update to approve/deny transfer requests
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/approveordeny", method = RequestMethod.PUT)
    public void approveDeny(@Valid @RequestBody Transfer transfer, Principal principal) {

        if(!principal.getName().equals(transfer.getSenderUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer update failed, user doesn't have access to these requests!");
        }

        if(!dao.updateRequest(transfer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer update failed");
        }


    }




    //This method handles HTTP post for new Transfers//THIS endpoint is not being used, it was only for testing
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public void create(@Valid @RequestBody Transfer transfer, Principal principal) {


        //This IF statement checks is the account number has same username as the principal account username
        if (!principal.getName().equals(userDao.getUsernameByAccountId(transfer.getSender()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer failed because account numbers don't line up");
        }
        if (!dao.create(transfer.getSender(),transfer.getRecipient(),transfer.getAmount(),transfer.getTransferType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer failed");
        }


    }

    //This method handles HTTP post for a Transfer with a username
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/post", method = RequestMethod.POST)
    public void postTransfer(@Valid @RequestBody TransferHandler transfer, Principal principal) {
        String recipient = transfer.getRecipient();
        String sender = principal.getName();


        List<Integer> accountIdSender = userDao.getAccountIdByUsername(sender);
        Integer accountIdRecipient = userDao.getAccountIdByUsername(recipient).get(0);


        if(sender.equals(recipient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer failed because you can't send a transfer to yourself");
        }

        Boolean created = false;
        for (Integer account_id: accountIdSender) {

            if (accountDao.getUserAccountBalanceById(account_id) - transfer.getAmount().doubleValue() >= 0) {
                created = dao.create(account_id, accountIdRecipient, transfer.getAmount(), "Transfer");
                break;
            }


        }

        if (!created) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer failed not enough funds");
        }

    }

    //This method handles HTTP post for Request with a username for sender
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/requests", method = RequestMethod.POST)
    public void request(@Valid @RequestBody TransferHandler transfer, Principal principal) {
        String recipient = principal.getName();
        String sender = transfer.getSender();


        List<Integer> accountIdSender = userDao.getAccountIdByUsername(sender);
        Integer accountIdRecipient = userDao.getAccountIdByUsername(recipient).get(0);


        if(sender.equals(recipient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request failed because you can't send a request to yourself");
        }

        Boolean created = false;
        for (Integer account_id: accountIdSender) {

            if (accountDao.getUserAccountBalanceById(account_id) - transfer.getAmount().doubleValue() >= 0) {
                created = dao.create(account_id, accountIdRecipient, transfer.getAmount(), "Request");
                break;
            }

        }


        if (!created) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request failed not enough funds!");
        }


    }







    //This method lists all transfers/requests
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<Transfer> listTransfers() {
        return dao.list();
    }



    //This method lists all requests received
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/received", method = RequestMethod.GET)
    public List<Transfer> requestsReceived(Principal principal) {
        return dao.listRequestsReceived(principal.getName());
    }


    //This method lists all requests sent
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/sent", method = RequestMethod.GET)
    public List<Transfer> requestsSent(Principal principal) {
        return dao.listRequestsSent(principal.getName());
    }







}
