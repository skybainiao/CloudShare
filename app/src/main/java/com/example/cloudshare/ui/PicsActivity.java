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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cloudshare.Model.Pic;
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

import org.w3c.dom.Text;

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
    private long num = 0;
    private int PICK_IMAGE_REQUEST = 111;
    private String username;
    private String folderName;
    private Uri filePath;
    private ArrayList<ImageView> imageViews;
    private StorageReference listRef;
    private final long ONE_MEGABYTE = 1024 * 1024 * 5;

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
        listRef = storage.getReference().child(username).child(folderName);


        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                num=listResult.getItems().size();
                System.out.println("num:"+num);
                for (int i = 0; i < num; i++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setPadding(0,50,0,0);
                    imageView.setAdjustViewBounds(true);
                    imageViews.add(imageView);
                    System.out.println("size:+++++"+imageViews.size());
                    if (isOdd(i)){
                        l3.addView(imageView);
                    }
                    else {
                        l4.addView(imageView);
                    }


                }
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

        loadPics();


    }

    public void loadPics(){
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (int i = 0; i < listResult.getItems().size(); i++) {
                            int finalI = i;
                            System.out.println("finalI:"+finalI);
                            listResult.getItems().get(i).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    System.out.println("this is imgs!!!!!!!!!!!!!!!!!!!!");
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    imageViews.get(finalI).setImageBitmap(bitmap);
                                    imageViews.get(finalI).setTag(bitmap);
                                    imageViews.get(finalI).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ImageView imageView = new ImageView(getApplicationContext());
                                            imageView.setImageBitmap(bitmap);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PicsActivity.this)
                                                    .setTitle("Message")
                                                    .setMessage("what do you want to do with the picture?")
                                                    .setCancelable(true)
                                                    .setView(imageView)
                                                    .setNegativeButton("Share", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            EditText editText = new EditText(PicsActivity.this);
                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(PicsActivity.this)
                                                                    .setTitle("Message")
                                                                    .setMessage("Enter the username of the user you want to share")
                                                                    .setCancelable(true)
                                                                    .setView(editText)
                                                                    .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Pic pic = new Pic(listResult.getItems().get(finalI).getPath(),username,editText.getText().toString());
                                                                            databaseReference.child("ImageTransfer").child(pic.getSender()).setValue(pic);
                                                                            Toast.makeText(PicsActivity.this,"Done",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    })
                                                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            //
                                                                        }
                                                                    });
                                                            builder1.show();

                                                            Toast.makeText(PicsActivity.this,"Share",Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            l3.removeView(imageViews.get(finalI));
                                                            l4.removeView(imageViews.get(finalI));
                                                            listResult.getItems().get(finalI).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(PicsActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                            builder.show();
                                        }
                                    });
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
                num++;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                TextView textView = new TextView(getApplicationContext());
                textView.setText("PIC");
                imageView.setAdjustViewBounds(true);
                if (isOdd(num)){
                    l3.addView(imageView);
                    l3.addView(textView);
                }
                else {
                    l4.addView(imageView);
                    l4.addView(textView);
                }
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