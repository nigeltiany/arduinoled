package com.tiany.nigel.arduinoled.paid;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.tiany.nigel.arduinoled.R;

import java.text.DecimalFormat;
import java.util.List;

public class play_Activity extends Fragment {
    private static final String LOG = play_Activity.class.getName();
    Spinner groups_spinner2;
    ListView color_listview;
    ToggleButton OnOffToggle;
    ListImager dataAdapter;
    List<String> lables;
    int menuItemIndex;
    AdapterView.AdapterContextMenuInfo info;
    List<String> label_ids;
    List<String> delays;
    public static int selected_ID;
    public Thread switchanimation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View play_root = inflater.inflate(R.layout.activity_play,container,false);
        groups_spinner2 = (Spinner) play_root.findViewById(R.id.groups_spinner2);
        color_listview = (ListView) play_root.findViewById(R.id.colors_list_view);
        color_listview.setDivider(null);
        OnOffToggle = (ToggleButton) play_root.findViewById(R.id.OnOffToggle);
        registerForContextMenu(color_listview);
        loadSpinnerData();
        getCurrentSpinnerItem();
        switchON();
        return play_root;
    }




    public void switchAnimation(){
        switchanimation = new Thread(new Runnable() {
            BluetoothV2 bluetooth = new BluetoothV2();
            int switcher = 0;
            public void run() {
                if (groups_spinner2.getSelectedItem() != null) {
                    if (lables.size() <= 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getContext(), "No Colors to play on " + groups_spinner2.getSelectedItem(), Toast.LENGTH_LONG).show();
                                OnOffToggle.setChecked(false);
                            }
                        });
                    } else {
                        int limit = lables.size();
                        while (switcher <= limit && switchanimation != null && limit > 0) {
                            int R = Color.red(Integer.parseInt(lables.get(switcher)));
                            int G = Color.green(Integer.parseInt(lables.get(switcher)));
                            int B = Color.blue(Integer.parseInt(lables.get(switcher)));
                            int delay = Integer.parseInt(delays.get(switcher));
                            int alpha = 255 - Color.alpha(Integer.parseInt(lables.get(switcher)));
                            if (R != 0) {
                                R = R - alpha;
                                if (R < 0) {
                                    R = 0;
                                }
                            }
                            if (G != 0) {
                                G = G - alpha;
                                if (G < 0) {
                                    G = 0;
                                }
                            }
                            if (B != 0) {
                                B = B - alpha;
                                if (B < 0) {
                                    B = 0;
                                }
                            }

                            bluetooth.Message(R + "," + G + "," + B + "\n", delay);
                            switcher += 1;
                            if (switcher != limit) {
                                continue;
                            } else {
                                switcher = 0;
                                continue;
                            }
                        }
                    }
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), "No LED groups created", Toast.LENGTH_SHORT).show();
                            OnOffToggle.setChecked(false);
                        }
                    });
                }
            }
        });
        switchanimation.start();
    }

    public void stop(){
        switchanimation = null;
        OnOffToggle.setChecked(false);
    }

    public void switchON(){
        OnOffToggle.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (OnOffToggle.isChecked()) {
                            switchAnimation();
                        } else if (OnOffToggle.isChecked() == false) {
                            stop();
                        }
                    }
                }
        );
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

        // attaching data adapter to spinner
        groups_spinner2.setAdapter(dataAdapter);

        dataAdapter.notifyDataSetChanged();
    }


    public void loadListData(String item) {
        //get spinner item
        String selected_spinner;
        selected_spinner = item;
        // database handler
        DB_Handler db = new DB_Handler(getContext());
        //get ids
        label_ids = db.getItemIDS(selected_spinner);
        //delay values
        delays = db.getItemDelays(selected_spinner);
        // Spinner Drop down elements
        lables = db.getSpinnerItemColors(selected_spinner);

        //Creating adapter for spinner
        //dataAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lables);
        //imager
        //ListImager addImages = new ListImager(getContext(),imageId/*,color.getCurrentColorValue()*/);
        //Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //MergeAdapter finalAdapter = new MergeAdapter();
        //finalAdapter.addAdapter(dataAdapter);
        //finalAdapter.addAdapter(addImages);
        // attaching data adapter to spinner
        dataAdapter = new ListImager(getContext(),lables);
        color_listview.setAdapter(dataAdapter);
    }

    public void getCurrentSpinnerItem(){
        groups_spinner2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String spinner_item = String.valueOf(groups_spinner2.getItemAtPosition(position));
                        loadListData(spinner_item);
                        BluetoothV2 bluetooth = new BluetoothV2();
                        DB_Handler db = new DB_Handler(getContext());
                        List<String> pins = db.getRGBpins(spinner_item);
                        int r = Integer.parseInt(pins.get(0));
                        int g = Integer.parseInt(pins.get(1));
                        int b = Integer.parseInt(pins.get(2));
                        bluetooth.Message(r + "," + g + "," + b + "?", 0);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.colors_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            TextView color = (TextView) color_listview.getChildAt(info.position - color_listview.getFirstVisiblePosition()).findViewById(R.id.color);
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
            menu.setHeaderTitle(color.getText());
            stop(); // stop any loops
            BluetoothV2 bluetooth = new BluetoothV2();
            int R = Color.red(Integer.parseInt(lables.get(info.position)));
            int G = Color.green(Integer.parseInt(lables.get(info.position)));
            int B = Color.blue(Integer.parseInt(lables.get(info.position)));
            bluetooth.Message(R+","+G+","+B+"\n",0);
        }
    }

    public int get_ID(){
        int ID = selected_ID;
        Log.e(LOG, String.valueOf(ID + "ID"));
        return ID;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = lables.get(info.position);
        // database handler
        DB_Handler db = new DB_Handler(getContext());
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch (menuItemName){
            case "Edit":
                //show color picker pop up
                showColorPickDialog();
                break;
            case "Delete":
                //delete item
                db.deleteColorSQL(new COLOR_DB_Contractor(listItemName));
                loadListData(String.valueOf(groups_spinner2.getSelectedItem()));
                dataAdapter.notifyDataSetChanged();
                break;
            case "Delete All":
                db.deleteAllColorsFor(String.valueOf(groups_spinner2.getSelectedItem()));
                loadListData(String.valueOf(groups_spinner2.getSelectedItem()));
                dataAdapter.notifyDataSetChanged();
                break;
        }

        return true;
    }

    public void showColorPickDialog() {

        AlertDialog.Builder colorPickAlert = new AlertDialog.Builder(getContext());

        colorPickAlert.setTitle("Update LED Color");

        final DecimalFormat format = new DecimalFormat("#.#"); //for seekbar seconds

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View dialoglayout = inflater.inflate(R.layout.color_picker_dialog, null);

        selected_ID = Integer.parseInt(label_ids.get(info.position));

        colorPickAlert.setView(dialoglayout);

        final ColorPicker picker = (ColorPicker) dialoglayout.findViewById(R.id.picker);

        final SeekBar seekBar = (SeekBar) dialoglayout.findViewById(R.id.seekBar);

        seekBar.setProgress(500); seekBar.setMax(5000);

        final OpacityBar opacityBar = (OpacityBar) dialoglayout.findViewById(R.id.opacitybar);

        final TextView red = (TextView) dialoglayout.findViewById(R.id.textView);

        final TextView green = (TextView) dialoglayout.findViewById(R.id.textView2);

        final TextView blue = (TextView) dialoglayout.findViewById(R.id.textView3);

        final TextView DelayValue = (TextView) dialoglayout.findViewById(R.id.DelayValueText);

        int value = Integer.parseInt(delays.get(info.position));
        Double seconds = (value*1.0)/1000;
        DelayValue.setText(String.valueOf(format.format(seconds)) + "s");

        Button colorSelectedBtn = (Button)dialoglayout.findViewById(R.id.button1);

        final AlertDialog colorDialog = colorPickAlert.show();

        picker.setColor(Integer.parseInt(lables.get(info.position)));

        opacityBar.setOpacity(Color.alpha(Integer.parseInt(lables.get(info.position))));

        colorSelectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DB_Handler db = new DB_Handler(getContext());
            db.updateColor(
                    get_ID(),
                    new COLOR_DB_Contractor(String.valueOf(picker.getColor())),
                    new COLOR_DB_Contractor(String.valueOf(groups_spinner2.getSelectedItem())),
                    new COLOR_DB_Contractor(opacityBar.getOpacity()),
                    new COLOR_DB_Contractor(seekBar.getProgress())
            );
            loadListData(String.valueOf(groups_spinner2.getSelectedItem()));
            dataAdapter.notifyDataSetChanged();
            colorDialog.dismiss();
            }
        });

        picker.addOpacityBar(opacityBar);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
            BluetoothV2 bluetooth = new BluetoothV2();
            int r, g, b;
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            bluetooth.Message(r + "," + g + "," + b + "\n", 0);
            red.setText(Integer.toString(r));
            green.setText(Integer.toString(g));
            blue.setText(Integer.toString(b));
            }
        });

        seekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Double seconds = (progress*1.0)/1000;
                    DelayValue.setText(String.valueOf(format.format(seconds)) + "s");
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(groups_spinner2.getSelectedItem() != null){
                loadListData(String.valueOf(groups_spinner2.getSelectedItem()));
            }
        } else {

        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

}
