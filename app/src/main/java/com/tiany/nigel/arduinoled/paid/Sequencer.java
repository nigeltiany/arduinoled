package com.tiany.nigel.arduinoled.paid;

import android.graphics.Color;
import android.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Created by Nigel on 11/26/2015.
 */
public class Sequencer implements Runnable{
    private static final String LOG = Sequencer.class.getName();
    private Thread thread;
    private boolean stopped = false;
    private String threadName;
    private List<String> colors = null;
    private List<String> delays;
    BluetoothV2 bluetooth = new BluetoothV2();
    private List<String> pins;
    ThreadGroup rootThreadGroup = Thread.currentThread().getThreadGroup();

    public Sequencer(String taskName,DB_Handler database){
        Log.e("Thread Name", taskName);
        this.threadName = taskName;
        colors = database.getSpinnerItemColors(taskName);
        delays = database.getItemDelays(taskName);
        pins = database.getRGBpins(taskName);
        //PrintList(colors);
    }

    @Override
    public void run() {
        int switcher = 0;
        int limit = colors.size();
        int r = Integer.parseInt(pins.get(0));
        int g = Integer.parseInt(pins.get(1));
        int b = Integer.parseInt(pins.get(2));
        bluetooth.Message(r + "," + g + "," + b + "?", 0);
        while(switcher<=limit && stopped == false && limit>0){
            int R = Color.red(Integer.parseInt(colors.get(switcher)));
            int G = Color.green(Integer.parseInt(colors.get(switcher)));
            int B = Color.blue(Integer.parseInt(colors.get(switcher)));
            int delay = Integer.parseInt(delays.get(switcher));
            bluetooth.Message(R + "," + G + "," + B + "\n", delay);
            switcher+=1;
            if(switcher != limit){
                continue;
            }else{
                switcher = 0;
                continue;
            }
        }
    }

    public void PrintList(List listObject){
        List<String> listtoPrint = listObject;
        for(String items : listtoPrint) {
            Log.e("List Items", items);
        }
    }

    public void start(){
        thread = new Thread(this);
        thread.setName(threadName);
        thread.start();
    }

    public void stop(String threadID){
        if (threadID == null)
            throw new NullPointerException( "Null name" );

        final Thread[] threads = getAllThreads(rootThreadGroup);
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        int limit = threadArray.length;
        for(int i=0;i<limit;i++){
            Log.e("Threads",threadArray[i].getName());
        }
        for (Thread thread : threads)
            if (thread.getName().equals(threadID)) {
                this.stopped = true;
            }
                //Log.e("Stop Request", String.valueOf(thread.isInterrupted()));
    }

    Thread[] getAllThreads(ThreadGroup group) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        group.enumerate(threadArray);
        return threadArray;
    }

    /*
     *
     * public Thread.State getThreadState(String threadID){
        Thread.State state = Thread.State.WAITING;
        if (threadID == null)
            throw new NullPointerException( "Null name" );
        final Thread[] threads = getAllThreads();
        for ( Thread thread : threads )
            if (thread.getName().equals(threadID))
                state = thread.getState();
        return state;
    }

    Thread[] getAllThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        //for (int i=0;i<threadArray.length;i++){
        //    Log.e("Threads",threadArray[i].getName());
        //}
        return threadArray;
    }

    public void switchON(){
        OnOffToggle.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String taskName = String.valueOf(groups_spinner2.getSelectedItem());
                    //DB_Handler dataBase = new DB_Handler(getContext());
                    //Sequencer sequencer = new Sequencer(taskName,dataBase);
                    if (OnOffToggle.isChecked()) {
                        //if(getThreadState(taskName) == Thread.State.NEW){
                            playAll();
                        //}
                    } else if (OnOffToggle.isChecked() == false) {
                        //if(getThreadState(taskName) == Thread.State.RUNNABLE){
                            //sequencer.stop(taskName);
                        //}
                    }
                }
            }
        );
    }

    public void playAll(){
        DB_Handler dataBase = new DB_Handler(getContext());
        List<String> spinnerList = dataBase.getAllDATA();
        for(int i=0;i<spinnerList.size();i++){
            Sequencer sequencer = new Sequencer(spinnerList.get(i),dataBase);
            sequencer.start();
        }
    }

    public void CreateRunnable(Runnable runnable){
        String taskName = String.valueOf(groups_spinner2.getSelectedItem());
        DB_Handler dataBase = new DB_Handler(getContext());
        Sequencer sequencer = new Sequencer(taskName,dataBase);
        runnable = sequencer;
        runnable.run();
    }

    public void DynamicButtons(final int buttonID, final Thread.State state){
        final ToggleButton toggleButton = new ToggleButton(getContext());
        toggleButton.setId(buttonID);
        toggleButton.setTextOff("Play");
        toggleButton.setTextOn("Stop");
        Drawable dw = ContextCompat.getDrawable(getContext(), R.drawable.roundresource);
        toggleButton.setBackgroundDrawable(dw);
        LinearLayout ll = new LinearLayout(getContext());
        ll.addView(toggleButton);
        playLayout.addView(ll);
        toggleButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String taskName = String.valueOf(groups_spinner2.getSelectedItem());
                        DB_Handler dataBase = new DB_Handler(getContext());
                        Sequencer sequencer = new Sequencer(taskName,dataBase);
                        if (OnOffToggle.isChecked()) {
                            //if(getThreadState(taskName) == Thread.State.NEW){
                                sequencer.start();
                            //}
                        } else if (OnOffToggle.isChecked() == false) {
                            //if(getThreadState(taskName) == Thread.State.RUNNABLE){
                                Log.e("Stop Request","Stop Request");
                            //}
                        }
                    }
                }
        );
    }
     */

}
