package com.app.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.app.asyncTask.LoadFav;
import com.app.screenie.R;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.SuccessListener;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.DBHelper;
import com.app.utils.Methods;

import java.util.ArrayList;


public class AdapterImageHome extends RecyclerView.Adapter {

    private ArrayList<ItemWallpaper> arrayList;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;
    private DBHelper dbHelper;
    private Methods methods;
    private String type;

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rootlayout;
        private RoundedImageView imageView;
        private LikeButton likeButton;
        private TextView textView_cat;

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
        dbHelper = new DBHelper(context);
        methods = new Methods(context);
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

        if(Constant.isLogged) {
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
                    if(!Constant.isLogged) {
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
        if(Constant.isLogged) {
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
                }, methods.getAPIRequest(Constant.METHOD_DO_FAV, 0, arrayList.get(posi).getId(), "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "wallpaper"));
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