package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

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
}
