package com.tiany.nigel.arduinoled.paid;

/**
 * Created by Nigel on 12/18/2015.
 */
public class Color_Slave {
    String HEX;
    String ID;
    String RED_VALUE;
    String GREEN_VALUE;
    String BLUE_VALUE;
    String COLOR_NAME;

    public String getCOLOR_NAME(){
        return COLOR_NAME;
    }

    public void setCOLOR_NAME(String name){
        this.COLOR_NAME = name;
    }

    public String getHEX(){
        return HEX;
    }

    public void setHEX(String hex){
        this.HEX = hex;
    }

    public String getID(){
        return ID;
    }

    public void setID(String id){
        this.ID = id;
    }

    public String getRED_VALUE(){
        return RED_VALUE;
    }

    public void setRED_VALUE(String red){
        this.RED_VALUE = red;
    }

    public String getGREEN_VALUE(){
        return GREEN_VALUE;
    }

    public void setGREEN_VALUE(String red){
        this.GREEN_VALUE = red;
    }

    public String getBLUE_VALUE(){
        return BLUE_VALUE;
    }

    public void setBLUE_VALUE_VALUE(String red){
        this.BLUE_VALUE = red;
    }

}