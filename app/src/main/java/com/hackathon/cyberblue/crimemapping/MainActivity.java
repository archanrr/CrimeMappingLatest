package com.hackathon.cyberblue.crimemapping;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.hackathon.cyberblue.crimemapping.R.id.bottom;
import static com.hackathon.cyberblue.crimemapping.R.id.transition_transform;

public class MainActivity extends AppCompatActivity  {
    BlankFragment b;
    MapFragment m;
    ProfileFragment p;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = new BlankFragment();
        m = new MapFragment();
        p = new ProfileFragment();
        FragmentTransaction f = getSupportFragmentManager().beginTransaction();
        f.add(R.id.frame_container,b);
        f.add(R.id.frame_container,m);
        f.add(R.id.frame_container,p);
        f.show(b);
        f.hide(m);
        f.hide(p);
        f.commit();
        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Crime");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_crimemap: {
                    toolbar.setTitle("Crime");
                    getSupportFragmentManager().beginTransaction().show(b).commit();
                    getSupportFragmentManager().beginTransaction().hide(m).commit();
                    getSupportFragmentManager().beginTransaction().hide(p).commit();

                    return true;
                }
                    case R.id.navigation_safemap:
                    toolbar.setTitle("Safe Map");
                        getSupportFragmentManager().beginTransaction().hide(b).commit();
                        getSupportFragmentManager().beginTransaction().show(m).commit();
                        getSupportFragmentManager().beginTransaction().hide(p).commit();
                       // Intent i=new Intent(MainActivity.this,MapsActivity.class);

                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    getSupportFragmentManager().beginTransaction().hide(b).commit();
                    getSupportFragmentManager().beginTransaction().hide(m).commit();

                    getSupportFragmentManager().beginTransaction().show(p).commit();
                    return true;

            }
            return false;
        }
    };

    /*public void add1(View view) {
        Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_SHORT).show();
    }*/
    public void open(View v)
    {
        startActivity(new Intent(getApplicationContext(), register.class));
    }
    public void login(View v)
    {
        EditText email=(EditText)v.findViewById(R.id.email);
        EditText pass=(EditText)v.findViewById(R.id.password);
        String e=email.getText().toString();
        String p=pass.getText().toString();
        if(TextUtils.isEmpty(e))
        {
            Toast.makeText(getApplicationContext(),"Plz Fill the mail id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(p))
        {
            Toast.makeText(getApplicationContext(),"Plz Fill Password Field", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progess=new ProgressDialog(getApplicationContext());
        progess.setMessage("Logining in...");
        progess.show();
        FirebaseAuth f=FirebaseAuth.getInstance();
        f.signInWithEmailAndPassword(e, p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                progess.dismiss();
                if (task.isSuccessful()) {
                    //new Acitivity

                    Intent i = new Intent(getApplicationContext(), profile.class);
                    startActivity(i);

                } else {

                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}