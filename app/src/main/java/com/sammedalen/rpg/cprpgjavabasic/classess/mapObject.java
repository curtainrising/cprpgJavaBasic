package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sam on 23/06/2016.
 */
public class mapObject {
    private Pair<Float, Float> location;
    private Pair<Float, Float> goal;
    private Pair<Float, Float> speed;
    private String objectId;
    private String gameId;
    private gameObject data;
    public mapObject(String objectId, String gameId, Pair<Float, Float> location){
        this.objectId = objectId;
        this.gameId = gameId;
        this.location = location;
    }
    public mapObject(String ObjectId, String gameId, Pair<Float, Float> location, JSONObject data){
        try {
            if (data.getString("type") == "player") {
                this.data = new character(data);
            }
        } catch (JSONException e){

        }
    }
    public void setGoal(Pair<Float, Float> goal){
        this.goal = goal;
        this.speed = goal;
    }
    public void doMove(Float deltaTime){

    }
}
