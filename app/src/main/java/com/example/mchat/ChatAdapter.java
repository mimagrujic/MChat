package com.example.mchat;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String currentUserId;
    private Context context;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    //when called this func inflates certain layouts depending on sender and makes the correct view for message placement
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    //when called this func shows messages on screen. it checks message type and then calls bind()
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentViewHolder)holder).bind(message);
        } else {
            ((ReceivedViewHolder)holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public SentViewHolder(View view) {
            super(view);
            textMessage = itemView.findViewById(R.id.textMessageSent);
        }
        public void bind(@NonNull Message message) {
            textMessage.setText(message.getText());
            TextView time = itemView.findViewById(R.id.time_bubble);
            String formattedTime = DateFormat.format("hh:mm a", message.getTime()).toString();
            time.setText(formattedTime);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int maxWidth = (int) (displayMetrics.widthPixels * 0.75);
            textMessage.setMaxWidth(maxWidth);
        }
    }

    private class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public ReceivedViewHolder(View view) {
            super(view);
            textMessage = itemView.findViewById(R.id.textMessageReceived);
        }
        public void bind(@NonNull Message message) {
            textMessage.setText(message.getText());
            TextView time = itemView.findViewById(R.id.time_bubble);
            String formattedTime = DateFormat.format("hh:mm a", message.getTime()).toString();
            time.setText(formattedTime);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int maxWidth = (int) (displayMetrics.widthPixels * 0.75);
            textMessage.setMaxWidth(maxWidth);
        }
    }
}
