package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    // A repository that serves as the DAO for Account related actions
    AccountRepository accRep;

    @Autowired
    public AccountService(AccountRepository accRep) {
        this.accRep = accRep;
    }

    /**
     * Calls the repository to see if account with username already exists
     * @param username
     * @return boolean
     */
    public boolean usernameAlreadyExists (String username) {
        return (this.accRep.findAccountByUsername(username) != null);
    }

    /**
     * Given an account, pass it to the repository and have it update the table with it.
     * Registration will fail and return null if:
     *  - username is blank
     *  - passowrd is < 4 chars long
     * @param acc
     * @return the persisted account
     */
    public Account persistAccount(Account acc) {
        // guard statements //
        String username = acc.getUsername();
        if (username == "" || username == null)
            return null;
        
        String password = acc.getPassword();
        if(password.length() < 4)
            return null;
        // end of guard statements //

        // account credentials are valid and will be passed onto the repository
        // to be registered
        return this.accRep.save(acc);
    }

}
