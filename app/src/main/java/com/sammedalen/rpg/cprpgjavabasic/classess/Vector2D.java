package com.sammedalen.rpg.cprpgjavabasic.classess;

import java.util.Vector;

/**
 * Created by Sam on 24/06/2016.
 */
public class Vector2D {
    private Float x;
    private Float y;
    public Vector2D(Float x, Float y){
        this.x = x;
        this.y = y;
    }
    public Vector2D(Integer x, Integer y){
        this.x = x * 1f;
        this.y = y * 1f;
    }
    public Vector2D add(Vector2D toAdd){
        this.x += toAdd.x;
        this.y += toAdd.y;
        return this;
    }
    public Vector2D add(Vector2D add1, Vector2D add2){
        return new Vector2D(add1.getX() + add2.getX(), add1.getY() + add2.getY());
    }
    public Vector2D mult(Float mag){
        return new Vector2D(this.x * mag, this.y * mag);
    }
    @Override
    public String toString(){
        return "{x:" + this.x + ",y:" + this.y + "}";
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
