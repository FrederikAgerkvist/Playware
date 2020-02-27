package com.example.frederikagerkvist.torturial_playware;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.OnAntEventListener;

import static com.livelife.motolibrary.AntData.EVENT_PRESS;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_RED;

public class GameActivity extends AppCompatActivity implements OnAntEventListener {


    MotoConnection connection;
    Button start_button, back_button;
    boolean gameStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        connection = MotoConnection.getInstance();
        connection.startMotoConnection(GameActivity.this);
        connection.saveRfFrequency(46);
        connection.setDeviceId(4);
        connection.registerListener(GameActivity.this);

        start_button = findViewById(R.id.start_button);
        back_button = findViewById(R.id.back_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gameStart){
                    connection.setAllTilesIdle(LED_COLOR_OFF);
                    connection.setTileColor(LED_COLOR_RED, connection.randomIdleTile());
                    start_button.setText("stop game");
                    gameStart = false;
                }else {
                    connection.setAllTilesToInit();
                    start_button.setText("start game");
                    gameStart = true;
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection.stopMotoConnection();
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onPause() {
        super.onPause();
        connection.stopMotoConnection();
        connection.unregisterListener(GameActivity.this);
    }

    protected void onRestart() {
        super.onRestart();
        connection.startMotoConnection(GameActivity.this);
        connection.registerListener(GameActivity.this);
    }

    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(GameActivity.this);
    }

    @Override
    public void onMessageReceived(byte[] bytes, long l) {

        int command = AntData.getCommand(bytes);
        int tileid = AntData.getId(bytes);
        int color = AntData.getColorFromPress(bytes);

        if (command == EVENT_PRESS){
            if (color == LED_COLOR_RED){
                connection.setAllTilesIdle(LED_COLOR_OFF);
                connection.setTileColor(LED_COLOR_RED, connection.randomIdleTile());
                connection.setTileIdle(LED_COLOR_OFF, tileid);
            }
        }

    }

    @Override
    public void onAntServiceConnected() {
        connection.setAllTilesToInit();
    }

    @Override
    public void onNumbersOfTilesConnected(int i) {

    }
}
