package com.tiany.nigel.arduinoled.paid;

import processing.core.PApplet;

/**
 * Created by Nigel on 1/12/2016.
 */
public class ProcessingLogics extends PApplet {
    @Override
    public void settings() {
        size(1920, 1080, OPENGL);
        fullScreen();
        println("settings()");
    }

    @Override
    public void setup() {
        println("setup()");
        background(0);
        strokeWeight(8.0f);
    }
}
