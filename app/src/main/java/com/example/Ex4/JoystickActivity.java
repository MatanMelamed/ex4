package com.example.Ex4;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class JoystickActivity extends AppCompatActivity {

    static TcpClient client = new TcpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = Integer.parseInt(intent.getStringExtra("port"));

        // invoke a task that starts the client
        new ConnectTask().execute(new Pair<>(ip, port));

        // set a listener for the joystick - to send information via client.
        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setMovementListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(float horizontalValue, float verticalValue) {
                if (client.IsRunning()) {
                    String aileronCmd = "set /controls/flight/aileron " + horizontalValue + "\r\n";
                    String elevatorCmd = "set /controls/flight/elevator " + verticalValue + "\r\n";
                    client.SendMessage(aileronCmd);
                    client.SendMessage(elevatorCmd);
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.Stop();
    }

    // An AsyncTask that gets ip as string and port as int, sets them to client and call start client.
    public class ConnectTask extends AsyncTask<Pair<String, Integer>, Void, Void> {
        @Override
        protected Void doInBackground(Pair<String, Integer>... pairs) {
            client.SetConnectionInfo(pairs[0].first, pairs[0].second);
            client.Start();
            return null;
        }
    }
}
