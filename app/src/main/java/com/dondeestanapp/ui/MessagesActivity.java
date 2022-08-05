package com.dondeestanapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dondeestanapp.R;
import com.dondeestanapp.model.MessageChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    private Integer userId;
    private String userType;
    private String driverPrivacyKey;
    private String name;
    private String lastName;

    private CircleImageView profilePicture;
    private TextView tv_name;
    private RecyclerView rv_messages;
    private EditText et_message;
    private Button btn_send_message;

    private AdapterMessage adapterMessage;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        userId = getIntent().getIntExtra("userId", 0);
        userType = getIntent().getStringExtra("userType");
        driverPrivacyKey = getIntent().getStringExtra("privacyKey");
        name = getIntent().getStringExtra("name");
        lastName = getIntent().getStringExtra("lastName");

        profilePicture = findViewById(R.id.profile_picture);
        tv_name = findViewById(R.id.tv_name);
        rv_messages = (RecyclerView) findViewById(R.id.rv_messages);
        et_message = findViewById(R.id.et_message);
        btn_send_message = findViewById(R.id.btn_send_message);

        firebaseDatabase = FirebaseDatabase.getInstance();

        String[] chatRoom = driverPrivacyKey.split("\\.");
        databaseReference = firebaseDatabase.getReference(chatRoom[0]);

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

                String nick = name + " " + lastName;
                if (userType.equals("observee")) {
                    nick = nick + " - CHOFER";
                }
                databaseReference.push().setValue(new MessageChat(
                                et_message.getText().toString(),
                                nick,
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

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageChat messageChat = snapshot.getValue(MessageChat.class);
                adapterMessage.addMessage(messageChat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTopScrollbar() {
        rv_messages.scrollToPosition(adapterMessage.getItemCount() - 1);
    }
}
