package com.z.p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    private List<Integer> list;

    public SimpleAdapter(List<Integer> list) {
        this.list = list;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        int imageResId = list.get(position);
        holder.image.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
