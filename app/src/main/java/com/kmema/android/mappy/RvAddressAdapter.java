package com.kmema.android.mappy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kmema on 2/5/2018.
 */

public class RvAddressAdapter extends RecyclerView.Adapter<RvAddressAdapter.ViewHolder>{



    private List<HashMap<String, String>> listData;
    private Context mContext;
    public RvAddressAdapter(List<HashMap<String, String>> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_in_rv, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvAddressName.setText("Name: "+listData.get(position).get("place_name"));
        holder.tvVicinity.setText("Vicinity: "+listData.get(position).get("vicinity"));
        holder.tvLongitude.setText("Latitude: "+listData.get(position).get("lat"));
        holder.tvLatitude.setText("Longitude: "+listData.get(position).get("lng"));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvAddressName, tvVicinity, tvLatitude, tvLongitude;


        public ViewHolder(View itemView) {
            super(itemView);
                tvAddressName = itemView.findViewById(R.id.tvName);
                tvLatitude = itemView.findViewById(R.id.tvLat);
                tvLongitude = itemView.findViewById(R.id.tvLong);
                tvVicinity = itemView.findViewById(R.id.tvVic);
        }
    }
}
