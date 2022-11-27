package com.example.cloudshare.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudshare.MainActivity;
import com.example.cloudshare.Model.User;
import com.example.cloudshare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private TextView tx51;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText username;
    private EditText password;
    private Button signIn;
    private ProgressBar progressBar;
    private int num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference=firebaseDatabase.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                num=(int) snapshot.child("Restaurant").getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        signUp=findViewById(R.id.textView53);
        username=findViewById(R.id.editTextTextPersonName2);
        password=findViewById(R.id.editTextTextPassword);
        signIn=findViewById(R.id.button59);
        progressBar=findViewById(R.id.progressBar);
        tx51=findViewById(R.id.textView51);

        tx51.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("num", num);
                startActivity(intent);
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!username.getText().toString().equals("") && !password.getText().toString().equals("")){
                            if (snapshot.child("Users").child(username.getText().toString()).child("password").getValue().toString().equals(password.getText().toString())){
                                progressBar.setVisibility(View.VISIBLE);
                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", username.getText().toString());
                                intent.putExtra("num", num);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"用户名或密码不正确", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"用户名或密码为空", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });




    }
}