package com.tiany.nigel.arduinoled.paid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.tiany.nigel.arduinoled.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Color_Activity extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String LOG = Color_Activity.class.getName();
    Spinner groups_spinner;
    ColorPicker color_Picker;
    OpacityBar color_picker_opacity;
    SeekBar delay,redSeek,greenSeek,blueSeek;
    Button save_color,takered,takegreen,takeblue,addred,addgreen,addblue;
    String colorValue;
    int delayValue;
    int fadeValue;
    int r, g, b;
    BluetoothV2 bluetooth = new BluetoothV2();
    String spinner_item;
    TextView red,green,blue,getSOLID,delayText;
    LinearLayout seekBars;
    ToggleButton speakButton;
    private static final int REQUEST_CODE = 0;
    FrameLayout redFrameLayout,greenFrameLayout,blueFrameLayout;
    DecimalFormat format = new DecimalFormat("#.#"); //for seekbar seconds
    Preferences preferences = new Preferences();
    int increaseby = preferences.getIncreaseby();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View color_root = inflater.inflate(R.layout.activity_color,container,false);
        color_Picker = (ColorPicker) color_root.findViewById(R.id.color_picker);
        color_picker_opacity = (OpacityBar) color_root.findViewById(R.id.color_picker_opacity);
        color_Picker.setShowOldCenterColor(false);
        color_Picker.addOpacityBar(color_picker_opacity);
        delay = (SeekBar) color_root.findViewById(R.id.delay);
        red = (TextView) color_root.findViewById(R.id.red);
        green = (TextView) color_root.findViewById(R.id.green);
        blue = (TextView) color_root.findViewById(R.id.blue);
        delayText = (TextView) color_root.findViewById(R.id.delayText);
        getSOLID = (TextView) color_root.findViewById(R.id.getSOLID);
        getSOLID.setVisibility(View.INVISIBLE);
        seekBars = (LinearLayout) color_root.findViewById(R.id.seekBars);
        redFrameLayout = (FrameLayout) color_root.findViewById(R.id.redFrameLayout);
        greenFrameLayout = (FrameLayout) color_root.findViewById(R.id.greenFrameLayout);
        blueFrameLayout = (FrameLayout) color_root.findViewById(R.id.blueFrameLayout);
        takered = (Button) color_root.findViewById(R.id.takered);
        takegreen = (Button) color_root.findViewById(R.id.takegreen);
        takeblue = (Button) color_root.findViewById(R.id.takeblue);
        addred = (Button) color_root.findViewById(R.id.addred);
        addgreen = (Button) color_root.findViewById(R.id.addgreen);
        addblue = (Button) color_root.findViewById(R.id.addblue);
        groups_spinner = (Spinner) color_root.findViewById(R.id.groups_spinner);
        redSeek = (SeekBar) color_root.findViewById(R.id.redSeekBar);
        greenSeek = (SeekBar) color_root.findViewById(R.id.greenSeekBar);
        blueSeek = (SeekBar) color_root.findViewById(R.id.blueSeekBar);
        redSeek.setOnSeekBarChangeListener(this);
        greenSeek.setOnSeekBarChangeListener(this);
        blueSeek.setOnSeekBarChangeListener(this);
        speakButton = (ToggleButton) color_root.findViewById(R.id.speakButton);
        speakButton.setTextOn(null);
        speakButton.setTextOff(null);

        voiceInputInit();

        String mytag = getTag();
        ((MainActivity)getActivity()).setTabFragManage(mytag);

        setHasOptionsMenu(true);
        //config.saved_names =  new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item);
        //config.saved_names.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //hey = getResources().getStringArray(R.array.savedGroups);
        //ArrayAdapter<String> list =hey;
        //groups_spinner.setAdapter( list);

        save_color = (Button) color_root.findViewById(R.id.save_color);
        loadSpinnerData();
        saveAction();
        sendLIVEcolors();
        RGB_ChangeListener();
        Opacity();
        SeekBarButtons();
        DelayChangeListener();

        return color_root;
    }

    public void SeekBarButtons(){
            /*
            takered.setVisibility(View.VISIBLE);
            addred.setVisibility(View.VISIBLE);
            timer = new CountDownTimer(8500, 50) { //less ticks = smoother exit on button on tick

                public void onTick(long millisUntilFinished) {
                    float alpha = (float) (millisUntilFinished * 1.0);
                    takered.setAlpha(alpha / 10000);
                    addred.setAlpha(alpha / 10000);
                }

                public void onFinish() {
                    takered.setVisibility(View.GONE);
                    addred.setVisibility(View.GONE);
                }

            };*/
            //timer.start();
            takered.setOnClickListener(   // as long as your clicking, button will always be there because timer will always start
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redSeek.setProgress(redSeek.getProgress() - increaseby);
                        if (preferences.getSave_automatically() == 1) {
                            save_color.callOnClick();
                        }
                    }
                }
            );
            addred.setOnClickListener(   // as long as your clicking, button will always be there
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            redSeek.setProgress(redSeek.getProgress() + increaseby);
                            if (preferences.getSave_automatically() == 1) {
                                save_color.callOnClick();
                            }
                        }
                    }
            );

            takegreen.setOnClickListener(   // as long as your clicking, button will always be there
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            greenSeek.setProgress(greenSeek.getProgress() - increaseby);
                            if (preferences.getSave_automatically() == 1) {
                                save_color.callOnClick();
                            }
                        }
                    }
            );
            addgreen.setOnClickListener(   // as long as your clicking, button will always be there
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            greenSeek.setProgress(greenSeek.getProgress() + increaseby);
                            if (preferences.getSave_automatically() == 1) {
                                save_color.callOnClick();
                            }
                        }
                    }
            );

            takeblue.setOnClickListener(   // as long as your clicking, button will always be there
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blueSeek.setProgress(blueSeek.getProgress() - increaseby);
                            if (preferences.getSave_automatically() == 1) {
                                save_color.callOnClick();
                            }
                        }
                    }
            );

            addblue.setOnClickListener(   // as long as your clicking, button will always be there
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            blueSeek.setProgress(blueSeek.getProgress() + increaseby);
                            if (preferences.getSave_automatically() == 1) {
                                save_color.callOnClick();
                            }
                        }
                    }
            );
    }

    public void moreoptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Arduino LED. SeekBar Mode");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialoglayout = inflater.inflate(R.layout.dialog, null);

        final CheckBox autosave = (CheckBox) dialoglayout.findViewById(R.id.autosave);

        if(preferences.getSave_automatically() == 1){
            autosave.setChecked(true);
        }else {
            autosave.setChecked(false);
        }

        final TextView increment = (TextView) dialoglayout.findViewById(R.id.incrementNumber);
        final ToggleButton autohideButtons = (ToggleButton) dialoglayout.findViewById(R.id.autohidebuttons);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                increaseby = Integer.parseInt(increment.getText().toString());
                preferences.setIncreaseby(increaseby);
                DB_Handler db_handler = new DB_Handler(getContext());
                db_handler.updatePreferences(1,preferences.getIncreaseby(),
                        preferences.getSave_automatically(),
                        preferences.getFadingbuttons());
                //if user opts for stuck add and minus buttons dont start timer else
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        autohideButtons.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (autohideButtons.isChecked()) {
                            preferences.setFadingbuttons(false);
                            addred.setVisibility(View.VISIBLE);
                            takered.setVisibility(View.VISIBLE);
                            addgreen.setVisibility(View.VISIBLE);
                            takegreen.setVisibility(View.VISIBLE);
                            addblue.setVisibility(View.VISIBLE);
                            takeblue.setVisibility(View.VISIBLE);
                        } else {
                            preferences.setFadingbuttons(true);
                            addred.setVisibility(View.INVISIBLE);
                            takered.setVisibility(View.INVISIBLE);
                            addgreen.setVisibility(View.INVISIBLE);
                            takegreen.setVisibility(View.INVISIBLE);
                            addblue.setVisibility(View.INVISIBLE);
                            takeblue.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        );

        autosave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (autosave.isChecked()) {
                            preferences.setSave_automatically(true);
                        } else {
                            preferences.setSave_automatically(false);
                        }
                    }
                }
        );

        builder.setView(dialoglayout);
        builder.show();
    }

    public void loadSpinnerData() {
        // database handler
        DB_Handler db = new DB_Handler(getContext());

        // Spinner Drop down elements
        List<String> lables = db.getAllDATA();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dataAdapter.notifyDataSetChanged();
        // attaching data adapter to spinner
        groups_spinner.setAdapter(dataAdapter);

        dataAdapter.notifyDataSetChanged();
    }

    public void save_Color(){
        DB_Handler db = new DB_Handler(getContext());
        COLOR_DB_Contractor color = new COLOR_DB_Contractor();
        //String colors = color.setColor(String.valueOf(color_Picker.getColor()));
        //get spinner item
        spinner_item = getCurrentSpinnerItem();
        //get delay_value
        delayValue = getCurrentDelayValue();
        //get fade value
        fadeValue = getCurrentFadeValue();
        //get color value
        if (color_Picker.getVisibility() == View.VISIBLE){ //which mode is the user viewing
            colorValue = getCurrentColorValue();
        } else {
            colorValue = String.valueOf(Color.rgb(
                    redSeek.getProgress(),
                    greenSeek.getProgress(),
                    blueSeek.getProgress()));
        }
        //saving all that
        db.saveColorSQL
                (
                        new COLOR_DB_Contractor(String.valueOf(colorValue)),
                        new COLOR_DB_Contractor(red.getText().toString()),
                        new COLOR_DB_Contractor(green.getText().toString()),
                        new COLOR_DB_Contractor(blue.getText().toString()),
                        new COLOR_DB_Contractor(spinner_item),
                        new COLOR_DB_Contractor(fadeValue),
                        new COLOR_DB_Contractor(delayValue)
                );
        BluetoothV2 bluetooth = new BluetoothV2();
        bluetooth.Message(r + "," + g + "," + b, 0);
    }

    public void refresh(){
        String TabOfFragManage = ((MainActivity) getActivity()).getTabFragManage();
        play_Activity fm = (play_Activity) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragManage);
        fm.loadListData(getCurrentSpinnerItem());
    }

    public void saveAction(){
        save_color.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groups_spinner.getSelectedItem() != null) {
                            save_Color();
                        } else {
                            Toast.makeText(getContext(), "Create an LED group to save the color to", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void DelayChangeListener(){
        delay.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Double seconds = (progress * 1.0) / 1000;
                        delayText.setText(String.valueOf(format.format(seconds)) + "s");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }

    public void Opacity(){
        color_picker_opacity.setOnOpacityChangedListener(
                new OpacityBar.OnOpacityChangedListener() {
                    @Override
                    public void onOpacityChanged(int opacity) {
                        int difference = 255 - opacity;
                        int negativeInt = opacity - opacity - opacity;
                        if (r != 0) {
                            red.setText(String.valueOf(r - difference));
                        }
                        if (r < 0) {
                            red.setText("OFF");
                        }

                        if (g != 0) {
                            green.setText(String.valueOf(g - difference));
                        }
                        if (g < 0) {
                            green.setText("OFF");
                        }

                        if (b != 0) {
                            blue.setText(String.valueOf(b - difference));
                        }
                        if (b < 0) {
                            blue.setText("OFF");
                        }
                        BluetoothV2 bluetooth = new BluetoothV2();
                        int MR, MG, MB;
                        MR = Integer.parseInt((String) red.getText());
                        MB = Integer.parseInt((String) blue.getText());
                        MG = Integer.parseInt((String) green.getText());
                        bluetooth.Message(MR + "," + MG + "," + MB + "\n", 0);
                    }
                }
        );
    }

    //get current spinner item
    public String getCurrentSpinnerItem(){
        spinner_item = groups_spinner.getItemAtPosition(groups_spinner.getSelectedItemPosition()).toString();
        return spinner_item;
    }

    public int getCurrentFadeValue(){
        fadeValue = color_picker_opacity.getOpacity();
        return fadeValue;
    }

    public int getCurrentDelayValue(){
        delayValue = delay.getProgress();
        return delayValue;
    }

    public String getCurrentColorValue(){
        colorValue = String.valueOf(color_Picker.getColor());
        return colorValue;
    }

    public void sendLIVEcolors(){
        color_Picker.setOnColorChangedListener(
                new ColorPicker.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        //play_activity.stop(); // stop any animations
                        BluetoothV2 bluetooth = new BluetoothV2();
                        r = Color.red(color);
                        g = Color.green(color);
                        b = Color.blue(color);
                        bluetooth.Message(r + "," + g + "," + b + "\n", 0);
                        red.setText(Integer.toString(r));
                        green.setText(Integer.toString(g));
                        blue.setText(Integer.toString(b));
                        getSOLID.setText(Integer.toString(Color.alpha(color)));
                    }
                }
        );
    }

    public void switchAnimation(){
        Thread switchanimation = new Thread(new Runnable() {
            BluetoothV2 bluetooth = new BluetoothV2();
            public void run() {
                int R=0;
                int G=0;
                int B=0;
                while(R<255){
                    bluetooth.Message(R+",0,0"+ "\n",20);
                    R++;
                }
                while(G<255){
                    bluetooth.Message("0,"+G+",0"+ "\n",20);
                    G++;
                }
                while(B<255) {
                    bluetooth.Message("0,0,"+B+ "\n", 20);
                    B++;
                }
            }
        });
        switchanimation.start();
    }

    public void RGB_ChangeListener(){
        groups_spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String spinner_item = String.valueOf(groups_spinner.getItemAtPosition(position));
                        BluetoothV2 bluetooth = new BluetoothV2();
                        DB_Handler db = new DB_Handler(getContext());
                        List<String> pins = db.getRGBpins(spinner_item);
                        int R = Integer.parseInt(pins.get(0));
                        int G = Integer.parseInt(pins.get(1));
                        int B = Integer.parseInt(pins.get(2));
                        bluetooth.Message(R + "," + G + "," + B + "?", 0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        // Find the menuItem to add your SubMenu
        //MenuItem subMenu = menu.findItem(R.id.SeekBars);

        // Inflating the sub_menu menu this way, will add its menu items
        // to the empty SubMenu you created in the xml
        //inflater.inflate(R.menu.sub_menu, subMenu.getSubMenu());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        int menuId = item.getItemId();
        if(menuId == R.id.ColorWheel)
        {
            seekBars.setVisibility(View.INVISIBLE);
            color_Picker.setVisibility(View.VISIBLE);
            color_picker_opacity.setVisibility(View.VISIBLE);
            speakButton.setVisibility(View.VISIBLE);
        }
        else if(menuId == R.id.SeekBars){
            color_Picker.setVisibility(View.INVISIBLE);
            color_picker_opacity.setVisibility(View.INVISIBLE);
            speakButton.setVisibility(View.INVISIBLE);
            seekBars.setVisibility(View.VISIBLE);
            if (red.getText() != "") { // only need to check for one
                redSeek.setProgress(Integer.parseInt(String.valueOf(red.getText())));
                greenSeek.setProgress(Integer.parseInt(String.valueOf(green.getText())));
                blueSeek.setProgress(Integer.parseInt(String.valueOf(blue.getText())));
            }
            moreoptions();
        }
        else if(menuId ==R.id.rate){
            launchMarket();
        }

        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        String mytag = getTag();
        ((MainActivity)getActivity()).setTabFragManage(mytag);
    }

    @Override
    public void onPause(){
        super.onPause();
        String mytag = getTag();
        ((MainActivity)getActivity()).setTabFragManage(mytag);
    }

    @Override
    public void onStop(){
        super.onStop();
        String mytag = getTag();
        ((MainActivity)getActivity()).setTabFragManage(mytag);
    }

    @Override
    public void onResume(){
        super.onResume();
        String mytag = getTag();
        ((MainActivity) getActivity()).setTabFragManage(mytag);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            String mytag = getTag();
            ((MainActivity)getActivity()).setTabFragManage(mytag);
        } else {

        }
    }

    public void showMenuOptions() {

        CharSequence [] options = {"Color Wheel", "SeekBars", "Rate App","Preferences"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Arduino LED");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    seekBars.setVisibility(View.INVISIBLE);
                    color_Picker.setVisibility(View.VISIBLE);
                    color_picker_opacity.setVisibility(View.VISIBLE);
                    speakButton.setVisibility(View.VISIBLE);
                } else if (which == 1) {
                    color_Picker.setVisibility(View.INVISIBLE);
                    color_picker_opacity.setVisibility(View.INVISIBLE);
                    speakButton.setVisibility(View.INVISIBLE);
                    seekBars.setVisibility(View.VISIBLE);
                    if (red.getText() != "") { // only need to check for one
                        redSeek.setProgress(Integer.parseInt(String.valueOf(red.getText())));
                        greenSeek.setProgress(Integer.parseInt(String.valueOf(green.getText())));
                        blueSeek.setProgress(Integer.parseInt(String.valueOf(blue.getText())));
                    }
                } else if (which == 2) {
                    launchMarket();
                } else if (which == 3) {
                    moreoptions();
                }
            }
        });

        AlertDialog dlg = builder.create();
        dlg.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        BluetoothV2 bluetooth = new BluetoothV2();
        String r = String.valueOf(redSeek.getProgress());
        String g = String.valueOf(greenSeek.getProgress());
        String b = String.valueOf(blueSeek.getProgress());
        red.setText(r);
        green.setText(g);
        blue.setText(b);
        bluetooth.Message(r + "," + g + "," + b + "\n", 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "This app doesn't exist on the Play Store", Toast.LENGTH_LONG).show();
        }
    }


    public void voiceInputInit(){
        speakButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (speakButton.isChecked()){
                            startVoiceRecognitionActivity();
                        }
                    }
                }
        );
        // Disable button if no recognition service is present
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
        }
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Which color do you want?");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        DB_Handler db = new DB_Handler(getContext());
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            List<String> all_colors = db.getAllColorsByName();
            List RGB = new ArrayList<>();
            for (String item : all_colors) {
                item.trim();
            }
            List<String> matchedColors = new ArrayList<>();
            // Variable names edited for readability
            for (String item : matches) {
                if (all_colors.contains(item.trim())) {
                    matchedColors.add(item);
                }
            }
            for (String item : matches) {
                if (item.equals("all colors")) {
                    addallcolors(getContext());
                }
            }
            if(matchedColors.size()>0){
                Log.e("ITS A MATCH", matchedColors.get(0));
                RGB = db.getColorRGBByName(matchedColors.get(0));
                String colorValue = String.valueOf(Color.rgb(
                        Integer.valueOf(String.valueOf(RGB.get(0))),
                        Integer.valueOf(String.valueOf(RGB.get(1))),
                        Integer.valueOf(String.valueOf(RGB.get(2)))));

                String spinner_item = String.valueOf(groups_spinner.getSelectedItem());

                db.saveColorSQL
                        (
                                new COLOR_DB_Contractor(String.valueOf(colorValue)),
                                new COLOR_DB_Contractor(String.valueOf(RGB.get(0))),
                                new COLOR_DB_Contractor(String.valueOf(RGB.get(1))),
                                new COLOR_DB_Contractor(String.valueOf(RGB.get(2))),
                                new COLOR_DB_Contractor(spinner_item),
                                new COLOR_DB_Contractor(0),
                                new COLOR_DB_Contractor(500)
                        );

                int r = Color.red(Integer.parseInt(colorValue));
                int g = Color.green(Integer.parseInt(colorValue));
                int b = Color.blue(Integer.parseInt(colorValue));
                bluetooth.Message(r + "," + g + "," + b + "\n", 0);
                Log.e("MESSAGE", r + "," + g + "," + b + "\n");
                Log.e("MESSAGE", String.valueOf(RGB.get(0)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        speakButton.setChecked(false);
    }

    private ProgressDialog pd;
    private void addallcolors(final Context context) {

        pd = new ProgressDialog(context);
        pd.setTitle("Arduino LED");
        pd.setMessage("Adding all colors");
        pd.setProgressStyle(pd.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setMax(865);
        pd.show();

        //start a new thread to process job
        new Thread(new Runnable() {
            @Override
            public void run() {
                //heavy job here
                DB_Handler db = new DB_Handler(context);
                List<Integer> all_color_ints = db.getAllColorsInts();
                String spinner_item = String.valueOf(groups_spinner.getSelectedItem());
                for (Integer color : all_color_ints) {
                    db.saveColorSQL
                        (
                            new COLOR_DB_Contractor(String.valueOf(color)),
                            new COLOR_DB_Contractor(Color.red(color)),
                            new COLOR_DB_Contractor(Color.green(color)),
                            new COLOR_DB_Contractor(Color.blue(color)),
                            new COLOR_DB_Contractor(spinner_item),
                            new COLOR_DB_Contractor(0),
                            new COLOR_DB_Contractor(delay.getProgress())
                        );
                    pd.incrementProgressBy(1);
                }
                //send message to main thread
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.setMessage("Done");
            pd.dismiss();
        }
    };

}


