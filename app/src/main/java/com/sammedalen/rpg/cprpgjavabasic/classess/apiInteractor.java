package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.sammedalen.rpg.cprpgjavabasic.R;
import com.sammedalen.rpg.cprpgjavabasic.classess.*;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class apiInteractor {
    private enum apiEndpoints{
        TEST ("test"),
        LOGIN ("player/login"),
        LOGOUT ("/player/:uid/logout"),
        GUEST ("player/guest"),
        SINGUP ("player"),
        ALLPLAYRECHARACTERS ("player/:uid/character"),
        CREATECHARACTER ("player/:uid/character"),
        USECHARACTER ("game/:uid/usecharacter"),
        MOVE ("game/:uid/move");
        private String value;
        private apiEndpoints(String endpoint){
            this.value = endpoint;
        }
    }

    private Socket mSocket;

    public JSONArray characterData;

    private String username;
    private String password;
    private String loginCred;
    private String authToken;
    private OkHttpClient client;
    private String baseUrl;
    private String test;
    public String userId;
    public apiInteractor(){
        this. client = new OkHttpClient();
        this.baseUrl = "http://rpg.sammedalen.com:8066/";
        this.username = "";
        this.password = "";
        this.loginCred = "";
        this.authToken = "";
        this.userId = "";
        this.test = "created";
    }
    public String createUrl(String endpoint, ArrayList data){

        return this.baseUrl + endpoint;
    }
    public Boolean isLoggedIn(){
        if(this.userId == ""){
            return false;
        }
        return true;
    }
    private void clearUserData(){
        this.username = "";
        this.userId = "";
    }
    // @String type : the type of request beng made.  Default for this package is get - POST requires a body
    // @String url : The url to be used in the api request
    // @RequestBody body : the data being sent - requires with a POST
    private Response makeCall(String type, String url, RequestBody body)throws IOException {
        Request.Builder requestBuild = new Request.Builder();
        requestBuild.url(url);
        if(body != null){
            requestBuild.method(type, body);
        }
        return client.newCall(requestBuild.build()).execute();
    }
    public void test(){
        new asyncCall("POST", this.createUrl(apiEndpoints.TEST.value, null), null, new asyncCallback() {
            @Override
            public void sendResponse(String responseString) {
                Log.d("callback", responseString);
            }
        }).execute();
    }
    public Boolean login(String username, String password){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jobj = new JSONObject();
        try {
            jobj
                    .accumulate("username", username)
                    .accumulate("password", password);
        } catch (JSONException e) {
            return false;
        }

        RequestBody body = RequestBody.create(JSON, jobj.toString());
        String url = this.createUrl(apiEndpoints.LOGIN.value, null);

        try {
            Response response = makeCall("POST", url, body);
            try {
                JSONObject responsebody = new JSONObject(response.body().string());
                this.userId = responsebody.getString("_id");
                this.username = responsebody.getString("username");
            } catch (JSONException e) {
                Log.e("JSON issue", e.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e("loginError", e.getMessage());
            return false;
        }
        return true;
    }
    public Boolean signUp(String username, String password){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jobj = new JSONObject();
        try {
            jobj
                    .accumulate("username", username)
                    .accumulate("password", password);
        } catch (JSONException e) {
            return false;
        }

        RequestBody body = RequestBody.create(JSON, jobj.toString());
        String url = this.createUrl(apiEndpoints.SINGUP.value, null);

        try {
            Response response = makeCall("POST", url, body);
            try {
                String temp = response.body().string();
                JSONObject responsebody = new JSONObject(temp);
                Log.d("signupresponse", temp);
                this.userId = responsebody.getString("_id");
                this.username = responsebody.getString("username");
            } catch (JSONException e) {
                Log.e("JSON issue", e.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e("loginError", e.getMessage());
            return false;
        }
        return true;
    }
    public Boolean guestLogin(){
        String url = this.createUrl(apiEndpoints.GUEST.value, null);

        try {
            Response response = makeCall("GET", url, null);
            try {
                JSONObject responsebody = new JSONObject(response.body().string());
                this.userId = responsebody.getString("_id");
                this.username = responsebody.getString("username");
            } catch (JSONException e) {
                Log.e("JSON issue", e.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e("loginError", e.getMessage());
            return false;
        }
        return true;
    }
    public void logOut(Boolean doAsync){
        //due to a weird kink with the way okhttps3 works, there must be a body to do a post
        String url = this.createUrl(apiEndpoints.LOGOUT.value, null).replace(":uid", this.userId);
        this.clearUserData();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jobj = new JSONObject();
        try{
            jobj.accumulate("empty", "data");
        }catch (JSONException e){}
        RequestBody body = RequestBody.create(JSON, jobj.toString());
        if(doAsync == true){
            new asyncCall("POST", url, body, new asyncCallback() {
                @Override
                public void sendResponse(String responseString) {
                    Log.d("logout", responseString);
                }
            });
        } else {
            try {
                Response response = makeCall("POST", url, body);
                Log.d("logout", response.body().string());
            } catch (IOException e) {
                Log.e("ccNetwork", e.getMessage());
            }
        }
    }
    public String createCharacter(JSONObject playerData){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, playerData.toString());

        String url = this.createUrl(apiEndpoints.CREATECHARACTER.value, null).replace(":uid", this.userId);

        try {
            Response response = makeCall("POST", url, body);
            try {
                JSONObject responsebody = new JSONObject(response.body().string());
                return responsebody.getString("_id");
            } catch (JSONException e) {
                Log.e("ccJSON", e.getMessage());
                return null;
            }
        } catch (IOException e) {
            Log.e("ccNetwork", e.getMessage());
            return null;
        }
    }
    public JSONArray findAllCharactersForPlayer(String uid){
        String url = this.createUrl(apiEndpoints.ALLPLAYRECHARACTERS.value.replace(":uid", this.userId), null);
        Log.d("url find all", url);
        try {
            Response response = makeCall("GET", url, null);
            try {
                JSONArray responsebody = new JSONArray(response.body().string());
                this.characterData = responsebody;
                return responsebody;

            } catch (JSONException e) {
                Log.e("find all JSON issue", e.getMessage());
                return null;
            }
        } catch (IOException e) {
            Log.e("findall error", e.getMessage());
            return null;
        }
    }
    public String useCharacter(String cid){
        String url = this.createUrl(apiEndpoints.USECHARACTER.value, null).replace(":uid", this.userId);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jobj = new JSONObject();
        try {
            jobj.accumulate("_id", cid);
        } catch (JSONException e) {
            return null;
        }

        RequestBody body = RequestBody.create(JSON, jobj.toString());
        try {
            Response response = makeCall("POST", url, body);
            return response.body().string();
        }catch (IOException e){
            return null;
        }
    }
    public void move(JSONObject moveData, Boolean async){
        String url = this.createUrl(apiEndpoints.MOVE.value, null).replace(":uid", this.userId);
        Log.d("url", url);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, moveData.toString());
        if(async == true){
            Log.d("move", "do async");
            new asyncCall("POST", url, body, new asyncCallback() {
                @Override
                public void sendResponse(String responseString) {
                    Log.d("move", responseString);
                }
            }).execute();
        } else {
            try {
                Response response = makeCall("POST", url, body);
                Log.d("logout", response.body().string());
            } catch (IOException e) {
                Log.e("ccNetwork", e.getMessage());
            }
        }
    }
    public void setTest(String testString){
        this.test = testString;
    }
    public void getTest(){
        Log.d("getTest", this.test);
    }


    interface asyncCallback{
        public void sendResponse(String responseString);
    }
    // can only be used to make calls that you don't need a response back from.
    public class asyncCall extends AsyncTask<Void, Void, Boolean> {

        String type;
        String url;
        RequestBody body;
        asyncCallback setter;
        asyncCall(String type, String url, RequestBody body, asyncCallback setter) {
            this.type = type;
            this.url = url;
            this.body = body;
            this.setter = setter;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Response response = makeCall(type, url, body);
                setter.sendResponse(response.body().string());
            }catch (IOException e) {
                Log.e("io error", e.getMessage());
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {


            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
