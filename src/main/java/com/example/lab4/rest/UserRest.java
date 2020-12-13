package com.example.lab4.rest;

import com.example.lab4.db.UserRepositoryJPA;
import com.example.lab4.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@CrossOrigin(origins = "http://localhost:9824")
@RestController
@RequestMapping("/user")
public class UserRest {

    @Autowired
    UserRepositoryJPA dbUsers;

    @PostMapping("/login")
    public String login(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        try {
            JsonElement root = new JsonParser().parse(json);
            String username = root.getAsJsonObject().get("username").getAsString();
            String password = root.getAsJsonObject().get("password").getAsString();


            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String encodedPass = Base64.getEncoder().encodeToString(hash);

            List<User> users = this.dbUsers.findAll();

            for (User user : users) {
                if (username.equals(user.getUsername()) &&
                        encodedPass.equals(user.getPasswordHash())) {
                    resp.key = Session.getNewKey();
                    return gson.toJson(resp, MyResponse.class);
                }
            }
            throw new Exception("Invalid username or pass");

        } catch (Exception e) {
            resp.status = MyResponse.statusFail;
            return gson.toJson(resp, MyResponse.class);
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        try {
            JsonElement root = new JsonParser().parse(json);
            String username = root.getAsJsonObject().get("username").getAsString();
            String password = root.getAsJsonObject().get("password").getAsString();

            if (!this.alreadyRegistered(username)) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                String encodedPass = Base64.getEncoder().encodeToString(hash);
                User user = new User();
                user.setUsername(username);
                user.setPasswordHash(encodedPass);

                this.dbUsers.save(user);
                resp.key = Session.getNewKey();
                return gson.toJson(resp, MyResponse.class);
            } else
                throw new Exception("Already registered");

        } catch (Exception e) {
            resp.status = MyResponse.statusFail;
            return gson.toJson(resp, MyResponse.class);
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = MyResponse.statusOk;
        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("key").getAsString();
        Session.deleteKey(key);
        return gson.toJson(resp, MyResponse.class);
    }

    private Boolean alreadyRegistered(String username) {
        List<User> users = this.dbUsers.findAll();

        for (User user : users) {
            if (username.equals(user.getUsername()))
                return true;
        }

        return false;
    }

}
