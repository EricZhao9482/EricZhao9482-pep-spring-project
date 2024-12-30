package com.example.controller;
import com.example.service.*;
import com.example.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
@Controller
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
     * @param acc from Request Body
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

    /**
     * Processes login attempt. 
     * @param account credentials from Request Body
     * @return Login Success: Logged in account (with the ID) + Status Code 200 (OK)
     *         Login Failure: Status Code 401 (UNAUTHORIZED)
     */
    @PostMapping("/login")
    public ResponseEntity loginHandler(@RequestBody Account acc) {

        //attempt login
        Account loggedInAccount = this.accService.login(acc);

        // check for successful login
        if (loggedInAccount != null) {
            // Status Code 200 if successful
            return ResponseEntity.status(HttpStatus.OK).body(loggedInAccount);
        }

        // status code 401 on failure
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    /**
     * Handles the creation of a new message. Passes the msg body to the msgService to 
     * handle it being updated to the table. Message creation will not be successful if:
     *  - message text is blank
     *  - message text length is > 255 chars
     *  - account that the message is posted by is tied to an existing account
     * Checks are handled by their respective service classes.
     * @param msg
     * @return Status code 200 (OK) + the created message. 
     *         If message creation fails: 400 (Client Error)
     */
    @PostMapping("/messages")
    public ResponseEntity createMessageHandler(@RequestBody Message msg) {
        
        // check if poster actually exists via ID
        Integer postedByID = msg.getPostedBy();

        // if no account with the postedBy ID exists, return status code 400
        if (!this.accService.accountIDExists(postedByID)) {
            return ResponseEntity.status(400).body(null);
        }

        // persist message to database
        Message createdMsg = this.msgService.persistMessage(msg);

        // if persist failed return status code 400 and exit
        if (createdMsg == null) {
            return ResponseEntity.status(400).body(null);
        }

        // return status code 200 and the successfully created message
        return ResponseEntity.status(HttpStatus.OK).body(createdMsg);

    }

    /**
     * Gets all messages through calling the service class. 
     * @return A list off all messages in the response body + code 200 (OK)
     */
    @GetMapping("/messages")
    public ResponseEntity getAllMessagesHandler() {
        List<Message> allMsgs = this.msgService.getAllMessages();
        
        // return status code 200 + a list of all the messages
        return ResponseEntity.status(HttpStatus.OK).body(allMsgs);
    }


    /**
     * Gets message by it's ID by calling the message service class.
     * @param messageId
     * @return The found message (null otherwise) in the response body + code 200 (OK)
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity getMessageByIdHandler(@PathVariable Integer messageId) {
        Message msg = this.msgService.getMessageById(messageId);
        
        // return status code 200 + the message if found
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }

    /**
     * Deletes message by ID by calling the message service class.
     * @param messageId
     * @return If message existed: number of rows affected in the response body + status 200
     *         If message did not ever exist: empty response body + status code 200 
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity deleteMessageByIdHandler(@PathVariable Integer messageId) {
        // call the service to attempt to delete the message and get back affected rows
        Integer numOfUpdatedRows = this.msgService.deleteMessageById(messageId);
        if (numOfUpdatedRows >= 1) {
            // response status code 200 + the number of updated rows (should be 1)
            return ResponseEntity.status(HttpStatus.OK).body(numOfUpdatedRows);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * Updates a message given a message ID. message contents are taken from the request body.
     * Update will be successful if:
     *  - Message content is not blank
     *  - Message content is not over 255 chars long
     *  - Message with the given ID exists
     * Checks are handled by the message service class
     * @param messageId
     * @param msg (from the request body)
     * @return Num of updated rows in response entity + code 200 (OK)
     *         If update fails: empty response body + code 400 (CLIENT ERROR)
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity updateMessageHandler(@PathVariable Integer messageId, @RequestBody Message msg) {
        String msgText = msg.getMessageText();
        Integer numOfUpdatedRows = this.msgService.updateMessageById(messageId, msgText);

        if (numOfUpdatedRows >= 1) {
            // return code 200 and the num of updated rows (1)
            return ResponseEntity.status(HttpStatus.OK).body(numOfUpdatedRows);
        }
        return ResponseEntity.status(400).body(null);
    }

    /**
     * Gets all messages posted by a user given an account ID.
     * @param accountId
     * @return a list of all retrieved messages in response entity + code 200 (OK)
     */
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity getAllMessagesFromUserHandler(@PathVariable Integer accountId) {
        List<Message> retrievedMsgs = this.msgService.getMessagesFromUser(accountId);
        // return status code 200
        return ResponseEntity.status(HttpStatus.OK).body(retrievedMsgs);
    }
}
