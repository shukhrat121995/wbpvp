package com.shukhrat.wbpvp.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shukhrat.wbpvp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class UserExpandInfo extends AppCompatActivity {

    LinearLayout adminContainer;
    TextView admin_reply, admin_reply_date;
    TextView title, description, location, date;

    ImageView userImage;

    String user_title, user_description, user_location, user_post_date, user_image, admin_reply_text, admin_reply_date_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_info);
        setTitle("Feedback Info");
        //retrieve data from AdminActivity
        Bundle b = getIntent().getExtras();
        if(b != null){
            user_title = b.getString("title");
            user_description = b.getString("description");
            user_image = b.getString("image");
            user_location = b.getString("location");
            user_post_date = b.getString("date");
            admin_reply_text = b.getString("admin_reply");
            admin_reply_date_text = b.getString("admin_reply_date");
            //initialize views for activity
            Initialization();
            CreateView();
        }

    }

    public void Initialization(){
        //admin property
        adminContainer = (LinearLayout)findViewById(R.id.admin_container);
        admin_reply = (TextView)findViewById(R.id.admin_reply);
        admin_reply_date = (TextView)findViewById(R.id.admin_reply_date);

        //user property
        title = (TextView)findViewById(R.id.user_feedbackTitle);
        description = (TextView)findViewById(R.id.user_feedbackDescription);
        location = (TextView)findViewById(R.id.user_feedbackLocation);
        date = (TextView)findViewById(R.id.user_feedbackDate);
        userImage = (ImageView)findViewById(R.id.user_imageView);
    }

    public void CreateView(){
        //imageView
        Picasso.get()
                .load(user_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit()
                .centerCrop()
                .into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(user_image)
                                .fit()
                                .centerCrop()
                                .into(userImage);
                    }
                });

        //TextViews
        title.setText(user_title);
        description.setText(user_description);
        location.setText(user_location);
        date.setText(user_post_date);

        if(admin_reply_text.equals("Empty") || admin_reply_text.equals(""))
            adminContainer.setVisibility(View.GONE);
        else{
            admin_reply.setText(admin_reply_text);
            admin_reply_date.setText(admin_reply_date_text);
        }
    }
}
