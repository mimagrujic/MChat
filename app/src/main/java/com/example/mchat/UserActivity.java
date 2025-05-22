package com.example.mchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference chatsRef;
    private List<String> chats = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        chatsRef = firebaseDatabase.getReference("chats");

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String username = i.getStringExtra("username");

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, " + name + "!");

        SearchView searchBar = findViewById(R.id.searchBar);
        searchBar.setQueryHint("Who are you looking for?");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(username, new ArrayList<>(), chats, new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(UserActivity.this, ConversationActivity.class);
                intent.putExtra("recipientName", user.getName());
                intent.putExtra("recipientSurname", user.getSurname());
                intent.putExtra("recipientUsername", user.getUsername());
                intent.putExtra("senderUsername", username);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(userAdapter);

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    if(child != null && child.getKey().contains(username)) {
                        chats.add(child.getKey());
                    }
                }
                fetchUsersFromDatabase(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserActivity", "Error with loading data. " + error.getMessage());
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    //fetchUsersFromDatabase(username);// --> works, but no need for it
                    userAdapter.updateUsers(users, allUsers);
                } else {
                    userAdapter.filter(newText);
                }
                return false;
            }
        });


    }

    private void fetchUsersFromDatabase(String username) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String otherUserId = userSnapshot.getKey();
                    if (user != null && !username.equals(otherUserId)) {
                        allUsers.add(user);
                        for(String id : chats) {
                            if (id.contains(otherUserId)) {
                                users.add(user);
                            }
                        }

                    }
                }
                userAdapter.updateUsers(users, allUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserActivity", "Error with loading data. " + error.getMessage());
            }

        });
    }
}
