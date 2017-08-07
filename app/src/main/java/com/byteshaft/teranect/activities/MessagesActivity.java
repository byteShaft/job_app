package com.byteshaft.teranect.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.teranect.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView title;
    private Toolbar toolbarTop;
    private ImageButton backButton;
    private ArrayList<String[]> messages;
    private ListView messageListView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        messages = new ArrayList<>();
        messages.add(new String[]{"", "Bilal", "Are you free next week for inter view?", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        messages.add(new String[]{"", "Bilal", "Hello world", "12:00"});

        adapter = new Adapter(getApplicationContext(), messages);

        messageListView = (ListView) findViewById(R.id.message_list);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        title = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        backButton = (ImageButton) toolbarTop.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
        title.setText(R.string.message_title);
        messageListView.setAdapter(adapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Snackbar.make(findViewById(android.R.id.content), "clicked", 1000).show();
                startActivity(new Intent(MessagesActivity.this, Conversation.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
        }
    }


    private class Adapter extends ArrayAdapter<String> {

        private ArrayList<String[]> messagesList;
        private ViewHolder viewHolder;

        public Adapter(Context context, ArrayList<String[]> messagesList) {
            super(context, R.layout.delegate_message_list);
            this.messagesList = messagesList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_message_list,
                        parent, false);
                viewHolder = new ViewHolder();

                viewHolder.userImage = (CircleImageView) convertView.findViewById(R.id.user_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                viewHolder.messageTime = (TextView) convertView.findViewById(R.id.time_and_date);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // set
            return convertView;
        }

        @Override
        public int getCount() {
            return messagesList.size();
        }
    }


    private class ViewHolder {
        TextView name;
        TextView messageBody;
        TextView messageTime;
        CircleImageView userImage;
    }
}
