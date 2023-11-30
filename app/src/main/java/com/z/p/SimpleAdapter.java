package com.z.p;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.z.p.models.ImageModel;
import com.z.p.room.BitmapModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {
    private final List<BitmapModel> list;
    public ImageListener imageListener;
    public interface ImageListener {
        void onImageSelected(Bitmap bitmap);
    }

    public  void onClick(ImageListener imageListener){
        this.imageListener = imageListener;
    }

    public SimpleAdapter(List<BitmapModel> list) {
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
        BitmapModel imageModel = list.get(position);
        holder.image.setImageBitmap(imageModel.getBitmap());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageListener != null){
                    int pos  = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        imageListener.onImageSelected(imageModel.getBitmap());
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
