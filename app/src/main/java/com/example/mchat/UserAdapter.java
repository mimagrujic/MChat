package com.example.mchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private String currentUser;
    private List<User>  userList;
    private List<User> allUsers;
    private OnItemClickListener listener;
    private List<String> chats;

    public UserAdapter(String currentUser, List<User> userList, List<String> chats, OnItemClickListener listener) {
        this.currentUser = currentUser;
        this.userList = new ArrayList<>(userList);
        this.chats = new ArrayList<>(chats);
        this.allUsers = new ArrayList<>(userList);
        this.listener = listener;
    }

    //using this to be able to start a chat
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onItemClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateUsers(List<User> newUsers, List<User> all) {
        userList.clear();
        userList.addAll(newUsers);
        allUsers.clear();
        allUsers.addAll(all);

        notifyDataSetChanged();
    }

    public void filter(String text) {
        userList.clear();
        if (text.isEmpty()) {
           // userList.addAll(allUsers);

        } else {
            text = text.toLowerCase();
            for (User user : allUsers) {
                if (user.getUsername().contains(text)) {
                    userList.add(user);
                }
            }
        }
        notifyDataSetChanged();

    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
