package com.example.mchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");


    }

    public void registerUser(View view) {
        TextInputEditText enteredName = findViewById(R.id.regName);
        TextInputEditText enteredSurame = findViewById(R.id.regSurname);
        TextInputEditText enteredUsername = findViewById(R.id.regUsername);
        EditText enteredPhone = findViewById(R.id.regPhone);
        EditText enteredPassword = findViewById(R.id.regPass);
        EditText enteredPasswordRepeated = findViewById(R.id.regPassRepeat);

        String name = enteredName.getText().toString();
        String surname = enteredSurame.getText().toString();
        String username = enteredUsername.getText().toString();
        String phone = enteredPhone.getText().toString();
        String password = enteredPassword.getText().toString();
        String passwordRepeated = enteredPasswordRepeated.getText().toString();

        if (username.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields. ", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(username)) {
                    Toast.makeText(RegistrationActivity.this, "Username has already been taken.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!password.equals(passwordRepeated)) {
                    Toast.makeText(RegistrationActivity.this, "Password mismatch.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    User user = new User(name, surname, username, phone, password);
                    usersRef.child(username).setValue(user);
                    Intent i = new Intent(RegistrationActivity.this, UserActivity.class);
                    i.putExtra("name", name);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "ERROR: " + error.getMessage());
            }
        });


    }
}
