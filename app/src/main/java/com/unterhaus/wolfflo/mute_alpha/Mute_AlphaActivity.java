package com.unterhaus.wolfflo.mute_alpha;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.media.AudioManager;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.net.wifi.ScanResult;

import android.net.wifi.WifiManager;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;





public  class  Mute_AlphaActivity extends AppCompatActivity implements Dialog.Delivery
                                {
    public static final String SSID_PREFERENCES = "SSIDprefs";
    String wifisFoundArray [];
       Dialog newFragment = new Dialog();
    String chosenSSID;
    AudioManager volume_state;
    List<ScanResult>  wifisFoundList;
     WifiManager wifi;






                                    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_mute__alpha);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView ssid = (TextView) findViewById(R.id.ssid_found);
        Button change = (Button) findViewById(R.id.button);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanWifi();
                doDialog();


            }
        });

        SharedPreferences SSIDs = getSharedPreferences(SSID_PREFERENCES, MODE_PRIVATE );
        chosenSSID = SSIDs.getString("storedSSID", "");


         wifi =  (WifiManager) getSystemService(Context.WIFI_SERVICE);
        volume_state = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
       ScanWifi();





        if (chosenSSID.equals("")) {
            doDialog();

            ssid.setText("Current Wifi:" + chosenSSID);
            startAlarm();





           }



        else {

        setSilent();
            startAlarm();
            ssid.setText("Current Wifi:" + chosenSSID);



        }
    }


                                    @Override
                                    protected void onStop() {
                                        super.onStop();


                                    }

                                    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_mute__alpha, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


public void doDialog () {


        newFragment.iwosiwos = wifisFoundArray;
        newFragment.show(getSupportFragmentManager(),"dosth");
    }





         @Override
           public void onSelectItem(int i) {
             Toast.makeText(this, "SSID:" + wifisFoundArray[i], Toast.LENGTH_SHORT).show();
             chosenSSID = (wifisFoundArray[i]);

             SharedPreferences SSIDs = getSharedPreferences(SSID_PREFERENCES,MODE_PRIVATE);
             SharedPreferences.Editor ssidEdit = SSIDs.edit();
             ssidEdit.putString("storedSSID", wifisFoundArray[i]);
             ssidEdit.apply();

             volume_state.setRingerMode(AudioManager.RINGER_MODE_SILENT);





         }


public void setSilent() {
    for (int i = 0; i < wifisFoundList.size() ; i++ ) {

        if (wifisFoundArray[i].equals(chosenSSID)) {


            volume_state.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getApplicationContext(), "Phone muted", Toast.LENGTH_SHORT).show();
            i = wifisFoundList.size();
        }

    }

                                }

  public void startAlarm () {

      Intent intent = new Intent(this, Receiver.class);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),1337,intent,0);
      AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
      alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 180000,pendingIntent );

  }
  public void ScanWifi() {

                                    wifi.startScan();

                                    wifisFoundList = wifi.getScanResults();

                                    wifisFoundArray  = new String[wifisFoundList.size()];
                                    for(int i = 0; i < wifisFoundList.size(); i++){
                                        wifisFoundArray[i] = wifisFoundList.get(i).SSID;
                                    }
                                }



             }

