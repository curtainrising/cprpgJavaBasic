package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.sammedalen.rpg.cprpgjavabasic.GameActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sam on 24/06/2016.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    MainThread thread;
    SurfaceHolder ourHolder;
    Paint paint;
    Bitmap bitmapBob;
    Boolean switchColour;
    GameActivity gA;
    Vector2D size;
    Map<String, gameObject> gameData;
    ArrayList<String> movingObjects;
    public GamePanel(GameActivity context, Bitmap bob) {
        super(context);
        switchColour = false;
        this.gA = context;
        //add the callback to the surfaceholder to intercept events
        ourHolder = getHolder();
        ourHolder.addCallback(this);
        setupGameData(context.getIntent().getExtras().getString("gameData"));
        setupSocketConnections(this.gA.getSocket(), this.gA.getApi().userId);
        thread = new MainThread(ourHolder, this);

        paint = new Paint();

        // Load Bob from his .png file
        bitmapBob = bob;
        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }
    public void setupGameData(String gameDataString){
        this.gameData = new HashMap<String, gameObject>();
      try{
          Log.d("gameData", gameDataString);
          JSONObject gameDataJSON = new JSONObject(gameDataString);
          this.size = new Vector2D(gameDataJSON.getInt("sizeX") * 1f, gameDataJSON.getInt("sizeY") * 1f);
          this.movingObjects = new ArrayList<>();
          JSONObject gameObjects = gameDataJSON.getJSONObject("objects");
          JSONObject gameObjectJson;
          Iterator<String> objectIds = gameObjects.keys();
          while(objectIds.hasNext()){
              String id = objectIds.next();
              gameObjectJson = gameObjects.getJSONObject(id);
              addGameObject(gameObjectJson, id);
          }
      }catch (JSONException e){
          Log.e("outerJSong", e.getMessage());
      }
    }
    public void addGameObject(JSONObject gameObjectJson, String id){
        try {
            //if key found is a player
            gameObjectJson.getString("id");
            character temp = new character(gameObjectJson);
            this.gameData.put(id, temp);
        }catch (JSONException e){//else it is a gameObject
            this.gameData.put("id", new gameObject(gameObjectJson));
        }
        Log.d("data", gameObjectJson.toString());
        if(this.gameData.get(id).isMoving()){
            this.movingObjects.add(id);
        }
    }
    public void removeObject(String id){
        if(this.movingObjects.contains(id)){
            this.movingObjects.remove(id);
        }
        if(this.gameData.containsKey(id)){
            this.gameData.remove(id);
        }
    }
    public void setupSocketConnections(socketInteractor client, String userId){
        client.connect(userId);
        client.hook("moveObject", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject moveData = new JSONObject(args[0].toString());
                    Log.d("move", moveData.toString());
                    if(gameData.containsKey(moveData.getString("id"))){
                        gameData.get(moveData.getString("id")).setGoal(moveData.getJSONObject("data"));
                        movingObjects.add(moveData.getString("id"));
                    }
                } catch (JSONException e){
                }
            }
        });
        client.hook("addObject", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject objectData = new JSONObject(args[0].toString());
                    Log.d("add", objectData.toString());
                    String objId = "";
                    if(objectData.has("id")){
                        objId = objectData.getString("id");
                    } else {
                        objId = objectData.getString("_id");
                    }
                    addGameObject(objectData, objId);
                } catch (JSONException e){

                }
            }
        });
        client.hook("removeObject", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String id = args[0].toString();
                Log.d("remove", id);
                removeObject(id);
            }
        });
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.thread.setIsRunning(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                String uid = this.gA.getApi().userId;
                Log.d("xpos", event.getX() + "");
                Log.d("ypos", event.getY() + "");
                Map<String, Vector2D> moveData  = this.gameData.get(uid).setGoal(new Vector2D(event.getX(), event.getY()));
                this.movingObjects.add(uid);
                JSONObject moveDataJson = new JSONObject();
                try{
                    JSONObject goal = new JSONObject();
                    JSONObject speed = new JSONObject();
                    goal.accumulate("x", moveData.get("goal").getX());
                    goal.accumulate("y", moveData.get("goal").getY());
                    speed.accumulate("x", moveData.get("speed").getX());
                    speed.accumulate("y", moveData.get("speed").getY());
                    moveDataJson.accumulate("goal", goal);
                    moveDataJson.accumulate("speed", speed);
                    this.gA.getApi().move(moveDataJson, true);
                }catch (JSONException e){
                    Log.e("sendmove", e.getMessage());
                }
                Log.d("endevent", moveDataJson.toString());
                break;
        }

        return true;
    }
    public void update(Float timeSinceLastLoop){
        for(String id : this.movingObjects){
            this.gameData.get(id).update(timeSinceLastLoop);
        }
        //this.switchColour = !switchColour;
    }

    public void draw(){
        Canvas canvas = ourHolder.lockCanvas();
        paint.setColor(Color.argb(255,  249, 129, 0));

        if(switchColour == true){
            //Log.d("draw", "0 0 255");
            canvas.drawColor(Color.rgb(0, 0, 255));
        } else {
            //Log.d("draw", "255 0 0");
            canvas.drawColor(Color.rgb(255, 0, 0));
        }
        Set<String> gameIdSet = this.gameData.keySet();
        Iterator<String> gameIds = gameIdSet.iterator();
        while(gameIds.hasNext()){
            String id = gameIds.next();
            this.gameData.get(id).draw(canvas, paint, this.bitmapBob);
        }
        try {
            ourHolder.unlockCanvasAndPost(canvas);
        }
        catch(Exception e){
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
    }

}
