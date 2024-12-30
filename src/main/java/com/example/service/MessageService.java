package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

import java.util.Optional;
import java.util.List;

@Service
public class MessageService {

    MessageRepository msgRep;

    @Autowired
    public MessageService(MessageRepository msgRep) {
        this.msgRep = msgRep;
    }

    /**
     * Calls the message repository to save a new/update a message to the database.
     * Message will be persisted if:
     * - It is not blank
     * - Message text is not over 255 characters
     * @param msg
     * @return the persisted message. null if otherwise.
     */
    public Message persistMessage(Message msg) {
        String msgText = msg.getMessageText();
        
        // guard statement 
        if (msgText == "" || msgText == null || msgText.length() > 255)
            return null;
        
        // persist message to database 
        return this.msgRep.save(msg);
    }

    /**
     * Calls the repository to get all messages from the database
     * @return a list of all messages
     */
    public List<Message> getAllMessages() {
        return this.msgRep.findAll();
    }

    /**
     * Calls the repository to get a message by its ID from the database.
     * This method is called by other methods in this class to check if message exists.
     * @param msgId
     * @return Message. Null if not found.
     */
    public Message getMessageById(Integer msgId) {
        Optional<Message> searchedMsg = this.msgRep.findById(msgId);
        if (searchedMsg.isPresent()) {
            return searchedMsg.get();
        }
        return null;
    }

    /**
     * Deletes the message from the database based on it's ID. 
     * @param msgId
     * @return The number of affected rows after the delete.
     */
    public Integer deleteMessageById(Integer msgId) {

        if (this.getMessageById(msgId) == null) {
            return 0;
        }

        // in order to get the number of updated rows after the delete, we will 
        // take the difference of messages in the table before and after the delete
        Integer msgsBeforeDelete = this.msgRep.findAll().size();
        
        // execute delete
        this.msgRep.deleteById(msgId);

        Integer msgsAfterDelete = this.msgRep.findAll().size();

        return msgsBeforeDelete - msgsAfterDelete;
    }

    /**
     * Calls the message repo to update a message in the database given an ID
     * Message update will be successful if:
     * - Message is not blank
     * - Message is not over 255 chars long
     * @param msgId
     * @return Number of rows updated
     */
    public Integer updateMessageById(Integer msgId, String msgText) {

        // check message text for validity 
        if (msgText == "" || msgText == null || msgText.length() > 255)
            return 0;

        
        // attempt to get message and make sure it exists before further editing
        Message retrievedMsg = this.getMessageById(msgId);
        if (retrievedMsg == null) {
            return 0;
        }

        // update text in message and save it to the database and return 1
        retrievedMsg.setMessageText(msgText);
        this.msgRep.save(retrievedMsg);

        return 1;
    }
}
