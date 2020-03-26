package com.shukhrat.wbpvp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.appus.splash.Splash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shukhrat.wbpvp.admin.AdminActivity;
import com.shukhrat.wbpvp.authentification.EnterPhoneNumber;


public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;

    private TextView mUID;

    private ProgressDialog progressDialog;




    /* * * Permission Read External Storage*/
    static final Integer READ_STORAGE_PERMISSION_REQUEST_CODE=0x3;
    /* * * Permission Read External Storage*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        splash.setSplashImage(getResources().getDrawable(R.drawable.title_logo));
        splash.perform();*/

        mAuth = FirebaseAuth.getInstance();

        mUID = (TextView)findViewById(R.id.user_uid);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_feedback, R.id.nav_admin,R.id.nav_dashboard, R.id.id_facebook, R.id.id_telegram)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.id_facebook){
                    facebook_open();
                }else if (destination.getId() == R.id.id_telegram){
                    telegram_open();
                }else if (destination.getId() == R.id.nav_admin){
                    onCreateDialog();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
            return;
        }



    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
            } else {
                // User refused to grant permission.
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.user_uid);
            navUsername.setText("ID: "+currentUser.getUid());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent intent = new Intent(this, PostActivity.class);
                startActivity(intent);
            } else{
                Intent intent = new Intent(this, EnterPhoneNumber.class);
                startActivity(intent);
            }

        }
        if(item.getItemId() == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void facebook_open()
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/%D0%9F%D1%80%D0%BE%D0%B5%D0%BA%D1%82-%D0%91%D0%BB%D0%B0%D0%B3%D0%BE%D1%83%D1%81%D1%82%D1%80%D0%BE%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5-%D1%81%D0%B5%D0%BB%D0%B0-%D0%A3%D0%B7%D0%B1%D0%B5%D0%BA%D0%B8%D1%81%D1%82%D0%B0%D0%BD%D0%B0-%D0%BF%D1%80%D0%B8-%D0%9C%D0%AD%D0%9F-%D0%A0%D0%A3%D0%B7-727457484339084/"));
        startActivity(browserIntent);
    }
    public void telegram_open(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/obodqishloqchannel"));
        startActivity(browserIntent);
    }

    public void onCreateDialog() {
        progressDialog.setMessage("Authentification...");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_signin, null);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                        if(TextUtils.isEmpty(username.getText().toString())){
                            return;
                        }
                        if(TextUtils.isEmpty(password.getText().toString())){
                            return;
                        }

                        //Authentication
                        mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                             .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        progressDialog.cancel();
                                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                        startActivity(intent);
                                    }
                                })
                             .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Failure",Toast.LENGTH_LONG).show();
                                Log.d("MainActivity", "onFailure: "+e.toString());
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}