package com.example.mchat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        TextView regAcc = findViewById(R.id.registerHere);

        regAcc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    regAcc.setTypeface(null, Typeface.BOLD);
                }
                else if(action == MotionEvent.ACTION_UP) {
                    regAcc.setTypeface(null, Typeface.NORMAL);
                }
                return false;
            }
        });
    }

    public void registerUserAccount(View view)
    {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
    }

    public void logInUser(View view) {
        TextInputEditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String enteredUser = username.getText().toString();
        String paswrd = password.getText().toString();

        if(enteredUser.isEmpty() || paswrd.isEmpty()) {
            Toast.makeText(this, "Check your username or password again.", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(enteredUser)) {
                    DataSnapshot child = snapshot.child(enteredUser);
                    User user = child.getValue(User.class);
                    if(user.getPassword().equals(paswrd)) {
                        Intent i = new Intent(MainActivity.this, UserActivity.class);
                        i.putExtra("name", user.getName());
                        i.putExtra("username", enteredUser);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "ERROR: " + error.getMessage());
            }
        });
    }

}