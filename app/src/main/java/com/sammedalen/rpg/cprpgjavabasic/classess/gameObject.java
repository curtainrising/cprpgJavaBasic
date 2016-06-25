package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam on 23/06/2016.
 */
public class gameObject {
    protected Vector2D size;
    protected Vector2D location;
    protected Vector2D speed;
    protected Vector2D goal;
    public gameObject(JSONObject data){
        try{
            JSONObject locationJson = data.getJSONObject("location");
            Log.d("location", locationJson.toString());
            this.location = new Vector2D(locationJson.getInt("x"), locationJson.getInt("y"));

            if(data.has("moveData")){
                JSONObject moveData = data.getJSONObject("moveData");
                JSONObject goalData = moveData.getJSONObject("goal");
                JSONObject speedData = moveData.getJSONObject("speed");
                speed = new Vector2D(speedData.getInt("x"), speedData.getInt("y"));
                goal = new Vector2D(goalData.getInt("x"), goalData.getInt("y"));
            }
            size = new Vector2D(5, 5);
        } catch (JSONException e){
            Log.e("gameObjectcon", e.getMessage());
        }
    }
    public void setGoal(JSONObject moveData){
        try{
            JSONObject goal = moveData.getJSONObject("goal");
            setGoal(new Vector2D(goal.getInt("x"), goal.getInt("y")));
        }catch (JSONException e){}
    }
    public Map<String, Vector2D> setGoal(Vector2D goal){
        Map<String, Vector2D> tempMoveData = new HashMap<>();
        tempMoveData.put("goal", goal);
        this.goal = goal;
        Float distanceX = goal.getX() - this.location.getX();
        Float distanceY = goal.getY() - this.location.getY();
        Float signX = distanceX >= 0? 1f: -1f;
        Float signY = distanceY >= 0? 1f: -1f;
        Double angle = Math.atan(Math.abs(distanceX/distanceY));
        this.speed = new Vector2D(
                (float)(20 * Math.sin(angle) * signX),
                (float)(20 * Math.cos(angle) * signY)
        );
        tempMoveData.put("speed", this.speed);
        Log.d("goal", goal.toString());
        Log.d("speed", speed.toString());
        return tempMoveData;
    };
    public Boolean isMoving(){
        if(this.goal != null){
            return true;
        }
        return false;
    }
    protected void update(Float timeSinceLastLoop){
        this.location.add(this.speed.mult(timeSinceLastLoop/1000f));
    }
    protected Boolean draw(Canvas canvas, Paint paint, Bitmap bitmapBob){
        canvas.drawBitmap(bitmapBob, this.location.getX(), this.location.getY(), paint);
        return true;
    };
    protected void setLocation(Integer x, Integer y){
        this.location = new Vector2D(y * 1f,x *1f);
    }
}
