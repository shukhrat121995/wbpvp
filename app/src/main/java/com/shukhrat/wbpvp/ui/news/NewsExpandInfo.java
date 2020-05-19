package com.shukhrat.wbpvp.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shukhrat.wbpvp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class NewsExpandInfo extends AppCompatActivity {

    TextView title, description, text, date;
    ImageView imageView;

    String news_title, news_description, news_text, news_date, news_image;

    final String url = "http://obodqishloq.geekart.club/wb/images/news_images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_expand_info);

        Bundle b = getIntent().getExtras();
        if(b != null){
            news_title = b.getString("title");
            news_description = b.getString("description");
            news_text = b.getString("text");
            news_image = b.getString("image");
            news_date = b.getString("date");

            //initialize views for activity
            Initialization();
            CreateView();
        }

        setTitle(news_title);

        imageView .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogNewsImageFullScreen();
            }
        });
    }

    public void Initialization(){
        //user property
        title = (TextView)findViewById(R.id.news_expand_title);
        description = (TextView)findViewById(R.id.news_expand_description);
        text = (TextView)findViewById(R.id.news_expand_text);
        date = (TextView)findViewById(R.id.news_expand_date);
        imageView = (ImageView)findViewById(R.id.news_expand_imageView);

    }

    public void CreateView(){
        //imageView
        final Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso
                .load(url+news_image)
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
                                .load(url+news_image)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                    }
                });

        //TextViews
        title.setText(news_title);
        description.setText(news_description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text.setText(Html.fromHtml(news_text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            text.setText(Html.fromHtml(news_text));
        }

        date.setText(news_date);
    }

    private void alertDialogNewsImageFullScreen(){

        final AlertDialog.Builder alertadd = new AlertDialog.Builder(NewsExpandInfo.this);
        LayoutInflater factory = LayoutInflater.from(NewsExpandInfo.this);

        final View view = factory.inflate(R.layout.news_fullscreen_image, null);
        final ImageView imageViewDialog = view.findViewById(R.id.dialog_imageview);

        final Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso
                .load(url+news_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageViewDialog, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        picasso
                                .load(url+news_image)
                                .into(imageViewDialog);
                    }
                });
        alertadd.setView(view);
        alertadd.show();
    }
}
