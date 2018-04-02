package com.hackathon.cyberblue.crimemapping;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;



public class ProfileFragment extends Fragment {
    View v;
    EditText email,pass;
    RelativeLayout r;
    Button b;
    TextView t;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile,container,false);
        email=(EditText)v.findViewById(R.id.email);
        pass=(EditText)v.findViewById(R.id.password);
        t=(TextView)v.findViewById(R.id.textView);
        b=(Button)v.findViewById(R.id.login);
        return v;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth f=FirebaseAuth.getInstance();
        if(f.getCurrentUser()!=null)
        {
            Intent i=new Intent(getContext(),profile.class);
            startActivity(i);
        }
    }
    public void login(View v)
    {
        String e=email.getText().toString();
        String p=pass.getText().toString();
        if(TextUtils.isEmpty(e))
        {
            Toast.makeText(getContext(),"Plz Fill the mail id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(p))
        {
            Toast.makeText(getContext(),"Plz Fill Password Field", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progess=new ProgressDialog(getContext());
        progess.setMessage("Logining in...");
        progess.show();
        FirebaseAuth f=FirebaseAuth.getInstance();
        f.signInWithEmailAndPassword(e, p).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                progess.dismiss();
                if (task.isSuccessful()) {
                    //new Acitivity

                    Intent i = new Intent(getContext(), profile.class);
                    startActivity(i);

                } else {

                    Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
