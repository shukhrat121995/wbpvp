package com.shukhrat.wbpvp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shukhrat.wbpvp.database.DatabaseHelper;
import com.shukhrat.wbpvp.language.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PostActivity extends BaseActivity {

    /* * * SQLite database * * */
    private static final String TAG = "PostActivity";
    DatabaseHelper mDatabaseHelper;
    /* * * SQLite database * * */

    private ImageButton imageButton;
    private static  final int GALLERY_REQUEST = 1;

    private EditText feedback_title, feedback_description;
    private Button sumit_feedback;

    Spinner spinner_region, spinner_district, spinner_village, spinner_gender;

    ArrayAdapter<String> arrayAdapter_Regions;
    ArrayAdapter<String> arrayAdapter_Districts;
    ArrayAdapter<String> arrayAdapter_Vilalges;
    ArrayAdapter<String> arrayAdapter_Gender;

    ArrayList<String> arrayList_Regions;
    ArrayList<String> arrayList_Districts;
    ArrayList<String> arrayList_Villages;
    ArrayList<String> arrayList_Gender;

    private Uri imageUri = null;
    Bitmap bitmap = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;

    private String reg, dis, vil = null;

    private String gender = null;

    private String absolute_path = null;

    private Switch anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle(getString(R.string.post_feedback));

        mStorage = FirebaseStorage.getInstance().getReference("Feedback_images");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback");

        mDatabase.keepSynced(true);

        mDatabaseHelper = new DatabaseHelper(this);

        imageButton = (ImageButton)findViewById(R.id.select_image);

        feedback_title = (EditText)findViewById(R.id.feedback_title);
        feedback_description = (EditText)findViewById(R.id.feedback_description);

        sumit_feedback = (Button)findViewById(R.id.submit_feedback);

        anonymous = (Switch)findViewById(R.id.switch_anonymous);


        spinner_region = (Spinner)findViewById(R.id.spinner_region);
        spinner_district = (Spinner)findViewById(R.id.spinner_district);
        spinner_village = (Spinner)findViewById(R.id.spinner_village);
        spinner_gender = (Spinner)findViewById(R.id.spinner_gender);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //* * * Spinner Regions start * * *
        arrayList_Regions = new ArrayList<>();
        arrayList_Regions.add(0, getString(R.string.select_region));
        arrayList_Regions.add(getString(R.string.andijan));
        arrayList_Regions.add(getString(R.string.jizzakh));
        arrayList_Regions.add(getString(R.string.namangan));
        arrayList_Regions.add(getString(R.string.syrdarya));
        arrayList_Regions.add(getString(R.string.fergana));


        arrayAdapter_Regions = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arrayList_Regions);
        spinner_region.setAdapter(arrayAdapter_Regions);

        arrayList_Districts = new ArrayList<>();
        arrayList_Districts.add(0, getString(R.string.select_district));

        spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    arrayList_Districts.clear();
                    arrayList_Districts.add(0, getString(R.string.select_district));
                }
                else {
                    Toast.makeText(PostActivity.this, arrayList_Regions.get(position), Toast.LENGTH_SHORT).show();
                    reg = arrayList_Regions.get(position);

                    if(reg.equals(getString(R.string.andijan))){
                        arrayList_Districts.clear();
                        arrayList_Districts.add(0, getString(R.string.select_district));
                        arrayList_Districts.add(getString(R.string.bulakbashi));
                        arrayList_Districts.add(getString(R.string.paxtaobod));
                        arrayList_Districts.add(getString(R.string.markhamat));
                        arrayList_Districts.add(getString(R.string.ulugnar));
                        arrayList_Districts.add(getString(R.string.boz));
                    }else if(reg.equals(getString(R.string.jizzakh))){
                        arrayList_Districts.clear();
                        arrayList_Districts.add(0, getString(R.string.select_district));
                        arrayList_Districts.add(getString(R.string.bakhmal));
                        arrayList_Districts.add(getString(R.string.zomin));
                        arrayList_Districts.add(getString(R.string.forish));
                        arrayList_Districts.add(getString(R.string.yangiobod));
                    }else if(reg.equals(getString(R.string.namangan))){
                        arrayList_Districts.clear();
                        arrayList_Districts.add(0, getString(R.string.select_district));
                        arrayList_Districts.add(getString(R.string.minbulak));
                        arrayList_Districts.add(getString(R.string.pop));
                        arrayList_Districts.add(getString(R.string.chartak));
                        arrayList_Districts.add(getString(R.string.chust));
                        arrayList_Districts.add(getString(R.string.yangikurgan));
                    }else if(reg.equals(getString(R.string.syrdarya))){
                        arrayList_Districts.clear();
                        arrayList_Districts.add(0, getString(R.string.select_district));
                        arrayList_Districts.add(getString(R.string.boevut));
                        arrayList_Districts.add(getString(R.string.sardoba));
                        arrayList_Districts.add(getString(R.string.hovos));
                    }else if(reg.equals(getString(R.string.fergana))){
                        arrayList_Districts.clear();
                        arrayList_Districts.add(0, getString(R.string.select_district));
                        arrayList_Districts.add(getString(R.string.sokh));
                        arrayList_Districts.add(getString(R.string.furqat));
                        arrayList_Districts.add(getString(R.string.yazyavan));
                        arrayList_Districts.add(getString(R.string.kushtepa));
                    }
                }
                arrayAdapter_Districts = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,arrayList_Districts);
                spinner_district.setAdapter(arrayAdapter_Districts);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //* * * Spinner Regions end * * *

        //* * * Spinner Districts start * * *
        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                }
                else {
                    Toast.makeText(PostActivity.this, arrayList_Districts.get(position), Toast.LENGTH_SHORT).show();
                    dis = arrayList_Districts.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //* * * Spinner District ends * * *

        //* * * Spinner Villages start * * *
        arrayList_Villages = new ArrayList<>();
        arrayList_Villages.add(0,getString(R.string.select_village));
        arrayList_Villages.add(getString(R.string.sokhil_mfy));


        arrayAdapter_Vilalges = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,arrayList_Villages);
        spinner_village.setAdapter(arrayAdapter_Vilalges);

        spinner_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                }
                else {
                    Toast.makeText(PostActivity.this, arrayList_Villages.get(position), Toast.LENGTH_SHORT).show();
                    vil = arrayList_Villages.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //* * * Spinner Villages end * * *

        //* * * Spinner Gender start * * *
        arrayList_Gender = new ArrayList<>();
        arrayList_Gender.add(0, "Gender");
        arrayList_Gender.add("Male");
        arrayList_Gender.add("Female");

        arrayAdapter_Gender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Gender);
        spinner_gender.setAdapter(arrayAdapter_Gender);

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                }
                else {
                    Toast.makeText(PostActivity.this, arrayList_Gender.get(position), Toast.LENGTH_SHORT).show();
                    gender = arrayList_Gender.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //* * * Spinner Gender end * * *


       imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeeryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeeryIntent.setType("image/*");
                startActivityForResult(galeeryIntent, GALLERY_REQUEST);
            }
        });

       sumit_feedback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(isNetworkConnected())
                   startPosting();
               else{

                   Date date = new Date();

                   String year         = (String) DateFormat.format("yyyy", date); // 2013
                   String monthString  = (String) DateFormat.format("MMM",  date); // Jun
                   String day          = (String) DateFormat.format("dd",   date); // 20

                   //store inside local database SQLite
                   final String title_val = feedback_title.getText().toString().trim();
                   final String description_val = feedback_description.getText().toString().trim();
                   if (!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(description_val)&&imageUri != null && absolute_path != null){
                       PostOfflineData(title_val, description_val, absolute_path, String.valueOf(new Date().getTime()), reg+"/"+dis+"/"+vil, day+"/"+monthString+"/"+year, false, anonymous.isChecked(), false+"_"+anonymous.isChecked(), gender);
                   }
                   else{
                       //Toast.makeText(PostActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                       new SweetAlertDialog(PostActivity.this, SweetAlertDialog.WARNING_TYPE)
                               .setTitleText(getString(R.string.empty_fields))
                               .setContentText(getString(R.string.please_fill_all_fields))
                               .show();
                   }
               }
           }
       });

    }

    private String save(Bitmap bitmap)
    {
        File save_path = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            try
            {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/wbpvp");
                boolean folder = dir.mkdirs();
                if(folder) {
                    File file = new File(dir, "wbpvp_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".JPEG");
                    save_path = file;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    FileOutputStream f = null;
                    f = new FileOutputStream(file);
                    MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
                    if (f != null) {
                        f.write(baos.toByteArray());
                        f.flush();
                        f.close();
                    }
                } else
                {
                    Toast.makeText(PostActivity.this, "folder not created", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
        }
        return String.valueOf(save_path);
    }

    public void PostOfflineData(String title, String description, String image, String timeStamp, String location, String date, Boolean status, Boolean anonymous, String status_anonymous, String gender){
        boolean insertData = mDatabaseHelper.addData(title, description, image, timeStamp, location, date, status, anonymous, status_anonymous, gender);

        if(insertData){
            new SweetAlertDialog(PostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(getString(R.string.success))
                    .setContentText(getString(R.string.feedback_successfully_saved))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                        }
                    })
                    .show();
            //toastMessage("Data successfully stored offline");
        } else{
            new SweetAlertDialog(PostActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.oops))
                    .setContentText(getString(R.string.something_went_wrong))
                    .show();
            //toastMessage("Something went wrong");
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void startPosting(){
        progressDialog.setMessage(getString(R.string.posting_feedback));
        progressDialog.show();
        final String title_val = feedback_title.getText().toString().trim();
        final String description_val = feedback_description.getText().toString().trim();

        if (!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(description_val) && imageUri != null && bitmap != null){
            //Uri file = imageUri;
            StorageReference riversRef = mStorage.child(imageUri.getLastPathSegment());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            riversRef.putBytes(data)
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
                                    newPost.child("feedback_title").setValue(title_val);
                                    newPost.child("feedback_description").setValue(description_val);
                                    newPost.child("image").setValue(url);
                                    newPost.child("timeStamp").setValue(new Date().getTime());
                                    newPost.child("location").setValue(reg+"/"+dis+"/"+vil);
                                    newPost.child("date").setValue(day+"/"+monthString+"/"+year);
                                    newPost.child("status").setValue(false);
                                    newPost.child("gender").setValue(gender);
                                    newPost.child("anonymous").setValue(anonymous.isChecked());
                                    newPost.child("status_anonymous").setValue(false+"_"+anonymous.isChecked());
                                    newPost.child("admin").setValue("new");
                                    newPost.child("admin_reply").setValue("Empty");
                                    progressDialog.dismiss();

                                    new SweetAlertDialog(PostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getString(R.string.success))
                                            .setContentText(getString(R.string.feedback_successfully_uploaded))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                                }
                                            })
                                            .show();
                                }
                            });
                            firebaseUri.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(getApplicationContext(), "CHECK", Toast.LENGTH_LONG).show();
                                    new SweetAlertDialog(PostActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getString(R.string.oops))
                                            .setContentText(getString(R.string.something_went_wrong))
                                            .show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            //Toast.makeText(this, String.valueOf(imageUri), Toast.LENGTH_LONG).show();
            imageButton.setImageURI(imageUri);

            try{
                if(isStoragePermissionGranted())
                {

                    //ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                    //bitmap = ImageDecoder.decodeBitmap(source);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    absolute_path = save(bitmap);

                }
            }catch (Exception ex){
                //Log.d(TAG, "onActivityResult: "+ex);

            }





        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
