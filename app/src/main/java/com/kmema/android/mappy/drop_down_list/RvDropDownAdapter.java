package com.kmema.android.mappy.drop_down_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kmema.android.mappy.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RvDropDownAdapter extends RecyclerView.Adapter<RvDropDownAdapter.MyDropDownViewHolder>{

    List<String> myDropDownMenuList;
    Context mContext;
    String[] myList = {"Airport","Amusement Park","Aquarium","Art Gallery","ATM", "Bakery", "Bank", "Bar", "Beauty Salon", "Book Store", "Bus Station", "Cafe", "Car Dealer"};
    public RvDropDownAdapter(Context mContext) {
        myDropDownMenuList = Arrays.asList(myList);
        this.mContext = mContext;
    }

    @Override
    public MyDropDownViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dropdown_item, parent, false);
        MyDropDownViewHolder myDropDownViewHolder = new MyDropDownViewHolder(view);
        return myDropDownViewHolder;
    }

    @Override
    public void onBindViewHolder(MyDropDownViewHolder holder, int position) {
        holder.textViewItem.setText(myDropDownMenuList.get(position));
    }

    @Override
    public int getItemCount() {
        return myDropDownMenuList.size();
    }

    class MyDropDownViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        public MyDropDownViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.tvDropDownItem);
        }
    }
}
