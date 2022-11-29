package com.example.cloudshare.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PicsActivity extends AppCompatActivity {

    private FragmentHomeBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayout l3;
    private LinearLayout l4;
    private FloatingActionButton floatingActionButton;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();;
    private int num;
    private String username;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics);

        Intent getIntent = getIntent();
        username = getIntent.getStringExtra("username");
        folderName = getIntent.getStringExtra("folderName");
        System.out.println(username);
        System.out.println(folderName);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        num=0;


        l3=findViewById(R.id.l3);
        l4=findViewById(R.id.l4);
        floatingActionButton=findViewById(R.id.floatingActionButton3);


    }
}