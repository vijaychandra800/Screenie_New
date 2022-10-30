package com.vpapps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.asyncTask.LoadFav;
import com.vpapps.hdwallpaper.R;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.items.ItemWallpaper;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterImageHome extends RecyclerView.Adapter {

    ArrayList<ItemWallpaper> arrayList;
    Context context;
    RecyclerViewClickListener recyclerViewClickListener;
    Methods methods;
    SharedPref sharedPref;
    String type;

    private class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootlayout;
        RoundedImageView imageView;
        LikeButton likeButton;
        TextView textView_cat;

        private MyViewHolder(View view) {
            super(view);
            rootlayout = view.findViewById(R.id.rootlayout);
            imageView = view.findViewById(R.id.iv_home_latest);
            likeButton = view.findViewById(R.id.button_home_fav);
            textView_cat = view.findViewById(R.id.tv_home_cat);
        }
    }

    public AdapterImageHome(Context context, String type, ArrayList<ItemWallpaper> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.type = type;
        methods = new Methods(context);
        sharedPref = new SharedPref(context);
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if (type.equals(context.getString(R.string.portrait))) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_home, parent, false);
        } else if (type.equals(context.getString(R.string.landscape))) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_home_l, parent, false);
        } else if ((type.equals(context.getString(R.string.square)))) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_home_s, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).likeButton.setLiked(arrayList.get(position).getIsFav());

        ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getCName());

        String imageurl = methods.getImageThumbSize(arrayList.get(position).getImageThumb(), type);
        if(imageurl.equals("")) {
            imageurl = "null";
        }

        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.placeholder_wall)
                .into(((MyViewHolder) holder).imageView);

        if(sharedPref.isLogged()) {
            ((MyViewHolder) holder).likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    try {
                        loadFav(holder.getAbsoluteAdapterPosition(), ((MyViewHolder) holder).rootlayout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    try {
                        loadFav(holder.getAbsoluteAdapterPosition(), ((MyViewHolder) holder).rootlayout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ((MyViewHolder) holder).likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!sharedPref.isLogged()) {
                        methods.clickLogin();
                    }
                }
            });
        }

        ((MyViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onClick(holder.getAdapterPosition());
            }
        });
    }

    private void loadFav(final int posi, RelativeLayout rootlayout) {
        if(sharedPref.isLogged()) {
            if (methods.isNetworkAvailable()) {
                LoadFav loadFav = new LoadFav(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            if (favSuccess.equals("1")) {
                                arrayList.get(posi).setIsFav(true);
                            } else {
                                arrayList.get(posi).setIsFav(false);
                            }
                            methods.showSnackBar(rootlayout, message);
                        }
                    }
                }, methods.getAPIRequest(Constant.METHOD_DO_FAV, 0, arrayList.get(posi).getId(), "", "", "", "", "", "", "", "", "", sharedPref.getUserId(), "wallpaper"));
                loadFav.execute();
            } else {
                Toast.makeText(context, context.getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.clickLogin();
        }
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