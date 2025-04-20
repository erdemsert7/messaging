package com.example.messaging.repository;

import com.example.messaging.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{$or: [{senderUsername: ?0, receiverUsername: ?1}, {senderUsername: ?1, receiverUsername: ?0}]}")
    List<Message> findBySenderAndReceiver(String senderUsername, String receiverUsername);
}