package com.devmasterteam.tasks.entities;

public class APIResponse {

    public String json;
    public Integer statusCode;

    /**
     * Construtor
     * */
    public APIResponse(String json, Integer status) {
        this.json = json;
        this.statusCode = status;
    }

}
