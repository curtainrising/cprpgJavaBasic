package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.Options;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;


/**
 * Created by Sam on 17/06/2016.
 */
public class socketInteractor {
    private Socket mSocket;
    private ArrayList<String> connections;

    public socketInteractor(){
        connections = new ArrayList<String>();
    }
    public boolean connect(String userId){
        try {
            Options testopt = new Options();
            testopt.query = "id=" + userId;
            mSocket = IO.socket("http://test.evolworld.com:8066", testopt);
        } catch (URISyntaxException e) {
            Log.e("connectionError", e.getMessage());
            return false;
        }
        mSocket.connect();
        return true;
    }
    public boolean hook(String id, Emitter.Listener callback){
        Log.d("hooking", id);
        mSocket.on(id, callback);
        connections.add(id);
        return true;
    }
    public boolean disconnect(){
        mSocket.disconnect();
        for(String i : connections){
            mSocket.off(i);
        }
        return true;
    }
}
