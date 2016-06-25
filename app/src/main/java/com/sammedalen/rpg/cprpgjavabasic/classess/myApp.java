package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.app.Application;
import com.sammedalen.rpg.cprpgjavabasic.classess.*;

import org.json.JSONObject;

/**
 * Created by Sam on 17/06/2016.
 */
public class myApp extends Application {
    public apiInteractor client;
    public socketInteractor mSocket;
    public character playerCharacter;
    public apiInteractor getAPIInstance(){
        if(this.client == null){
            client = new apiInteractor();
        }
        return client;
    }
    public void setCurrentCharacter(JSONObject data){
        this.playerCharacter = new character(data);
    }
    public void setCurrentCharacter(character playerCharacter){
        this.playerCharacter = playerCharacter;
    }
    public socketInteractor getSocketInstance(){
        if(mSocket == null){
            mSocket = new socketInteractor();
        }
        return mSocket;
    }

}
