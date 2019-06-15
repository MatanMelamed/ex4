package com.example.ex4;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class JoystickActivity extends AppCompatActivity {

    private static final String tag = "Joy";
    static TcpClient client = new TcpClient();

    public void MyLog(String message) {
        Log.v(tag, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        MyLog("on create called");
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = Integer.parseInt(intent.getStringExtra("port"));
        MyLog("going to send " + ip + " " + port);
        new ConnectTask().execute(new Pair<>(ip, port));

        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setMovementListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(float horizontalValue, float verticalValue) {
                String aileronCmd = "set /controls/flight/aileron " + horizontalValue + "\r\n";
                String elevatorCmd = "set /controls/flight/elevator " + verticalValue + "\r\n";
                client.SendMessage(aileronCmd);
                client.SendMessage(elevatorCmd);
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.Stop();
    }

    public class ConnectTask extends AsyncTask<Pair<String, Integer>, Void, Void> {

        @Override
        protected Void doInBackground(Pair<String, Integer>... pairs) {
            MyLog("setting and starting");
            client.SetConnectionInfo(pairs[0].first, pairs[0].second);
            client.Start();
            client.SendMessage("finished");
            MyLog("finished sent");
            return null;
        }
    }
}
