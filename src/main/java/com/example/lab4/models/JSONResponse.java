package com.example.lab4.models;

import com.example.lab4.models.Point;

import java.util.List;

public class JSONResponse {
    public static final String statusOk = "ok";
    public static final String statusFail = "failed";

    public String status;
    public String details;

    public String key;
    public List<Point> data;
    public Point last_point;

    public JSONResponse() {
    }
}

