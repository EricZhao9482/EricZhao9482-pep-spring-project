package com.example.controller;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.*;
import com.example.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

/**
 * A controller that uses the Spring API. Has endpoints that handles various HTTP requests from the user.
 * These requests get passed to an adequate service class to handle operations on the database.
 */
@RestController
public class SocialMediaController {

    AccountService accService;
    MessageService msgService;

    @Autowired
    public SocialMediaController(AccountService accService, MessageService msgService) {
        this.accService = accService;
        this.msgService = msgService;
    }

    /**
     * Processes account registration. Registration will be successful if:
     *  - username is not blank
     *  - username is not already taken
     *  - password is at least 4 chars long
     * These checks are handled by the account service class.
     * @param acc
     * @return Response Entity with the registered account and status code 200 (OK).
     *         Returns 409 (CONFLICT) if username is already taken.
     *         Retruns 400 (Client Error) for all other registration errors. 
     */
    @PostMapping("/register")
    public ResponseEntity accountRegistrationHandler(@RequestBody Account acc) {
        
        // check if the username already exists
        if (this.accService.usernameAlreadyExists(acc.getUsername())) {
            // return error code 409 (CONFLICT)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        
        // register the account
        Account registeredAcc = this.accService.persistAccount(acc);

        // check if account registration was successful
        if (registeredAcc != null) {
            // do not return the account ID to the client
            acc.setAccountId(null);
            return ResponseEntity.status(HttpStatus.OK).body(acc);
        }
        
        // if acc registration was not successful return code 400 (client error)
        return ResponseEntity.status(400).body(null);
    }

}
