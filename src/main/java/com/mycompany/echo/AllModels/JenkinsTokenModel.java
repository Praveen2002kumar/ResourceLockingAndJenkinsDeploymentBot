package com.mycompany.echo.AllModels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document(collection = "Tokens")
@Component
public class JenkinsTokenModel {
    @Id
    private String id;
    private String email;
    private String token;
    public static String encrypt(String plaintext, int key) {
        StringBuilder encryptedText = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                char encryptedChar = (char) (((ch - base + key) % 26) + base);
                encryptedText.append(encryptedChar);
            } else {
                encryptedText.append(ch);
            }
        }
        return encryptedText.toString();
    }

    // Decrypt a ciphertext with a given key
    public static String decrypt(String ciphertext, int key) {
        return encrypt(ciphertext, 26 - key);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return decrypt(token,3);
    }

    public void setToken(String token) {
        this.token = token;

    }
}
