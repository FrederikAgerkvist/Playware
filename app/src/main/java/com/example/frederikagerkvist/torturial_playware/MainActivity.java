package com.example.frederikagerkvist.torturial_playware;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.OnAntEventListener;

import static com.livelife.motolibrary.AntData.EVENT_PRESS;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;

public class MainActivity extends AppCompatActivity implements OnAntEventListener {

    MotoConnection connection;
    Button startparringButton, finishparringButton, gameButton;
    TextView connected_TextView;
    boolean isParing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connected_TextView = findViewById(R.id.connected_textView);

        connection = MotoConnection.getInstance();
        connection.startMotoConnection(MainActivity.this);
        connection.saveRfFrequency(46);
        connection.setDeviceId(4);
        connection.registerListener(MainActivity.this);

        startparringButton = findViewById(R.id.startparringButton);
        finishparringButton = findViewById(R.id.finishparringButton);
        gameButton = findViewById(R.id.gamebutton);



        startparringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isParing){
                    connection.pairTilesStart();
                    finishparringButton.setEnabled(true);
                    startparringButton.setText("Stop connection");
                } else {
                    connection.pairTilesStop();
                    startparringButton.setText("Start Paring");
                    connected_TextView.setText("0");
                }
                isParing = !isParing;
            }
        });
        finishparringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.pairTilesStop();
                connected_TextView.setText("0");
                finishparringButton.setEnabled(false);
                gameButton.setEnabled(true);
                connected_TextView.setText(Integer.toString(connection.connectedTiles.size()));
            }
        });
        gameButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        }));

    }


    protected void onPause() {
        super.onPause();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);

    }

    protected void onRestart() {
        super.onRestart();
        connection.startMotoConnection(MainActivity.this);
        connection.registerListener(MainActivity.this);
    }

    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity.this);
    }


    @Override
    public void onMessageReceived(byte[] bytes, long l) {

    }

    @Override
    public void onAntServiceConnected() {

        connection.setAllTilesToInit();
    }

    @Override
    public void onNumbersOfTilesConnected(int i) {


    }
}
