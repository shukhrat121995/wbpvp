package com.piu.ridp;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.piu.ridp.admin.AdminActivity;
import com.piu.ridp.authentification.EnterPhoneNumber;
import com.piu.ridp.language.BaseActivity;
import com.piu.ridp.language.LocaleManager;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends BaseActivity {

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

        mAuth = FirebaseAuth.getInstance();

        mUID = (TextView)findViewById(R.id.user_uid);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_main, R.id.nav_feedback, R.id.nav_admin,R.id.nav_dashboard, R.id.id_facebook, R.id.id_telegram, R.id.id_twitter, R.id.id_share, R.id.logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.id_facebook){
                    web_open("https://www.facebook.com/%D0%9F%D1%80%D0%BE%D0%B5%D0%BA%D1%82-%D0%91%D0%BB%D0%B0%D0%B3%D0%BE%D1%83%D1%81%D1%82%D1%80%D0%BE%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5-%D1%81%D0%B5%D0%BB%D0%B0-%D0%A3%D0%B7%D0%B1%D0%B5%D0%BA%D0%B8%D1%81%D1%82%D0%B0%D0%BD%D0%B0-%D0%BF%D1%80%D0%B8-%D0%9C%D0%AD%D0%9F-%D0%A0%D0%A3%D0%B7-727457484339084/");
                }else if (destination.getId() == R.id.id_telegram){
                    web_open("https://t.me/obodqishloqchannel");
                }else if (destination.getId() == R.id.nav_admin){
                    onCreateDialog();
                }else if(destination.getId()==R.id.id_twitter){
                    web_open("https://twitter.com/pvp24032020");
                }else if(destination.getId()==R.id.id_share){
                    ShareAppUrl();
                }else if(destination.getId()==R.id.logout){
                    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().signOut();
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    } else{
                        Intent intent = new Intent(MainActivity.this, EnterPhoneNumber.class);
                        startActivity(intent);
                    }
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
                Toast.makeText(this, R.string.permission_message, Toast.LENGTH_LONG).show();
            } else {
                // User refused to grant permission.
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Menu nv = navigationView.getMenu();
        MenuItem item = nv.findItem(R.id.logout);
        TextView navUsername = (TextView) headerView.findViewById(R.id.user_uid);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){


            navUsername.setText("ID: "+currentUser.getUid());
            item.setTitle(getString(R.string.logout));

        }else{
            navUsername.setText("ID: null");
            item.setTitle(getString(R.string.sign_in));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        return super.onPrepareOptionsMenu(menu);
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
        /*if(item.getItemId() == R.id.action_sign_out){

        }else */if(item.getItemId() == R.id.language_en){
            setNewLocale(this, LocaleManager.ENGLISH);

        }else if(item.getItemId() == R.id.language_ru){
            setNewLocale(this, LocaleManager.RUSSIAN);

        }else if(item.getItemId() == R.id.language_uz){
            setNewLocale(this, LocaleManager.UZBEK);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void web_open(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void ShareAppUrl(){
        ShareCompat.IntentBuilder.from(MainActivity.this)
                .setType("text/plain")
                .setChooserTitle("Chooser title")
                .setText("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())
                .startChooser();
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
        builder.setView(view);
        builder.setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                progressDialog.show();
                if (TextUtils.isEmpty(username.getText().toString())) {
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, R.string.failure,Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    progressDialog.cancel();
                    Toast.makeText(MainActivity.this, R.string.failure,Toast.LENGTH_LONG).show();
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
                                Toast.makeText(MainActivity.this, R.string.failure,Toast.LENGTH_LONG).show();
                                Log.d("MainActivity", "onFailure: "+e.toString());
                            }
                            });

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });// Add action buttons
        builder.show();
    }

    @Override
    public void onBackPressed() {
        final SweetAlertDialog alertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
        alertDialog
                .setTitleText(getString(R.string.are_you_sure_you_want_to_exit_application))
                .setConfirmText(getString(R.string.exit))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setCancelButton(getString(R.string.cancel), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        alertDialog.dismissWithAnimation();
                    }
                })
                .show();
        //Exit
        Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
        btn.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.alertDialogDelete));
        btn.setTextSize(14);
        //Cancel
        Button btnDelete = (Button) alertDialog.findViewById(R.id.cancel_button);
        btnDelete.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimary));
        btnDelete.setTextSize(14);
    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
