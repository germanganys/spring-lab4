package com.example.lab4.rest;

import com.example.lab4.jpa.PointRepositoryJPA;
import com.example.lab4.models.JSONResponse;
import com.example.lab4.models.Point;
import com.example.lab4.services.KeyVault;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/points")
public class PointRest {

    @Autowired
    PointRepositoryJPA dbPoints;

    @Autowired
    KeyVault keyVault;

    @PostMapping("/add")
    public String add(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("token").getAsString();

        if (keyVault.isValidUser(key)) {
            try {
                String x = root.getAsJsonObject().get("x").getAsString();
                String y = root.getAsJsonObject().get("y").getAsString();
                String r = root.getAsJsonObject().get("r").getAsString();

                if (r.length() > 4 || Double.parseDouble(r) <= 0 || Double.parseDouble(r) > 5)
                    throw new Exception("Invalid r");


                Point point = new Point();
                point.setX(Double.parseDouble(x));
                point.setY(Double.parseDouble(y));
                point.setR(Double.parseDouble(r));
                point.setResult(Point.calculate(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(r)));
                this.dbPoints.save(point);
                resp.last_point = point;
                resp.status = JSONResponse.statusOk;
            } catch (Exception e) {
                resp.details = e.getMessage();
                resp.status = JSONResponse.statusFail;
            }
        } else {
            resp.details = "Invalid key";
            resp.status = JSONResponse.statusFail;
        }
        return gson.toJson(resp, JSONResponse.class);
    }

    @PostMapping("/get")
    public String results(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("token").getAsString();

        if (keyVault.isValidUser(key)) {
            resp.data = this.dbPoints.findAll();
            resp.status = JSONResponse.statusOk;
            return gson.toJson(resp, JSONResponse.class);
        } else {
            resp.status = JSONResponse.statusFail;
            resp.details = "Invalid key";
        }
        return gson.toJson(resp, JSONResponse.class);
    }

    @PostMapping("/clear")
    public String clear(@RequestBody String json) {
        Gson gson = new Gson();
        JSONResponse resp = new JSONResponse();
        resp.status = JSONResponse.statusFail;

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("token").getAsString();

        if (keyVault.isValidUser(key)) {
            this.dbPoints.deleteAll();
            resp.status = JSONResponse.statusOk;
            return gson.toJson(resp, JSONResponse.class);
        }
        return gson.toJson(resp, JSONResponse.class);
    }
}
