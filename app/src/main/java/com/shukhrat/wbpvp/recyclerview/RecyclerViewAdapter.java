package com.shukhrat.wbpvp.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shukhrat.wbpvp.R;
import com.shukhrat.wbpvp.database.DatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> locations = new ArrayList<>();
    private ArrayList<Boolean> status_states = new ArrayList<>();
    private ArrayList<Boolean> anonymous_states = new ArrayList<>();

    private OnNoteListener mOnNoteListener;

    private Context mContext;



    DatabaseHelper mDatabaseHelper;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<String> images, ArrayList<String> dates, ArrayList<String> locations, ArrayList<Boolean> status_states, ArrayList<Boolean> anonymous_states, OnNoteListener onNoteListener) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.images = images;
        this.dates = dates;
        this.locations = locations;
        this.status_states = status_states;
        this.anonymous_states = anonymous_states;
        this.mContext = mContext;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row, parent, false);
        ViewHolder holder = new ViewHolder(view, mOnNoteListener);

        mDatabaseHelper = new DatabaseHelper(view.getContext());

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final File imgFile = new  File(images.get(position));
        //Upload image
        if(imgFile.exists()) {
            Picasso.get()
                    .load(imgFile)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(holder.postImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(imgFile)
                                    .fit()
                                    .centerCrop()
                                    .into(holder.postImage);
                        }
                    });
        }

        holder.postTitle.setText(titles.get(position));
        holder.postDescription.setText(descriptions.get(position));
        holder.postLocation.setText(locations.get(position));
        holder.postDate.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView postImage;
        TextView postTitle;
        TextView postDescription;
        TextView postLocation;
        TextView postDate;
        CardView parentLayout;

        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull final View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postLocation = itemView.findViewById(R.id.post_location);
            postDate = itemView.findViewById(R.id.post_date);
            parentLayout = itemView.findViewById(R.id.blog_id);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //Setting message manually and performing action on button click
                    builder.setMessage("What do you want to do with this feedback?")
                            .setCancelable(true)
                            .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //finish();

                                    //Toast.makeText(itemView.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    //Toast.makeText(itemView.getContext(), images.get(getAdapterPosition()), Toast.LENGTH_LONG).show();

                                    Cursor data = mDatabaseHelper.getItemID(images.get(getAdapterPosition()));
                                    int itemID = -1;
                                    while (data.moveToNext()){
                                        itemID = data.getInt(0);
                                    }

                                    if(itemID>-1){
                                        //Toast.makeText(itemView.getContext(), String.valueOf(itemID), Toast.LENGTH_LONG).show();



                                        mDatabaseHelper.DeleteRow(itemID);
                                    }
                                    else{
                                        Toast.makeText(itemView.getContext(), "No id associated with that feedback", Toast.LENGTH_LONG).show();
                                    }

                                    //dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Choose action!");
                    alert.show();
                }
            });*/


        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
