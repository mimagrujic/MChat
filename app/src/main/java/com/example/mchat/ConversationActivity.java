package com.example.mchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    private String senderUsername;
    private String recipientUsername;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatRef;
    private RecyclerView convoSpace;
    private EditText typeInMessage;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversation);

        Intent i = getIntent();
        String recipientName = i.getStringExtra("recipientName");
        String recipientSurname = i.getStringExtra("recipientSurname");
        recipientUsername = i.getStringExtra("recipientUsername");
        senderUsername = i.getStringExtra("senderUsername");

        TextView contName = findViewById(R.id.conName);
        contName.setText(recipientName + " " + recipientSurname);

        firebaseDatabase = FirebaseDatabase.getInstance();
        String chatId = senderUsername.compareTo(recipientUsername) < 0 ?
                senderUsername + "-" + recipientUsername : recipientUsername + "-" + senderUsername;
        chatRef = firebaseDatabase.getReference("chats").child(chatId);

        convoSpace = findViewById(R.id.convoRecyclerView);
        typeInMessage = findViewById(R.id.typeMessage);
        sendButton = findViewById(R.id.sendMessage);
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages, senderUsername, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(Message msg) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
                builder.setTitle("Delete a message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        chatRef.child(msg.getId()).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("ConversationActivity", "Error with deleting a message." + error.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        convoSpace.setAdapter(chatAdapter);
        convoSpace.setLayoutManager(new LinearLayoutManager(this));

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            //snapshot is a pic of state of my current ref (chatRef)
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                //children are messages of chatRef (conversation between user1 and user 2)
                for(DataSnapshot child : snapshot.getChildren()) {
                    Message message = child.getValue(Message.class);
                    if(message != null
                            && message.getVisibleTo().get(senderUsername)
                            && ((message.getSender().equals(senderUsername) && message.getReceiver().equals(recipientUsername))
                            || (message.getSender().equals(recipientUsername) && message.getReceiver().equals(senderUsername)))) {
                        try {
                            String decryptedMessage = CryptoManager.decrypt(
                                    message.getIv() + ":" + message.getText());
                            message.setText(decryptedMessage);
                            messages.add(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
                chatAdapter.notifyDataSetChanged();
                convoSpace.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConversationActivity.this, "Unable to load messages.", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typeInMessage.getText().toString().trim();
                if(!msg.isEmpty()) {
                    try {
                        sendMessage(msg);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    typeInMessage.setText("");
                }
            }
        });

        typeInMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String message = typeInMessage.getText().toString().trim();
                    if(!message.isEmpty()) {
                        try {
                            sendMessage(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        typeInMessage.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        convoSpace.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) convoSpace.getLayoutManager();
                int firstVisibleMessage = layoutManager.findFirstVisibleItemPosition();
                int lastScrollIndex = firstVisibleMessage;
                int offset = 5;
                if(dy < 0) {
                    if(lastScrollIndex - offset < 0)
                        lastScrollIndex = 0;
                    else
                        lastScrollIndex = lastScrollIndex - offset;
                    recyclerView.smoothScrollToPosition(lastScrollIndex);
                }
            }


        });
    }

    private void sendMessage(String msg) throws Exception {
        String messageId = chatRef.push().getKey();
        String encryptedData = CryptoManager.encrypt(msg);
        String[] encryptedParts = encryptedData.split(":");
        if (encryptedParts.length != 2) {
            return;
        }
        String iv = encryptedParts[0];
        String text = encryptedParts[1];
        Message message = new Message(messageId, senderUsername, recipientUsername, text, iv, System.currentTimeMillis());
        if(messageId != null)
            chatRef.child(messageId).setValue(message);
    }
}
