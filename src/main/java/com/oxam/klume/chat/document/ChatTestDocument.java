package com.oxam.klume.chat.document;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "test_docs")
public class ChatTestDocument {

    @Id
    private String id;
    private String message;

    public ChatTestDocument(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

}
