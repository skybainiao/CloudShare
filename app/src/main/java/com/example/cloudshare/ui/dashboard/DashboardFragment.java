package com.example.cloudshare.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentDashboardBinding;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();;
    private String username;
    private String sender;
    private LinearLayout l5;
    private String path;
    private String name;
    private Bitmap bitmap1;
    private long num = 0;
    private StorageReference pathReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Intent getIntent = getActivity().getIntent();
        username = getIntent.getStringExtra("username");
        l5 = root.findViewById(R.id.l5);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("ImageTransfer").hasChild(username)){
                    path=snapshot.child("ImageTransfer").child(username).child("path").getValue().toString();
                    sender=snapshot.child("ImageTransfer").child(username).child("sender").getValue().toString();
                    System.out.println(path);
                    System.out.println(sender);
                    num=1;
                    pathReference = storageReference.child(path);
                    System.out.println(pathReference.getName());
                    name=pathReference.getName();
                    loadMessages();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }

    public void loadMessages(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.mipmap.message);
        TextView textView = new TextView(getContext());
        textView.setText(sender+" shared a picture with you");
        textView.setTextSize(20);
        textView.setPadding(20,50,0,0);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.WHITE);
        linearLayout.addView(imageView);
        linearLayout.addView(textView);
        l5.addView(linearLayout);
        linearLayout.setPadding(0,30,0,0);
        l5.setPadding(50,150,0,0);



        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                ImageView imageView1 = new ImageView(getContext());
                pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        bitmap1=bitmap;
                        imageView1.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Message")
                        .setMessage("Do you want to receive this picture?")
                        .setCancelable(true)
                        .setView(imageView1)
                        .setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();

                                StorageReference childRef = storageReference.child(username+"/"+"folder1"+"/"+name);

                                UploadTask uploadTask = childRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(),exception.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        l5.removeView(linearLayout);
                                        databaseReference.child("ImageTransfer").child(username).removeValue();
                                        Toast.makeText(getContext(),"Accepted,The picture has been saved to your folder1",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNeutralButton("Refuse", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                l5.removeView(linearLayout);
                                databaseReference.child("ImageTransfer").child(username).removeValue();
                                Toast.makeText(getContext(),"Refused",Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();


            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}