package com.rizikh.wfc.app;

import processing.core.PApplet;

public class TestProcessing extends PApplet {
    public void settings() {
        size(400, 400);
    }

    public void draw() {
        background(200);
        line(0, 0, width, height);
    }

    public static void main(String[] args) {
        PApplet.main(TestProcessing.class);
    }
}
