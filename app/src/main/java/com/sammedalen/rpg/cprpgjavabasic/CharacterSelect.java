package com.sammedalen.rpg.cprpgjavabasic;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sammedalen.rpg.cprpgjavabasic.classess.myApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CharacterSelect extends AppCompatActivity {
    private Integer characterIndex;
    private JSONArray characters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);
        this.characters = ((myApp)getApplicationContext()).getAPIInstance().characterData;
        ArrayList<String> names  = new ArrayList<String>();
        try{
            for(Integer i = 0; i < characters.length(); i ++){
                Log.d("characterData", characters.getJSONObject(i).toString());
                names.add(characters.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e){

        }


        Spinner spinner = (Spinner)findViewById(R.id.character_select_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.add("Choose One");
        spinnerAdapter.addAll(names);
        spinnerAdapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position > 0) {
                    try {
                        displayCharacter(characters.getJSONObject(position - 1));
                        characterIndex = position - 1;
                    } catch (JSONException e) {

                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0){

            }
        });

        Button confirmChoice = (Button) findViewById(R.id.character_select_choose_button);
        confirmChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //try {
                    //((myApp) getApplicationContext()).setCurrentCharacter(characters.getJSONObject(characterIndex));
                    new asyncUseCharacter().execute();
                //}catch (JSONException e){

                //}
            }
        });
    }
    private void displayCharacter(JSONObject data){
        RelativeLayout characterDisplay = (RelativeLayout) findViewById(R.id.character_select_display);
        characterDisplay.setVisibility(RelativeLayout.VISIBLE);
        Button confirmChoice = (Button) findViewById(R.id.character_select_choose_button);
        confirmChoice.setVisibility(Button.VISIBLE);
        TextView vName = (TextView) findViewById(R.id.character_select_name_value);
        TextView vClass = (TextView) findViewById(R.id.character_select_class_value);
        TextView vStrength = (TextView) findViewById(R.id.character_select_strength_value);
        TextView vDexterity = (TextView) findViewById(R.id.character_select_dexterity_value);
        TextView vIntelligence = (TextView) findViewById(R.id.character_select_intelligence_value);
        TextView vAttack = (TextView) findViewById(R.id.character_select_attack_value);
        TextView vMagicAttack = (TextView) findViewById(R.id.character_select_magic_attack_value);
        TextView vHealth = (TextView) findViewById(R.id.character_select_health_value);
        TextView vMana = (TextView) findViewById(R.id.character_select_mana_value);
        try {
            String temp = "";
            vName.setText(data.getString("name"));
            vClass.setText(data.getString("class"));
            temp = data.getInt("strength") + "";
            vStrength.setText(temp);
            temp = data.getInt("dexterity") + "";
            vDexterity.setText(temp);
            temp = data.getInt("intellegence") + "";
            vIntelligence.setText(temp);
            temp = data.getInt("attack") + "";
            vAttack.setText(temp);
            temp = data.getInt("magicAttack") + "";
            vMagicAttack.setText(temp);
            temp = data.getInt("health") + "";
            vHealth.setText(temp);
            temp = data.getInt("mana") + "";
            vMana.setText(temp);
        }catch (JSONException e){

        }
    }
    public class asyncUseCharacter extends AsyncTask<Void, Void, Boolean> {
        public asyncUseCharacter(){

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            //String cid = ((myApp) getApplicationContext()).playerCharacter.getCid();
            String cid = "";
            try {
                 cid = characters.getJSONObject(characterIndex).getString("_id");
            }catch (JSONException e){

            }
            String result = ((myApp)getApplicationContext()).getAPIInstance().useCharacter(cid);

            setResult(AppCompatActivity.RESULT_OK, getIntent().putExtra("gameData", result));
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {
                //finish();

                finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
