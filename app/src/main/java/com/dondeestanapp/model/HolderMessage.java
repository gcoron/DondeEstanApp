package com.dondeestanapp.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dondeestanapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMessage extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView message;
    private TextView hour;
    private CircleImageView profile_picture_message;

    public HolderMessage(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_name_message);
        message = itemView.findViewById(R.id.tv_message);
        hour = itemView.findViewById(R.id.tv_hour_message);
        profile_picture_message =  itemView.findViewById(R.id.profile_picture_message);
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getMessage() {
        return message;
    }

    public void setMessage(TextView message) {
        this.message = message;
    }

    public TextView getHour() {
        return hour;
    }

    public void setHour(TextView hour) {
        this.hour = hour;
    }

    public CircleImageView getProfile_picture_message() {
        return profile_picture_message;
    }

    public void setProfile_picture_message(CircleImageView profile_picture_message) {
        this.profile_picture_message = profile_picture_message;
    }
}
