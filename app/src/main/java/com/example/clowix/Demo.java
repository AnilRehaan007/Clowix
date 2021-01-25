package com.example.clowix;

import java.io.Serializable;

public class Demo implements Serializable {

   private final String name;
   private final String roll;

    public Demo(String name, String roll) {
        this.name = name;
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public String getRoll() {
        return roll;
    }
}
