package com.example.cloudshare.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.cloudshare.MainActivity;
import com.example.cloudshare.Model.Pic;
import com.example.cloudshare.MyAdapter;
import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentHomeBinding;
import com.example.cloudshare.ui.LoginActivity;
import com.example.cloudshare.ui.PicsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayout l1;
    private LinearLayout l2;
    private FloatingActionButton floatingActionButton;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();;
    private int num;
    private int folders;
    private String username;
    private ArrayList<String> folderList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Intent getIntent = getActivity().getIntent();
        username = getIntent.getStringExtra("username");
        System.out.println(username);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        num=0;
        folders = 0;
        folderList = new ArrayList<>();

        l1=root.findViewById(R.id.l1);
        l2=root.findViewById(R.id.l2);
        floatingActionButton=root.findViewById(R.id.floatingActionButton2);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long size = snapshot.child("Users").child(username).child("folders").getChildrenCount();
                System.out.println(size);
                for (int i = 0; i < size; i++) {
                    addFolder();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loadPic();





        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Message")
                        .setMessage("Do you want to add a new folder?")
                        .setCancelable(false)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addFolder();
                                //saveFolders(folderList);
                                Toast.makeText(getContext(),"Folder",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();


            }
        });



        System.out.println("SHA:"+sHA1(getContext()));

        return root;
    }

    public void addFolder(){
        folders++;
        ImageView imageView = new ImageView(getContext());
        imageView.setTag("folder"+folders);
        imageView.setImageResource(R.mipmap.xiazai);
        imageView.setAdjustViewBounds(true);
        TextView textView = new TextView(getContext());
        textView.setText("folder"+folders);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        folderList.add(textView.getText().toString());
        System.out.println(folderList);
        if (isOdd(folders)){
            l2.addView(imageView);
            l2.addView(textView);
        }
        else {
            l1.addView(imageView);
            l1.addView(textView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PicsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("folderName", imageView.getTag().toString());
                System.out.println(imageView.getTag());
                startActivity(intent);
            }
        });

    }

    public void saveFolders(ArrayList<String> folderList){
        databaseReference.child("Users").child(username).child("folders").setValue(folderList);
    }

    public boolean isOdd(int a){
        if((a&1) != 1){
            return true;
        }
        return false;
    }

    public void loadPic(){

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        StorageReference ref = storageReference.child("img/WeChat Image_20220408133019.jpg");

        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            TextView textView = new TextView(getContext());

            ImageView imageView1 = new ImageView(getContext());
            imageView1.setAdjustViewBounds(true);
            TextView textView1 = new TextView(getContext());


            //set img
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                    imageView1.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            textView.setText("");
            imageView.setAdjustViewBounds(true);
            l1.addView(imageView);
            l1.addView(textView);

            textView1.setText("");
            imageView1.setAdjustViewBounds(true);
            l2.addView(imageView1);
            l2.addView(textView1);

        }
    }


    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }




        @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveFolders(folderList);
        binding = null;
    }
}