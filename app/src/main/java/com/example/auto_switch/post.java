package com.example.auto_switch;

import java.util.HashMap;
import java.util.Map;

public class post {
    public String name;
    public String start_time;
    public String end_time;
    public boolean status;

    public post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public post(String name, String start_time, String end_time, boolean status) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("start_time", start_time);
        result.put("end_time", end_time);
        result.put("status", status);

        return result;
    }


}

