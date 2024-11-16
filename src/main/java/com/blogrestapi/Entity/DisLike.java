package com.blogrestapi.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dislike")
public class DisLike {
    @MongoId
    private int id;
    @DBRef
    private User user;
    @DBRef
    private Post post;
}
