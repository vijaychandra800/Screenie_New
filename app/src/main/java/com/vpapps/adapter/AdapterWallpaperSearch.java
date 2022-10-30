package com.vpapps.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.vpapps.asyncTask.LoadFav;
import com.vpapps.hdwallpaper.R;
import com.vpapps.interfaces.RecyclerViewClickListener;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.items.ItemWallpaper;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterWallpaperSearch extends RecyclerView.Adapter {

    ArrayList<ItemWallpaper> arrayList;
    ArrayList<ItemWallpaper> filteredArrayList;
    NameFilter filter;
    Context context;
    SharedPref sharedPref;
    Methods methods;
    String type;
    int columnWidth = 0, columnHeight = 0;
    RecyclerViewClickListener recyclerViewClickListener;

    final int VIEW_ITEM = 1;

    Boolean isAdLoaded = false;
    List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    List<NativeAdDetails> nativeAdsStartApp = new ArrayList<>();

    public AdapterWallpaperSearch(Context context, String type, ArrayList<ItemWallpaper> arrayList, RecyclerViewClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.type = type;
        methods = new Methods(context);
        sharedPref = new SharedPref(context);
        setColumnWidthHeight(type);
        this.recyclerViewClickListener = recyclerViewClickListener;
        loadNativeAds();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootlayout;
        LikeButton likeButton;
        TextView textView_cat;
        View vieww;
        SimpleDraweeView my_image_view;

        private MyViewHolder(View view) {
            super(view);
            textView_cat = view.findViewById(R.id.tv_wall_cat);
            vieww = view.findViewById(R.id.view_wall);
            rootlayout = view.findViewById(R.id.rootlayout);
            likeButton = view.findViewById(R.id.button_wall_fav);
            my_image_view = view.findViewById(R.id.my_image_view);
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
        if (viewType >= 1000) {
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

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(columnWidth, (int) (columnHeight * 0.4));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            ((MyViewHolder) holder).vieww.setLayoutParams(params);
            ((MyViewHolder) holder).my_image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((MyViewHolder) holder).my_image_view.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight));

            ((MyViewHolder) holder).my_image_view.setImageURI(Uri.parse(methods.getImageThumbSize(arrayList.get(position).getImageThumb(), type)));

            if (sharedPref.isLogged()) {
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
                        if (!sharedPref.isLogged()) {
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
                    switch (Constant.nativeAdType) {
                        case Constant.AD_TYPE_ADMOB:
                        case Constant.AD_TYPE_FACEBOOK:
                            if (mNativeAdsAdmob.size() >= 1) {

                                int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                                NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                                ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                ((ADViewHolder) holder).rl_native_ad.addView(adView);

                                ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                            }
                            break;
                        case Constant.AD_TYPE_STARTAPP:
                            int i = new Random().nextInt(nativeAdsStartApp.size() - 1);

                            RelativeLayout nativeAdView = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);
                            populateStartAppNativeAdView(nativeAdsStartApp.get(i), nativeAdView);

                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                            break;
                        case Constant.AD_TYPE_APPLOVIN:
                            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.ad_native_id, context);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    nativeAdView.setPadding(0, 0, 0, 10);
                                    nativeAdView.setBackgroundColor(Color.WHITE);
                                    ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                    ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                }
                            });

                            nativeAdLoader.loadAd();
                            break;
                    }
                }
            }
        }
    }

    private void loadFav(final int posi, RelativeLayout rootlayout) {
        if (sharedPref.isLogged()) {
            if (methods.isNetworkAvailable()) {
                LoadFav loadFav = new LoadFav(new SuccessListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd(String success, String favSuccess, String message) {
                        if (success.equals("1")) {
                            arrayList.get(posi).setIsFav(favSuccess.equals("1"));
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

    public String getID(int position) {
        return arrayList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) == null) {
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

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemWallpaper> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getTags();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemWallpaper>) results.values;
            notifyDataSetChanged();
        }
    }

    public int getRealPos(int pos, ArrayList<ItemWallpaper> arrayListTemp) {

        int count = 0;
        for (int i = 0; i < arrayListTemp.size(); i++) {
            if (arrayListTemp.get(i).getId().equals(arrayList.get(pos).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    @SuppressLint("MissingPermission")
    private void loadNativeAds() {
        if (Constant.isNativeAd) {
            switch (Constant.nativeAdType) {
                case Constant.AD_TYPE_ADMOB:
                case Constant.AD_TYPE_FACEBOOK:
                    AdLoader.Builder builder = new AdLoader.Builder(context, Constant.ad_native_id);
                    AdLoader adLoader = builder.forNativeAd(
                            new NativeAd.OnNativeAdLoadedListener() {
                                @Override
                                public void onNativeAdLoaded(@NotNull NativeAd nativeAd) {
                                    mNativeAdsAdmob.add(nativeAd);
                                    isAdLoaded = true;
                                }
                            }).build();

                    // Load the Native Express ad.
                    Bundle extras = new Bundle();
                    if (ConsentInformation.getInstance(context).getConsentStatus() != ConsentStatus.PERSONALIZED) {
                        extras.putString("npa", "1");
                    }
                    AdRequest adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                            .build();

                    adLoader.loadAds(adRequest, 5);
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    StartAppNativeAd nativeAd = new StartAppNativeAd(context);

                    nativeAd.loadAd(new NativeAdPreferences()
                            .setAdsNumber(3)
                            .setAutoBitmapDownload(true)
                            .setPrimaryImageSize(2), new AdEventListener() {
                        @Override
                        public void onReceiveAd(Ad ad) {
                            nativeAdsStartApp.addAll(nativeAd.getNativeAds());
                            isAdLoaded = true;
                        }

                        @Override
                        public void onFailedToReceiveAd(Ad ad) {
                        }
                    });
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    isAdLoaded = true;
                    break;
            }
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

    private void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails, RelativeLayout nativeAdView) {
        ImageView icon = nativeAdView.findViewById(R.id.icon);
        TextView title = nativeAdView.findViewById(R.id.title);
        TextView description = nativeAdView.findViewById(R.id.description);
        Button button = nativeAdView.findViewById(R.id.button);

        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
        title.setText(nativeAdDetails.getTitle());
        description.setText(nativeAdDetails.getDescription());
        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
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