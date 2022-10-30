package com.vpapps.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vpapps.hdwallpaper.R;
import com.vpapps.items.ItemColors;
import com.vpapps.utils.Methods;

import java.util.ArrayList;


public class AdapterColors extends RecyclerView.Adapter {

    private ArrayList<ItemColors> arrayList;
    private Methods methods;
    private ArrayList<String> arrayListSelected = new ArrayList<>();

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView, iv_color_tick;

        private MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_color);
            iv_color_tick = view.findViewById(R.id.iv_color_tick);
        }
    }

    public AdapterColors(Context context, ArrayList<ItemColors> arrayList) {
        this.arrayList = arrayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_colors_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if(arrayListSelected.contains(arrayList.get(position).getId())) {
            ((MyViewHolder) holder).imageView.setBorderColor(Color.WHITE);
            ((MyViewHolder) holder).iv_color_tick.setVisibility(View.VISIBLE);
        } else {
            ((MyViewHolder) holder).imageView.setBorderColor(Color.TRANSPARENT);
            ((MyViewHolder) holder).iv_color_tick.setVisibility(View.GONE);
        }

        ((MyViewHolder) holder).imageView.setColorFilter(Color.parseColor(arrayList.get(position).getColorHex()));
        Picasso.get()
                .load(arrayList.get(position).getColorHex())
                .placeholder(R.drawable.placeholder_cat)
                .into(((MyViewHolder) holder).imageView, new Callback() {
                    @Override
                    public void onSuccess() {
//                        try {
//                            Picasso.get()
//                                    .load(arrayList.get(holder.getAdapterPosition()).getImage().replace(" ", "%20"))
//                                    .into(((MyViewHolder) holder).imageView);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onError(Exception e) {
//                        Log.e("aaaa","error");
//                        Picasso.get()
//                                .load(arrayList.get(holder.getAdapterPosition()).getImage().replace(" ", "%20"))
//                                .placeholder(R.drawable.placeholder_cat)
//                                .into(((MyViewHolder) holder).imageView);
                    }
                });
    }

    public void setSelected(int position) {
        if(arrayListSelected.contains(arrayList.get(position).getId())) {
            arrayListSelected.remove(arrayList.get(position).getId());
        }else {
            arrayListSelected.add(arrayList.get(position).getId());
        }
        notifyItemChanged(position);
    }

    public String getSelected() {
        String selectedIDs="";
        if(arrayListSelected.size()>0 && arrayListSelected.size() < getItemCount()) {
            StringBuilder selectedIDsBuilder = new StringBuilder(arrayListSelected.get(0));
            for (int i = 1; i < arrayListSelected.size(); i++) {
                selectedIDsBuilder.append(",").append(arrayListSelected.get(i));
            }
            selectedIDs = selectedIDsBuilder.toString();
        }
        return selectedIDs;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}