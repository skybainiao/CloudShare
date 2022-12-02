package com.example.cloudshare.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentDashboardBinding;
import com.example.cloudshare.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();;
    private String username;
    private LinearLayout l5;
    private int num;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Intent getIntent = getActivity().getIntent();
        username = getIntent.getStringExtra("username");
        l5 = root.findViewById(R.id.l5);
        num=3;


        loadMessages();






        return root;
    }

    public void loadMessages(){
        for (int i = 0; i < num; i++) {
            System.out.println(i);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.mipmap.message);
            TextView textView = new TextView(getContext());
            textView.setText("xxx send share a picture to you");
            textView.setTextSize(18);
            textView.setPadding(20,50,0,0);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(Color.WHITE);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            l5.addView(linearLayout);
            linearLayout.setPadding(0,30,0,0);
            l5.setPadding(50,150,0,0);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}