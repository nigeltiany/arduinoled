package com.tiany.nigel.arduinoled.paid;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.tiany.nigel.arduinoled.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nigel on 11/3/2015.
 */
public class DB_Handler extends SQLiteOpenHelper {
    // All Static variables
    // Logcat tag
    private static final String LOG = DB_Handler.class.getName();
    // Database Version
    private static final int DATABASE_VERSION = 24;

    // Database Name
    private static final String DATABASE_NAME = "user_app_data";

    // Data table name
    private static final String DATA = "data";
    //color table name
    private static final String COLORS = "colors";
    //merged table
    private static final String PREFERENCES = "PREFERENCES";

    private static final String ALL_COLORS = "all_colors";

    private static final String FREQUENCY_COLORS = "frequency_colors";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";      /*<-------  Relates to  ------->*/    private static final String KEY_COLOR_ID = "color_id";
    private static final String KEY_ID_MERGE = "merge_id";

    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_FADE = "fade";
    private static final String KEY_DELAY = "delay";
    private static final String KEY_r_PIN = "R_PIN";
    private static final String KEY_g_PIN = "G_PIN";
    private static final String KEY_b_PIN = "B_PIN";
    private static final String KEY_r_VALUE = "R_VALUE";
    private static final String KEY_g_VALUE = "G_VALUE";
    private static final String KEY_b_VALUE = "B_VALUE";
    private static final String KEY_PREFERENCE_ID = "ID";
    private static final String KEY_increaseby = "INCREASE_BY";
    private static final String KEY_fadingbuttons = "FADING_BUTTONS";
    private static final String KEY_autosave = "AUTOSAVE";
    private static final String KEY_dontshow = "DONTSHOW";
    private static final String KEY_ALL_ID = "ID";
    private static final String KEY_COLOR_NAME = "COLOR_NAME";
    private static final String KEY_HEX_VALUE = "HEX";
    private static final String KEY_RED_VALUE = "RED";
    private static final String KEY_GREEN_VALUE = "GREEN";
    private static final String KEY_BLUE_VALUE = "BLUE";

    private static final String KEY_Hz = "Hz";
    private static final String KEY_COLOR_Hz = "COLOR";
    private static final String KEY_R_Hz = "R_Hz";
    private static final String KEY_G_Hz = "G_Hz";
    private static final String KEY_B_Hz = "B_Hz";

    // Color Table Column names
    private static final String KEY_COLOR = "color";
    private final Context mycontext;

    public DB_Handler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mycontext = context;
    }

    // Color table create statement
    String CREATE_TABLE_COLORS = "CREATE TABLE " + COLORS
            + "("
            + KEY_COLOR_ID + " INTEGER PRIMARY KEY,"
            + KEY_COLOR + " TEXT,"
            + KEY_r_VALUE + " TEXT,"
            + KEY_g_VALUE + " TEXT,"
            + KEY_b_VALUE + " TEXT,"
            + KEY_GROUP_NAME + " TEXT,"
            + KEY_FADE  + " INT,"
            + KEY_DELAY + " INT"
            +  ")";

    String CREATE_TABLE = "CREATE TABLE " + DATA
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_GROUP_NAME + " TEXT,"
            + KEY_r_PIN + " INT,"
            + KEY_g_PIN + " INT,"
            + KEY_b_PIN + " INT"
            + ")";

    String CREATE_TABLE_PREFERENCES = "CREATE TABLE " + PREFERENCES
            + "("
            + KEY_PREFERENCE_ID + " INTEGER PRIMARY KEY,"
            + KEY_autosave + " INT,"
            + KEY_fadingbuttons + " INT,"
            + KEY_increaseby + " INT"
            + ")";

    String CREATE_TABLE_ALL_COLORS = "CREATE TABLE " + ALL_COLORS
            + "("
            + KEY_ALL_ID + " TEXT PRIMARY KEY,"
            + KEY_COLOR_NAME + " TEXT,"
            + KEY_HEX_VALUE + " TEXT,"
            + KEY_RED_VALUE + " TEXT,"
            + KEY_GREEN_VALUE + " TEXT,"
            + KEY_BLUE_VALUE + " TEXT"
            + ")";

    String CREATE_TABLE_FREQUENCY_COLORS = "CREATE TABLE " + FREQUENCY_COLORS
            + "("
            + KEY_Hz + " TEXT PRIMARY KEY,"
            + KEY_COLOR_Hz + " INT,"
            + KEY_R_Hz + " TEXT,"
            + KEY_G_Hz + " TEXT,"
            + KEY_B_Hz + " TEXT"
            + ")";

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_COLORS);
        db.execSQL(CREATE_TABLE_PREFERENCES);
        db.execSQL(CREATE_TABLE_ALL_COLORS);
        db.execSQL(CREATE_TABLE_FREQUENCY_COLORS);
        ADD_ALL_COLOR_REFS(db);
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DATA);
        db.execSQL("DROP TABLE IF EXISTS " + COLORS);
        db.execSQL("DROP TABLE IF EXISTS " + PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + ALL_COLORS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    void addGroup(DB_Contractor r,DB_Contractor g,DB_Contractor b,DB_Contractor group_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_r_PIN, r.get_r());
        values.put(KEY_g_PIN, g.get_g());
        values.put(KEY_b_PIN, b.get_b());
        values.put(KEY_GROUP_NAME, group_name.getName()); // group Name
        // Inserting Row
        db.insert(DATA, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<String> getAllDATA() {
        List<String> contactList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DB_Contractor contact = new DB_Contractor();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                //contact.set_color(cursor.getString(2));
                // Adding contact to list
                contactList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    /*
    * getting all color under a single group
    * */
    public boolean ifExists(String tag_name) {
        List<String> result = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + DATA + " WHERE "
                + KEY_GROUP_NAME + " = '" + tag_name + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DB_Contractor contact = new DB_Contractor();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                //contact.set_color(cursor.getString(2));
                // Adding contact to list
                result.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        boolean exits = false;
        if(result.size()!=0){//then it does exits
            exits = true;
        }
        // return contact list
        return exits;
    }


    public List<String> getSpinnerItemColors(String current_spinner_item) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> todos = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + COLORS
                + " WHERE "
                + KEY_GROUP_NAME + " = '" + current_spinner_item + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            COLOR_DB_Contractor td = new COLOR_DB_Contractor();
            td.setId((c.getInt((c.getColumnIndex(KEY_COLOR_ID)))));
            td.setColor((c.getString(c.getColumnIndex(KEY_COLOR))));
            ids.add(String.valueOf(c.getInt(c.getColumnIndex(KEY_COLOR_ID))));
            todos.add(c.getString(c.getColumnIndex(KEY_COLOR)));
        } while (c.moveToNext());

        return todos;
    }

    public List<String> getRGBpins(String current_spinner_item) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> RGBPINS = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();

        String selectQuery = "SELECT "+ KEY_r_PIN+","+KEY_g_PIN+","+KEY_b_PIN+" FROM " + DATA
                + " WHERE "
                + KEY_GROUP_NAME + " = '" + current_spinner_item + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            COLOR_DB_Contractor td = new COLOR_DB_Contractor();
            td.set_r((c.getInt(c.getColumnIndex(KEY_r_PIN))));
            td.set_g((c.getInt(c.getColumnIndex(KEY_g_PIN))));
            td.set_b((c.getInt(c.getColumnIndex(KEY_b_PIN))));
            RGBPINS.add(c.getString(c.getColumnIndex(KEY_r_PIN)));
            RGBPINS.add(c.getString(c.getColumnIndex(KEY_g_PIN)));
            RGBPINS.add(c.getString(c.getColumnIndex(KEY_b_PIN)));
        } while (c.moveToNext());

        return RGBPINS;
    }
    
    /*
     * Creating a color
     */
    public void saveColorSQL(COLOR_DB_Contractor color,COLOR_DB_Contractor r,COLOR_DB_Contractor g,COLOR_DB_Contractor b, COLOR_DB_Contractor group_name, COLOR_DB_Contractor fade, COLOR_DB_Contractor delay) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_COLOR, color.getColor());
        values.put(KEY_r_VALUE, r.get_r());
        values.put(KEY_g_VALUE, g.get_g());
        values.put(KEY_b_VALUE, b.get_b());
        values.put(KEY_GROUP_NAME, group_name.getGroup_name());
        values.put(KEY_FADE, fade.getFade());
        values.put(KEY_DELAY, delay.getDelay());

        // insert row
        db.insert(COLORS, null, values);
        db.close();
    }

    /*
     * Updating a color
     */
    public int updateColor(int color_id,COLOR_DB_Contractor color, COLOR_DB_Contractor group_name, COLOR_DB_Contractor fade, COLOR_DB_Contractor delay) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + COLORS +
            " SET " + KEY_COLOR + " = "+ color.getColor() +"," +
                      KEY_FADE + " = "+ fade.getFade() +"," +
                      KEY_DELAY + " = "+ delay.getDelay() +
            " WHERE "+KEY_COLOR_ID + " = " + color_id;

        Log.e(LOG, query);

        ContentValues values = new ContentValues();
        //values.put(KEY_COLOR_ID, color_id
        values.put(KEY_COLOR, color.getColor());
        values.put(KEY_GROUP_NAME, group_name.getGroup_name());
        values.put(KEY_FADE, fade.getFade());
        values.put(KEY_DELAY, delay.getDelay());

        db.execSQL(query);

        // updating row
        return db.update(COLORS, values, KEY_COLOR_ID + " = " + color_id,null);

    }

   /*
    * Deleting a color
    */
    public void deleteColorSQL(COLOR_DB_Contractor item) {
        SQLiteDatabase db = this.getWritableDatabase();
        // now delete the color
        db.delete(COLORS, KEY_COLOR + " = ?", new String[] { String.valueOf(item.getColor()) });
    }

    public void deleteAllColorsFor(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        // now delete the color
        db.delete(COLORS, KEY_GROUP_NAME + " = ?", new String[]{item});
    }

    /*
     * Deleting a groups
     */
    public void deleteGroup(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        // now delete the color
        db.delete(COLORS, KEY_GROUP_NAME + " = ?", new String[]{item});
        db.delete(DATA, KEY_GROUP_NAME + " = ?", new String[]{item});
    }

    public void deleteAllGroups() {
        SQLiteDatabase db = this.getWritableDatabase();
        // now delete the color
        String deleteAllColors = "DELETE FROM "+COLORS;
        String deleteAllData = "DELETE FROM "+DATA;
        db.execSQL(deleteAllColors);
        db.execSQL(deleteAllData);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public List<String> getItemIDS(String current_spinner_item) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<String> ids = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + COLORS
                + " WHERE "
                + KEY_GROUP_NAME + " = '" + current_spinner_item + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            COLOR_DB_Contractor td = new COLOR_DB_Contractor();
            td.setId((c.getInt((c.getColumnIndex(KEY_COLOR_ID)))));
            ids.add(String.valueOf(c.getInt(c.getColumnIndex(KEY_COLOR_ID))));
        } while (c.moveToNext());

        return ids;
    }

    public List<String> getItemDelays(String current_spinner_item) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<String> delays = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + COLORS
                + " WHERE "
                + KEY_GROUP_NAME + " = '" + current_spinner_item + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            COLOR_DB_Contractor td = new COLOR_DB_Contractor();
            td.setId((c.getInt((c.getColumnIndex(KEY_DELAY)))));
            delays.add(String.valueOf(c.getInt(c.getColumnIndex(KEY_DELAY))));
        } while (c.moveToNext());

        return delays;
    }

    public long defaultPreferences() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + PREFERENCES +
                " VALUES (1,0,1,5) ";

        Log.e(LOG, query);

        ContentValues values = new ContentValues();
        //values.put(KEY_COLOR_ID, color_id
        values.put(KEY_increaseby, 5);
        values.put(KEY_autosave, 0);
        values.put(KEY_fadingbuttons, 0);

        db.execSQL(query);

        // updating row
        return db.insert(PREFERENCES, null, values);
    }

    public int updatePreferences(int id, int increaseValue, int autosave, int fadingbuttons) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + PREFERENCES +
                " SET " +
                KEY_autosave + " = "+ autosave +"," +
                KEY_fadingbuttons + " = "+ fadingbuttons +"," +
                KEY_increaseby + " = "+ increaseValue +
                " WHERE "+KEY_PREFERENCE_ID + " = " + id;

        Log.e(LOG, query);

        ContentValues values = new ContentValues();
        //values.put(KEY_COLOR_ID, color_id
        values.put(KEY_increaseby, increaseValue);
        values.put(KEY_autosave, autosave);
        values.put(KEY_fadingbuttons, fadingbuttons);

        db.execSQL(query);

        // updating row
        return db.update(PREFERENCES, values, KEY_PREFERENCE_ID + " = " + id, null);

    }

    public void ADD_ALL_COLOR_REFS(SQLiteDatabase db){

        //SQLiteDatabase db = this.getWritableDatabase();
        //Add default records to animals
        ContentValues _Values = new ContentValues();
        //Get xml resource file
        Resources res = mycontext.getResources();

        //Open xml file
        XmlResourceParser _xml = res.getXml(R.xml.all_colors_xml);
        try
        {
            //Check for end of document
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Search for record tags
                if ((eventType == XmlPullParser.START_TAG) &&(_xml.getName().equals("color"))){
                    //Record tag found, now get values and insert record
                    String _id = _xml.getAttributeValue(null, Color_columns.id);
                    //String _Color_name = _xml.getAttributeValue(null, Color_columns.color_name);
                    String _hex = _xml.getAttributeValue(null, Color_columns.hex);
                    String _red = _xml.getAttributeValue(null, Color_columns.red);
                    String _green = _xml.getAttributeValue(null, Color_columns.green);
                    String _blue = _xml.getAttributeValue(null, Color_columns.blue);
                    String _Color_name = _xml.nextText();
                    _Values.put(Color_columns.id, _id);
                    _Values.put(Color_columns.hex, _hex);
                    _Values.put(Color_columns.red, _red);
                    _Values.put(Color_columns.green, _green);
                    _Values.put(Color_columns.blue, _blue);
                    _Values.put(Color_columns.color_name, _Color_name);
                    db.insert(Color_columns.TABLENAME, null, _Values);
                }
                eventType = _xml.next();
            }
        }
        //Catch errors
        catch (XmlPullParserException e)
        {
          //  Log.e(TAG, e.getMessage(), e);
        }
        catch (IOException e)
        {
           // Log.e(TAG, e.getMessage(), e);

        }
        finally
        {
            //Close the xml file
            _xml.close();
        }
    }

    public List<String> getColorNameByRGB(int R, int G, int B){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> color_name = new ArrayList<String>();

        String selectQuery = "SELECT color_name FROM " + ALL_COLORS
                + " WHERE "
                + KEY_RED_VALUE + " = '" + R + "'"
                + " AND "
                + KEY_GREEN_VALUE + " = '" + G + "'"
                + " AND "
                + KEY_BLUE_VALUE + " = '" + B + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            Color_Slave constructor = new Color_Slave();
            constructor.setCOLOR_NAME(String.valueOf((c.getInt((c.getColumnIndex(KEY_COLOR_NAME))))));
            color_name.add(String.valueOf(c.getString(c.getColumnIndex(KEY_COLOR_NAME))));
        } while (c.moveToNext());

        return color_name;
    }

    public List<String> getColorRGBByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> color_RGB = new ArrayList<String>();

        String selectQuery = "SELECT "+KEY_RED_VALUE+","+KEY_GREEN_VALUE+","+KEY_BLUE_VALUE+" FROM " + ALL_COLORS
                + " WHERE "
                + KEY_COLOR_NAME + " = '" + name + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            Color_Slave constructor = new Color_Slave();
            constructor.setRED_VALUE(String.valueOf((c.getInt((c.getColumnIndex(KEY_RED_VALUE))))));
            constructor.setGREEN_VALUE(String.valueOf((c.getInt((c.getColumnIndex(KEY_GREEN_VALUE))))));
            constructor.setBLUE_VALUE_VALUE(String.valueOf((c.getInt((c.getColumnIndex(KEY_BLUE_VALUE))))));
            color_RGB.add(String.valueOf(c.getString(c.getColumnIndex(KEY_RED_VALUE))));
            color_RGB.add(String.valueOf(c.getString(c.getColumnIndex(KEY_GREEN_VALUE))));
            color_RGB.add(String.valueOf(c.getString(c.getColumnIndex(KEY_BLUE_VALUE))));
        } while (c.moveToNext());

        return color_RGB;
    }


    public List<String> getAllColorsByName() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> todos = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + ALL_COLORS;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            todos.add(c.getString(c.getColumnIndex(KEY_COLOR_NAME)));
        } while (c.moveToNext());

        return todos;
    }

    public List<Integer> getAllColorsInts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> todos = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + ALL_COLORS;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) do {
            int R = Integer.parseInt(c.getString(c.getColumnIndex(KEY_RED_VALUE)));
            int G = Integer.parseInt(c.getString(c.getColumnIndex(KEY_GREEN_VALUE)));
            int B = Integer.parseInt(c.getString(c.getColumnIndex(KEY_BLUE_VALUE)));
            todos.add((Color.rgb(R,G,B)));
        } while (c.moveToNext());

        return todos;
    }

}
