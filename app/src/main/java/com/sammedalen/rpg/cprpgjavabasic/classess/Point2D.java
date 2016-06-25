package com.sammedalen.rpg.cprpgjavabasic.classess;

/**
 * Created by Sam on 24/06/2016.
 */
public class Point2D {
    private Float x;
    private Float y;
    public Point2D(Float x, Float y){
        this.x = x;
        this.y = y;
    }
    public Point2D(Integer x, Integer y){
        this.x = x * 1f;
        this.y = y * 1f;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getX() {
        return x;
    }
}
