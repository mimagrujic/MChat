package com.example.mchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ConversationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversation);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        long phone = i.getLongExtra("phone", 0);

        TextView contName = findViewById(R.id.conName);
        contName.setText(name + "\n" + "0" + phone);
    }
}
