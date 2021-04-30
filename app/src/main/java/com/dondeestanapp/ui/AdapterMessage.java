package com.dondeestanapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dondeestanapp.R;
import com.dondeestanapp.api.model.HolderMessage;
import com.dondeestanapp.api.model.MessageChat;

import java.util.ArrayList;
import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<HolderMessage> {

    private List<MessageChat> messageList = new ArrayList<>();
    private Context context;

    public AdapterMessage(Context context) {
        this.context = context;
    }

    public void addMessage(MessageChat messageChat) {
        this.messageList.add(messageChat);
        notifyItemInserted(messageList.size());
    }

    @NonNull
    @Override
    public HolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(this.context)
                .inflate(R.layout.card_view_messages, parent, false);
        return new HolderMessage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMessage holder, int position) {
        holder.getName().setText(messageList.get(position).getName());
        holder.getMessage().setText(messageList.get(position).getMessage());
        holder.getHour().setText(messageList.get(position).getHour());

        if (messageList.get(position).getName().contains("- CHOFER")) {
            holder.getProfile_picture_message().setImageResource(R.mipmap.ic_school_bus);
        } else {
            holder.getProfile_picture_message().setImageResource(R.mipmap.ic_person);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
