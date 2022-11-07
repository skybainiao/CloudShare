package com.example.cloudshare.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudshare.MainActivity;
import com.example.cloudshare.Model.Pic;
import com.example.cloudshare.MyAdapter;
import com.example.cloudshare.R;
import com.example.cloudshare.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Context mContext;
    private GridView grid_photo;
    private BaseAdapter mAdapter = null;
    private ArrayList<Pic> mData = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mContext = getContext();
        grid_photo = (GridView) root.findViewById(R.id.grid_photo);

        mData = new ArrayList<Pic>();
        mData.add(new Pic(R.mipmap.iv_icon_1, "图标1"));
        mData.add(new Pic(R.mipmap.iv_icon_2, "图标2"));
        mData.add(new Pic(R.mipmap.iv_icon_3, "图标3"));
        mData.add(new Pic(R.mipmap.iv_icon_4, "图标4"));
        mData.add(new Pic(R.mipmap.iv_icon_5, "图标5"));
        mData.add(new Pic(R.mipmap.iv_icon_6, "图标6"));
        mData.add(new Pic(R.mipmap.iv_icon_7, "图标7"));

        mAdapter = new MyAdapter<Pic>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Pic obj) {
                holder.setImageResource(R.id.img_icon, obj.getId());
                holder.setText(R.id.txt_icon, obj.getName());
            }
        };

        grid_photo.setAdapter(mAdapter);

        grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "你点击了~" + position + "~项", Toast.LENGTH_SHORT).show();
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