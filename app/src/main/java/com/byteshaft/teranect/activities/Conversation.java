package com.byteshaft.teranect.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.serializer.ChatModel;
import com.byteshaft.teranect.utils.CircleTransform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.byteshaft.teranect.R.id.conversation;


public class Conversation extends AppCompatActivity implements View.OnClickListener {


    private TextView title;
    private Toolbar toolbarTop;
    private ImageButton backButton;
    private ArrayList<ChatModel> chatModels;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        chatModels = new ArrayList<>();
        overridePendingTransition(R.anim.enter, R.anim.exit);
        toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        title = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        backButton = (ImageButton) toolbarTop.findViewById(R.id.back_button);
        recyclerView = (RecyclerView) findViewById(conversation);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.canScrollVertically(1);
        recyclerView.setHasFixedSize(true);
        backButton.setOnClickListener(this);
        title.setText(R.string.message_title);
        ChatModel chatModel = new ChatModel();
        chatModel.setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                " Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                " when an unknown printer took a galley of type and scrambled it to make a " +
                "type specimen book. It has survived not only five centuries, but also the " +
                "leap into electronic typesetting, remaining essentially unchanged. It was " +
                "popularised in the 1960s with the release of Letraset sheets containing Lorem " +
                "Ipsum passages, and more recently with desktop publishing software like Aldus " +
                "PageMaker including versions of Lorem Ipsum.");
        chatModel.setTimeStamp(String.valueOf(new Date().getTime()));
        chatModel.setId(1);
        chatModels.add(chatModel);
        ChatAdapter chatAdapter = new ChatAdapter(chatModels);
        recyclerView.setAdapter(chatAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyChatViewHolder> {


        private static final int RIGHT_MSG = 0;
        private static final int LEFT_MSG = 1;
        private ArrayList<ChatModel> modelArrayList;


        public ChatAdapter(ArrayList<ChatModel> modelArrayList) {
            super();
            this.modelArrayList = modelArrayList;
        }

        @Override
        public int getItemCount() {
            return modelArrayList.size();
        }

        @Override
        public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == RIGHT_MSG) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
                return new MyChatViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
                return new MyChatViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(MyChatViewHolder holder, int position) {
            ChatModel chatModel = modelArrayList.get(position);
            holder.setTxtMessage(chatModel.getMessage());
            holder.setTvTimestamp(chatModel.getTimeStamp());
        }


        @Override
        public int getItemViewType(int position) {
            ChatModel model = modelArrayList.get(position);
            if (model.getId() == 0) {
                return RIGHT_MSG;
            } else {
                return LEFT_MSG;
            }
        }

        public class MyChatViewHolder extends RecyclerView.ViewHolder {

            TextView tvTimestamp;
            EmojiconTextView txtMessage;
            ImageView profilePhoto;

            public MyChatViewHolder(View itemView) {
                super(itemView);
                tvTimestamp = (TextView) itemView.findViewById(R.id.timestamp);
                txtMessage = (EmojiconTextView) itemView.findViewById(R.id.txtMessage);
                profilePhoto = (ImageView) itemView.findViewById(R.id.ivUserChat);
            }


            public void setTxtMessage(String message) {
                if (txtMessage == null) return;
                txtMessage.setText(message);
            }

            public void setProfilePhoto(final String urlPhotoUser) {
                if (profilePhoto == null) return;
                Glide.with(profilePhoto.getContext()).load(urlPhotoUser).centerCrop()
                        .transform(new CircleTransform(profilePhoto.getContext())).override(60, 60)
                        .into(profilePhoto);
            }

            public String getDate(String createdAt) {
                // Create a DateFormatter object for displaying date in specified format.
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
                formatter.setTimeZone(TimeZone.getTimeZone("GMT +05:00"));
                Date date = null;
                try {
                    date = formatter.parse(createdAt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return String.valueOf(date.getTime());
            }

            public void setTvTimestamp(String timestamp) {
                if (tvTimestamp == null) return;
                tvTimestamp.setText(" "+converteTimestamp(timestamp));
            }
        }

        private CharSequence converteTimestamp(String mileSegundos) {
            return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        }
    }
}
