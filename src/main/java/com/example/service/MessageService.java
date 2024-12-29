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
     * Calls the repository to get a message by its ID from the database
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
}
