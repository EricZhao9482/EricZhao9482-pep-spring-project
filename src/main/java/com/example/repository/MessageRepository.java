package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Message;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>{

    List<Message> findMessagesByPostedBy(Integer postedBy);
}
