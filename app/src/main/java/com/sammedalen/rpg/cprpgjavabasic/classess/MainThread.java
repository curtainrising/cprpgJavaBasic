package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Sam on 24/06/2016.
 */
public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    Boolean isRunning;
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.isRunning = true;
    }
    public void setIsRunning(Boolean running){
        this.isRunning = running;
    }
    @Override
    public void run(){
        long targetTime = 1000;
        long startTime;
        long timeMillis;
        long waitTime = targetTime;
        while(this.isRunning == true){
            //Log.d("loop", "looping");
            startTime = System.nanoTime();
            this.gamePanel.update((float)waitTime);
            this.gamePanel.draw();
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime-timeMillis;
            try {
                this.sleep(waitTime);
            } catch (Exception  e){

            }
        }
    }
}
