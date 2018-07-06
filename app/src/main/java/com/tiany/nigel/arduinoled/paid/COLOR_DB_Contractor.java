package com.tiany.nigel.arduinoled.paid;

/**
 * Created by Nigel on 11/3/2015.
 */
public class COLOR_DB_Contractor {
    int id;
    String color;
    String group_name;
    int fade;
    int delay;
    int _r,_g,_b;

    // constructors
    public COLOR_DB_Contractor() {

    }

    public COLOR_DB_Contractor(String color, String group_name) {
        this.color = color;
        this.group_name = group_name;
    }

    public COLOR_DB_Contractor(int id, String color,int r,int g,int b,String group_name,int fade,int delay) {
        this.id = id;
        this.color = color;
        this._r = r;
        this._g = g;
        this._b = b;
        this.group_name = group_name;
        this.fade = fade;
        this.delay = delay;
    }

    public COLOR_DB_Contractor(int myInts) {
        this._r = myInts;
        this._g = myInts;
        this._b = myInts;
        this.fade = myInts;
        this.delay = myInts;
    }

    public COLOR_DB_Contractor(String s) {
        this.color = s;
        this.group_name = s;
    }

    // setters
    public void setId(int id) {this.id = id;}

    public void setColor(String color) {this.color = color;}

    public void setGroup_name(String group_name){this.group_name = group_name;}

    public void setFade(int fade) {this.fade = fade;}

    public void setDelay(int delay) {this.delay = delay;}

    // getters
    public int getId() { return this.id;}

    public String getColor() {
        return this.color;
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public int getFade() {
        return this.fade;
    }

    public int getDelay() {
        return this.delay;
    }


    //update
    public int get_r(){
        return this._r;
    }

    public int get_g(){
        return this._g;
    }

    public int get_b(){ return this._b;}

    public void set_r(int r){
        this._r = r;
    }

    public void set_g(int g){
        this._g = g;
    }

    public void set_b(int b){
        this._b = b;
    }
}
