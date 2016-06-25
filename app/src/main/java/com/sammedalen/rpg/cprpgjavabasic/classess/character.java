package com.sammedalen.rpg.cprpgjavabasic.classess;

import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Sam on 23/06/2016.
 */
public class character extends gameObject{
    private String cid;
    private String id;
    private String name;
    private String className;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer healthTotal;
    private Integer healthCurrent;
    private Integer manaTotal;
    private Integer manaCurrent;
    private Integer attack;
    private Integer magicAttack;
    private Map<String, Integer> skills;
    private Map<String, Integer> inventory;
    public character(JSONObject characterData){
        super(characterData);
        try {
            cid = characterData.getString("_id");
            name = characterData.getString("name");
            className = characterData.getString("class");
            strength = characterData.getInt("strength");
            dexterity = characterData.getInt("dexterity");
            intelligence = characterData.getInt("intelligence");
            healthCurrent = healthTotal = characterData.getInt("health");
            manaCurrent = manaTotal = characterData.getInt("mana");
            attack = characterData.getInt("attack");
            magicAttack = characterData.getInt("magicAttack");
            skills = new HashMap<String, Integer>();
            inventory = new HashMap<String, Integer>();
            JSONObject skillsTemp = characterData.getJSONObject("skills");
            JSONObject inventoryTemp = characterData.getJSONObject("intventory");

            Iterator<String> skillItor = skillsTemp.keys();
            Iterator<String> invItor = inventoryTemp.keys();
            while(skillItor.hasNext()){
                String skillName = skillItor.next();
                skills.put(skillName, skillsTemp.getInt(skillName));
            }
            while(invItor.hasNext()){
                String invName = invItor.next();
                inventory.put(invName, inventoryTemp.getInt(invName));
            }
        }catch (JSONException e){

        }
    }

    public JSONObject toJSONObject(){
        JSONObject data = new JSONObject();
        try {
            data.put("name", name);
            data.put("class", className);
            data.put("strength", strength);
            data.put("intelligence", intelligence);
            data.put("dexterity", dexterity);
            data.put("health", healthTotal);
            data.put("mana", manaTotal);
            data.put("attack", attack);
            data.put("magicAttack", magicAttack);
            JSONObject skillsTemp = new JSONObject();
            Iterator<Map.Entry<String, Integer>> skillItor = skills.entrySet().iterator();
            while(skillItor.hasNext()){
                Map.Entry<String, Integer> entry = skillItor.next();
                skillsTemp.put(entry.getKey(), entry.getValue());
            }
            data.put("skills", skillsTemp);
            JSONObject invTemp = new JSONObject();
            Iterator<Map.Entry<String, Integer>> invItor = inventory.entrySet().iterator();
            while(skillItor.hasNext()){
                Map.Entry<String, Integer> entry = invItor.next();
                skillsTemp.put(entry.getKey(), entry.getValue());
            }
            data.put("inventory", invTemp);

        } catch (JSONException e){

        }
        return data;
    };

    public void setCid(String cid){
        this.cid = cid;
    }
    public String getCid(){
        return this.cid;
    }
    public void setId(String id){
        this.id = id;
    }
}
