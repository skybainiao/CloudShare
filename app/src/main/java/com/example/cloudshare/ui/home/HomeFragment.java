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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private LinearLayout l1;
    private LinearLayout l2;
    private FloatingActionButton floatingActionButton;
    private int num;
    private int folders;
    private String username;


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
        folders = 1;
        l1=root.findViewById(R.id.l1);
        l2=root.findViewById(R.id.l2);
        floatingActionButton=root.findViewById(R.id.floatingActionButton2);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.xiazai);
        imageView.setAdjustViewBounds(true);
        TextView textView = new TextView(getContext());
        textView.setText("Default folder");
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        l1.addView(imageView);
        l1.addView(textView);

        loadPic();



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Message")
                        .setMessage("Do you want to add folders or pictures?")
                        //点击窗口以外的区域，窗口消失 (默认为true)
                        .setCancelable(false)
                        //点击其中一个按钮才消失弹窗
                        //一般有三个 Button类型：位置、命名不同，但方法一样
                        // PositiveButton（确定，位置在最右边）,NegativeButton（否定，位置在最右边的左边）,NeutralButton（中立，位置在最左边）
                        .setPositiveButton("Picture", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Picture", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Folder", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addFolder();
                                Toast.makeText(getContext(),"Folder",Toast.LENGTH_SHORT).show();
                            }
                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
        imageView.setImageResource(R.mipmap.xiazai);
        imageView.setAdjustViewBounds(true);
        TextView textView = new TextView(getContext());
        textView.setText("folder"+folders);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (isOdd(folders)){
            l2.addView(imageView);
            l2.addView(textView);
        }
        else {
            l1.addView(imageView);
            l1.addView(textView);
        }
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
        binding = null;
    }
}