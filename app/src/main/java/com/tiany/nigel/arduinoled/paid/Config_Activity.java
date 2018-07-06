package com.tiany.nigel.arduinoled.paid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.tiany.nigel.arduinoled.R;

import java.util.List;

public class Config_Activity extends Fragment {
    private static final String LOG = Color_Activity.class.getName();
    NumberPicker r_Pin,g_Pin,b_Pin;
    EditText group_name;
    int menuItemIndex;
    AdapterView.AdapterContextMenuInfo info;
    Button save_group_button;
    ListView groups_listview;
    Color_Activity fromColorActivity;
    List<String> lables;
    final String[] RpinValues= {"R","0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
    final String[] GpinValues= {"G","0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
    final String[] BpinValues= {"B","0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};

    String selected_R,selected_G,selected_B;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View config_root = inflater.inflate(R.layout.activity_config,container,false);
        r_Pin = (NumberPicker) config_root.findViewById(R.id.r_PIN);
        g_Pin = (NumberPicker) config_root.findViewById(R.id.g_Pin);
        b_Pin = (NumberPicker) config_root.findViewById(R.id.b_PIN);
        save_group_button = (Button) config_root.findViewById(R.id.save_group_button);
        groups_listview = (ListView) config_root.findViewById(R.id.groups_listview);
        group_name = (EditText) config_root.findViewById(R.id.group_name);
        registerForContextMenu(groups_listview);

        r_Pin.setMaxValue(RpinValues.length - 1);   r_Pin.setMinValue(0);  r_Pin.setDisplayedValues(RpinValues);
        g_Pin.setMaxValue(GpinValues.length - 1);   g_Pin.setMinValue(0);  g_Pin.setDisplayedValues(GpinValues);
        b_Pin.setMaxValue(BpinValues.length - 1);   b_Pin.setMinValue(0);  b_Pin.setDisplayedValues(BpinValues);
        change_listener();
        save_config();
        loadSpinnerData();

        return config_root;
    }

    public void change_listener(){
        //Set a value change listener for NumberPicker
        r_Pin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected value from picker
                selected_R = RpinValues[newVal];
            }
        });
        g_Pin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected value from picker
                selected_G = GpinValues[newVal];
            }
        });
        b_Pin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                selected_B = BpinValues[newVal];
            }
        });
    }

    public void saveAnimation(){
        Thread animation = new Thread(new Runnable() {
            BluetoothV2 bluetooth = new BluetoothV2();
            public void run() {
                bluetooth.Message("255,0,0" + "\n", 550);
                bluetooth.Message("0,255,0" + "\n", 550);
                bluetooth.Message("0,0,255" + "\n", 550);
                bluetooth.Message("255,255,255"+"\n",550);
            }
        });
        animation.start();
    }

    public void save_config(){
        save_group_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String label = group_name.getText().toString().trim();
                        DB_Handler db = new DB_Handler(getContext());
                        boolean label_Exists = false;
                        boolean null_ints = false;
                        boolean repeats = false;
                        if (db.ifExists(label)) {
                            label_Exists = true;
                            Toast.makeText(getContext(), "Group Name Already Exits.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (selected_R == "R" || selected_G == "G" || selected_B == "B") {
                            null_ints = true;
                            Toast.makeText(getContext(), "Please select valid R - G - B Pins on the arduino.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (selected_R == null || selected_G == null || selected_B == null) {
                            null_ints = true;
                            Toast.makeText(getContext(), "Please select valid R - G - B Pins on the arduino.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (selected_B == selected_R || selected_B == selected_G || selected_R == selected_G) {
                            Toast.makeText(getContext(), "Please selected a pin only once",
                                    Toast.LENGTH_SHORT).show();
                            repeats = true;
                        }

                        if (label.trim().length() > 0 && label_Exists == false && null_ints == false && repeats == false) {
                            // inserting new label into database
                            db.addGroup(new DB_Contractor(Integer.parseInt(selected_R)), new DB_Contractor(Integer.parseInt(selected_G)), new DB_Contractor(Integer.parseInt(selected_B)), new DB_Contractor(label));
                            BluetoothV2 bluetooth = new BluetoothV2();
                            bluetooth.Message(selected_R + "," + selected_G + "," + selected_B + "?", 0);
                            saveAnimation();
                            loadSpinnerData();
                            String TabOfFragManage = ((MainActivity) getActivity()).getTabFragManage();
                            Color_Activity fm = (Color_Activity) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragManage);
                            fm.loadSpinnerData();
                        }
                        if (label.trim().length() <= 0) {
                            Toast.makeText(getContext(), "Please enter LED group name",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    /**
     * Function to load the spinner data from SQLite database
     * */
    public void loadSpinnerData() {
        // database handler
        DB_Handler db = new DB_Handler(getContext());

        // Spinner Drop down elements
        lables = db.getAllDATA();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,lables);

        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        groups_listview.setAdapter(dataAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.groups_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("LED Groups");
            String[] menuItems = getResources().getStringArray(R.array.group_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.group_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = lables.get(info.position);
        // database handler
        DB_Handler db = new DB_Handler(getContext());
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch (menuItemName){
            case "Delete":
                //delete item
                db.deleteGroup(listItemName);
                refresh();
                loadSpinnerData();
                break;
            case "Delete All":
                db.deleteAllGroups();
                refresh();
                loadSpinnerData();
                break;
        }

        return true;
    }

    public void refresh(){
        String TabOfFragManage = ((MainActivity) getActivity()).getTabFragManage();
        Color_Activity fm = (Color_Activity) getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragManage);
        fm.loadSpinnerData();
    }

}
