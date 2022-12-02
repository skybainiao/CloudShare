package com.example.cloudshare.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentDashboardBinding;
import com.example.cloudshare.databinding.FragmentNotificationsBinding;
import com.example.cloudshare.ui.LoginActivity;
import com.example.cloudshare.ui.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://cloudshare-f4727-default-rtdb.europe-west1.firebasedatabase.app/");;
    private DatabaseReference databaseReference=firebaseDatabase.getReference();
    private int PicNum;
    private int FolderNum;

    private String username;
    private TextView usernameImg;
    private TextView username1;
    private TextView picNum;
    private TextView folderNum;
    private TextView picStorage;
    private Button changePassword;
    private Button logout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        storage = FirebaseStorage.getInstance();

        Intent getIntent = getActivity().getIntent();
        username = getIntent.getStringExtra("username");
        usernameImg=root.findViewById(R.id.textView2);
        username1=root.findViewById(R.id.textView4);
        picNum=root.findViewById(R.id.textView6);
        folderNum=root.findViewById(R.id.textView9);
        picStorage=root.findViewById(R.id.textView11);
        changePassword=root.findViewById(R.id.button);
        logout=root.findViewById(R.id.button2);

        StorageReference storageReference1 = storage.getReference().child(username);

        usernameImg.setText(username);
        username1.setText(username);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot.child("Users").child(username).child("folders").getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference1.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            System.out.println(listResult.getPrefixes().size());
                            FolderNum=listResult.getPrefixes().size();
                            folderNum.setText(String.valueOf(FolderNum));
                            System.out.println(prefix.getName());
                            StorageReference storageReference2 = storage.getReference().child(username).child(prefix.getName());
                            storageReference2.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    PicNum+=listResult.getItems().size();
                                    System.out.println(PicNum);
                                    picNum.setText(String.valueOf(PicNum));
                                    picStorage.setText(PicNum+"/"+1000);
                                }
                            });
                        }

                        for (StorageReference item : listResult.getItems()) {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(getContext());
                EditText editText1 = new EditText(getContext());
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(editText);
                linearLayout.addView(editText1);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Message")
                        .setMessage("Enter your new password twice")
                        .setCancelable(true)
                        .setView(linearLayout)
                        .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editText.getText().toString().equals(editText1.getText().toString())){
                                    databaseReference.child("Users").child(username).child("password").setValue(editText.getText().toString());
                                    Toast.makeText(getContext(),"Success", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();



            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}