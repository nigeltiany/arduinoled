package com.tiany.nigel.arduinoled.paid;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Nigel on 12/18/2015.
 */
public class Color_Master extends Activity{
/**
    DB_Handler mDb = new DB_Handler(getBaseContext());

    public Color_Master loadBulkData(String content){

        Color_Master lastItem = null;

        try {


            Log.w(TAG, "Start");
            //Handling XML
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            ItemXMLHandler myXMLHandler = new ItemXMLHandler();
            xr.setContentHandler(myXMLHandler);
            InputSource inStream = new InputSource();
            Log.w(TAG, "Parse1");
            inStream.setCharacterStream(new StringReader(content));
            Log.w(TAG, "Parse2");
            xr.parse(inStream);
            Log.w(TAG, "Parse3");

            mDb.beginTransaction();
            String sql = "Insert or Replace into Items (itemNumber, description,unitOfMeasure, weight) values(?,?,?,?)";
            SQLiteStatement insert = mDb.compileStatement(sql);
            ArrayList<Color_Master> itemsList = myXMLHandler.getItemsList();
            for(int i=0;i<itemsList.size();i++){
                Color_Master item = itemsList.get(i);
                if(i == (numberOfRows-1)){
                    lastItem = item;
                }
                insert.bindString(1, item.getItemNumber());
                insert.bindString(2, item.getItemDescription1());
                insert.bindString(3, item.getSellingUOM());
                insert.bindDouble(4, item.getWeight());
                insert.execute();
            }
            mDb.setTransactionSuccessful();
            Log.w(TAG, "Done");
        }
        catch (Exception e) {
            Log.w("XML:",e );
        }
        finally {
            mDb.endTransaction();
        }

        return lastItem;

    }
*/
}
