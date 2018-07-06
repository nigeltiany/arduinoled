package com.tiany.nigel.arduinoled.paid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tiany.nigel.arduinoled.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class BluetoothV2 extends Fragment {

    /****************************************************************************************************************/

    private static final int REQUEST_ENABLE_BT = 0;
    private static final String TAG = BluetoothV2.class.getName();
    public final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public boolean connected = false;

    /****************************************************************************************************************/

    ToggleButton bluetooth_button,Paired,New;
    ImageButton search_button;
    ImageButton arduino_sketch,connections;
    ListView devices_list, new_devices_list;
    TextView textView1,whenconnected;

    /****************************************************************************************************************/

    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    /****************************************************************************************************************/

    BluetoothAdapter bluetoothAdapter;
    boolean hasBluetooth = (bluetoothAdapter == null);
    BluetoothDevice bluetoothDevice;
    BluetoothDevice device;
    BluetoothSocket bluetoothSocket;
    public static void gethandler(Handler handler){//Bluetooth handler
        mHandler = handler;
    }
    static Handler mHandler = new Handler();
    static ConnectedThread connectedThread;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    /****************************************************************************************************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.activity_bluetooth,container,false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_button = (ToggleButton) root.findViewById(R.id.bluetooth_button);
        bluetooth_button.setTextOn(null); bluetooth_button.setTextOff(null); bluetooth_button.setText(null);
        Paired = (ToggleButton) root.findViewById(R.id.Paired);
        New = (ToggleButton) root.findViewById(R.id.New);
        search_button = (ImageButton) root.findViewById(R.id.search_button);
        search_button.setVisibility(View.VISIBLE);
        textView1 = (TextView) root.findViewById(R.id.color);
        whenconnected = (TextView) root.findViewById(R.id.whenconnected);
        arduino_sketch = (ImageButton) root.findViewById(R.id.arduino);
        connections = (ImageButton) root.findViewById(R.id.connections);

    /****************************************************************************************************************/

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);

        // Find and set up the ListView for paired devices
        devices_list = (ListView) root.findViewById(R.id.devices_list);
        ListView pairedListView = (ListView) root.findViewById(R.id.devices_list);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        new_devices_list = (ListView) root.findViewById(R.id.new_devices_list);
        ListView newDevicesListView = (ListView) root.findViewById(R.id.new_devices_list);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getContext().registerReceiver(mReceiver, filter);

        /****************************************************************************************************************/

        if(hasBluetooth) {
            newDevicesListView.setVisibility(View.INVISIBLE);
            if(bluetoothAdapter.isEnabled()){
                search_and_show();
            }
        }

        /****************************************************************************************************************/

        turnON_OFFbluetooth();  // turn on - off bluetooth.
        search_bluetooth_devices(); // searches bluetooth devices on search button click.
        toggler();           // This hide paired or new devices listview with respect to the toggle buttons.
        Connect();           // connects the device selected on a listview.
        arduino_sketch();
        arduino_connections();

        /****************************************************************************************************************/

        return root;
    }

    private void arduino_sketch() {
        arduino_sketch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openSketch = new Intent(getContext(),SketchActivity.class);
                        startActivity(openSketch);
                    }
                }
        );
    }

    private void arduino_connections() {
        connections.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openConnections = new Intent(getContext() , HowToWireUp.class);
                        startActivity(openConnections);
                    }
                }
        );
    }

    public void turnON_OFFbluetooth(){
        //make button okay
        if (hasBluetooth && bluetoothAdapter.isEnabled()) {
            bluetooth_button.setChecked(true);
        }else{
            bluetooth_button.setChecked(false);
        }

        bluetooth_button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //enable the bluetooth adapter
                    if (bluetoothAdapter.isEnabled() == false && bluetooth_button.isChecked() == true) {
                        if (hasBluetooth) {
                            // prompt the user to turn BlueTooth on
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }
                    if (bluetoothAdapter.isEnabled() == true && bluetooth_button.isChecked() == false) {
                        bluetooth_button.setChecked(false);
                        bluetoothAdapter.disable();
                        mNewDevicesArrayAdapter.clear();
                        mPairedDevicesArrayAdapter.clear();
                    }
                }
            }
        );
    }

    public void toggler(){
        Paired.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    New.setChecked(false);
                    new_devices_list.setVisibility(View.GONE);
                    devices_list.setVisibility(View.VISIBLE);
                    //search_button.setVisibility(View.INVISIBLE);
                    if(Paired.isChecked()==false){
                        New.setChecked(true);
                    }
                    if(New.isChecked()==true){
                        new_devices_list.setVisibility(View.VISIBLE);
                        devices_list.setVisibility(View.INVISIBLE);
                        //search_button.setVisibility(View.VISIBLE);
                    }
                }
            }
        );

        New.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paired.setChecked(false);
                    devices_list.setVisibility(View.GONE);
                    new_devices_list.setVisibility(View.VISIBLE);
                    //search_button.setVisibility(View.VISIBLE);
                    if(New.isChecked()==false){
                        Paired.setChecked(true);
                    }
                    if(Paired.isChecked()==true){
                        new_devices_list.setVisibility(View.GONE);
                        devices_list.setVisibility(View.VISIBLE);
                        //search_button.setVisibility(View.INVISIBLE);
                    }
                }
            }
        );

    }

    public void search_and_show(){
        //**list the bluetooth device**
        bluetoothAdapter.cancelDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mReceiver, filter);
        mNewDevicesArrayAdapter.clear();
        mPairedDevicesArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
    }

    public void search_bluetooth_devices() {
        search_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_and_show();
                    }
                }
        );
    }

    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            bluetoothAdapter.cancelDiscovery();
            final String info = ((TextView) v).getText().toString();
            Log.e(TAG, info);
            String address = info.substring(info.length()-17);
            Log.e(TAG, info);
            BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            // Cancel discovery as it will slow down the connection
            bluetoothAdapter.cancelDiscovery();
            // Initiate a connection request in a separate thread
            //ConnectThread t = new  ConnectThread(bluetoothDevice);
            //t.start();
        }
    };

    public void connectAttempt(final String device){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), "Connecting to " + device, Toast.LENGTH_LONG).show();
                whenconnected.setText("Connecting to " + device);
            }
        });
    }

    public void success(final String device){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), "Connected to " + device, Toast.LENGTH_LONG).show();
                whenconnected.setText("Connected to " + device);
            }
        });
    }

    public void unsuccessful(final String device){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), "Unable to connect to " + device + ". Please try again", Toast.LENGTH_LONG).show();
                whenconnected.setText("Please try connecting to " + device + " again");
            }
        });
    }

    public void Connect(){
        devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) devices_list.getItemAtPosition(position);
                String MAC = itemValue.substring(itemValue.length()- 17);
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
                //BluetoothDevice selectedDevice = mPairedDevicesArrayAdapter.getItem(position);
                // Cancel discovery as it will slow down the connection
                bluetoothAdapter.cancelDiscovery();
                // Initiate a connection request in a separate thread
                ConnectThread connect = new ConnectThread(bluetoothDevice);
                connect.start();
                connectAttempt(bluetoothDevice.getName());
            }
        });

        new_devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) new_devices_list.getItemAtPosition(position);
                String MAC = itemValue.substring(itemValue.length()- 17);
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
                // Cancel discovery as it will slow down the connection
                //bluetoothAdapter.cancelDiscovery();
                // Initiate a connection request in a separate thread
                //BluetoothDevice selectedDevice = get(arg2);
                ConnectThread connect = new ConnectThread(bluetoothDevice);
                connect.start();
                connectAttempt(bluetoothDevice.getName());
            }
        });
    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
 /*-watch->*/if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mNewDevicesArrayAdapter.notifyDataSetChanged();
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mPairedDevicesArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void Message(String message, int delay){
        if (connectedThread != null){
            connectedThread.write(message,delay);
        }
    }

    public static void disconnect(){
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getContext(), " ", "Connecting...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!connected){

                    }
                } catch (Exception e) {
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            try {
                if (bluetoothSocket != null){
                    bluetoothSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        disconnect();           // disconnect from any threads
        getContext().unregisterReceiver(mReceiver);
    }

    private class ListeningThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        public ListeningThread() {
            BluetoothServerSocket temp = null;
            try {
                temp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), uuid);

            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothServerSocket = temp;
        }

        public void run() {
            BluetoothSocket bluetoothSocket;
            // This will block while listening until a BluetoothSocket is returned
            // or an exception occurs
            while (true) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection is accepted
                if (bluetoothSocket != null) {
                    connected = true;
                    // Code to manage the connection in a separate thread
                   /*
                       manageBluetoothConnection(bluetoothSocket);
                   */

                    try {
                        bluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Cancel the listening socket and terminate the thread
        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                success(mmDevice.getName());
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                unsuccessful(mmDevice.getName());
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        StringBuffer sbb = new StringBuffer();

        public void run() {

            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    try {
                        sleep(30);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buffer = new byte[1024];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String income, int delay) {

            try {
                mmOutStream.write(income.getBytes());
                for(int i=0;i<income.getBytes().length;i++) {
                    Log.e("outStream" + Integer.toString(i), Character.toString((char) (Integer.parseInt(Byte.toString(income.getBytes()[i])))));
                }
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) { }
            delay(delay);
        }

        public void delay(int delay){
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}

