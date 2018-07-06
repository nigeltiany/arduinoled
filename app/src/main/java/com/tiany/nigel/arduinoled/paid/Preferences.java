package com.tiany.nigel.arduinoled.paid;

import android.app.Activity;

import com.tiany.nigel.arduinoled.paid.DB_Handler;

/**
 * Created by Nigel on 12/15/2015.
 */
public class Preferences extends Activity{
    DB_Handler database = new DB_Handler(getBaseContext());
    int increaseby;
    boolean fadingbuttons;
    boolean save_automatically;
    boolean dontshowdialog;

    public void setIncreaseby(int increaseValue){
        this.increaseby = increaseValue;
    }

    public void setFadingbuttons(boolean fadingbuttons){
        this.fadingbuttons = fadingbuttons;
    }

    public void setSave_automatically(boolean save_automatically){
        this.save_automatically = save_automatically;
    }

    public void setDontshowdialog(boolean dontshowdialog){
        this.dontshowdialog = dontshowdialog;
    }

    public int getIncreaseby(){
        return this.increaseby;
    }

    public int getFadingbuttons(){
        return toStringYesNo(this.fadingbuttons);
    }

    public int getSave_automatically(){
        return toStringYesNo(this.save_automatically);
    }

    public int getdontshowdialog(){
        return toStringYesNo(this.dontshowdialog);
    }

    public static int toStringYesNo(boolean bool) {
        return toString(bool, 1, 0);
    }

    /**
     * Converts a boolean to a String returning one of the input Strings.
     *
     * <pre>
     *   BooleanUtils.toString(true, "true", "false")   = "true"
     *   BooleanUtils.toString(false, "true", "false")  = "false"
     * </pre>
     *
     * @param bool  the Boolean to check
     * @param trueString  the String to return if <code>true</code>,
     *  may be <code>null</code>
     * @param falseString  the String to return if <code>false</code>,
     *  may be <code>null</code>
     * @return one of the two input Strings
     *
     *
     * Select COLUMN_NAME  values from db.
     * This will be integer value, you can convert this int value back to Boolean as follows
     * Boolean flag2 = (intValue == 1)? true : false;
     */
    public static int toString(boolean bool, int trueString, int falseString) {
        return bool ? trueString : falseString;
    }

    public boolean getBoolFadingbuttons() {
        return this.fadingbuttons;
    }

    public boolean getBoolSave_automatically() {
        return this.save_automatically;
    }

    public boolean getBooldontshowdialog() {
        return this.dontshowdialog;
    }
}
