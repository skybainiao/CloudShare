package com.example.cloudshare.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cloudshare.Model.User;
import com.example.cloudshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private ImageView back;
    private Button signUp;
    private EditText username;
    private EditText password1;
    private EditText password2;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference=firebaseDatabase.getReference();


        signUp=findViewById(R.id.button60);
        back=findViewById(R.id.imageView14);
        username=findViewById(R.id.editTextTextPersonName3);
        password1=findViewById(R.id.editTextTextPassword2);
        password2=findViewById(R.id.editTextTextPassword3);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp(new User(username.getText().toString(),password1.getText().toString(),null));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }




    public void SignUp(User user){
        if (!username.getText().toString().equals("") && !password1.getText().toString().equals("") && !password2.getText().toString().equals("")){
            if (password1.getText().toString().equals(password2.getText().toString())){
                databaseReference.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        System.out.println(task.getResult().hasChild("chen"));
                        if (!task.getResult().hasChild(username.getText().toString())){
                            databaseReference.child("Users").child(user.getUsername()).setValue(user);
                            Toast.makeText(getApplicationContext(),"Register Success", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Username already exists", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(),"Please enter the same password", Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(getApplicationContext(),"Username or password is empty", Toast.LENGTH_LONG).show();
        }

    }







}
