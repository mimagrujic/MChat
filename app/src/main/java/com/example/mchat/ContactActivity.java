package com.example.mchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);

        TextView cont1 = findViewById(R.id.con1);
        TextView cont2 = findViewById(R.id.con2);
        TextView cont3 = findViewById(R.id.con3);


        if(!Contact.contacts.isEmpty()){
            cont1.setText(Contact.contacts.get(0).toString());
            cont2.setText(Contact.contacts.get(1).toString());
            cont3.setText(Contact.contacts.get(2).toString());
        }

    }

    public void openChat(View view){
        Intent i = new Intent(this, ConversationActivity.class);
        TextView tw = (TextView) view;
        String name = tw.getText().toString();
        Contact c = Contact.findContactByName(name);
        if(c != null) {
            i.putExtra("name", name);
            i.putExtra("phone", c.getPhone());
        }
        startActivity(i);
    }
}
