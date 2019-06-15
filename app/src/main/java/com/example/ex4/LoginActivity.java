package com.example.ex4;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    private static final String tag = "LogAc";

    public void MyLog(String message){
        Log.v(tag,message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void SwitchToJoystickActivity(View view) {
        EditText ipBox = (EditText) findViewById(R.id.IP_TextBox);
        EditText portBox = (EditText) findViewById(R.id.Port_TextBox);

        Intent intent = new Intent(this, JoystickActivity.class);
        intent.putExtra("ip",ipBox.getText().toString());
        intent.putExtra("port",portBox.getText().toString());
        startActivity(intent);
    }

}
