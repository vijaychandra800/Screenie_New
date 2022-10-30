package com.vpapps.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.hdwallpaper.R;
import com.vpapps.items.ItemCat;
import com.vpapps.utils.Methods;

import java.util.ArrayList;


public class AdapterCategories extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ItemCat> arrayList;
    private ArrayList<ItemCat> filteredArrayList;
    private NameFilter filter;
    private int columnWidth = 0;
    private Methods methods;

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private RoundedImageView imageView;
        private TextView textView_cat, textView_item_no;

        private MyViewHolder(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.rl_cat);
            imageView = view.findViewById(R.id.iv_cat);
            textView_cat = view.findViewById(R.id.tv_cat_title);
            textView_item_no = view.findViewById(R.id.tv_cat_number);
        }
    }

    public AdapterCategories(Context context, ArrayList<ItemCat> arrayList) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        methods = new Methods(context);
        columnWidth = methods.getColumnWidth(3, 10);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categories, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(columnWidth, columnWidth);
        params.setMargins(20,20,30,20);
        ((MyViewHolder) holder).relativeLayout.setLayoutParams(params);
        ((MyViewHolder) holder).textView_cat.setTypeface(((MyViewHolder) holder).textView_cat.getTypeface(), Typeface.BOLD);
        ((MyViewHolder) holder).textView_cat.setText(arrayList.get(position).getName());
        ((MyViewHolder) holder).textView_item_no.setText("Items (" + arrayList.get(position).getTotalWallpaper() + ")");
        Picasso.get()
                .load(methods.getImageThumbSize(arrayList.get(position).getImageThumb().replace(" ", "%20"),context.getString(R.string.categories)))
                .placeholder(R.drawable.placeholder_cat)
                .into(((MyViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
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
                ArrayList<ItemCat> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arrayList = (ArrayList<ItemCat>) results.values;
            notifyDataSetChanged();
        }
    }
}