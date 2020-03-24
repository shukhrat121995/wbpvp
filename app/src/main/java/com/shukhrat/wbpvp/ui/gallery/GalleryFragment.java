package com.shukhrat.wbpvp.ui.gallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shukhrat.wbpvp.MainActivity;
import com.shukhrat.wbpvp.PostActivity;
import com.shukhrat.wbpvp.R;
import com.shukhrat.wbpvp.database.DatabaseHelper;
import com.shukhrat.wbpvp.recyclerview.RecyclerViewAdapter;

import java.io.File;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GalleryFragment extends Fragment implements RecyclerViewAdapter.OnNoteListener {

    private GalleryViewModel galleryViewModel;

    private static final String TAG = "GalleryFragment";

    DatabaseHelper mDatabaseHelper;

    /* * * Alert Dialog * * */
    AlertDialog.Builder builder;
    /* * * Alert Dialog * * */

    public ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> locations = new ArrayList<>();
    private ArrayList<Boolean> status_states = new ArrayList<>();
    private ArrayList<Boolean> anonymous_states = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerViewAdapter adapter;

    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = root.findViewById(R.id.offline_blog_list);
        mDatabaseHelper = new DatabaseHelper(getContext());
        mStorage = FirebaseStorage.getInstance().getReference("Feedback_images");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");
        builder = new AlertDialog.Builder(getContext());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        InitializeArrayLists();
        initRecyclerView();

        return root;
    }

    public void InitializeArrayLists(){
        Cursor data = mDatabaseHelper.getData();
        while(data.moveToNext()){
            titles.add(data.getString(1));
            descriptions.add(data.getString(2));
            images.add(data.getString(3));
            locations.add(data.getString(5));
            dates.add(data.getString(6));
            status_states.add(Boolean.valueOf(data.getString(7)));
            anonymous_states.add(Boolean.valueOf(data.getString(8)));

        }
    }

    private void initRecyclerView(){
        adapter = new RecyclerViewAdapter(getContext(), titles, descriptions, images, dates, locations, status_states, anonymous_states, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void onNoteClick(int position) {
        //Toast.makeText(getContext(), titles.get(position), Toast.LENGTH_LONG).show();
        AlertDialog(position);
    }

    public void AlertDialog(final int position){
        builder.setMessage("What do you want to do with this feedback?")
                .setCancelable(true)
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        if(isNetworkConnected()) {
                            StartPosting(position);

                        }

                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        //Toast.makeText(itemView.getContext(), images.get(getAdapterPosition()), Toast.LENGTH_LONG).show();

                        Cursor data = mDatabaseHelper.getItemID(images.get(position));
                        int itemID = -1;
                        while (data.moveToNext()){
                            itemID = data.getInt(0);
                        }

                        if(itemID>-1){
                            mDatabaseHelper.DeleteRow(itemID);
                            RemoveFromArrayLists();
                            InitializeArrayLists();
                            adapter.notifyItemRemoved(position);
                        }
                        else{
                            Toast.makeText(getContext(), "No id associated with that feedback", Toast.LENGTH_LONG).show();
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

    private void RemoveFromArrayLists(){
        titles.clear();
        descriptions.clear();
        images.clear();
        locations.clear();
        dates.clear();
        status_states.clear();
        anonymous_states.clear();
    }

    private void StartPosting(final int position){
        progressDialog.setMessage("Posting Feedback...");
        progressDialog.show();
        Uri file = Uri.fromFile(new File(images.get(position)));
        if(file != null)
        {
            StorageReference riversRef = mStorage.child(file.getLastPathSegment());
            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = uri.toString();

                                    Date date = new Date();

                                    String year         = (String) DateFormat.format("yyyy", date); // 2013
                                    String monthString  = (String) DateFormat.format("MMM",  date); // Jun
                                    String day          = (String) DateFormat.format("dd",   date); // 20

                                    DatabaseReference newPost = mDatabase.push();
                                    newPost.child("feedback_title").setValue(titles.get(position));
                                    newPost.child("feedback_description").setValue(descriptions.get(position));
                                    newPost.child("image").setValue(url);
                                    newPost.child("timeStamp").setValue(new Date().getTime());
                                    newPost.child("location").setValue(locations.get(position));
                                    newPost.child("date").setValue(day+"/"+monthString+"/"+year);
                                    newPost.child("status").setValue(false);
                                    newPost.child("anonymous").setValue(anonymous_states.get(position));

                                    newPost.child("status_anonymous").setValue(false+"_"+anonymous_states.get(position));
                                    progressDialog.dismiss();

                                    /*Update Recycler View*/

                                    Cursor data = mDatabaseHelper.getItemID(images.get(position));
                                    int itemID = -1;
                                    while (data.moveToNext()){
                                        itemID = data.getInt(0);
                                    }

                                    if(itemID>-1){
                                        mDatabaseHelper.DeleteRow(itemID);
                                        RemoveFromArrayLists();
                                        InitializeArrayLists();
                                        adapter.notifyItemRemoved(position);
                                        new SweetAlertDialog(getContext())
                                                .setTitleText("Success!")
                                                .show();
                                    }
                                    else{
                                        //Toast.makeText(getContext(), "No id associated with that feedback", Toast.LENGTH_LONG).show();
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText("Something went wrong!")
                                                .show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!")
                                    .show();
                        }
                    });
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
