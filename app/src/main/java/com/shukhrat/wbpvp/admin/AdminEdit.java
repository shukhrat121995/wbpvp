package com.shukhrat.wbpvp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shukhrat.wbpvp.R;
import com.shukhrat.wbpvp.language.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminEdit extends BaseActivity {

    String title, description, image, location, date, feedback_id, admin_reply_text = null;

    Boolean anonymous;

    ImageView imageView;
    TextView feedback_title;
    TextView feedback_description;
    TextView feedback_location;
    TextView feedback_date;
    EditText admin_reply;
    MaterialButton admin_reject;
    MaterialButton admin_verify;

    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);
        setTitle(getString(R.string.verify_post));
        //retrieve data from AdminActivity
        Bundle b = getIntent().getExtras();
        if(b != null){
            title = b.getString("title");
            description = b.getString("description");
            image = b.getString("image");
            location = b.getString("location");
            date = b.getString("date");
            feedback_id = b.getString("feedback_id");
            admin_reply_text = b.getString("admin_reply");
            anonymous = b.getBoolean("anonymous");
            //initialize views for activity
            Initialization();
            CreateView();
        }


        admin_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminEditPost("rejected", false);
            }
        });
        admin_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminEditPost("approved", true);
            }
        });
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback").child(feedback_id);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    public void CreateView(){
        //imageView
        final Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso
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
                        picasso
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

        if(!admin_reply_text.equals("Empty"))
            admin_reply.setText(admin_reply_text);
    }

    public void AdminEditPost(String post_status, final boolean status){
        progressDialog.setMessage(getString(R.string.posting_feedback));
        progressDialog.show();
        Date date = new Date();

        final String year         = (String) DateFormat.format("yyyy", date); // 2013
        final String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        final String day          = (String) DateFormat.format("dd",   date); // 20

        final String reply = admin_reply.getText().toString().trim();

        mDatabase.child("admin").setValue(post_status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("admin_reply").setValue(reply).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.child("status").setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabase.child("status_anonymous").setValue(status+"_"+anonymous).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child("admin_reply_date").setValue(day+"/"+monthString+"/"+year).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.cancel();
                                                new SweetAlertDialog(AdminEdit.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText(getString(R.string.success))
                                                        .show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
