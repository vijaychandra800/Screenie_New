package com.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.app.asyncTask.LoadFav;
import com.app.screenie.R;
import com.app.interfaces.RecyclerViewClickListener;
import com.app.interfaces.SuccessListener;
import com.app.items.ItemWallpaper;
import com.app.utils.Constant;
import com.app.utils.Methods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class AdapterWallpaper extends RecyclerView.Adapter {

    private ArrayList<ItemWallpaper> arrayList;
    private Context context;
    private RecyclerViewClickListener recyclerViewClickListener;
    private Methods methods;
    private String type;
    private int columnWidth = 0, columnHeight = 0;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private AdLoader adLoader = null;
    private List<NativeAd> mNativeAdsAdmob = new ArrayList<>();

    public AdapterWallpaper(Context context, String type, ArrayList<ItemWallpaper> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.type = type;
        methods = new Methods(context);
        setColumnWidthHeight(type);
        this.recyclerViewClickListener = recyclerViewClickListener;
        loadNativeAds();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rootlayout;
        private LikeButton likeButton;
        private TextView textView_cat;
        //private View vieww;
        private RoundedImageView my_image_view;

        private MyViewHolder(View view) {
            super(view);
            rootlayout = view.findViewById(R.id.rootlayout);
            my_image_view = view.findViewById(R.id.my_image_view);
            likeButton = view.findViewById(R.id.button_wall_fav);
            textView_cat = view.findViewById(R.id.tv_wall_cat);
            //vieww = view.findViewById(R.id.view_wall);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        //private static CircularProgressBar progressBar;
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_wall, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            ((MyViewHolder) holder).likeButton.setLiked(arrayList.get(position).getIsFav());
            ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getCName());

            //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(columnWidth, (int) (columnHeight * 0.4));
            //params.gravity=RelativeLayout.ALIGN_PARENT_BOTTOM;
            //((MyViewHolder) holder).vieww.setLayoutParams(params);

            ((MyViewHolder) holder).my_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // ((MyViewHolder) holder).my_image_view.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight));
            Log.e("url is ", "is " + arrayList.get(position).getImageThumb());

            Picasso.get().load(Uri.parse(methods.getImageThumbSize(arrayList.get(position).getImageThumb(), type)))
                    .into(((MyViewHolder) holder).my_image_view);
            /*((MyViewHolder) holder).my_image_view.
                    setImageURI();*/

            if(Constant.isLogged) {
                ((MyViewHolder) holder).likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        try {
                            loadFav(holder.getAbsoluteAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        try {
                            loadFav(holder.getAbsoluteAdapterPosition());
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


            ((MyViewHolder) holder).my_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickListener.onClick(holder.getAdapterPosition());
                }
            });
        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    if (mNativeAdsAdmob.size() >= 5) {

                        int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

//                            CardView cardView = (CardView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                        NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                        populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                        ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                        ((ADViewHolder) holder).rl_native_ad.addView(adView);

                        ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void loadFav(final int posi) {
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
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        Log.d("mridx", "hideHeader: came to hide header");
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public void setType(String type) {
        this.type = type;
        setColumnWidthHeight(type);
    }

    private void setColumnWidthHeight(String type) {
        if (type.equals("") || type.equals(context.getString(R.string.portrait))) {
            columnWidth = methods.getColumnWidth(3, 3);
            columnHeight = (int) (columnWidth * 1.55);
        } else if (type.equals(context.getString(R.string.landscape))) {
            columnWidth = methods.getColumnWidth(2, 3);
            columnHeight = (int) (columnWidth * 0.54);
        } else {
            columnWidth = methods.getColumnWidth(3, 3);
            columnHeight = columnWidth;
        }
    }

    public int getRealPos(int pos, ArrayList<ItemWallpaper> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
    }

    private void loadNativeAds() {
        if (Constant.isNativeAd) {
            AdLoader.Builder builder = new AdLoader.Builder(context, Constant.ad_native_id);
            adLoader = builder.forNativeAd(
                    new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            mNativeAdsAdmob.add(nativeAd);
                            isAdLoaded = true;
                        }
                    }).build();
            adLoader.loadAds(new AdRequest.Builder().build(), 5);
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}