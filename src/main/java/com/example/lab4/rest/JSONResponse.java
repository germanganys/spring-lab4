package com.example.lab4.rest;

import com.example.lab4.models.Point;

import java.util.List;

public class JSONResponse {
    public static String statusOk = "ok";
    public static String statusFail = "failed";

    public String status;
    public String key;
    public List<Point> data;
    public Point last_point;

    public JSONResponse() {
    }
}

