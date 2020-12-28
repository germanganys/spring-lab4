package com.example.lab4.rest;

import com.example.lab4.jpa.UserRepositoryJPA;
import com.example.lab4.models.JSONResponse;
import com.example.lab4.models.User;
import com.example.lab4.services.KeyVault;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/lk")
public class UserRest {

    @Autowired
    UserRepositoryJPA dbUsers;

    @Autowired
    KeyVault keyVault;

    @PostMapping("/login")
    public String login(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();
        resp.status = JSONResponse.statusOk;
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
                    resp.key = keyVault.newKey();
                    return gson.toJson(resp, JSONResponse.class);
                }
            }
            throw new Exception("Invalid username or pass");

        } catch (Exception e) {
            resp.details = e.getMessage();
            resp.status = JSONResponse.statusFail;
            return gson.toJson(resp, JSONResponse.class);
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();
        resp.status = JSONResponse.statusOk;
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
                resp.key = keyVault.newKey();
                return gson.toJson(resp, JSONResponse.class);
            } else
                throw new Exception("Already registered");

        } catch (Exception e) {
            resp.details = e.getMessage();
            resp.status = JSONResponse.statusFail;
            return gson.toJson(resp, JSONResponse.class);
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();
        resp.status = JSONResponse.statusOk;
        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("key").getAsString();
        keyVault.removeKey(key);
        return gson.toJson(resp, JSONResponse.class);
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
