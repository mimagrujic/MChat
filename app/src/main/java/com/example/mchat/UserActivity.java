package com.example.mchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference chatsRef;
    private List<String> chats = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    public String USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        chatsRef = firebaseDatabase.getReference("chats");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLogedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String username = sharedPreferences.getString("username", null);
        String name = sharedPreferences.getString("name", null);
        USER = username;

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

            @Override
            public void onItemLongClick(User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                builder.setTitle("Delete a conversation")
                        .setMessage("Are you sure you want to delete this conversation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String chatId = username.compareTo(user.getUsername()) < 0 ?
                                        username + "-" + user.getUsername() : user.getUsername() + "-" + username;
                                DatabaseReference chat = chatsRef.child(chatId);
                                chat.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int i = 0;
                                        for(DataSnapshot msg : snapshot.getChildren()) {
                                            i++;
                                            Message message = msg.getValue(Message.class);
                                            Map<String, Boolean> map = new HashMap<>();
                                            map.put(username, false);
                                            map.put(user.getUsername(), message.getVisibleTo().get(user.getUsername()));
                                            if(!map.get(username) && !map.get(user.getUsername())) {
                                                chat.child(message.getId()).removeValue();
                                                i--;
                                            } else {
                                                message.setVisibleTo(map);
                                                chat.child(message.getId()).setValue(message);
                                            }
                                        }
                                        if(i == 0) {
                                            chat.removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("UserActivity", "Error with loading data. " + error.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        recyclerView.setAdapter(userAdapter);

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
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
                allUsers.clear();
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

    public void deleteUser(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle("Account Deactivation")
        .setMessage("Are you sure you want to delete your account? Once you delete it, you won't be able to log in again.")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference userRef = usersRef.child(USER);
                userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(UserActivity.this, "Account deleted.", Toast.LENGTH_SHORT).show();
                            logOutUser(view);

                        } else {
                            Toast.makeText(UserActivity.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        })
        .setNegativeButton("No", null)
        .show();
    }

    public void logOutUser(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.apply();
        startActivity(new Intent(UserActivity.this, MainActivity.class));
        finish();
    }
}
