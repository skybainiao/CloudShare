package com.example.cloudshare.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class PicsActivity extends AppCompatActivity {

    private FragmentHomeBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayout l3;
    private LinearLayout l4;
    private FloatingActionButton floatingActionButton;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();
    private long num;
    private int PICK_IMAGE_REQUEST = 111;
    private String username;
    private String folderName;
    private Uri filePath;
    private ArrayList<ImageView> imageViews;

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
        l3=findViewById(R.id.l3);
        l4=findViewById(R.id.l4);
        imageViews=new ArrayList<>();
        floatingActionButton=findViewById(R.id.floatingActionButton3);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        num=2;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                num= snapshot.child("Users").child(username).child(folderName).getChildrenCount();
                System.out.println(num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                Toast.makeText(getApplicationContext(),"Picture",Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageViews.add(imageView);

            if (isOdd(num)){
                l4.addView(imageView);
            }
            else {
                l3.addView(imageView);
            }



        }

        StorageReference listRef = storage.getReference().child(username).child(folderName);

        System.out.println("wwwwwwwwwwwwwwwwwwwww");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {

                        }
                        for (StorageReference item : listResult.getItems()) {
                            System.out.println(item.getPath());
                            item.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    for (int i = 0; i < imageViews.size(); i++) {
                                        System.out.println("this is imgs!!!!!!!!!!!!!!!!!!!!");
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        imageViews.get(i).setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });



    }




    public boolean isOdd(long a){
        if((a&1) != 1){
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            if(filePath != null) {
                System.out.println("pppppppppppppp"+getEntryName(filePath.getPath()));
                StorageReference childRef = storageReference.child(username+"/"+folderName+"/"+getEntryName(filePath.getPath()));

                UploadTask uploadTask = childRef.putFile(filePath);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_SHORT).show();
            }

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);

                //imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public static String getEntryName(String picturePath) {
        if (!TextUtils.isEmpty(picturePath)) {
            int start = picturePath.lastIndexOf("/");
            String format = "";
            if (start < 0) {
                format = picturePath;
            } else {
                format = picturePath.substring(start + 1);
            }
            return format;
        }
        return "";
    }


}