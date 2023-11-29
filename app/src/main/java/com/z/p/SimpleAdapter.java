package com.z.p;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.z.p.models.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    private final ArrayList<ImageModel> list;
    public ImageListener imageListener;
    public interface ImageListener {
        void onImageSelected(String image);
    }

    public  void onClick(ImageListener imageListener){
        this.imageListener = imageListener;
    }

    public SimpleAdapter(ArrayList<ImageModel> list) {
        this.list = list;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        ImageModel imageModel = list.get(position);
        Picasso.get().load(imageModel.getImage()).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageListener != null){
                    int pos  = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        imageListener.onImageSelected(imageModel.getImage());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
