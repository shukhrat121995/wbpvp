package com.shukhrat.wbpvp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.shukhrat.wbpvp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AdminEdit extends AppCompatActivity {

    String title, description, image, location, date = null;

    ImageView imageView;
    TextView feedback_title;
    TextView feedback_description;
    TextView feedback_location;
    TextView feedback_date;
    EditText admin_reply;
    MaterialButton admin_reject;
    MaterialButton admin_verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);
        setTitle("Verify Post");

        //initialize views for activity
        Initialization();

        //retrieve data from AdminActivity
        Bundle b = getIntent().getExtras();
        if(b != null){
            title = b.getString("title");
            description = b.getString("description");
            image = b.getString("image");
            location = b.getString("location");
            date = b.getString("date");
            CreateView();
        }
    }

    public void Initialization(){
        imageView = (ImageView)findViewById(R.id.admin_imageView);
        feedback_title =(TextView)findViewById(R.id.admin_feedbackTitle);
        feedback_description =(TextView)findViewById(R.id.admin_feedbackDescription);
        feedback_location =(TextView)findViewById(R.id.admin_feedbackLocation);
        feedback_date =(TextView)findViewById(R.id.admin_feedbackDate);
        admin_reply =(EditText) findViewById(R.id.admin_reply);
        admin_reject = (MaterialButton)findViewById(R.id.admin_reject);
        admin_verify = (MaterialButton)findViewById(R.id.admin_verify);
    }

    public void CreateView(){
        //imageView
        Picasso.get()
                .load(image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit()
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(image)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                    }
                });

        //TextViews
        feedback_title.setText(title);
        feedback_description.setText(description);
        feedback_location.setText(location);
        feedback_date.setText(date);
    }
}
