package com.sammedalen.rpg.cprpgjavabasic;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sammedalen.rpg.cprpgjavabasic.classess.apiInteractor;
import com.sammedalen.rpg.cprpgjavabasic.classess.character;
import com.sammedalen.rpg.cprpgjavabasic.classess.myApp;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CharacterCreate extends AppCompatActivity {
    Map<String, Integer> stats;
    JSONObject characterJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_create);
        setupStartingdata();
        setupButtons();
    }
    protected void setupStartingdata(){
        stats = new HashMap<String, Integer>();
        stats.put("strength", 5);
        stats.put("intelligence", 5);
        stats.put("dexterity", 5);
        stats.put("available", 5);
    }
    protected void updateStats(String type){
        String text = stats.get(type) + "";
        if(type == "strength"){
            ((TextView)findViewById(R.id.character_creation_strength_value)).setText(text);
        } else if (type == "intelligence"){
            ((TextView)findViewById(R.id.character_creation_intelligence_value)).setText(text);
        } else if (type == "dexterity"){
            ((TextView)findViewById(R.id.character_creation_dexterity_value)).setText(text);
        }
        text = stats.get("available") + "";
        ((TextView) findViewById(R.id.character_creation_available_stat_points_value)).setText(text);
    }

    protected void setupButtons(){
        View.OnClickListener subtract = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String type = view.getTag().toString();
                if(stats.get(type) -1 >= 5){
                    stats.put(type, stats.get(type) -1);
                    stats.put("available", stats.get("available") +1 );
                    updateStats(type);
                }
            }
        };
        View.OnClickListener add = new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String type = view.getTag().toString();
                if(stats.get("available") -1 >= 0) {
                    stats.put(type, stats.get(type) + 1);
                    stats.put("available", stats.get("available") - 1);
                    updateStats(type);
                }
            }
        };
        View.OnClickListener choose = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String name = ((TextView)findViewById(R.id.character_create_name_textbox)).getText().toString();
                String className = "warriror";
                Map<String, Integer> skills = new HashMap<String, Integer>();
                skills.put("hearty", 1);
                characterJSON = new JSONObject();
                try {
                    characterJSON.accumulate("name", name);
                    characterJSON.accumulate("class", className);
                    characterJSON.accumulate("strength", stats.get("strength"));
                    characterJSON.accumulate("dexterity", stats.get("dexterity"));
                    characterJSON.accumulate("intelligence", stats.get("intelligence"));
                    characterJSON.accumulate("health", 5);
                    characterJSON.accumulate("mana", 5);
                    characterJSON.accumulate("attack", 5);
                    characterJSON.accumulate("magicAttack", 5);
                    JSONObject jsonSkills = new JSONObject();
                    jsonSkills.accumulate("hearty", 1);
                    characterJSON.accumulate("skills", jsonSkills);
                    characterJSON.accumulate("inventory", new JSONObject());
                }catch(JSONException e){
                }
                /*((myApp)getApplicationContext()).setCurrentCharacter(new character(
                        name,
                        className,
                        stats.get("strength"),
                        stats.get("dexterity"),
                        stats.get("intelligence"),
                        5,
                        5,
                        5,
                        5,
                        skills,
                        new HashMap<String, Integer>()
                ));*/
                new createCharacterTask().execute();
            }
        };
        Button strengthSub = (Button) findViewById(R.id.character_creation_strength_subtract);
        Button dexteritySub = (Button) findViewById(R.id.character_creation_dexterity_subtract);
        Button intelligenceSub = (Button) findViewById(R.id.character_creation_intelligence_subtract);
        strengthSub.setOnClickListener(subtract);
        dexteritySub.setOnClickListener(subtract);
        intelligenceSub.setOnClickListener(subtract);

        Button strengthAdd = (Button) findViewById(R.id.character_creation_strength_add);
        Button dexterityAdd = (Button) findViewById(R.id.character_creation_dexterity_add);
        Button intelligenceAdd = (Button) findViewById(R.id.character_creation_intelligence_add);
        strengthAdd.setOnClickListener(add);
        dexterityAdd.setOnClickListener(add);
        intelligenceAdd.setOnClickListener(add);

        Button create = (Button) findViewById(R.id.character_creation_create_button);
        create.setOnClickListener(choose);
    }
    public class createCharacterTask extends AsyncTask<Void, Void, Boolean> {
        private apiInteractor api;

        createCharacterTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            api = ((myApp) getApplicationContext()).getAPIInstance();
            //api.createCharacter(((myApp) getApplicationContext()).playerCharacter.toJSONObject());
            String cid = api.createCharacter(characterJSON);
            String result = ((myApp)getApplicationContext()).getAPIInstance().useCharacter(cid);
            getIntent().putExtra("gameData", result);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //finish();
                setResult(AppCompatActivity.RESULT_OK, getIntent());
                finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
