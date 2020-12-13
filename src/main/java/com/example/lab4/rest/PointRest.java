package com.example.lab4.rest;

import com.example.lab4.db.PointRepositoryJPA;
import com.example.lab4.models.Point;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:9824")
@RestController
@RequestMapping("/api")
public class PointRest {

    @Autowired
    PointRepositoryJPA dbPoints;

    @PostMapping("/add")
    public String add(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = "failed";

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("key").getAsString();

        if (Session.isValidUser(key)) {
            try {
                String x = root.getAsJsonObject().get("x").getAsString();
                String y = root.getAsJsonObject().get("y").getAsString();
                String r = root.getAsJsonObject().get("r").getAsString();

                if (r.length() > 4 || Double.parseDouble(r) <= 0 || Double.parseDouble(r) > 4.99)
                    throw new Exception("Invalid r");


                Point point = new Point();
                point.setX(Double.parseDouble(x));
                point.setY(Double.parseDouble(y));
                point.setR(Double.parseDouble(r));
                point.setResult(Point.calculate(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(r)));
                this.dbPoints.save(point);
                resp.last_point = point;
                resp.status = "ok";
            } catch (Exception e) {
                resp.status = "failed";
            }
        } else {
            resp.status = "failed";
        }
        return gson.toJson(resp, MyResponse.class);
    }

    @PostMapping("/get")
    public String results(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = "failed";

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("key").getAsString();

        if (Session.isValidUser(key)) {
            resp.data = this.dbPoints.findAll();
            resp.status = "ok";
            return gson.toJson(resp, MyResponse.class);
        }
        return gson.toJson(resp, MyResponse.class);
    }

    @PostMapping("/clear")
    public String clear(@RequestBody String json) {
        Gson gson = new Gson();
        MyResponse resp = new MyResponse();
        resp.status = "failed";

        JsonElement root = new JsonParser().parse(json);
        String key = root.getAsJsonObject().get("key").getAsString();

        if (Session.isValidUser(key)) {
            this.dbPoints.deleteAll();
            resp.status = "ok";
            return gson.toJson(resp, MyResponse.class);
        }
        return gson.toJson(resp, MyResponse.class);
    }
}
