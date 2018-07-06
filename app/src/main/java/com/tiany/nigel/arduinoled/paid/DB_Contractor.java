package com.tiany.nigel.arduinoled.paid;

/**
 * Created by Nigel on 11/3/2015.
 */
public class DB_Contractor {
    //private variables
    int _id,_delay,_fade;
    String _groupname;
    int _r,_g,_b;

    // Empty constructor
    public DB_Contractor(){

    }
    // constructor
    public DB_Contractor(int id,String name,int r,int g,int b){
        this._id = id;
        this._groupname = name;
        this._r = r;
        this._g = g;
        this._b = b;
    }

    public DB_Contractor(int i) {
        this._r = i;
        this._g = i;
        this._b = i;
    }

    public DB_Contractor(String i){
        this._groupname = i;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

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

    // getting name
    public String getName(){
        return this._groupname;
    }

    // setting name
    public void setName(String name){
        this._groupname = name;
    }
}
