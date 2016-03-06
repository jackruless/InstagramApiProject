package com.example.administrator.myinstaapp.Pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 3/5/2016.
 */
public class Picture implements Serializable {
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    private String URL;


}
