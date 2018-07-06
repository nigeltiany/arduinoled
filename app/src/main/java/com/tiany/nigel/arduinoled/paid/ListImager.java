package com.tiany.nigel.arduinoled.paid;

/**
 * Created by Nigel on 11/12/2015.
 */
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiany.nigel.arduinoled.R;
import com.tiany.nigel.arduinoled.paid.DB_Handler;

import java.util.List;

public class ListImager extends ArrayAdapter<String>{

    private final Context context;
    private final List<String> values;
    private DB_Handler database;

    public ListImager(Context context, List<String> values) {
        super(context, R.layout.program_list, values);
        this.context = context;
        this.values = values;
        database = new DB_Handler(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.program_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.color);
        TextView redValue = (TextView) rowView.findViewById(R.id.red_value);
        TextView greenValue = (TextView) rowView.findViewById(R.id.green_value);
        TextView blueValue = (TextView) rowView.findViewById(R.id.blue_value);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.led_image);
        imageView.setImageResource(R.drawable.led_diode);
        //convertView.setBackgroundColor(Color.YELLOW);
        int colors = Integer.parseInt(getItem(position));
        //get RGB values and search database for matching name
        int R = Color.red(colors);
        int G =  Color.green(colors);
        int B = Color.blue(colors);
        List<String> name = database.getColorNameByRGB(R, G, B);
        if(name.size()>0){
            textView.setText(String.valueOf(name).replaceAll("[\\[\\](){}]",""));
        }else {
            textView.setText(values.get(position));
        }
        redValue.setText(String.valueOf(R));
        greenValue.setText(String.valueOf(G));
        blueValue.setText(String.valueOf(B));
        imageView.setBackgroundColor(colors);
        return rowView;
    }
}