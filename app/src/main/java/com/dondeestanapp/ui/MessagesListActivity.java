package com.dondeestanapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dondeestanapp.R;
import com.dondeestanapp.api.model.MessageChat;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesListActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextView tv_name;
    private RecyclerView rv_messages;
    private EditText et_message;
    private Button btn_send_message;

    private AdapterMessage adapterMessage;

    //private FirebaseDatabase firebaseDatabase;
    //private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        profilePicture = findViewById(R.id.profile_picture);
        tv_name = findViewById(R.id.tv_name);
        rv_messages = (RecyclerView) findViewById(R.id.rv_messages);
        et_message = findViewById(R.id.et_message);
        btn_send_message = findViewById(R.id.btn_send_message);



        adapterMessage = new AdapterMessage(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_messages.setLayoutManager(linearLayoutManager);
        rv_messages.setAdapter(adapterMessage);

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat;
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String currentDateandTime = simpleDateFormat.format(new Date());
                adapterMessage.addMessage(new MessageChat(
                                et_message.getText().toString(),
                                tv_name.getText().toString(),
                                "",
                                currentDateandTime
                        )
                );
                et_message.setText("");
            }
        });

        adapterMessage.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setTopScrollbar();
            }
        });

    }

    private void setTopScrollbar() {
        rv_messages.scrollToPosition(adapterMessage.getItemCount() - 1);
    }
}
